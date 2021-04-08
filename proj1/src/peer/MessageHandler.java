package peer;

import files.BackupChunk;
import files.BackupFile;
import messages.InvalidMessageException;
import tasks.StoreTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;


public class MessageHandler {
    private Peer peer;
    protected final String doubleCRLF = "\r\n\r\n";
    private final String ENHANCED = "2.0";

    public MessageHandler(Peer peer){
        this.peer = peer;
    }

    public void process(byte[] message, String address, int port) throws InvalidMessageException {
        String newMessage = new String(message, StandardCharsets.ISO_8859_1);
        ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(newMessage.split(this.doubleCRLF, 2)));

        String headerAsString = messageArray.get(0);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));

        if (headerArray.size() < 4){
            throw new InvalidMessageException("Invalid Header");
        }

        Header newHeader = new Header(headerArray);

        byte[] body = new byte[0];
        if (messageArray.size() != 1) {
            body = messageArray.get(1).getBytes(StandardCharsets.ISO_8859_1);
        }
  
        switch(newHeader.messageType) {
            case "PUTCHUNK":

                if (newHeader.senderId != this.peer.id && this.peer.storage.occupiedSpace + body.length <= this.peer.storage.maxCapacityAllowed){
                    String chunkId = newHeader.fileId + newHeader.chunkNo;
                    BackupChunk newChunk = new BackupChunk(chunkId, body.length, newHeader.replicationDegree, body);

                    // CREATES TASK THAT SENDS STORED MESSAGES
                    StoreTask newTask = new StoreTask(this.peer, newHeader, newChunk);
                    newTask.run();
                }
                break;

            case "STORED":

                if (newHeader.senderId != this.peer.id){
                    String chunkId = newHeader.fileId + newHeader.chunkNo;

                    ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation.computeIfAbsent(chunkId, value -> new ConcurrentSkipListSet<>());
                    if (!currentChunkStorageList.contains(newHeader.senderId)){

                        // INCREASES REPLICATION DEGREE OF STORED CHUNK
                        Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.putIfAbsent(chunkId, 1);
                        if (currentReplicationDegree != null){
                            this.peer.storage.chunksReplicationDegree.replace(chunkId, currentReplicationDegree + 1);
                        }

                        // UPDATES THE LIST OF CHUNKS' LOCATION
                        currentChunkStorageList.add(newHeader.senderId);
                    }

                    if(this.peer.storage.files.containsKey(newHeader.fileId)){
                        // INCREASES REPLICATION DEGREE OF BACKED UP CHUNK IN FILE MAP
                        BackupFile backedUpFile = this.peer.storage.files.get(newHeader.fileId);
                        backedUpFile.updateChunk(chunkId);
                    }

                }
                break;

            case "GETCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                System.out.println("getChunk");
                break;
            case "CHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                System.out.println("chunk");
                break;
            case "DELETE":
                // delete file
                this.peer.storage.files.remove(newHeader.fileId);

                // delete chunks and their references
                for (int i = 0; i < 10; i++){
                    String chunkId = newHeader.fileId + i;

                    if (this.peer.storage.backedUpChunks.containsKey(chunkId)){
                        this.peer.storage.backedUpChunks.remove(chunkId);
                    }

                    if (this.peer.storage.chunksReplicationDegree.containsKey(chunkId)){
                        this.peer.storage.chunksReplicationDegree.remove(chunkId);
                    }

                    if (this.peer.storage.chunksLocation.containsKey(chunkId)){
                        this.peer.storage.chunksLocation.remove(chunkId);
                    }
                }

                break;
                
            case "REMOVED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                System.out.println("remove");
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
