import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

public class PeerMultiThread extends Thread {

    private String peerAddress, peerPort;
    private DatagramPacket packet;
    private MulticastSocket multicastControlSocket;

    public PeerMultiThread(String peerAddress, String peerPort)
            throws IOException {

        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastControlSocket = new MulticastSocket(Integer.parseInt(multicastPort.trim()));
        this.multicastControlSocket.joinGroup(group);

        // create service message & datagramPacket
        String announcement = serverAddress + " " + serverPort;
        byte[] buf = announcement.getBytes();
        this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));
    }

    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    // send server announcement to multicast
                    multicastSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // print announcement sent to multicast
                System.out.println("multicast: " + multicastAddress + " " + multicastPort + " : " + serverAddress + " "
                        + serverPort);

            }
        };

        Timer t = new Timer();
        t.schedule(task, 0, 1000);
    }

}
