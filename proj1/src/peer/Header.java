package peer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Header {
    public String version; // 3 bytes
    public String messageType; // 7 bytes max
    public int senderId;
    public String fileId;
    public int chunkNo;
    public int replicationDegree;

    /**
     * Constructor of the PUTCHUNK Header
     * 
     * @param version           Message version
     * @param messageType       Message type
     * @param senderId          ID of the peer sending
     * @param fileId            ID of the file the chunk belongs to
     * @param chunkNo           Number of the chunk of the file
     * @param replicationDegree Replication degree of the chunk
     */
    public Header(String version, String messageType, int senderId, String fileId, int chunkNo, int replicationDegree) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.replicationDegree = replicationDegree;
    }

    /**
     * Constructor of the STORED, GETCHUNK, CHUNK AND REMOVED Headers
     * 
     * @param version     Message version
     * @param messageType Message type
     * @param senderId    ID of the peer sending
     * @param fileId      ID of the file the chunk belongs to
     * @param chunkNo     Number of the chunk of the file
     */
    public Header(String version, String messageType, int senderId, String fileId, int chunkNo) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    /**
     * Constructor of the DELETED Header
     * 
     * @param version     Message version
     * @param messageType Message type
     * @param senderId    ID of the peer sending
     * @param fileId      ID of the file to be deleted
     */
    public Header(String version, String messageType, int senderId, String fileId) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
        this.fileId = fileId;
    }

    /**
     * Constructor of the HELLO Header
     * 
     * @param version     Message version
     * @param messageType Message type
     * @param senderId    ID of the peer sending
     */
    public Header(String version, String messageType, int senderId) {
        this.version = version;
        this.messageType = messageType;
        this.senderId = senderId;
    }

    /**
     * Constructor of he Header
     * 
     * @param array Header in the form of ArrayList
     */
    public Header(ArrayList<String> array) {
        // Header PUTCHUNK
        if (array.size() == 6) {
            this.version = array.get(0).trim();
            this.messageType = array.get(1).trim();
            this.senderId = Integer.parseInt(array.get(2).trim());
            this.fileId = array.get(3).trim();
            this.chunkNo = Integer.parseInt(array.get(4).trim());
            this.replicationDegree = Integer.parseInt(array.get(5).trim());
        }

        // Header STORED, GETCHUNK, CHUNK AND REMOVED HEADER
        if (array.size() == 5) {
            this.version = array.get(0).trim();
            this.messageType = array.get(1).trim();
            this.senderId = Integer.parseInt(array.get(2).trim());
            this.fileId = array.get(3).trim();
            this.chunkNo = Integer.parseInt(array.get(4).trim());
        }

        // Header DELETE
        if (array.size() == 4) {
            this.version = array.get(0).trim();
            this.messageType = array.get(1).trim();
            this.senderId = Integer.parseInt(array.get(2).trim());
            this.fileId = array.get(3).trim();
        }
    }

    /**
     * Setter for the Message version
     * 
     * @param version Message version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Converts to String
     */
    @Override
    public String toString() {
        if (messageType.equals("PUTCHUNK")) {
            return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo + " "
                    + replicationDegree;
        }
        if (messageType.equals("DELETE")) {
            return version + " " + messageType + " " + senderId + " " + fileId;
        }
        return version + " " + messageType + " " + senderId + " " + fileId + " " + chunkNo; // STORED, GETCHUNK, CHUNK
                                                                                            // AND REMOVED
    }

    /**
     * Get size of header in bytes
     * 
     * @return size of header in bytes
     */
    public int getSizeInBytes() {
        String header = this.toString();
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        return headerBytes.length;
    }
}
