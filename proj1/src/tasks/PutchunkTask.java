package tasks;

import messages.Message;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PutchunkTask extends Task{

    private int tries;

    public PutchunkTask(Peer peer, Message message) {
        super(peer, message);
        this.tries = 0;

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    public void run(){
        try {

            byte[] messageInBytes = this.message.convertToBytes();
            String chunkId = message.header.fileId + message.header.chunkNo;

            //this.peer.storage.chunksReplicationDegree.putIfAbsent(chunkId, 0);

            System.out.println("Rep degree in storage: " + this.peer.storage.chunksLocation.get(chunkId));
            System.out.println("Rep degree in message header: " + message.header.replicationDegree);

            if (!this.peer.storage.chunksLocation.containsKey(chunkId) || this.peer.storage.chunksLocation.get(chunkId).size() < message.header.replicationDegree){
                MulticastSocket socket = new MulticastSocket(this.message.port);
                socket.setTimeToLive(1);
                socket.joinGroup(InetAddress.getByName(this.message.address));

                //sending request
                DatagramPacket putChunkPacket = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(this.message.address), this.message.port);

                socket.send(putChunkPacket);

                socket.close();

                if (this.tries < 4){
                    scheduler.schedule(this, (long) Math.pow(2, this.tries), TimeUnit.SECONDS);
                }
                this.tries++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
