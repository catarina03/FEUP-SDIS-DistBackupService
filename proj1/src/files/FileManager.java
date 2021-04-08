package files;

import peer.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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
            byte[] buffer = new byte[MAX_SIZE_CHUNK]; // TEM QUE SER MENOS
            
            int size;
            while ((size = stream.read(buffer)) > 0) {
                chunk = new BackupChunk(file.fileId + chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);
                
                file.chunks.putIfAbsent(chunk.id, file.desiredReplicationDegree);

                peer.sendPutChunk(chunk, header);

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];
            }

            //check if needs 0 size chunk
            if(chunk.getSize() == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                chunk = new BackupChunk(file.fileId + chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);

                file.chunks.putIfAbsent(chunk.id, file.desiredReplicationDegree);

                peer.sendPutChunk(chunk, header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
