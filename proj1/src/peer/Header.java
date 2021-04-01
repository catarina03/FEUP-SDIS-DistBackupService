package peer;

public class Header {
    public String version;
    public String messageType;
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

    public void setVersion(String version) {
        this.version = version;
    }
}
