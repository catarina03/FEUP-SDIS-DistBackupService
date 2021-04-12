package peer;

import files.BackupChunk;
import files.BackupFile;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DiskState implements Serializable {
    /**
     * For serializable implementation
     */
    private static final long serialVersionUID = 1L;

    // used to manage disk state and get information
    public int peerId;
    public long maxCapacityAllowed = 200000000;
    public long occupiedSpace;

    // chunks & files
    public ConcurrentHashMap<String, BackupChunk> backedUpChunks; // Other's chunks that this peer stored
    public ConcurrentHashMap<String, BackupFile> files; // Own files that initiator peer asked to be backed up
    public ConcurrentHashMap<String, ConcurrentSkipListSet<Integer>> chunksLocation; // This saves the peers where a
                                                                                     // certain sent chunk is

    public ConcurrentHashMap<String, ConcurrentSkipListSet<Integer>> deletedFilesLocation; // Keeps all the deleted
                                                                                           // files and their peer
                                                                                           // location

    public transient ConcurrentHashMap<String, byte[]> toBeRestoredChunks; // This saves the body of the chunks that
                                                                           // will be used to
    // restore a file

    /**
     * Disk State Constructor
     * @param peerId Peer ID which disk state belongs to
     */
    public DiskState(int peerId) {
        this.peerId = peerId;
        this.occupiedSpace = 0;

        this.backedUpChunks = new ConcurrentHashMap<>();
        this.files = new ConcurrentHashMap<>();
        this.chunksLocation = new ConcurrentHashMap<>();
        this.toBeRestoredChunks = new ConcurrentHashMap<>();
        this.deletedFilesLocation = new ConcurrentHashMap<>();

        new File("../peerStorage/peer" + peerId + "/chunks").mkdirs();
        new File("../peerStorage/peer" + peerId + "/files").mkdirs();
    }

    /**
     * Override of method toString to show Disk State contents in a user friendly way
     */
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
                result += "\nChunk [" + chunkKey + "] - Current Replication Degree: " + backupFile.chunks.get(chunkKey)
                        + " - Located At: " + this.chunksLocation.get(chunkKey);
            }
            result += "\n";
        }

        result += "\n\n---------- BACKED UP CHUNKS OF PEER " + this.peerId + " ----------\n\n";
        for (String key : this.backedUpChunks.keySet()) {
            BackupChunk backupChunk = this.backedUpChunks.get(key);

            result += "\nID: " + backupChunk.id;
            result += "\nSize: " + backupChunk.getSize();
            result += "\nDesired Replication Degree: " + backupChunk.getDesiredReplicationDegree();
            result += "\nPerceived replication degree: " + this.chunksLocation.get(key).size();
            result += "\nLocations: " + this.chunksLocation.get(key) + "\n";
        }

        return result;
    }

    /**
     * Gets the location of a file's chunks
     * @param fileId File ID whose chunks we wish to locate
     * @return list of locations where chunks of file are stored in
     */
    public ConcurrentSkipListSet<Integer> getFileChunksLocation(String fileId) {
        
        ConcurrentSkipListSet<Integer> fileLocations = new ConcurrentSkipListSet<>();
       
        if (this.files.containsKey(fileId)) {
            BackupFile file = this.files.get(fileId);

            for (String id : file.chunks.keySet()) {
                if (this.chunksLocation.containsKey(id)) {
                    fileLocations.addAll(this.chunksLocation.get(id));
                }
            }
        }

        return fileLocations;
    }

    /**
     * Calculates the number of chunks in a file
     * @param backupFile file to count chunks of
     * @return number of chunks of backup file
     */
    public int getMaxNumberOfFileChunks(BackupFile backupFile) {
        return (int) backupFile.chunks.mappingCount();
    }

    /**
     * Verifies if all chunks of file exist
     * @param fileId file whose chunks were verifying to exist
     * @return true if all chunks exist, false otherwise
     */
    public boolean allChunksExist(String fileId) {
        BackupFile file = this.files.get(fileId);
        int chunkNumber = getMaxNumberOfFileChunks(file);
        int count = 0;

        for (int i = 0; i < chunkNumber; i++) {
            String chunkId = file.fileId + i;
            if (toBeRestoredChunks.get(chunkId) != null) {
                count++;
            }
        }
        return chunkNumber == count;
    }
}