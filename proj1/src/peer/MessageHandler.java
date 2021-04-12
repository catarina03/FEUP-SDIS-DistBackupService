package peer;

import files.BackupChunk;
import files.BackupFile;
import messages.InvalidMessageException;
import messages.PutchunkMessage;
import tasks.StoreTask;
import tasks.AssembleFileTask;
import tasks.ChunkTask;
import tasks.PutchunkTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageHandler {
    private static final int NUMBER_OF_WORKERS = 10;
    private Peer peer;
    protected final String doubleCRLF = "\r\n\r\n";
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

            System.out.println("Sender id: " + newHeader.senderId);
            System.out.println("Peer id: " + this.peer.id);
            long sum = this.peer.storage.occupiedSpace + body.length;
            System.out.println("Occupied space: " + this.peer.storage.occupiedSpace + " Body length: " + body.length
                    + " Sum: " + sum);
            System.out.println("Max capacity: " + this.peer.storage.maxCapacityAllowed);
            System.out.println("Contains file in files? " + this.peer.storage.files.containsKey(newHeader.fileId));

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

            System.out.println("Márcio is very pretty -> stored" + this.peer.storage);
            if (newHeader.senderId != this.peer.id) {
                String chunkId = newHeader.fileId + newHeader.chunkNo;

                if (this.peer.storage.files.containsKey(newHeader.fileId) || this.peer.storage.backedUpChunks.containsKey(chunkId)) {

                    ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation
                            .computeIfAbsent(chunkId, value -> new ConcurrentSkipListSet<>());

                    if (!currentChunkStorageList.contains(newHeader.senderId)) {

                        // UPDATES THE LIST OF CHUNKS' LOCATION
                        currentChunkStorageList.add(newHeader.senderId);

                        // INCREASES REPLICATION DEGREE OF STORED CHUNK
                        // Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree
                        //         .putIfAbsent(chunkId, 1);
                        // if (currentReplicationDegree != null) {
                        //     this.peer.storage.chunksReplicationDegree.replace(chunkId, currentChunkStorageList.size());
                        // }

                        System.out.println("Márcio is very pretty -> before -> Header" + newHeader);
                        System.out.println("Márcio is very pretty -> before -> RD" + this.peer.storage.chunksReplicationDegree);

                        this.peer.storage.chunksReplicationDegree.computeIfPresent(chunkId, (k,v)->v+1);

                        this.peer.storage.chunksReplicationDegree.putIfAbsent(chunkId, 1);
                        
                        System.out.println("Márcio is very pretty -> after -> Header" + newHeader);
                        System.out.println(
                                "Márcio is very pretty -> after -> RD" + this.peer.storage.chunksReplicationDegree);

                    }else{
                        // INCREASES REPLICATION DEGREE OF BACKED UP CHUNK IN FILE MAP
                        BackupFile backedUpFile = this.peer.storage.files.get(newHeader.fileId);
                        backedUpFile.updateChunk(chunkId);
                    }
                }
            }
            System.out.println("Márcio is very pretty -> stored" + this.peer.storage);

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
                // chunkTask.run();
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

            // not host recieving chunks -> peer sending chunks -> detected sent chunk in
            // channel -> makes boolean true for a certain ammount of time to avoid sending
            // chunk and overloading the host peer
            this.peer.recievedChunkMessage = true; // every chunk message perceived, boolean goes true

            // waits some time before making boolean false again
            Random rand = new Random();
            int upperbound = 401;
            int randomDelay = rand.nextInt(upperbound);

            System.out.println("The peer " + this.peer.id
                    + " is sleeping in the chunk handler. It will not send chunks in the meantime.");

            try {
                Thread.sleep(randomDelay);  //FIXME: i shall not sleep
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.peer.recievedChunkMessage = false;
            break;

        case "DELETE":
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

                    this.peer.storage.chunksReplicationDegree.remove(deleteChunkId);

                    this.peer.storage.chunksLocation.remove(deleteChunkId);

                    // delete chunks in local storage
                    this.peer.fileManager.deleteChunkFromDirectory(this.peer.id, newHeader.fileId, backupChunk.chunkNo);
                }
            }
            /*
             * for (int i = 0; i < 10; i++) { String deleteChunkId = newHeader.fileId + i;
             * 
             * if (this.peer.storage.backedUpChunks.containsKey(deleteChunkId)) {
             * this.peer.storage.backedUpChunks.remove(deleteChunkId); }
             * 
             * if (this.peer.storage.chunksReplicationDegree.containsKey(deleteChunkId)) {
             * this.peer.storage.chunksReplicationDegree.remove(deleteChunkId); }
             * 
             * if (this.peer.storage.chunksLocation.containsKey(deleteChunkId)) {
             * this.peer.storage.chunksLocation.remove(deleteChunkId); }
             * 
             * // delete chunks in local storage
             * this.peer.fileManager.deleteFileFromDirectory(this.peer.id, newHeader.fileId,
             * i); }
             */

            break;

        case "REMOVED":
            String removedChunkId = newHeader.fileId + newHeader.chunkNo;
            System.out.println("I'm being removed: " + removedChunkId);
            System.out.println("Márcio é lindo 1: " + this.peer.storage.chunksReplicationDegree);

            if (newHeader.senderId != this.peer.id && this.peer.storage.backedUpChunks.containsKey(removedChunkId)) {

                // ON RECEIVING REMOVED, PEER UPDATES MAPAS DE PERCEIVED E LOCATION

                // this.peer.storage.chunksReplicationDegree.replace(removedChunkId,
                // this.peer.storage.chunksReplicationDegree.get(removedChunkId),
                // this.peer.storage.chunksReplicationDegree.get(removedChunkId) - 1);

                System.out.println(removedChunkId);
                System.out.println("Replication degree before update: "
                        + this.peer.storage.chunksReplicationDegree.get(removedChunkId));

                this.peer.storage.chunksReplicationDegree.computeIfPresent(removedChunkId, (k, v) -> v - 1);
                Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.get(removedChunkId);

                System.out.println("Replication degree after update: "
                        + this.peer.storage.chunksReplicationDegree.get(removedChunkId));

                ConcurrentSkipListSet<Integer> locations = this.peer.storage.chunksLocation.get(removedChunkId);
                if (locations != null) {
                    locations.remove(newHeader.senderId);
                }

                // SE ALGUM CHUNK DROPS BELOW DESIRED REPLICATION DEGREE ENTAO MANDA-SE PUTCHUNK
                // PARA ESSE CHUNK
                if (currentReplicationDegree < this.peer.storage.backedUpChunks.get(removedChunkId)
                        .getDesiredReplicationDegree()) {
                    Header putchunkHeader = new Header(this.peer.version, "PUTCHUNK", this.peer.id, newHeader.fileId,
                            newHeader.chunkNo,
                            this.peer.storage.backedUpChunks.get(removedChunkId).getDesiredReplicationDegree());
                    PutchunkMessage putchunkMessage = new PutchunkMessage(putchunkHeader,
                            this.peer.storage.backedUpChunks.get(removedChunkId), this.peer.multicastDataBackupAddress,
                            this.peer.multicastDataBackupPort);

                    PutchunkTask putchunkTask = new PutchunkTask(this.peer, putchunkMessage);
                    putchunkTask.run();
                }
            }
            System.out.println("Márcio é lindo 2: " + this.peer.storage.chunksReplicationDegree);

            break;

        case "HELLO":
            // FIXME: finish me
            break;

        default:
            System.out.println("Message type " + newHeader.messageType + " not recognized.");
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
