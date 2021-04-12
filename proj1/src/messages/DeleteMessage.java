package messages;

import peer.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DeleteMessage extends Message {

    public DeleteMessage(Header header, String address, int port) {
        super(header, address, port);
    }

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