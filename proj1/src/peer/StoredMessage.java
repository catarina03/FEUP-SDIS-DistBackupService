package peer;

import files.Chunk;

import java.io.IOException;

public class StoredMessage extends Message{
    public StoredMessage(Header header, String address, int port) {
        super(header, address, port);
    }

    @Override
    public byte[] convertToBytes() throws IOException {
        return new byte[0];
    }

    @Override
    public void action() {

    }
}
