package files;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class File implements Serializable {
    //public Path filePath;
    public String pathname;
    public String fileId;
    public int desiredReplicationDegree;
    ConcurrentHashMap<String, Integer> chunckData;

    public File(String pathname, String fileId, int desiredReplicationDegree) {
        this.pathname = pathname;
        this.fileId = fileId;
        this.desiredReplicationDegree = desiredReplicationDegree;
    }

    //TO DO
    public void addChunk(){
        String key = "";
        Integer value = 0;
        chunckData.put(key, value);
    }





/*
    private void generateFileId() {
        try {
            BasicFileAttributes fileMetadata = Files.readAttributes(filePath, BasicFileAttributes.class);
            String hashMetadata = filename + fileMetadata.creationTime() + fileMetadata.lastModifiedTime() + fileMetadata.getOwner();
        } catch (Exception e) {
            //TODO: handle exception
        }

    }
    */
}
