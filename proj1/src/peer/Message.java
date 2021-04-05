package peer;

public abstract class Message {
    
    public Header header;
    public String body;
    //public String type = "NONE";
    public String address;
    public int port;

    /*
    public Message(String header, String body, String address, int port){
        this.type = "PUTCHUNK";
        this.address = address;
        this.port = port;
    };
    */

    public Message(Header header, String body, String address, int port){
        this.header = header;
        this.body = body;
        this.address = address;
        this.port = port;
    }

    public Message(){
        
    }

    /*
    abstract public String getHeader();
    abstract public void action(String message);
    */


    public Header getHeader(){
        return this.header;
    };

    public abstract void action();
    
}
