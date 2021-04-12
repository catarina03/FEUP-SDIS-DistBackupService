package files;

import java.io.Serializable;

public abstract class Chunk implements Serializable {
    /**
     * For Serializable implementation
     */
    private static final long serialVersionUID = 1L;
    
    public String id;
    public String fileId;
    public int chunkNo;
    protected int size;
    protected int desiredReplicationDegree;
    public byte[] body;
    
    /**
     * Chunk constructor - no arguments
     */
    public Chunk() {
        this.body = new byte[0];
        this.size = 0;
    }
    
    /**
     * 
     * @param id                       Chunk id
     * @param fileId                   File id of which the chunk belongs to
     * @param chunkNo                  Chunk number
     * @param size                     Chunk size
     * @param desiredReplicationDegree Desired Replication Degree wished to achieve
     *                                 for this chunk
     * @param body                     Chunk body containing file data
     */
    public Chunk(String id, String fileId, int chunkNo, int size, int desiredReplicationDegree, byte[] body) {
        this.id = id;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.body = body;
    }
    
    /**
     * Getter for chunk's size
     * @return chunk's size
     */
    public int getSize() {
        return size;
    }

    /**
     * Getter for chunk's desired replication degree
     * @return chunk's desired replication degree
     */
    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    /**
     * Getter for chunk's body
     * @return chunk's body
     */
    public byte[] getBody() {
        return body;
    }
}