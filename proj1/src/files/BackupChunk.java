package files;


import java.io.ObjectStreamException;
import java.io.IOException;


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

    /*
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{

    }

    private void readObjectNoData() throws ObjectStreamException{
        
    }
    */

}
