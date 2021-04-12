package files;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;


public class BackupFile implements Serializable {
    /**
     * For serializable implementation
     */
    private static final long serialVersionUID = 1L;

    public String pathname;
    public String fileId;
    public int desiredReplicationDegree;
    public ConcurrentHashMap<String, Integer> chunks;

    /**
     * BackupFile Constructor
     * @param pathname file's path
     * @param desiredReplicationDegree desired replication degree for all chunks of this backup file
     */
    public BackupFile(String pathname, int desiredReplicationDegree) {
        this.pathname = pathname;
        this.fileId = generateFileId(pathname);
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.chunks = new ConcurrentHashMap<>();
    }

    /**
     * Updates chunks list, adds chunk to chunks list if it doesnt exist or add to its replication degree if it already exists
     */
    public void updateChunk(String chunkId){
        chunks.computeIfPresent(chunkId, (k, v) -> v + 1);
        chunks.putIfAbsent(chunkId, 1);
    }

    /**
     * Generates file ID, based on its path, owner, creation time and last modified time
     * @param filePath file's path
     * @return string with fileId
     */
    private String generateFileId(String filePath) {
        String fileId = "";
        try {
            BasicFileAttributes fileMetadata = Files.readAttributes(Path.of(filePath), BasicFileAttributes.class);
            String fileOwner = Files.getOwner(Path.of(filePath)).toString();
            String hashMetadata = filePath + fileMetadata.creationTime() + fileMetadata.lastModifiedTime() + fileOwner;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(hashMetadata.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexStr = new StringBuilder();
            for (byte singleByte : encodedhash) {
                String m = Integer.toHexString(0xff & singleByte);
                if (m.length() == 1)
                    hexStr.append('0');
                hexStr.append(m);
            }
            fileId = hexStr.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }
}