package files;

import peer.Header;

import java.io.Serializable;

public abstract class Chunk implements Serializable{
//public class Chunk implements Serializable{
    //public int chunk_number;
    public Header header;
    public byte[] body;
    public int size;
    //public int chunkId;

    public Chunk() {
        //this.chunk_number = 0;
        //this.header = new Header()
        this.body = new byte[0];
        this.size = 0;
    }

    public Chunk(byte[] body, int size){
        //this.chunk_number = chunk_number;
        //this.header = header;
        this.body = body;
        this.size = size;
        //this.chunkId = this.header.fileId + "_" + this.chunk_number;
    }

    public String getChunkId(){
        return this.header.fileId + "_" + this.header.chunkNo;


    }




    
}
