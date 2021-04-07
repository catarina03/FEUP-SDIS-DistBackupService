package files;

import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BackupChunk extends Chunk implements Serializable {
    // public String version;
    // public int senderId;
    // public int fileId;
    // public int chunkNo;
    // public int ReplicationDegree;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BackupChunk() {
    }

    /*
     * public BackupChunk(byte[] body, int size) { super(body, size); }
     */
    // <Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg>
    // <CRLF><CRLF><Body>

    public BackupChunk(String id, int size, int desiredReplicationDegree, byte[] body) {
        super(id, size, desiredReplicationDegree, body);
    }

    public String getId() {
        return this.id;
    }


    @Override
    public String toString() {
        return "ID: " + this.id + " SIZE: " + this.size + " REPDEGREE: " + this.desiredReplicationDegree + " BODY: "
                + this.body;
    }



}
