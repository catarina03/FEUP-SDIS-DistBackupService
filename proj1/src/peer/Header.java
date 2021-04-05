package peer;

public class Header {
    public String version;
    public String messageType;
    public String senderId;
    public int fileId;
    public int chunkNo; 
    public int replicationDegree;

    public Header(String version, String messageType, String senderId, int fileId, int chunkNo, int replicationDegree){
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
    }
}
