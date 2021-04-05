package files;

public class BackupChunk extends Chunk{
    //public String version;
    //public int senderId;
    //public int fileId;
    //public int chunkNo;
    //public int ReplicationDegree;


    /**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public BackupChunk() {
    }

    public BackupChunk(byte[] body, int size){
        super(body, size);
    }
    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>


    public BackupChunk(String id, int size, int desiredReplicationDegree, int currentReplicationDegree, byte[] body) {
        super(id, size, desiredReplicationDegree, currentReplicationDegree, body);
    }

}
