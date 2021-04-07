package peer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

    /*
    public Header(String messageType, int senderId, String fileId, int chunkNo, int replicationDegree){
        this.version = "1.0";
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
    }

     */

    // STORED HEADER
    public Header(String version, String messageType, int senderId, String fileId, int chunkNo) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public Header(ArrayList<String> array){
        // Header PUTCHUNK
        if (array.size() == 6){
            this.version = array.get(0).trim();
            this.messageType = array.get(1).trim();
            this.senderId = Integer.parseInt(array.get(2).trim());
            this.fileId = array.get(3).trim();
            this.chunkNo = Integer.parseInt(array.get(4).trim());
            this.replicationDegree = Integer.parseInt(array.get(5).trim());
        }

        //Header STORED
        if (array.size() == 5){
            this.version = array.get(0).trim();
            this.messageType = array.get(1).trim();
            this.senderId = Integer.parseInt(array.get(2).trim());
            this.fileId = array.get(3).trim();
            this.chunkNo = Integer.parseInt(array.get(4).trim());
        }
    }



    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString(){
        if (messageType.equals("PUTCHUNK")){
            return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDegree;
        }
        //if (messageType == "STORED"){
            return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo;
        //}
    }

    public int getSizeInBytes(){
        String header = this.toString();
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        return headerBytes.length;
    }
}
