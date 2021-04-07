package tasks;

import files.BackupChunk;
import messages.StoredMessage;
import peer.Header;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoreTask extends Task{
    private final String ENHANCED = "2.0";

    public StoreTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }



    private void backupAndAcknowledge(){
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
        this.peer.storage.saveChunkToDirectory(this.chunk, this.peer.id, this.header.chunkNo, this.header.fileId);

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
        System.out.println("IN ENHANCED BACKUP FUNCTION");
        System.out.println("Current rep degree: " + this.peer.storage.chunksReplicationDegree.get(chunk.id) + " Ideal rep degree: " + this.header.replicationDegree);
        if (this.peer.storage.chunksReplicationDegree.get(chunk.id)  == null ||
                this.peer.storage.chunksReplicationDegree.get(chunk.id) < this.header.replicationDegree){
            System.out.println("REPLICATION DEGREE NOT YET REACHED");

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
            this.peer.storage.saveChunkToDirectory(this.chunk, this.peer.id, this.header.chunkNo, this.header.fileId);

            // BUILDING STORED MESSAGE AND SENDING IT
            Header storedHeader = new Header(this.peer.version, "STORED", this.peer.id, this.header.fileId, this.header.chunkNo);
            StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);
            byte[] messageInBytes = storedMessage.convertToBytes();
            sendStorageMessage(messageInBytes);
        }
    }



    public void run(){
        if (this.peer.version.equals(this.ENHANCED) && this.header.version.equals(this.ENHANCED)){
            System.out.println("ENHANCED BACKUP");

            // ENHANCED VERSION
            Random rand = new Random();
            int upperbound = 401;
            int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400

            //sleep randomDelay
            scheduler.schedule(this::backupAndAcknowledgeEnhanced, randomDelay, TimeUnit.MILLISECONDS);
        }
        else {
            System.out.println("BACKUP SIMPLE");

            // SIMPLE VERSION
            backupAndAcknowledge();
        }
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