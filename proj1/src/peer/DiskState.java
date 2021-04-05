package peer;

import files.BackupChunk;
import files.FileManager;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class DiskState {
    //used to manage disk state and get information
    public int peerId;
    public long maxCapacityAllowed;
    public long occupiedSpace;
    public transient String storageFolderName;
    public FileManager fileManager;
    
    //public ConcurrentHashMap<String,Chunk> ownChunks; Peer não guarda próprios chunks, só files I think
    public ConcurrentHashMap<String,BackupChunk> backedUpChunks; //Other's chunks that this peer stored

    public DiskState(int peerId) {

        this.peerId=peerId;
        this.storageFolderName="peer" + peerId;

        this.fileManager = new FileManager(this.peerId);

        //this.files = new ConcurrentHashMap<String, Chunk>();
        this.backedUpChunks = new ConcurrentHashMap<String, BackupChunk>();

        new File("peer" + peerId + "/chunks").mkdirs();
        new File("peer" + peerId + "/files").mkdirs();
    }

    /*
    public void readFile(String filepath){
        ConcurrentHashMap<String, Chunk> fileChunks = fileManager.readFileIntoChunks(filepath);
        this.ownChunks.putAll(fileChunks);
    }

     */

    //public void prepareBackUp




}
