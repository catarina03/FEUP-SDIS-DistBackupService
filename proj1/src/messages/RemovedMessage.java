package messages;

import peer.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RemovedMessage extends Message{

    /**
     * Removed Message Constructor
     * @param header  Message Header
     * @param address Address where message should be sent
     * @param port    Port where message should be sent
     */
    public RemovedMessage(Header header, String address, int port) {
        super(header, address, port);
    }

    /**
     * Converts message to bytes
     */
    @Override
    public byte[] convertToBytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            stream.write(header.toString().getBytes());
            stream.write(doubleCRLF.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}