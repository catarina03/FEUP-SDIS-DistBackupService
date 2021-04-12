package peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadControl implements Runnable {

    private String multicastAddress;
    private int multicastPort;
    private MulticastSocket multicastControlSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;
    private final int BUFFER_SIZE = 64000;

    /**
     * Constructor of PeerMultiThreadControl
     * 
     * @param peer             Owner of this multicast channel
     * @param version          Peer version
     * @param multicastAddress Address of the control channel
     * @param multicastPort    Port of the control channel
     * @param nThreads         number of worker threads of pool
     * @throws IOException
     */
    public PeerMultiThreadControl(Peer peer, String version, String multicastAddress, int multicastPort, int nThreads) throws IOException {

        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peer);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastControlSocket = new MulticastSocket(multicastPort);
        this.multicastControlSocket.joinGroup(group);

        // start worker service
        this.workerService = Executors.newFixedThreadPool(nThreads);
    }

    /**
     * Getter for multicast address
     * @return multicast address
     */
    public String getMulticastAddress() {
        return multicastAddress;
    }

    /**
     * Getter for multicast port
     * @return multicast port
     */
    public int getMulticastPort() {
        return multicastPort;
    }

    /**
     * Run method to read messages from channel
     */
    public void run() {
        
        try {
            // reading from channel
            byte[] mbuf = new byte[BUFFER_SIZE];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastControlSocket.receive(multicastPacket);

                byte[] copy = Arrays.copyOf(multicastPacket.getData(), multicastPacket.getLength());

                this.handleMessage(copy, multicastPacket.getAddress().getHostAddress(), multicastPacket.getPort());

                mbuf = new byte[BUFFER_SIZE];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message Handler method
     * 
     * @param packet        packet with message to be handled
     * @param packetAddress address where packet comes from
     * @param packetPort    port where packet comes from
     */
    public void handleMessage(byte[] packet, String packetAddress, int packetPort) {

        Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }
}
