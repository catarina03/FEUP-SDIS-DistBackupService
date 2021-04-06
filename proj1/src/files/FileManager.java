package files;

import peer.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.IOException;

public class FileManager {
    public Peer peer;
    public int MAX_SIZE_CHUNK = 64000; //in bytes

    public FileManager(Peer peer){
        this.peer = peer;
        //this.maximumStorage = 10000;
        //this.availableSpace = this.maximumStorage;



    }

    
    public void readFileIntoChunks(Path absolutePath, BackupFile file) {

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(String.valueOf(absolutePath)))) {

            int chunk_no = 0;
            BackupChunk chunk = new BackupChunk();
            // ConcurrentHashMap<String, BackupChunk> chunkMap = new ConcurrentHashMap<String, BackupChunk>();
            byte[] buffer = new byte[MAX_SIZE_CHUNK]; // TEM QUE SER MENOS
            
            int size;
            while ((size = stream.read(buffer)) > 0) {
                chunk = new BackupChunk(file.fileId + chunk_no, size, file.desiredReplicationDegree, 0, Arrays.copyOf(buffer, size));
                Header header = new Header("1.0", "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);
                
                // chunkMap.put(file.fileId + "_" + chunk_no, chunk);
                
                peer.sendPutChunk(chunk, header);

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];

            }

            //check if needs 0 size chunk
            if(chunk.getSize() == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                chunk = new BackupChunk(file.fileId + chunk_no, size, file.desiredReplicationDegree, 0, Arrays.copyOf(buffer, size));
                Header header = new Header("1.0", "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);
                peer.sendPutChunk(chunk, header);

            }

            // return chunkMap;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // return new ConcurrentHashMap<String, BackupChunk>();
    }

    // public void sendPutChunk(){

    //     PutchunkMessage message = new PutchunkMessage(header, chunkMap.get(chunk_name),
    //             multichannelsbackup.getMulticastAddress(), multichannelsbackup.getMulticastPort());
    //     BackupTask backupTask = new BackupTask(message);
    //     backupTask.run();

    //     System.out.println(message.header.toString());
    // }
     

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
