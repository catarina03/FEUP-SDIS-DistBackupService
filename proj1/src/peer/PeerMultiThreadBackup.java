package peer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

public class PeerMultiThreadBackup extends Thread {

    private String peerID, multicastAddress, multicastPort;
    private DatagramPacket packet;
    private MulticastSocket multicastBackupSocket;

    public PeerMultiThreadBackup(String peerID, String multicastAddress, String multicastPort) throws IOException {

        this.peerID = peerID;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastBackupSocket = new MulticastSocket(Integer.parseInt(multicastPort.trim()));
        this.multicastBackupSocket.joinGroup(group);

        // create service message & datagramPacket
        String announcement = peerID + " ";
        byte[] buf = announcement.getBytes();
        this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));
    }

    public void run() {
        // TimerTask task = new TimerTask() {
        //     @Override
        //     public void run() {
        //         try {
        //             // send server announcement to multicast
        //             multicastBackupSocket.send(packet);
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }

        //         // print announcement sent to multicast
        //         System.out.println("multicastBackup: " + multicastAddress + " " + multicastPort + " : PeerID -> "
        //                 + peerID + "\n");

        //     }
        // };
        // Timer t = new Timer();
        // t.schedule(task, 0, 1000);

        try {
            // reading from channel
            byte[] mbuf = new byte[256];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastBackupSocket.receive(multicastPacket);
                String multicastResponse = new String(multicastPacket.getData());

                // print multicast received message
                System.out.println("Received BackUp: " + multicastResponse + '\n');

                // TODO: parse received messages and answer requests

                

                mbuf = new byte[256];
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    public void initiateBackup(File file){

        // send backup request for each chunk -> pool.createWorkerBackup -> workerBackup.sendChunk&Request 

        // wait for store message -> pool.createWorkerStore -> workerStore.waitForStoreMessage -> true
    }

}
