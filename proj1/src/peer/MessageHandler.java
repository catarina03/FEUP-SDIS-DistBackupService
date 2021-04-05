package peer;

import files.BackupChunk;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class MessageHandler {

    private Peer peer;
    //private String id;
    private String version;
    protected final String doubleCRLF = "\r\n\r\n";

    public MessageHandler(Peer peer, String version){
        this.peer = peer;
        this.version = version;
    }

    public void process(byte[] message, String address, int port) throws InvalidMessageException {

        System.out.println("IN MESSAGE HANDLER PROCESS");

        String newMessage = new String(message, StandardCharsets.ISO_8859_1);
        ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(newMessage.split(this.doubleCRLF, 2)));

        String headerAsString = messageArray.get(0);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));
        if (headerArray.size() < 5){
            throw new InvalidMessageException("Invalid Header");
        }
        //Header header = new Header(messageArray.get(0));
        Header newHeader = null;
        if (headerArray.size() == 6){
            newHeader = new Header(headerArray.get(0).trim(),
                    headerArray.get(1).trim(),
                    Integer.parseInt(headerArray.get(2).trim()),
                    headerArray.get(3).trim(),
                    Integer.parseInt(headerArray.get(4).trim()),
                    Integer.parseInt(headerArray.get(5).trim()));
        }
        if (headerArray.size() == 5){
            newHeader = new Header(headerArray.get(0).trim(),
                    headerArray.get(1).trim(),
                    Integer.parseInt(headerArray.get(2).trim()),
                    headerArray.get(3).trim(),
                    Integer.parseInt(headerArray.get(4).trim()));
        }

        System.out.println("IN PEER MESSAGE HANDLER PROCESS FUNCTION, JUST RECEIVED A HEADER WITH SIZE " + headerArray.size());
        System.out.println(headerArray);



        byte[] body = new byte[0];
        if (messageArray.size() != 1) {
            body = messageArray.get(1).getBytes(StandardCharsets.ISO_8859_1);
        }

        System.out.println("IN MESSAGE HANDLER PARSER FROM PEER " + this.peer.id + " - HANDLING " + newHeader.messageType + " FROM " + newHeader.senderId);


        // \r\n
        /*
        String[] arrayOfMessage = message.split("\r\n", 2);

        String[] arrayOfHeader = arrayOfMessage[0].split(" ", 6);
        Header messageHeader = new Header(arrayOfHeader[0],arrayOfHeader[1], 
                                            parseInt(arrayOfHeader[2]), arrayOfHeader[3],
                                            parseInt(arrayOfHeader[4]), parseInt(arrayOfHeader[5]));
        */
        //Message chunkMessage = new ErrorMessage();




        //chunkMessage.address = address;
        //chunkMessage.port = port;

        switch(newHeader.messageType) {
            case "PUTCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                // inicializar protocol PUTCHUNK(id, version)
                // fazer action da mensagem

                if (newHeader.senderId != this.peer.id){
                    System.out.println("INSIDE SWITCH CASE FOR PUTCHUNK FROM " + newHeader.senderId);
                    BackupChunk newChunk = new BackupChunk(body, body.length);
                    //return new PutchunkTask(this.peer, newHeader, newChunk);
                    StoredTask newTask = new StoredTask(this.peer, new PutchunkMessage(newHeader, newChunk, address, port));
                    newTask.run();
                }


                //chunkMessage = new PutchunkMessage(messageHeader, arrayOfMessage[1], address, port);
                //return chunkMessage;

            case "STORED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA
                //this.peer.protocol.stored(message);
                System.out.println("INSIDE SWITCH CASE FOR STORED FROM " + newHeader.senderId);


                System.out.println("Stored");
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





    }

    public void handle(DatagramPacket packet, String address, int port) {

        System.out.println("IN MESSAGE HANDLER");

        byte[] message = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        //System.out.println(message);

        //Message receivedMessage = null; //?
        try {
            System.out.println("IN MESSAGE HANDLER, GOING TO PROCESS");
            this.process(message, address, port);
            //System.out.println("FINISHED PARSING MESSAGE");
        } catch (InvalidMessageException e) {
            e.printStackTrace();
        }



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
