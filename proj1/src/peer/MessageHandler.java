package peer;

import files.BackupChunk;
import messages.InvalidMessageException;
import messages.PutchunkMessage;
import tasks.StoreTask;
import tasks.AssembleFileTask;
import tasks.ChunkTask;
import tasks.PutchunkTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageHandler {
    private static final int NUMBER_OF_WORKERS = 10;
    private Peer peer;
    public final static byte CR = 0xD;
    public final static byte LF = 0xA;
    private final String ENHANCED = "2.0";

    public MessageHandler(Peer peer) {
        this.peer = peer;
    }

    public int getFirstCRLFPosition(byte[] message) {
        int messageLength = message.length;

        for (int i = 0; i < messageLength - 3; i++) {
            if (message[i] == CR && message[i + 1] == LF && message[i + 2] == CR && message[i + 3] == LF) {
                return i;
            }
        }

        return -1;
    }

    public void process(byte[] message, String address, int port) throws InvalidMessageException {

        int firstCRLFPosition = getFirstCRLFPosition(message);
        if (firstCRLFPosition == -1) {
            throw new InvalidMessageException("Invalid message: no CRLF");
        }

        String headerAsString = new String(message, 0, firstCRLFPosition);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));
        if (headerArray.size() < 4) {
            throw new InvalidMessageException("Invalid Header");
        }

        Header newHeader = new Header(headerArray);
        byte[] body = Arrays.copyOfRange(message, firstCRLFPosition + 4, message.length);

        switch (newHeader.messageType) {
        case "PUTCHUNK":

            if (newHeader.senderId != this.peer.id
                    && this.peer.storage.occupiedSpace + body.length <= this.peer.storage.maxCapacityAllowed
                    && !this.peer.storage.files.containsKey(newHeader.fileId)) {
                String chunkId = newHeader.fileId + newHeader.chunkNo;
                BackupChunk newChunk = new BackupChunk(chunkId, newHeader.fileId, newHeader.chunkNo, body.length,
                        newHeader.replicationDegree, body);

                // CREATES TASK THAT SENDS STORED MESSAGES
                StoreTask newTask = new StoreTask(this.peer, newHeader, newChunk);
                newTask.run();
            }
            break;

        case "STORED":
            if (newHeader.senderId != this.peer.id) {
                String chunkId = newHeader.fileId + newHeader.chunkNo;

                // ONLY UPDATES IF THE CHUNK IS FROM A FILE STORED IN THIS PEER OR IF IT IS FROM
                // A CHUNK BACKED UP IN THIS PEER
                if (this.peer.storage.files.containsKey(newHeader.fileId)
                        || this.peer.storage.backedUpChunks.containsKey(chunkId)) {
                    ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation
                            .computeIfAbsent(chunkId, value -> new ConcurrentSkipListSet<>());

                    // IF CHUNK FILE IS PRESENT IN FILE MAP IT INCREASES REPLICATION DEGREE OF THE
                    // BACKED UP CHUNK IN THE MAP
                    if (this.peer.storage.files.containsKey(newHeader.fileId)) {
                        // IF CHUNK IS NOT PRESENT IN THE CHUNK LOCATION MAP IT UPDATES IT (IF ITS
                        // PRESENT DOES NOTHING)
                        if (!currentChunkStorageList.contains(newHeader.senderId)) {
                            currentChunkStorageList.add(newHeader.senderId);
                            // BackupFile backedUpFile = this.peer.storage.files.get(newHeader.fileId);
                            // backedUpFile.updateChunk(chunkId);
                            this.peer.storage.files.get(newHeader.fileId).updateChunk(chunkId);
                        }
                    } else {
                        // IF CHUNK IS NOT PRESENT IN THE CHUNK LOCATION MAP IT UPDATES IT (IF ITS
                        // PRESENT DOES NOTHING)
                        if (!currentChunkStorageList.contains(newHeader.senderId)) {
                            currentChunkStorageList.add(newHeader.senderId);
                        }
                    }
                }
            }
            break;

        case "GETCHUNK":
            String chunkId = newHeader.fileId + newHeader.chunkNo;

            // Verificar se temos o chunk
            BackupChunk chunk = this.peer.storage.backedUpChunks.get(chunkId);

            // Se tivermos criamos uma task para enviar o chunk de volta
            if (chunk != null) {
                Header chunkHeader = new Header(newHeader.version, "CHUNK", this.peer.id, newHeader.fileId,
                        newHeader.chunkNo);
                ChunkTask chunkTask = new ChunkTask(this.peer, chunkHeader, chunk);

                Random rand = new Random();
                int upperbound = 401;
                int randomDelay = rand.nextInt(upperbound); // generate random values from 0-400

                ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
                scheduler.schedule(chunkTask, randomDelay, TimeUnit.MILLISECONDS);
            }
            break;

        case "CHUNK":

            if (this.peer.storage.files.containsKey(newHeader.fileId)
                    && !this.peer.storage.toBeRestoredChunks.containsKey(newHeader.fileId + newHeader.chunkNo)) { // host
                                                                                                                  // receiving
                                                                                                                  // chunks
                String chunkChunkId = newHeader.fileId + newHeader.chunkNo;

                // Guardar chunk num mapa de chunk para serem usados em restore
                this.peer.storage.toBeRestoredChunks.putIfAbsent(chunkChunkId, body);

                // Criar uma task (?) que vai buscar todos os chunks necessarios e monta o
                // ficheiro
                BackupChunk chunkChunk = new BackupChunk(chunkChunkId, newHeader.fileId, newHeader.chunkNo, body.length,
                        0, body);

                AssembleFileTask assembleFileTask = new AssembleFileTask(this.peer, newHeader, chunkChunk);
                assembleFileTask.run();

                break; // it needs to leave, otherwise it sleeps
                // FIXME: it isnt leaving
            }
            break;

        case "DELETE":

            if (newHeader.senderId != this.peer.id) {
                // delete file
                this.peer.storage.files.remove(newHeader.fileId);

                // delete file in local storage
                this.peer.fileManager.deleteFileFromDirectory(this.peer.id, newHeader.fileId);

                // delete chunks and their references
                for (BackupChunk backupChunk : this.peer.storage.backedUpChunks.values()) {
                    if (backupChunk.fileId.equals(newHeader.fileId)) {
                        String deleteChunkId = backupChunk.id;
                        this.peer.storage.occupiedSpace -= backupChunk.getSize();

                        this.peer.storage.backedUpChunks.remove(deleteChunkId);
                        this.peer.storage.chunksLocation.remove(deleteChunkId);

                        // delete chunks in local storage
                        this.peer.fileManager.deleteChunkFromDirectory(this.peer.id, newHeader.fileId,
                                backupChunk.chunkNo);
                    }
                }
            }
            break;

        case "REMOVED":
            String removedChunkId = newHeader.fileId + newHeader.chunkNo;

            if (newHeader.senderId != this.peer.id && this.peer.storage.backedUpChunks.containsKey(removedChunkId)) {
                ConcurrentSkipListSet<Integer> locations = this.peer.storage.chunksLocation.get(removedChunkId);
                if (locations != null) {
                    locations.remove(newHeader.senderId);
                }

                // SE ALGUM CHUNK DROPS BELOW DESIRED REPLICATION DEGREE ENTAO MANDA-SE PUTCHUNK
                // PARA ESSE CHUNK
                if (this.peer.storage.chunksLocation.get(removedChunkId).size() < this.peer.storage.backedUpChunks
                        .get(removedChunkId).getDesiredReplicationDegree()) {
                    Header putchunkHeader = new Header(this.peer.version, "PUTCHUNK", this.peer.id, newHeader.fileId,
                            newHeader.chunkNo,
                            this.peer.storage.backedUpChunks.get(removedChunkId).getDesiredReplicationDegree());
                    PutchunkMessage putchunkMessage = new PutchunkMessage(putchunkHeader,
                            this.peer.storage.backedUpChunks.get(removedChunkId), this.peer.multicastDataBackupAddress,
                            this.peer.multicastDataBackupPort);

                    PutchunkTask putchunkTask = new PutchunkTask(this.peer, putchunkMessage);
                    putchunkTask.run();

                    Header storeHeader = new Header(this.peer.version, "STORED", this.peer.id, newHeader.fileId,
                            newHeader.chunkNo);
                    StoreTask storeTask = new StoreTask(this.peer, storeHeader,
                            this.peer.storage.backedUpChunks.get(removedChunkId));
                    storeTask.run();
                }
            }

            // to update locations on initiator peer
            if (newHeader.senderId != this.peer.id && this.peer.storage.files.containsKey(newHeader.fileId)) {
                ConcurrentSkipListSet<Integer> locations = this.peer.storage.chunksLocation.get(removedChunkId);
                if (locations != null) {
                    locations.remove(newHeader.senderId);
                }
                this.peer.storage.files.get(newHeader.fileId).chunks.computeIfPresent(removedChunkId, (k, v) -> v - 1);
            }
            break;

        case "HELLO":

            if (this.peer.version.equals(ENHANCED) && newHeader.senderId != this.peer.id) {
                for (String fileId : this.peer.storage.deletedFilesLocation.keySet()) {
                    if (this.peer.storage.deletedFilesLocation.get(fileId).contains(newHeader.senderId)) {
                        Header header = new Header(this.peer.version, "DELETE", this.peer.id, fileId);
                        this.peer.sendDelete(header);
                    }
                }
            }
            break;

        default:
            System.err.println("Message type " + newHeader.messageType + " not recognized.");
            break;
        }
        return;
    }

    public void handle(byte[] packet, String address, int port) {
        try {
            this.process(packet, address, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}