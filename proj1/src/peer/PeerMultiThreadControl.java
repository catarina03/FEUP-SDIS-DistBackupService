package peer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

public class PeerMultiThreadControl extends Thread {

    private String peerID, multicastAddress, multicastPort;
    private DatagramPacket packet;
    private MulticastSocket multicastControlSocket;

    public PeerMultiThreadControl(String peerID, String multicastAddress, String multicastPort)
            throws IOException {

        this.peerID = peerID;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        this.multicastControlSocket = new MulticastSocket(Integer.parseInt(multicastPort.trim()));
        this.multicastControlSocket.joinGroup(group);

        // create service message & datagramPacket
        String announcement = peerID + " ";
        byte[] buf = announcement.getBytes();
        this.packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(multicastPort));
    }

    public void run() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    // send server announcement to multicast
                    multicastControlSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // print announcement sent to multicast
                System.out.println("multicastControl: " + multicastAddress + " " + multicastPort + " : PeerID -> " + peerID + "\n");

            }
        };
        Timer t = new Timer();
        t.schedule(task, 0, 1000);


        try {
            //reading from channel
            byte[] mbuf = new byte[256];
            DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
            while(true){
                multicastControlSocket.receive(multicastPacket);
                String multicastResponse = new String(multicastPacket.getData());
        
                // print multicast received message
                System.out.println("Received: " + multicastResponse + '\n');
    
                mbuf = new byte[256];    
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: handle exception
        }


    }

}
