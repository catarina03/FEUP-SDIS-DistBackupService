package messages;

import files.BackupFile;
import peer.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HelloMessage extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HelloMessage(Header header, String address, int port) {
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
