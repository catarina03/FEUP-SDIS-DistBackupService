package peer;

import files.BackupChunk;
import files.BackupFile;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DiskState implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // used to manage disk state and get information
    public int peerId;
    public long maxCapacityAllowed = 200000;
    public long occupiedSpace;
    // public transient String storageFolderName;

    // chunks & files
    public ConcurrentHashMap<String, BackupChunk> backedUpChunks; // Other's chunks that this peer stored
    public ConcurrentHashMap<String, BackupFile> files; // Own files that initiator peer asked to be backed up
    public ConcurrentHashMap<String, Integer> chunksReplicationDegree; // This saves the replication degree for each
                                                                       // chunk I sent/store
    public ConcurrentHashMap<String, ConcurrentSkipListSet<Integer>> chunksLocation; // This saves the peers where a
                                                                                     // certain sent chunk is
    public transient ConcurrentHashMap<String, byte[]> toBeRestoredChunks; // This saves the body of the chunks that will be used to
                                                                      // restore a file

    public DiskState(int peerId) {
        this.peerId = peerId;
        this.occupiedSpace = 0;
        // this.storageFolderName = "peer" + peerId;

        this.backedUpChunks = new ConcurrentHashMap<>();
        this.files = new ConcurrentHashMap<>();
        this.chunksReplicationDegree = new ConcurrentHashMap<>();
        this.chunksLocation = new ConcurrentHashMap<>();
        this.toBeRestoredChunks=new ConcurrentHashMap<>();

        new File("../peerFiles/peer" + peerId + "/chunks").mkdirs();
        new File("../peerFiles/peer" + peerId + "/files").mkdirs();
    }

    @Override
    public String toString() {
        String result;

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
        for (String key : this.backedUpChunks.keySet()) {
            BackupChunk backupChunk = this.backedUpChunks.get(key);

            // LOOK 1 - mais compacto
            /*
             * result += "ID: " + backupChunk.id + " | Size: " + backupChunk.getSize() +
             * " | Desired Replication Degree: " + backupChunk.getDesiredReplicationDegree()
             * + " | Perceived replication degree: " + this.chunksReplicationDegree.get(key)
             * + "\n";
             * 
             */

            // LOOK 2 - mais disperso
            result += "\nID: " + backupChunk.id;
            result += "\nSize: " + backupChunk.getSize();
            result += "\nDesired Replication Degree: " + backupChunk.getDesiredReplicationDegree();
            result += "\nPerceived replication degree: " + this.chunksReplicationDegree.get(key) + "\n";
        }
        return result;
    }


    public int getMaxNumberOfFileChunks(BackupFile backupFile){
        return (int) backupFile.chunks.mappingCount();
    }

    public boolean allChunksExist(String fileId){
        BackupFile file = this.files.get(fileId);
        int chunkNumber = getMaxNumberOfFileChunks(file);
        int count = 0;

        for (int i = 0; i < chunkNumber; i++){
            String chunkId = file.fileId + i;
            if (toBeRestoredChunks.get(chunkId) != null){
                count++;
            }
        }

        System.out.println("Checking if all chunks are present, max chunk number: " + chunkNumber + " we have: " + count);
        System.out.println(chunkNumber == count);

        return chunkNumber == count;
    }

}