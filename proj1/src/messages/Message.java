package messages;

import files.Chunk;
import peer.Header;

import java.io.IOException;
import java.io.Serializable;

public abstract class Message implements Serializable {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    public Header header;
    public Chunk body;
    //public String type = "NONE";
    protected final String doubleCRLF = "\r\n\r\n";
    public String address;
    public int port;

    /*
    public Message(String header, String body, String address, int port){
        this.type = "PUTCHUNK";
        this.address = address;
        this.port = port;
    };
    */

    public Message(Header header, Chunk body, String address, int port){
        this.header = header;
        this.body = body;
        this.address = address;
        this.port = port;
    }

    public Message(Header header, String address, int port) {
        this.header = header;
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

    public abstract byte[] convertToBytes() throws IOException;

    public abstract void action();
    
}
