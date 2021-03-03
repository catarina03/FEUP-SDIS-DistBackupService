import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        // -------------------------------ARGUMENT
        // OPERATIONS-------------------------------
        // check for right argument number
        if (args.length < 4) {
            System.out.println(
                    "Insuficient or too many arguments. Usage: java Client <mcast_addr> <mcast_port> <operation> <arguments>*");
        }

        String multicastAddress = String.valueOf(args[0]); // get multicast service address from arguments
        int multicastPort = Integer.valueOf(args[1]); // get multicast service port from arguments

        String message = parser(args); // parse arguments to get request
        if (message.equals("")) { // check for wrong arguments
            System.out.println(
                    "Request in the wrong format. Usage: java Client <mcast_addr> <mcast_port> <operation> <arguments>*");
        }

        // TODO: check for args[4] format (eg. www.fe.up.pt)

        // TODO: check for args[5] format IPv4

        // ------------------------------- Join Multicast
        // Group-------------------------------

        // join multicast socket
        InetAddress group = InetAddress.getByName(multicastAddress);
        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(group);

        // receive packet containing server info from multicastSocket
        byte[] mbuf = new byte[256];
        DatagramPacket multicastPacket = new DatagramPacket(mbuf, mbuf.length);
        multicastSocket.receive(multicastPacket);

        String multicastResponse = new String(multicastPacket.getData());

        // print multicast message
        System.out.println("multicast: " + multicastAddress + " " + multicastPort + " : " + multicastResponse + '\n');

        // parse response and get server info
        String[] serverInfo = multicastResponse.split(" ");
        String serverAddress = serverInfo[0];
        int serverPort = Integer.parseInt(serverInfo[1].trim());

        // -------------------------------Perform a
        // request-------------------------------

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(serverAddress);

        byte[] buf = message.getBytes(); // request message

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
        clientSocket.send(packet); // sends datagram with request

        // -------------------------------Handle request
        // answer-------------------------------

        // receive datagram
        byte[] rbuf = new byte[256];
        DatagramPacket response = new DatagramPacket(rbuf, rbuf.length);
        clientSocket.receive(response);
        String result = new String(response.getData()); // get server result

        System.out.println("Client: " + message + " :: " + result);

        clientSocket.close(); // terminate socket
    }

    public static String parser(String[] args) { // parse Client arguments to a request message
        String r = "";
        if (args[2].equals("REGISTER")) { // REGISTER operation
            r = args[2] + ' ' + args[3] + ' ' + args[4];
            return r;
        } else if (args[2].equals("LOOKUP")) { // LOOKUP operation
            r = args[2] + ' ' + args[3];
            return r;
        } else
            return r;
    }
}
