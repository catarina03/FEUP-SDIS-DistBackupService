package peer;

import static java.lang.Integer.parseInt;

public class MessageHandler {
    
    private String id;
    private String version;

    public MessageHandler(String id, String version){
        this.id = id;
        this.version = version;
    }

    public Message parse(String message, String address, int port){
        // \r\n
        String[] arrayOfMessage = message.split("\r\n", 2);

        String[] arrayOfHeader = arrayOfMessage[0].split(" ", 6);
        Header messageHeader = new Header(arrayOfHeader[0],arrayOfHeader[1], 
                                            parseInt(arrayOfHeader[2]), arrayOfHeader[3],
                                            parseInt(arrayOfHeader[4]), parseInt(arrayOfHeader[5]));
        
        Message chunkMessage = new ErrorMessage();


        //chunkMessage.address = address;
        //chunkMessage.port = port;

        switch(messageHeader.messageType) {
            case "PUTCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA 
                // inicializar protocol PUTCHUNK(id, version)
                // fazer action da mensagem
                System.out.println("PUTCHUNK");
                //chunkMessage = new PutchunkMessage(messageHeader, arrayOfMessage[1], address, port);
                //return chunkMessage;
                break;
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
                System.out.println("Message type " + messageHeader.messageType + " not recognized.");
                break;
        }

        return chunkMessage;

    }

    public void handle(String message, String address, int port) {

        Message m = this.parse(message, address, port); //?

        // if the message is from the own Peer
        if(m.header.senderId == parseInt(this.id)) {
            return;
        }

        m.action();

    }
}
