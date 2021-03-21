package files;

public class BackupChunk extends Chunk{
    public String version;
    public int senderId;
    public int fileId;
    public int chunkNo;
    public int ReplicationDegree;

    public byte[] body;

    //<Version> PUTCHUNK <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body> 

    
}
