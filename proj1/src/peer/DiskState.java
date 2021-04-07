package peer;

import files.BackupChunk;
import files.BackupFile;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DiskState {
    // used to manage disk state and get information
    public int peerId;
    public long maxCapacityAllowed = 200000;
    public long occupiedSpace;
    public transient String storageFolderName;

    // public ConcurrentHashMap<String,Chunk> ownChunks; Peer não guarda próprios
    // chunks, só files I think
    public ConcurrentHashMap<String, BackupChunk> backedUpChunks; // Other's chunks that this peer stored
    public ConcurrentHashMap<String, BackupFile> files; // Own files that initiator peer asked to be backed up

    public ConcurrentHashMap<String, Integer> chunksReplicationDegree; // This saves the replication degree for each
                                                                       // chunk I sent/store
    public ConcurrentHashMap<String, ConcurrentSkipListSet<Integer>> chunksLocation; // This saves the peers where a
                                                                                     // certain sent chunk is

    public DiskState(int peerId) {

        this.peerId = peerId;
        this.occupiedSpace = 0;
        this.storageFolderName = "peer" + peerId;

        // this.files = new ConcurrentHashMap<String, Chunk>();
        this.backedUpChunks = new ConcurrentHashMap<>();
        this.files = new ConcurrentHashMap<>();
        this.chunksReplicationDegree = new ConcurrentHashMap<>();
        this.chunksLocation = new ConcurrentHashMap<>();

        new File("../peerFiles/peer" + peerId + "/chunks").mkdirs();
        new File("../peerFiles/peer" + peerId + "/files").mkdirs();
    }

    /*
     * public void readFile(String filepath){ ConcurrentHashMap<String, Chunk>
     * fileChunks = fileManager.readFileIntoChunks(filepath);
     * this.ownChunks.putAll(fileChunks); }
     * 
     */

    // public void prepareBackUp

    public void recoverState() {
        // PERCORRER FILES

        try {
            File dir = new File("../peerFiles/peer" + peerId + "/files");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File f : directoryListing) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    BackupFile backupFile = (BackupFile) objectInputStream.readObject();
                    this.files.putIfAbsent(backupFile.fileId, backupFile);
                    objectInputStream.close();
                }
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException){
                System.out.println("Creating files directory for peer " + peerId + "...");
            }
            else {
                e.printStackTrace();
            }
        }

        System.out.println("FILE MAP AFTER RECOVER STATE");
        System.out.println(this.files);

        try {
            File dir = new File("../peerFiles/peer" + peerId + "/chunks");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File f : directoryListing) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    BackupChunk backupChunk = (BackupChunk) objectInputStream.readObject();

                    System.out.println("BACKUP CHUNK");
                    System.out.println(backupChunk.id);
                    System.out.println(backupChunk);

                    this.backedUpChunks.putIfAbsent(backupChunk.id, backupChunk);
                    objectInputStream.close();
                }
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException){
                System.out.println("Creating chunks directory for peer " + peerId + "...");
            }
            else {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    "../peerFiles/peer" + peerId + "/chunksReplicationDegree.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.chunksReplicationDegree = (ConcurrentHashMap<String, Integer>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    "../peerFiles/peer" + peerId + "/chunksLocation.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.chunksLocation = (ConcurrentHashMap<String, ConcurrentSkipListSet<Integer>>) objectInputStream
                    .readObject();
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    public void updateState() {

        File replicationDegreeFile = new File("../peerFiles/peer" + peerId + "/chunksReplicationDegree.ser");

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(replicationDegreeFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(chunksReplicationDegree);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File locationFile = new File("../peerFiles/peer" + peerId + "/chunksLocation.ser");
        try {
            fileOutputStream = new FileOutputStream(locationFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(chunksLocation);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void saveFileToDirectory(int peerId, BackupFile file) {
        File newFile = new File("../peerFiles/peer" + peerId + "/files/" + file.fileId + ".ser");

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(newFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(file);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveChunkToDirectory(BackupChunk chunk, int peerId, int chunkNo, String fileId) {

        File newFile = new File("../peerFiles/peer" + peerId + "/chunks/" + chunkNo + "_" + fileId + ".ser");

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(newFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(chunk);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {

        String result;

        /*
         * For each file whose backup it has initiated: The file pathname The backup
         * service id of the file The desired replication degree For each chunk of the
         * file: Its id Its perceived replication degree For each chunk it stores: Its
         * id Its size (in KBytes) The desired replication degree Its perceived
         * replication degree The peer's storage capacity, i.e. the maximum amount of
         * disk space that can be used to store chunks, and the amount of storage (both
         * in KBytes) used to backup the chunks.
         */

        result = "\n---------- " + "FILES OF PEER " + this.peerId + " ----------\n";
        for (String key : this.files.keySet()) {
            BackupFile backupFile = this.files.get(key);
            result += "\nPathname: " + backupFile.pathname;
            result += "\nID: " + backupFile.fileId;
            result += "\nDesired Replication degree: " + backupFile.desiredReplicationDegree;

            for (String chunkKey : backupFile.chunks.keySet()) {
                result += "\nChunk " + chunkKey + ": " + backupFile.desiredReplicationDegree;
            }

            // ...
        }

        result += "\n\n---------- BACKED UP CHUNKS OF PEER " + this.peerId + " ----------\n\n";
        for (String key : this.backedUpChunks.keySet()){
            BackupChunk backupChunk = this.backedUpChunks.get(key);

            // LOOK 1 - mais compacto
            /*
            result += "ID: " + backupChunk.id +
                        " | Size: " + backupChunk.getSize() +
                        " | Desired Replication Degree: " + backupChunk.getDesiredReplicationDegree() +
                        " | Perceived replication degree: " + this.chunksReplicationDegree.get(key) + "\n";

             */

            //LOOK 2 - mais disperso
            result += "\nID: " + backupChunk.id;
            result += "\nSize: " + backupChunk.getSize();
            result += "\nDesired Replication Degree: " + backupChunk.getDesiredReplicationDegree();
            result += "\nPerceived replication degree: " + this.chunksReplicationDegree.get(key) + "\n";
        }
        return result;
    }
}
