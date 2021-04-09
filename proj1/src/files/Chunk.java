package files;

import java.io.Serializable;

public abstract class Chunk implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    public String id;
    public String fileId;
    public int chunkNo;
    protected int size;
    protected int desiredReplicationDegree;
    public byte[] body;
    
    public Chunk() {
        this.body = new byte[0];
        this.size = 0;
    }
    
    public Chunk(String id, String fileId, int chunkNo, int size, int desiredReplicationDegree, byte[] body) {
        this.id = id;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.body = body;
    }
    
    public int getSize() {
        return size;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    public byte[] getBody() {
        return body;
    }
}