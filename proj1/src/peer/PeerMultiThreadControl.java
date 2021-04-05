package peer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadControl implements Runnable {

    private Peer peer;
    private String multicastAddress;
    private int multicastPort;
    //private DatagramPacket packet;
    private MulticastSocket multicastControlSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;
    private final int BUFFER_SIZE = 64000;

    public PeerMultiThreadControl(Peer peer, String version, String multicastAddress, int multicastPort, int nThreads)
            throws IOException {

        this.peer = peer;
        //this.peerID = peer.id;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peer);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastControlSocket = new MulticastSocket(multicastPort);
        this.multicastControlSocket.joinGroup(group);

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
                multicastControlSocket.receive(multicastPacket);
                // String multicastResponseString = new String(multicastPacket.getData());

                System.out.println("In thread - Received-Stored");
                //System.out.println("Received-BackUp: " + multicastResponseString + '\n');

                this.handleMessage(multicastPacket, multicastPacket.getAddress().getHostAddress(), multicastPacket.getPort());

                mbuf = new byte[BUFFER_SIZE];
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: handle exception
        }


    }

    public void handleMessage(DatagramPacket packet, String packetAddress, int packetPort) {

        System.out.println(Arrays.toString(packet.getData()));

        Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }

}
