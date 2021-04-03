package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadRestore implements Runnable {

    private String peerID, multicastAddress, multicastPort;
    private DatagramPacket packet;
    private MulticastSocket multicastRestoreSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;

    public PeerMultiThreadRestore(String peerID, String version, String multicastAddress, String multicastPort,
            int nThreads) throws IOException {

        this.peerID = peerID;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peerID, version);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastRestoreSocket = new MulticastSocket(Integer.parseInt(multicastPort.trim()));
        this.multicastRestoreSocket.joinGroup(group);

        // create service message & datagramPacket
        String announcement = peerID + " ";
        byte[] buf = announcement.getBytes();
        this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));

        // start worker service
        this.workerService = Executors.newFixedThreadPool(nThreads);
    }

    public void run() {
        
        try {
            // reading from channel
            byte[] mbuf = new byte[256];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastRestoreSocket.receive(multicastPacket);
                String multicastResponse = new String(multicastPacket.getData());

                // print multicast received message
                System.out.println("Received-Restore: " + multicastResponse + '\n');

                this.handleMessage(multicastResponse, multicastPacket.getAddress().getHostAddress(), multicastPacket.getPort());

                mbuf = new byte[256];
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    public void handleMessage(String message, String packetAddress, int packetPort) {

        Runnable processMessage = () -> this.messageHandler.handle(message, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }

}
