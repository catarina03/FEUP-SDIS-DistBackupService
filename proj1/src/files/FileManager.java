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

    public FileManager(Peer peer){
        this.peer = peer;
    }

    public void readFileIntoChunks(Path absolutePath, BackupFile file) {

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(String.valueOf(absolutePath)))) {

            int chunk_no = 0;
            BackupChunk chunk = new BackupChunk();
            byte[] buffer = new byte[MAX_SIZE_CHUNK]; // TEM QUE SER MENOS
            
            int size;
            while ((size = stream.read(buffer)) > 0) {
                chunk = new BackupChunk(file.fileId + chunk_no, file.fileId, chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);
                
                file.chunks.putIfAbsent(chunk.id, file.desiredReplicationDegree);

                peer.sendPutChunk(chunk, header);

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];
            }

            //check if needs 0 size chunk
            if(chunk.getSize() == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                chunk = new BackupChunk(file.fileId + chunk_no, file.fileId, chunk_no, size, file.desiredReplicationDegree, Arrays.copyOf(buffer, size));
                Header header = new Header(this.peer.version, "PUTCHUNK", this.peer.id, file.fileId, chunk_no, file.desiredReplicationDegree);

                file.chunks.putIfAbsent(chunk.id, file.desiredReplicationDegree);

                peer.sendPutChunk(chunk, header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void recoverState() {
        // iterate files
        try {
            File dir = new File("../peerFiles/peer" + this.peer.id + "/files");
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
            File dir = new File("../peerFiles/peer" + this.peer.id + "/chunks");
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
                    "../peerFiles/peer" + this.peer.id + "/diskState.ser");
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


    public void updateState() {
        File diskStateFile = new File("../peerFiles/peer" + this.peer.id + "/diskState.ser");
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


    public void saveFileToDirectory(int peerId, BackupFile file) {
        File newFile = new File("../peerFiles/peer" + peerId + "/files/" + file.fileId + ".ser");
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


    public void saveChunkToDirectory(BackupChunk chunk, int peerId, int chunkNo, String fileId) {
        File newFile = new File("../peerFiles/peer" + peerId + "/chunks/" + chunkNo + "_" + fileId + ".ser");
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


    public void deleteFileFromDirectory(int peerId, String fileId){
        File newFile = new File("../peerFiles/peer" + peerId + "/files/" + fileId + ".ser");
        newFile.delete();
    }

    public void deleteChunkFromDirectory(int peerId, String fileId, int chunkNo){
        File newFile = new File("../peerFiles/peer" + peerId + "/chunks/" + chunkNo + "_" + fileId + ".ser");
        newFile.delete();
    }

}