package peer;

import java.util.concurrent.ConcurrentHashMap;

import files.*;

public class DiskState {
    //used to manage disk state and get information
    public int peerID;
    public long maxCapacityAllowed;
    public long occupiedSpace;
    public transient String storageFolderName;
    
    public ConcurrentHashMap<String,Chunk> ownChunks;
    public ConcurrentHashMap<String,Chunk> othersChunks;

    public DiskState(int peerID) {

        this.peerID=peerID;
        this.storageFolderName="peer_"+Integer.toString(peerID);

        this.ownChunks = new ConcurrentHashMap<String, Chunk>();
        this.othersChunks = new ConcurrentHashMap<String, Chunk>();
    }



}
