package messages;

import peer.Header;
import files.BackupChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChunkMessage extends Message {

    /**
     * Chunk Message Constructor
     * @param header Message header
     * @param chunk Chunk to be sent
     * @param address Address where message should be sent
     * @param port Port where message should be sent
     */
    public ChunkMessage(Header header, BackupChunk chunk, String address, int port) {
        super(header, chunk, address, port);
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
            stream.write(this.body.body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}