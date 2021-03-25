package peer;

public class Protocol {

    public String version = "1.0";


    public Protocol(String version){
        this.version=version;
    }

    //TODO: argumentos de todos os métodos tem de passar a ser das classes concretas de message
    public void putChunk(Message m){
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

    public void stored(Message m) {
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

    public void getChunk(Message m) {
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

    public void delete(Message m) {
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

    public void removed(Message m) {
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

    public void deleted(Message m) {
        System.out.println("Message type " + m.type + " received in Protocol.");
    }

}
