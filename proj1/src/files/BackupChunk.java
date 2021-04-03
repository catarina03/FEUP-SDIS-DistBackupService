package files;

public class BackupChunk extends Chunk{
    //public String version;
    //public int senderId;
    //public int fileId;
    //public int chunkNo;
    //public int ReplicationDegree;


    public BackupChunk() {
    }

    public BackupChunk(byte[] body, int size){
        super(body, size);
    }
    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>

    
}
