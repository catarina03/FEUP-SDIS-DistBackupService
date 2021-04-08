package files;

public class BackupChunk extends Chunk{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BackupChunk() {
    }

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
