package peer;

import files.Chunk;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class StoredTask extends Task{
    public StoredTask(Peer peer, Header header, Chunk chunk) {
        super(peer, header, chunk);
    }

    public StoredTask(Peer peer, Message message) {
        super(peer, message);
    }




    // MESS - THIS CAME FROM BACKUP TASK
    public void run(){

        //System.out.println("TO DO: Running backup task");

        try {
            //get file id and chunk number
            //String[] req = new String(request).split(" ",5);
            //String fileID = req[3] , chunkNo = req[4];

            //create socket

            Header storedHeader = new Header("1.0", "STORED", this.peer.id, this.message.header.fileId, this.message.header.chunkNo);
            StoredMessage storedMessage = new StoredMessage(storedHeader, this.peer.multicastControlAddress, this.peer.multicastControlPort);

            byte[] messageInBytes = storedMessage.convertToBytes();

            //if (this.tries < 5){
                MulticastSocket socket = new MulticastSocket(this.peer.multicastControlPort);
                socket.setTimeToLive(1);
                socket.joinGroup(InetAddress.getByName(this.peer.multicastControlAddress));



                //sending request
                DatagramPacket replyPacket = new DatagramPacket(messageInBytes, messageInBytes.length, InetAddress.getByName(this.peer.multicastControlAddress), this.peer.multicastControlPort);
                socket.send(replyPacket);

                System.out.println("In STORED TASK - Sent packet: " + storedMessage.header.toString());
                socket.close();
            //}


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



        } catch (IOException e) {
            e.printStackTrace();
        }

        //Ve se o ficheiro nao é seu, se não for:
        //Esperar 0 - 400 ms
        //Guarda o chunk se o replication degree ainda nao for suficiente
        //Manda o STORED
    }




}
