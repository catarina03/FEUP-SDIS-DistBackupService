package peer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Header {
    public String version;  //3 bytes
    public String messageType;   //7 bytes max
    public int senderId;
    public String fileId;
    public int chunkNo; 
    public int replicationDegree;

    public Header(String version, String messageType, int senderId, String fileId, int chunkNo, int replicationDegree){
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
    }

    public Header(String messageType, int senderId, String fileId, int chunkNo, int replicationDegree){
        this.version = "1.0";
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
    }

    // STORED HEADER
    public Header(String version, String messageType, int senderId, String fileId, int chunkNo) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString(){
        return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDegree;
    }

    public int getSizeInBytes(){
        String header = this.toString();
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        return headerBytes.length;
    }
}
