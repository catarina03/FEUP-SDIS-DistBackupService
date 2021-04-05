package peer;

import files.BackupChunk;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class MessageHandler {

    private Peer peer;
    protected final String doubleCRLF = "\r\n\r\n";

    public MessageHandler(Peer peer){
        this.peer = peer;
    }




    //public void process(byte[] message, String address, int port) throws InvalidMessageException {
    public void process(DatagramPacket packet, String address, int port) throws InvalidMessageException {

        //System.out.println(message.toString());

        System.out.println("IN PROCESS");

        byte[] message = packet.getData(); //THIS SHIT HERE

        System.out.println("AFTER PROCESS");

        String newMessage = new String(message, StandardCharsets.ISO_8859_1);
        ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(newMessage.split(this.doubleCRLF, 2)));

        String headerAsString = messageArray.get(0);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));
        if (headerArray.size() < 5){
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
                    
                    StoredTask newTask = new StoredTask(this.peer, new PutchunkMessage(newHeader, newChunk, address, port));
                    newTask.run();
                }

                break;

            case "STORED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.stored(message);
                System.out.println("INSIDE SWITCH FOR STORED FROM " + newHeader.senderId);
                String chunkId = newHeader.fileId + newHeader.chunkNo;

                //System.out.println("Storage before update: " + this.peer.storage.backedUpChunks.toString());

                //System.out.println("Message received: " + newHeader);

                System.out.println("ID OF HEADER I JUST CREATED: " + newHeader.senderId);
                System.out.println("ID OF CURRENT PEER: " + this.peer.id);

                if (newHeader.senderId != this.peer.id){
                    if (this.peer.storage.backedUpChunks.contains(chunkId)){
                        //BackupChunk savedChunk = this.peer.storage.backedUpChunks.get(chunkId);
                        //savedChunk.setCurrentReplicationDegree(savedChunk.getCurrentReplicationDegree() + 1);
                        //this.peer.storage.backedUpChunks.replace(chunkId, savedChunk);
                        System.out.println("THIS DOOESNT WORK YET BC HASHMAPS");
                    }
                    System.out.println("UPDATING CHUNK REPLICATION DEGREE");
                }

                
                //System.out.println("Storage after update: " + this.peer.storage.backedUpChunks.toString());
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

    public void handle(DatagramPacket packet, String address, int port) {

        System.out.println("\nIN MESSAGE HANDLER");

        //byte[] message = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        //System.out.println(message);
        //System.out.println("fiz o array uwu " + Arrays.toString(packet.getData()));

        //Message receivedMessage = null; //?
        try {
            System.out.println("GOING TO PROCESS");
            //this.process(packet.getData(), address, port);
            this.process(packet, address, port);
            System.out.println("FINISHED PARSING MESSAGE\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return;

        // if the message is from the own Peer
/*
        if (receivedMessage != null){
            if(receivedMessage.header.senderId != this.peer.id) {
                System.out.println("MESSAGE: " + receivedMessage.header.toString() + " PARSED, BEING HANDLED NOW");

                Task newTask = new Task(message)
                //receivedMessage.action();
            }
            else{
                System.out.println("THIS MESSAGE IS MINE\n");
            }
        }

 */




    }
}
