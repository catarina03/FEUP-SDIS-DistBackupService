package tasks;

import files.BackupChunk;
import files.BackupFile;
import peer.Header;
import peer.Peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AssembleFileTask extends Task{
    private final String ENHANCED = "2.0";

    public AssembleFileTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);
        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    private String trimPath(String pathname){
        ArrayList<String> pathnameArray = new ArrayList<>(Arrays.asList(pathname.split("/")));
        return pathnameArray.get(pathnameArray.size() - 1);
    }

    public void run(){

        BackupFile backupFile = this.peer.storage.files.get(this.header.fileId);

        if (this.peer.storage.allChunksExist(backupFile.fileId)){

            //make file
            int chunkNumber = this.peer.storage.getMaxNumberOfFileChunks(backupFile);

            File restoredFile = new File("../peerStorage/peer" + this.peer.id + "/restored_" + trimPath(backupFile.pathname));
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(restoredFile);
                FileChannel fc = fileOutputStream.getChannel();

                for (int i = 0; i < chunkNumber; i++){
                    String chunkId = backupFile.fileId + i;
                    byte[] body = this.peer.storage.toBeRestoredChunks.get(chunkId);

                    if (body != null){
                        fc.write(ByteBuffer.wrap(body));
                    }   
                    else {
                        System.out.println("Chunk doesn't exist\n");
                        throw new IOException("Missing chunk");
                    }
                }

                fc.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RemoveRestoredChunksTask removeRestoredChunksTask = new RemoveRestoredChunksTask(this.peer, this.header, this.chunk);
            this.scheduler.schedule(removeRestoredChunksTask, 10, TimeUnit.SECONDS);
        }
    }
}