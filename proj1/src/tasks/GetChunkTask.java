package tasks;

import messages.Message;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GetChunkTask extends Task {
    private int tries;

    /**
     * Constructor of GetChunkTask
     * 
     * @param peer    Peer that will run the Task
     * @param message Message received
     */
    public GetChunkTask(Peer peer, Message message) {
        super(peer, message);
        this.tries = 0;

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    /**
     * Sends the GETCHUNK message to the multicast channel
     */
    public void run() {
        try {
            byte[] messageInBytes = this.message.convertToBytes();

            MulticastSocket socket = new MulticastSocket(this.message.port);
            socket.setTimeToLive(1);
            socket.joinGroup(InetAddress.getByName(this.message.address));

            // sending request
            DatagramPacket getChunkPacket = new DatagramPacket(messageInBytes, messageInBytes.length,
                    InetAddress.getByName(this.message.address), this.message.port);
            socket.send(getChunkPacket);

            socket.close();

            if (this.tries < 3) {
                if (!this.peer.storage.allChunksExist(this.message.header.fileId)) {
                    scheduler.schedule(this, (long) Math.pow(2, this.tries), TimeUnit.SECONDS);
                }
            }
            this.tries++;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}