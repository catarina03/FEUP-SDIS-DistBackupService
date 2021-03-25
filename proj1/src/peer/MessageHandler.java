package peer;

public class MessageHandler {
    
    private String id;
    private String version;

    public MessageHandler(String id, String version){
        this.id = id;
        this.version = version;
    }

    public void handle(String message, String address, int port) {

        Message m = new Message(message, address, port);

        // if(m.type.equals("NONE")){
        //     System.out.println("Message type " + m.type + " not recognized.");
        //     return;
        // }

        // if the message is from the own Peer
        // if(m.getHeader().getSenderId() == this.id) {
        //     return;
        // }

        m.address = address;
        m.port = port;

        switch (m.type) {
            case "PUTCHUNK":
                // TODO: PASSAR MENSAGEM PARA CLASSE CONCRETA 
                // inicializar protocol PUTCHUNK(id, version)
                // fazer action do protocol
                //this.peer.protocol.putChunk(message);
                System.out.println("PUTCHUNK");
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
                break;
        }
    }
}
