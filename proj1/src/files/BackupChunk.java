package files;

public class BackupChunk extends Chunk{

    /**
     * For serializable implementation
     */
    private static final long serialVersionUID = 1L;

    /**
     * No arguments constructor
     */
    public BackupChunk() {}

    // <Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg>
    // <CRLF><CRLF><Body>
    /**
     * BackupChunk constructor
     * @param id Chunk id
     * @param fileId File id of which the chunk belongs to
     * @param chunkNo Chunk number
     * @param size Chunk Size
     * @param desiredReplicationDegree Desired Replication Degree wished to achieve for this chunk
     * @param body Chunk body containing file data
     */
    public BackupChunk(String id, String fileId, int chunkNo, int size, int desiredReplicationDegree, byte[] body) {
        super(id, fileId, chunkNo, size, desiredReplicationDegree, body);
    }

    /**
     * Getter for Chunk ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Override of method toString to show Chunk contents in a user friendly way
     */
    @Override
    public String toString() {
        return "ID: " + this.id + " SIZE: " + this.size + " REPDEGREE: " + this.desiredReplicationDegree + " BODY: "
                + this.body;
    }
}
