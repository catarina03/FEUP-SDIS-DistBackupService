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

    /*
    private void backupAndAcknowledge(){
        // STORES CHUNK
        this.peer.storage.backedUpChunks.putIfAbsent(this.chunk.id, this.chunk);

        // DECREASES PEER STORAGE SPACE
        this.peer.storage.occupiedSpace -= this.chunk.body.length;

        // INCREASES REPLICATION DEGREE OF STORED CHUNK
        Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.putIfAbsent(this.chunk.id, 1);
        if (currentReplicationDegree != null){
            this.peer.storage.chunksReplicationDegree.replace(this.chunk.id, currentReplicationDegree + 1);
        }

        // UPDATES THE LIST OF CHUNKS' LOCATION
        ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation.computeIfAbsent(this.chunk.id, value -> new ConcurrentSkipListSet<>());
        currentChunkStorageList.add(this.peer.id);

        // SAVES CHUNK TO FILE DIRECTORY
        this.peer.fileManager.saveChunkToDirectory(this.chunk, this.peer.id, this.header.chunkNo, this.header.fileId);

        // BUILDING STORED MESSAGE
        Header storedHeader = new Header(this.peer.version, "STORED", this.peer.id, this.header.fileId, this.header.chunkNo);
        StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);
        byte[] messageInBytes = storedMessage.convertToBytes();

        // SLEEPS RANDOM DELAY AND SENDS MESSAGE
        Random rand = new Random();
        int upperbound = 401;
        int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400
        scheduler.schedule( () -> sendStorageMessage(messageInBytes), randomDelay, TimeUnit.MILLISECONDS);
    }

    private void backupAndAcknowledgeEnhanced(){
        if (this.peer.storage.chunksReplicationDegree.get(chunk.id)  == null ||
                this.peer.storage.chunksReplicationDegree.get(chunk.id) < this.header.replicationDegree){

            // STORES CHUNK
            this.peer.storage.backedUpChunks.putIfAbsent(this.chunk.id, this.chunk);

            //DECREASES PEER STORAGE SPACE
            this.peer.storage.occupiedSpace -= this.chunk.body.length;

            // INCREASES REPLICATION DEGREE OF STORED CHUNK
            Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.putIfAbsent(this.chunk.id, 1);
            if (currentReplicationDegree != null){
                this.peer.storage.chunksReplicationDegree.replace(this.chunk.id, currentReplicationDegree + 1);
            }

            // UPDATES THE LIST OF CHUNKS' LOCATION
            ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation.computeIfAbsent(this.chunk.id, value -> new ConcurrentSkipListSet<>());
            currentChunkStorageList.add(this.peer.id);

            // SAVES CHUNK TO FILE DIRECTORY
            this.peer.fileManager.saveChunkToDirectory(this.chunk, this.peer.id, this.header.chunkNo, this.header.fileId);

            // BUILDING STORED MESSAGE AND SENDING IT
            Header storedHeader = new Header(this.peer.version, "STORED", this.peer.id, this.header.fileId, this.header.chunkNo);
            StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);
            byte[] messageInBytes = storedMessage.convertToBytes();
            sendStorageMessage(messageInBytes);
        }
    }
    */

    private int getMaxNumberOfFileChunks(BackupFile backupFile){
        return (int) backupFile.chunks.mappingCount();
    }

    private boolean allChunksExist(BackupFile file){
        int chunkNumber = getMaxNumberOfFileChunks(file);
        int count = 0;

        for (int i = 0; i < chunkNumber; i++){
            String chunkId = file.fileId + i;
            if (this.peer.storage.toBeRestoredChunks.get(chunkId) != null){
                count++;
            }   
        }

        System.out.println("Checking if all chunks are present, max chunk number: " + chunkNumber + " we have: " + count);

        return chunkNumber == count;
    } 


    private String trimPath(String pathname){
        ArrayList<String> pathnameArray = new ArrayList<>(Arrays.asList(pathname.split("/")));
        return pathnameArray.get(pathnameArray.size() - 1);
    }


    public void run(){

        BackupFile backupFile = this.peer.storage.files.get(this.header.fileId);

        if (allChunksExist(backupFile)){

            //make file
            int chunkNumber = getMaxNumberOfFileChunks(backupFile);

            File restoredFile = new File("../peerFiles/peer" + this.peer.id + "/restored_" + trimPath(backupFile.pathname));
            FileOutputStream fileOutputStream;
            try {
                restoredFile.createNewFile(); // if file already exists will do nothing 
                fileOutputStream = new FileOutputStream(restoredFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                for (int i = 0; i < chunkNumber; i++){
                    String chunkId = backupFile.fileId + i;
                    System.out.println("CHUNKID FOR RESTORE: " + chunkId);
                    byte[] body = this.peer.storage.toBeRestoredChunks.get(chunkId);
                    if (body != null){
                        objectOutputStream.writeObject(body);
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


            //clean map with restore chunks from peer.storage
            for (int i = 0; i < chunkNumber; i++){
                String chunkId = backupFile.fileId + i;
                this.peer.storage.toBeRestoredChunks.remove(chunkId);
            }
        }



        /*
        if (this.peer.version.equals(this.ENHANCED) && this.header.version.equals(this.ENHANCED)){
            // ENHANCED VERSION
            Random rand = new Random();
            int upperbound = 401;
            int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400

            //sleep randomDelay
            scheduler.schedule(this::backupAndAcknowledgeEnhanced, randomDelay, TimeUnit.MILLISECONDS);
        }
        else {
            // SIMPLE VERSION
            backupAndAcknowledge();
        }
        */
    }

    private void sendStorageMessage(byte[] messageInBytes){
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(this.peer.multicastControlPort);
            socket.setTimeToLive(1);
            socket.joinGroup(InetAddress.getByName(this.peer.multicastControlAddress));

            //sending request
            DatagramPacket replyPacket = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(this.peer.multicastControlAddress), this.peer.multicastControlPort);
            socket.send(replyPacket);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}