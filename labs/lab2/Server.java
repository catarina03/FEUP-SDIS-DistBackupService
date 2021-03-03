import java.io.*;
import java.net.*;


public class Server {
    public static void main(String[] args) throws IOException {

        if(args.length != 3){
            System.out.println("Usage: java Server <port> <mcast_addr> <mcast_port>");
        }else{

            DNSService dnsService = new DNSService();        //create dns service handler

            //parse server arguments
            String serverPort = args[0];
            String multicastAddress = args[1];
            String multicastPort = args[2];
            
            //------------------------- MULTICAST SERVER -------------------------
            //get Server Address
            String serverAddress = InetAddress.getLocalHost().getHostAddress(); 

            //start ServerThread, where server announces its service
            ServerThread announcer = new ServerThread(serverAddress, serverPort, multicastAddress, multicastPort);
            announcer.start();

            //Open request server socket
            DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(serverPort));
            
            System.out.println("The server is running");

            while(true){        //loop for receiving requests
                try{

                    //receive datagram
                    byte[] rbuf = new byte[256];
                    DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
                    serverSocket.receive(packet);

                    //print request
                    String received = new String(packet.getData());
                    System.out.println("Server: " + received);

                    //parse request
                    Request request = parse(received);

                    //handle request (DNS)
                    String serverResponse;
                    if(request.operation.equals("REGISTER")){
                        serverResponse = dnsService.addEntry(request);
                    }else if(request.operation.equals("LOOKUP")){ 
                        serverResponse = dnsService.getEntry(request);
                    }else{
                        serverResponse = "ERROR: invalid operation";
                    }

                    //answer client request: get address and port from packet received from client
                    InetAddress clientSocket = packet.getAddress();
                    int clientPort = packet.getPort();

                    //create response packet
                    byte[] buf = new byte[512];
                    buf=serverResponse.getBytes();
                    DatagramPacket response = new DatagramPacket(buf,buf.length, clientSocket, clientPort);
                    serverSocket.send(response);

                }catch (IOException e){ //catch exception and handle it
                    e.printStackTrace();
                    break;
                }


            }

            serverSocket.close();   //close server socket
        }

    }

    public static Request parse(String r){
        Request request = new Request();

        String[] s = r.split(" ");
        request.operation = s[0];
        request.name = s[1];

        if(s[0].equals("REGISTER")){
           request.ip_address=s[2]; 
        }

        return request; 
    }
}




// public class Server {
//     public static void main(String[] args) throws IOException {
//         DatagramSocket serverSocket = new DatagramSocket(4160);

//         byte[] rbuf = new byte[256];
//         DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
//         serverSocket.receive(packet);
//         String response = new String(packet.getData());
//         System.out.println("Response: " + response);
//     }
// }
