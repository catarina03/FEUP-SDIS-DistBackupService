package messages;

import files.Chunk;
import peer.Header;
import java.io.IOException;

public abstract class Message{
    
    public Header header;
    public Chunk body;
    protected final String doubleCRLF = "\r\n\r\n";
    public String address;
    public int port;

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

    public Message(){}

    public Header getHeader(){
        return this.header;
    };

    public abstract byte[] convertToBytes() throws IOException;
}