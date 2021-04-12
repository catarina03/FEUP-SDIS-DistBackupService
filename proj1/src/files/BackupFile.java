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
     *
     */
    private static final long serialVersionUID = 1L;

    public String pathname;
    public String fileId;
    public int desiredReplicationDegree;
    public ConcurrentHashMap<String, Integer> chunks;

    public BackupFile(String pathname, int desiredReplicationDegree) {
        this.pathname = pathname;
        this.fileId = generateFileId(pathname);
        this.desiredReplicationDegree = desiredReplicationDegree;
        this.chunks = new ConcurrentHashMap<>();
    }

    public void updateChunk(String chunkId){
        chunks.computeIfPresent(chunkId, (k, v) -> v + 1);
        chunks.putIfAbsent(chunkId, 1);
        //Integer value = chunks.putIfAbsent(chunkId, 1);
        //System.out.println(chunkId);
        //System.out.println(value);

        /*
        if (value != null){
            chunks.replace(chunkId, value + 1);
        }

         */

        System.out.println(this.chunks);
    }

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
