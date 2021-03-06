package files;

import peer.*;

import java.nio.file.Path;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class FileManager {
    public Peer peer;
    public int MAX_SIZE_CHUNK = 64000; //in bytes

    /**
     * File Manager's constructor
     */
    public FileManager(Peer peer){
        this.peer = peer;
    }

    /**
     * Reads file and separates it into 64 kBytes chunks
     * @param absolutePath file's absolute path
     * @param file file for backup
     */
    public void readFileIntoChunks(Path absolutePath, BackupFile file) {

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(String.valueOf(absolutePath)))) {

            int chunk_no = 0;
            BackupChunk chunk = new BackupChunk();
            byte[] buffer = new byte[MAX_SIZE_CHUNK];
            
            int size;
            while ((size = stream.read(buffer)) > 0) {
                chunk = new BackupChunk(file.fileId + chunk_no, file.fileId, chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);
                
                file.chunks.putIfAbsent(chunk.id, 0);

                peer.sendPutChunk(chunk, header);

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];
            }

            //check if needs 0 size chunk
            if(chunk.getSize() == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                chunk = new BackupChunk(file.fileId + chunk_no, file.fileId, chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);

                file.chunks.putIfAbsent(chunk.id, 0);

                peer.sendPutChunk(chunk, header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to recover state from serializable files (run everytime a peer wakes up, to recover previous state); if there's not previous state, it creates a new file
     */
    public void recoverState() {
        // iterate files
        try {
            File dir = new File("../peerStorage/peer" + this.peer.id + "/files");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File f : directoryListing) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    BackupFile backupFile = (BackupFile) objectInputStream.readObject();
                    this.peer.storage.files.putIfAbsent(backupFile.fileId, backupFile);
                    objectInputStream.close();
                }
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException){
                System.out.println("[No files directory for peer " + this.peer.id + ", will make a new one]");
            }
            else {
                e.printStackTrace();
            }
        }

        // iterate chunks
        try {
            File dir = new File("../peerStorage/peer" + this.peer.id + "/chunks");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File f : directoryListing) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    BackupChunk backupChunk = (BackupChunk) objectInputStream.readObject();

                    this.peer.storage.backedUpChunks.putIfAbsent(backupChunk.id, backupChunk);
                    objectInputStream.close();
                }
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException){
                System.out.println("[No chunks directory for peer " + this.peer.id + ", will make a new one]");
            }
            else {
                e.printStackTrace();
            }
        }

        // iterate diskState
        try {
            FileInputStream fileInputStream = new FileInputStream(
                    "../peerStorage/peer" + this.peer.id + "/diskState.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.peer.storage = (DiskState) objectInputStream.readObject();
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            if (e instanceof IOException){
                System.out.println("[No diskState file to read from disk, will make a new one]\n");
            }
            else {
                e.printStackTrace();
            }
        }

    }

    /**
     * Updates the serializable file containing all the information from the disk state 
     */
    public void updateState() {
        File diskStateFile = new File("../peerStorage/peer" + this.peer.id + "/diskState.ser");
        FileOutputStream fileOutputStream;
        try {
            diskStateFile.createNewFile(); // if file already exists will do nothing 
            fileOutputStream = new FileOutputStream(diskStateFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.peer.storage);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves file to peer directory
     * @param peerId peer ID
     * @param file file to save
     */
    public void saveFileToDirectory(int peerId, BackupFile file) {
        File newFile = new File("../peerStorage/peer" + peerId + "/files/" + file.fileId + ".ser");
        FileOutputStream fileOutputStream;
        try {
            newFile.createNewFile(); // if file already exists will do nothing 
            fileOutputStream = new FileOutputStream(newFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(file);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves chunk to directory
     * 
     * @param chunk   Chunk to be saved
     * @param peerId  Peer ID which should save the chunk
     * @param chunkNo Chunk number
     * @param fileId  File id of which the chunk belongs to
     */
    public void saveChunkToDirectory(BackupChunk chunk, int peerId, int chunkNo, String fileId) {
        File newFile = new File("../peerStorage/peer" + peerId + "/chunks/" + chunkNo + "_" + fileId + ".ser");
        FileOutputStream fileOutputStream;
        try {
            newFile.createNewFile(); // if file already exists will do nothing 
            fileOutputStream = new FileOutputStream(newFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(chunk);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes file from directory
     * @param peerId Peer ID whose directory belongs to
     * @param fileId File ID to delete
     */
    public void deleteFileFromDirectory(int peerId, String fileId){
        File newFile = new File("../peerStorage/peer" + peerId + "/files/" + fileId + ".ser");
        newFile.delete();
    }

    /**
     * Deletes chunk from directory
     * @param peerId  Peer ID that stores the chunk
     * @param fileId  File id of which the chunk belongs to
     * @param chunkNo Chunk number
     */
    public void deleteChunkFromDirectory(int peerId, String fileId, int chunkNo){
        File newFile = new File("../peerStorage/peer" + peerId + "/chunks/" + chunkNo + "_" + fileId + ".ser");
        newFile.delete();
    }
}