package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadRestore implements Runnable {

    private Peer peer;
    private String multicastAddress;
    private int multicastPort;
    private DatagramPacket packet;
    private MulticastSocket multicastRestoreSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;
    private final int BUFFER_SIZE = 64000;

    public PeerMultiThreadRestore(Peer peer, String version, String multicastAddress, int multicastPort,
            int nThreads) throws IOException {

        this.peer = peer;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peer);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastRestoreSocket = new MulticastSocket(multicastPort);
        this.multicastRestoreSocket.joinGroup(group);

        // create service message & datagramPacket
        //String announcement = peerID + " ";
        //byte[] buf = announcement.getBytes();
        //this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));

        // start worker service
        this.workerService = Executors.newFixedThreadPool(nThreads);
    }

    public void run() {

        try {
            // reading from channel
            byte[] mbuf = new byte[BUFFER_SIZE];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastRestoreSocket.receive(multicastPacket);
                String multicastResponseString = new String(multicastPacket.getData());

                // print multicast received message
                //System.out.println("Received-Restore: " + multicastResponseString + '\n');

                byte[] copy = Arrays.copyOf(multicastPacket.getData(), multicastPacket.getLength());

                this.handleMessage(copy, multicastPacket.getAddress().getHostAddress(), multicastPacket.getPort());

                mbuf = new byte[BUFFER_SIZE];
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    public void handleMessage(byte[] packet, String packetAddress, int packetPort) {

        Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }

}
