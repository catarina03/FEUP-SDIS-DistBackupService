package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadBackup implements Runnable {

    private Peer peer;
    private String multicastAddress;
    private int multicastPort;
    private MulticastSocket multicastBackupSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;
    private final int BUFFER_SIZE = 64000;

    public PeerMultiThreadBackup(Peer peer, String version, String multicastAddress, int multicastPort, int nThreads) throws IOException {

        this.peer = peer;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peer);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastBackupSocket = new MulticastSocket(multicastPort);
        this.multicastBackupSocket.joinGroup(group);

        // start worker service
        this.workerService = Executors.newFixedThreadPool(nThreads);
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void run() {
        
        try {
            // reading from channel
            byte[] mbuf = new byte[BUFFER_SIZE];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastBackupSocket.receive(multicastPacket);

                byte[] copy = Arrays.copyOf(multicastPacket.getData(), multicastPacket.getLength());

                this.handleMessage(copy, multicastPacket.getAddress().getHostAddress(), multicastPacket.getPort());

                mbuf = new byte[BUFFER_SIZE];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleMessage(byte[] packet, String packetAddress, int packetPort) {

        Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }
}
