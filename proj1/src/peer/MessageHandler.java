package peer;

import files.BackupChunk;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class MessageHandler {
    
    private String id;
    private String version;
    protected final String doubleCRLF = "\r\n\r\n";

    public MessageHandler(String id, String version){
        this.id = id;
        this.version = version;
    }

    public Message parse(byte[] message, String address, int port) throws InvalidMessageException {

        String newMessage = new String(message, StandardCharsets.ISO_8859_1);
        ArrayList<String> messageArray = new ArrayList<>(Arrays.asList(newMessage.split(this.doubleCRLF, 2)));

        String headerAsString = messageArray.get(0);
        ArrayList<String> headerArray = new ArrayList<>(Arrays.asList(headerAsString.split(" ", 6)));
        if (headerArray.size() < 6){
            throw new InvalidMessageException("Invalid Header");
        }
        //Header header = new Header(messageArray.get(0));
        Header newHeader = new Header(headerArray.get(0).trim(),
                headerArray.get(1).trim(),
                Integer.parseInt(headerArray.get(2).trim()),
                headerArray.get(3).trim(),
                Integer.parseInt(headerArray.get(4).trim()),
                Integer.parseInt(headerArray.get(5).trim()));

        byte[] body = new byte[0];
        if (messageArray.size() != 1) {
            body = messageArray.get(1).getBytes(StandardCharsets.ISO_8859_1);
        }

        System.out.println("IN MESSAGE HANDLER PARSER - HANDLING " + newHeader.messageType + " FROM " + newHeader.senderId);


        // \r\n
        /*
        String[] arrayOfMessage = message.split("\r\n", 2);

        String[] arrayOfHeader = arrayOfMessage[0].split(" ", 6);
        Header messageHeader = new Header(arrayOfHeader[0],arrayOfHeader[1], 
                                            parseInt(arrayOfHeader[2]), arrayOfHeader[3],
                                            parseInt(arrayOfHeader[4]), parseInt(arrayOfHeader[5]));
        */
        Message chunkMessage = new ErrorMessage();




        //chunkMessage.address = address;
        //chunkMessage.port = port;

        switch(newHeader.messageType) {
            case "PUTCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA 
                // inicializar protocol PUTCHUNK(id, version)
                // fazer action da mensagem
                System.out.println("INSIDE SWITCH CASE FOR PUTCHUNK");
                BackupChunk newChunk = new BackupChunk(body, body.length);
                return new PutchunkMessage(newHeader, newChunk, address, port);

                //chunkMessage = new PutchunkMessage(messageHeader, arrayOfMessage[1], address, port);
                //return chunkMessage;

            case "STORED":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA 
                //this.peer.protocol.stored(message);

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

        return chunkMessage;

    }

    public void handle(DatagramPacket packet, String address, int port) {

        System.out.println("IN MESSAGE HANDLER");

        byte[] message = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        System.out.println(message);

        Message m = null; //?
        try {
            m = this.parse(message, address, port);
            System.out.println("FINISHED PARSING MESSAGE");
        } catch (InvalidMessageException e) {
            e.printStackTrace();
        }

        // if the message is from the own Peer

        if (m != null){
            if(m.header.senderId != parseInt(this.id)) {
                System.out.println("MESSAGE: " + m.header.toString() + " PARSED, BEING HANDLED NOW");

                m.action();
            }
            else{
                System.out.println("THIS MESSAGE IS MINE\n");
            }
        }




    }
}
