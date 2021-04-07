package files;

import java.io.Serializable;

public abstract class Chunk implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // public int chunk_number;
    // public Header header;
    public String id;
    protected int size;
    protected int desiredReplicationDegree;
    // protected int currentReplicationDegree;
    public byte[] body;

    // public int chunkId;

    
    public Chunk() {
        // this.chunk_number = 0;
        // this.header = new Header()
        this.body = new byte[0];
        this.size = 0;
    }
    


    /*
     * public Chunk(byte[] body, int size){ //this.chunk_number = chunk_number;
     * //this.header = header; this.body = body; this.size = size; //this.chunkId =
     * this.header.fileId + "_" + this.chunk_number; }
     */

    public Chunk(String id, int size, int desiredReplicationDegree, byte[] body) {
        this.id = id;
        this.size = size;
        this.desiredReplicationDegree = desiredReplicationDegree;
        // this.currentReplicationDegree = currentReplicationDegree;
        this.body = body;
    }

    // public String getId() {
    //     return id;
    // }

    public int getSize() {
        return size;
    }

    public int getDesiredReplicationDegree() {
        return desiredReplicationDegree;
    }

    /*
    public int getCurrentReplicationDegree() {
        return currentReplicationDegree;
    }
    */

    public byte[] getBody() {
        return body;
    }

    /*
     * public void setCurrentReplicationDegree(int newReplicationDegree) {
     * this.currentReplicationDegree = newReplicationDegree; }
     */

}
