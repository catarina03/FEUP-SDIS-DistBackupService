package tasks;

import messages.Message;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeleteTask extends Task {

    private int tries;

    public DeleteTask(Peer peer, Message message) {
        super(peer, message);
        this.tries = 0;

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    public void run() {
        try {

            byte[] messageInBytes = this.message.convertToBytes();

            MulticastSocket socket = new MulticastSocket(this.message.port);
            socket.setTimeToLive(1);
            socket.joinGroup(InetAddress.getByName(this.message.address));

            // sending request
            DatagramPacket deletePacket = new DatagramPacket(messageInBytes, messageInBytes.length,
                    InetAddress.getByName(this.message.address), this.message.port);
            socket.send(deletePacket);

            socket.close();

            if (this.tries < 4) {
                Random rand = new Random();
                int upperbound = 501;
                int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-500

                scheduler.schedule(this, randomDelay, TimeUnit.MILLISECONDS);
            }

            this.tries++;
        

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
