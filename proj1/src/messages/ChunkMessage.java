package messages;

import peer.Header;
import files.BackupChunk;
import files.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChunkMessage extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ChunkMessage(Header header, BackupChunk chunk, String address, int port) {
        super(header, chunk, address, port);
    }

    @Override
    public byte[] convertToBytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            stream.write(header.toString().getBytes());
            stream.write(doubleCRLF.getBytes());
            stream.write(this.body.body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}