package messages;


import files.Chunk;
import peer.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PutchunkMessage extends Message{

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PutchunkMessage(Header header, Chunk body, String address, int port){
        super(header, body, address, port);
    }

    public byte[] convertToBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(header.toString().getBytes());
        stream.write(doubleCRLF.getBytes());
        stream.write(body.body);
        return stream.toByteArray();
    }


}

