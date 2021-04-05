package peer;


import java.io.IOException;

public class StoredMessage extends Message{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
