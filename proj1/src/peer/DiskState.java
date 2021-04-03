package peer;

import files.Chunk;
import files.FileManager;

import java.util.concurrent.ConcurrentHashMap;

public class DiskState {
    //used to manage disk state and get information
    public int peerID;
    public long maxCapacityAllowed;
    public long occupiedSpace;
    public transient String storageFolderName;
    public FileManager fileManager;
    
    public ConcurrentHashMap<String,Chunk> ownChunks;
    public ConcurrentHashMap<String,Chunk> othersChunks;

    public DiskState(int peerID) {

        this.peerID=peerID;
        this.storageFolderName="peer_"+Integer.toString(peerID);

        this.fileManager = new FileManager(this.peerID);

        this.ownChunks = new ConcurrentHashMap<String, Chunk>();
        this.othersChunks = new ConcurrentHashMap<String, Chunk>();
    }

    /*
    public void readFile(String filepath){
        ConcurrentHashMap<String, Chunk> fileChunks = fileManager.readFileIntoChunks(filepath);
        this.ownChunks.putAll(fileChunks);
    }

     */

    //public void prepareBackUp




}
