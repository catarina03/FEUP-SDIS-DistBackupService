package peer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerMultiThreadBackup implements Runnable {

    private String peerID, multicastAddress, multicastPort;
    private DatagramPacket packet;
    private MulticastSocket multicastBackupSocket;
    private ExecutorService workerService;
    private MessageHandler messageHandler;
    private final int BUFFER_SIZE = 64000;

    public PeerMultiThreadBackup(String peerID, String version, String multicastAddress, String multicastPort,
            int nThreads) throws IOException {

        this.peerID = peerID;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.messageHandler = new MessageHandler(peerID, version);

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastBackupSocket = new MulticastSocket(Integer.parseInt(multicastPort.trim()));
        this.multicastBackupSocket.joinGroup(group);

        // create service message & datagramPacket
        String announcement = peerID + " ";
        byte[] buf = announcement.getBytes();
        this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));

        // start worker service
        this.workerService = Executors.newFixedThreadPool(nThreads);
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public String getMulticastPort() {
        return multicastPort;
    }

    public void run() {
        
        try {
            // reading from channel
            byte[] mbuf = new byte[BUFFER_SIZE];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while (true) {
                multicastBackupSocket.receive(multicastPacket);
                String multicastResponseString = new String(multicastPacket.getData());

                // print multicast received message
                System.out.println("In thread - Received-BackUp");
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

        Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

        this.workerService.execute(processMessage);
    }


}
