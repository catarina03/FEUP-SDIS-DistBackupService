package peer;

import files.BackupChunk;
import files.BackupFile;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;


public class MessageHandler {
    private Peer peer;
    protected final String doubleCRLF = "\r\n\r\n";

    public MessageHandler(Peer peer){
        this.peer = peer;
    }

    public void process(byte[] message, String address, int port) throws InvalidMessageException {
        System.out.println("IN PROCESS");

        String newMessage = new String(message, StandardCharsets.ISO_8859_1);
        ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(newMessage.split(this.doubleCRLF, 2)));

        String headerAsString = messageArray.get(0);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));

        if (headerArray.size() < 5){
            System.out.println("\nERROR IN ARRAY RECEIVED");
            System.out.println("SIZE: " + headerArray.size());
            System.out.println(headerArray);
            throw new InvalidMessageException("Invalid Header");
        }

        Header newHeader = new Header(headerArray);

        byte[] body = new byte[0];
        if (messageArray.size() != 1) {
            body = messageArray.get(1).getBytes(StandardCharsets.ISO_8859_1);
        }

        System.out.println("IN MESSAGE PARSER FROM PEER " + this.peer.id + " - HANDLING " + newHeader.messageType + " FROM " + newHeader.senderId);
  
        switch(newHeader.messageType) {
            case "PUTCHUNK":

                if (newHeader.senderId != this.peer.id){
                    System.out.println("INSIDE SWITCH FOR PUTCHUNK FROM " + newHeader.senderId);
                    BackupChunk newChunk = new BackupChunk(body, body.length);
                    String chunkId = newHeader.fileId + newHeader.chunkNo;

                    // STORES CHUNK
                    this.peer.storage.backedUpChunks.putIfAbsent(chunkId, newChunk);

                    // INCREASES REPLICATION DEGREE OF STORED CHUNK
                    Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.putIfAbsent(chunkId, 1);
                    if (currentReplicationDegree != null){
                        this.peer.storage.chunksReplicationDegree.replace(chunkId, currentReplicationDegree + 1);
                    }

                    // UPDATES THE LIST OF CHUNKS' LOCATION
                    ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation.computeIfAbsent(chunkId, value -> new ConcurrentSkipListSet<>());
                    currentChunkStorageList.add(this.peer.id);
                   
                    this.peer.fileManager.saveChunkToDirectory(newChunk, this.peer.id, newHeader.chunkNo);

                    System.out.println("\nUPDATING CHUNK REPLICATION DEGREE");
                    System.out.println(this.peer.storage.chunksReplicationDegree);

                    System.out.println("\nUPDATING CHUNK LOCATION");
                    System.out.println(this.peer.storage.chunksLocation);

                    //StoredTask newTask = new StoredTask(this.peer, new PutchunkMessage(newHeader, newChunk, address, port));
                    StoredTask newTask = new StoredTask(this.peer, newHeader, newChunk);
                    newTask.run();
                }
                break;

            case "STORED":
                System.out.println("INSIDE SWITCH FOR STORED FROM " + newHeader.senderId);

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

                        System.out.println("\nUPDATING FILE CHUNK REPLICATION DEGREE");
                        System.out.println(this.peer.storage.files);
                        System.out.println(this.peer.storage.files.get(newHeader.fileId).chunks);
                    }

                    System.out.println("\nUPDATING CHUNK REPLICATION DEGREE");
                    System.out.println(this.peer.storage.chunksReplicationDegree);

                    System.out.println("\nUPDATING CHUNK LOCATION");
                    System.out.println(this.peer.storage.chunksLocation);
                }
                break;

            case "GETCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.getChunk(message);

                System.out.println("getChunk");
                break;
            case "CHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.receiveChunk(message);
                System.out.println("chunk");
                break;
            case "DELETE":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.delete(message);
                System.out.println("delete");
                break;
            case "REMOVED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.removed(message);
                System.out.println("remove");
                break;
            case "DELETED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.deleted(message)
                System.out.println("DELETED");
                break;
            default:
                System.out.println("Message type " + newHeader.messageType + " not recognized.");
                break;
        }


        return;
    }

    public void handle(byte[] packet, String address, int port) {

        System.out.println("\nIN MESSAGE HANDLER, GOT THIS PACKET: " + packet);

        try {
            System.out.println("GOING TO PROCESS");
            this.process(packet, address, port);
            System.out.println("FINISHED PARSING MESSAGE\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
