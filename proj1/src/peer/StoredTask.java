package peer;

import files.BackupChunk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StoredTask extends Task{
    public StoredTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }


    public void run(){
        try {
            // SAVING CHUNK IF IT DOESN'T EXIST ALREADY
            /*
            String chunkId = this.header.fileId + this.header.chunkNo;
            if (this.peer.id != header.senderId){

                this.peer.storage.backedUpChunks.putIfAbsent(chunkId, chunk);
            }


            Integer currentReplicationDegree = this.peer.storage.chunksReplicationDegree.putIfAbsent(chunkId, 1);
            if (currentReplicationDegree != null){
                this.peer.storage.chunksReplicationDegree.replace(chunkId, currentReplicationDegree + 1);
            }

            ConcurrentSkipListSet<Integer> currentChunkStorageList = this.peer.storage.chunksLocation.computeIfAbsent(chunkId, value -> new ConcurrentSkipListSet<>());
            currentChunkStorageList.add(this.peer.id);

             */


            System.out.println("\nIn STORED Task");

            Header storedHeader = new Header("1.0", "STORED", this.peer.id, this.header.fileId, this.header.chunkNo);
            StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);

            byte[] messageInBytes = storedMessage.convertToBytes();

            Random rand = new Random();
            int upperbound = 401;

            //generate random values from 0-400
            int randomDelay = rand.nextInt(upperbound);

            scheduler.schedule( () -> sendStorageMessage(messageInBytes), randomDelay, TimeUnit.MILLISECONDS);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void sendStorageMessage(byte[] messageInBytes){
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
