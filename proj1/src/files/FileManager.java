package files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileManager {
    public int peerId;
    public int MAX_SIZE_CHUNK = 64000; //in bytes

    public FileManager(int peerId){
        this.peerId = peerId;
        //this.maximumStorage = 10000;
        //this.availableSpace = this.maximumStorage;


    }

    /*
    public ConcurrentHashMap<String, Chunk> readFileIntoChunks(String filepath) {
        Path path = Paths.get(filepath);
        Path absolutePath = path.toAbsolutePath();

        int chunk_no = 0;
        ConcurrentHashMap<String, Chunk> chunkMap = new ConcurrentHashMap<>();
        byte[] buffer = new byte[MAX_SIZE_CHUNK];

        Chunk chunk = new Chunk();
        String fileId = generateFileId(filepath);

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(String.valueOf(absolutePath)))) {
            int size;
            while((size = stream.read(buffer)) > 0){
                chunk = new Chunk(chunk_no, Arrays.copyOf(buffer, size), size);
                chunkMap.put(fileId + "_" + chunk_no, chunk);

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];
            }

            //check if needs 0 size chunk
            if(chunk.size == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                chunk = new Chunk(chunk_no, new byte[0], 0);
                chunkMap.put(fileId + "_" + chunk_no, chunk);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chunkMap;
    }

     */

    // private String generateFileId(String filePath) {
    //     String hashMetadata = "";

    //     try {
    //         BasicFileAttributes fileMetadata = Files.readAttributes(Path.of(filePath), BasicFileAttributes.class);
    //         String fileOwner = Files.getOwner(Path.of(filePath)).toString();
    //         hashMetadata = filePath + fileMetadata.creationTime() + fileMetadata.lastModifiedTime() + fileOwner;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return hashMetadata;
    // }

/*
    public void createDirectory(String directory_name){
        if(!directory.exists){
            
        }
    }
    */
}
