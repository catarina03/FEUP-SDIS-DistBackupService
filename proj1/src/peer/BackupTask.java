package peer;

import files.BackupChunk;
import files.Chunk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class BackupTask extends Task{

    //private Peer peer;
    //private PutchunkMessage putchunkMessage;
    //private Header header;
    //private BackupChunk chunk;
    //private String address;
    //private int port;
    private int tries;

/*

    public BackupTask(Peer peer, Header header, Chunk chunk) {
        super(peer, header, chunk);
        this.tries = 0;
    }

    public BackupTask(Peer peer, PutchunkMessage message) {
        super(peer, message);
        this.tries = 0;
    }
    */

    public BackupTask(Message message) {
        super(message);
        this.tries = 0;
    }

    public void run(){

        //System.out.println("TO DO: Running backup task");

        try {
            //get file id and chunk number
            //String[] req = new String(request).split(" ",5);
            //String fileID = req[3] , chunkNo = req[4];

            //create socket

            //Header storedHeader = new Header("1.0", this.peer.id, this.message.header.fileId, this.message.header.chunkNo);
            //StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);

            byte[] messageInBytes = this.message.convertToBytes();

            if (this.tries < 5){
                MulticastSocket socket = new MulticastSocket(this.message.port);
                socket.setTimeToLive(1);
                socket.joinGroup(InetAddress.getByName(this.message.address));



                //sending request
                DatagramPacket replyPacket = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(this.message.address), this.message.port);
                socket.send(replyPacket);

                System.out.println("In PUTCHUNK - Sent packet: " + message.header.toString());
                socket.close();
            }


            //int time = new Random().nextInt(400);
            //socket.setSoTimeout(time); //max time it will listen to

            //listen for x time
            //long oldTime = System.currentTimeMillis();
            //byte[] b = new byte[65000];

            //DatagramPacket receive = new DatagramPacket(b,b.length);
            //while(System.currentTimeMillis()-oldTime < time){
/*
                try {
                    socket.receive(receive);
                }catch (IOException e)
                {
                    System.out.println("\nReached time out - no one has sent PUTCHUNK!");
                    break;
                }

                String[] ms = new String(receive.getData()).split(" ",6);

 */

                /*
                if(ms[1]=="PUTCHUNK" && ms[3]==message.header.fileId && ms[4]==message.header.chunkNo){
                    System.out.println("Someone already sent it!");
                    socket.close();
                    return;//exit
                }

                 */
            //}



            this.tries++;

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Ve se o ficheiro nao é seu, se não for:
        //Esperar 0 - 400 ms
        //Guarda o chunk se o replication degree ainda nao for suficiente
        //Manda o STORED
    }


}
