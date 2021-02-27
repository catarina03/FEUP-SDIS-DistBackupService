import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {


        // -------------------------------ARGUMENT OPERATIONS-------------------------------
        //check for right argument number
        if(args.length < 4){
            System.out.println("Insuficient or too many arguments. Usage: java Client <host> <port> <operation> <arguments>*");
        }

        String hostname = String.valueOf(args[0]);       // get server address from arguments
        int port = Integer.valueOf(args[1]);            //get server port from arguments
        
        String message = parser(args);              //parse arguments to get request
        if(message.equals("")){                     //check for wrong arguments
            System.out.println("Request in the wrong format. Usage: java Client <host> <port> <operation> <arguments>*");
        }

        //TODO: check for args[4] format (eg. www.fe.up.pt)

        //TODO: check for args[5] format IPv4



        // -------------------------------Perform a request-------------------------------

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);

        byte[] buf = message.getBytes();    //request message

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        clientSocket.send(packet);          //sends datagram with request


        // -------------------------------Handle request answer-------------------------------

        //receive datagram
        byte[] rbuf = new byte[256];
        DatagramPacket response = new DatagramPacket(rbuf, rbuf.length);
        clientSocket.receive(response);
        String result = new String(response.getData()); //get server result

        System.out.println("Client: "+ message + " :: " + result);

        clientSocket.close();   //terminate socket
    }

    public static String parser(String[] args){     //parse Client arguments to a request message
        String r="";
        if (args[2].equals("REGISTER")) {           // REGISTER operation
            r = args[2]+' '+args[3]+' '+args[4];
            return r;
        }else if(args[2].equals("LOOKUP")){         // LOOKUP operation
            r=args[2]+' '+args[3];
            return r;
        }else return r;
    }
}
