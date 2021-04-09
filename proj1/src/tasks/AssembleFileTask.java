package tasks;

import files.BackupChunk;
import files.BackupFile;
import messages.StoredMessage;
import peer.Header;
import peer.Peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
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

            File restoredFile = new File("../peerFiles/peer" + this.peer.id + "/restored_" + trimPath(backupFile.pathname));
            FileOutputStream fileOutputStream;
            try {
                restoredFile.createNewFile(); // if file already exists will do nothing 
                fileOutputStream = new FileOutputStream(restoredFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                for (int i = 0; i < chunkNumber; i++){

                    /*
                    try (FileOutputStream fos = new FileOutputStream("pathname")) {
                        fos.write(myByteArray);
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    }
                    */




                    String chunkId = backupFile.fileId + i;
                    System.out.println("CHUNKID FOR RESTORE: " + chunkId);
                    byte[] body = this.peer.storage.toBeRestoredChunks.get(chunkId);
                    System.out.println(new String(body));
                    if (body != null){
                        //objectOutputStream.writeObject(body);
                        objectOutputStream.write(body);
                    }   
                    else {
                        // SOMETHING IS VERY WRONG
                        System.out.print("Chunk doesn't exist\n");
                        throw new IOException("Missing chunk");
                    }
                }
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RemoveRestoredChunksTask removeRestoredChunksTask = new RemoveRestoredChunksTask(this.peer, this.header, this.chunk);
            this.scheduler.schedule(removeRestoredChunksTask, 10, TimeUnit.SECONDS);
/*
            //clean map with restore chunks from peer.storage
            for (int i = 0; i < chunkNumber; i++){
                String chunkId = backupFile.fileId + i;
                this.peer.storage.toBeRestoredChunks.remove(chunkId);
            }

 */


        }

    }




}