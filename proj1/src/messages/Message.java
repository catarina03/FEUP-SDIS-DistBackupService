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

    /**
     * Message Constructor
     * @param header  Message Header
     * @param body    Message Body
     * @param address Address where message should be sent
     * @param port    Port where message should be sent
     */
    public Message(Header header, Chunk body, String address, int port){
        this.header = header;
        this.body = body;
        this.address = address;
        this.port = port;
    }

    /**
     * Message Constructor
     * @param header  Message Header
     * @param address Address where message should be sent
     * @param port    Port where message should be sent
     */
    public Message(Header header, String address, int port) {
        this.header = header;
        this.address = address;
        this.port = port;
    }

    /**
     * Message Constructor - no arguments
     */
    public Message(){}

    /**
     * Getter for header
     * @return header from message
     */
    public Header getHeader(){
        return this.header;
    };

    /**
     * Converts message to bytes
     */
    public abstract byte[] convertToBytes() throws IOException;
}