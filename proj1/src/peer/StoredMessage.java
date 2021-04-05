package peer;


import java.io.ByteArrayOutputStream;
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(header.toString().getBytes());
        stream.write(doubleCRLF.getBytes());
        return stream.toByteArray();
    }

    @Override
    public void action() {

    }
}
