package messages;

import files.Chunk;
import peer.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PutchunkMessage extends Message{

    /**
     * Put Chunk Message Constructor
     * @param header  Message Header
     * @param body    Message Body
     * @param address Address where message should be sent
     * @param port    Port where message should be sent
     */
	public PutchunkMessage(Header header, Chunk body, String address, int port){
        super(header, body, address, port);
    }

    /**
     * Converts message to bytes
     */
    public byte[] convertToBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(header.toString().getBytes());
        stream.write(doubleCRLF.getBytes());
        stream.write(body.body);
        return stream.toByteArray();
    }
}