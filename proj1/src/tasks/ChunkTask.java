package tasks;

import files.BackupChunk;
import messages.ChunkMessage;
import peer.Header;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChunkTask extends Task {
    private final String ENHANCED = "2.0";

    /**
     * Constructor of ChunkTask
     * 
     * @param peer   Peer that will run the task
     * @param header Header received
     * @param chunk  Chunk received
     */
    public ChunkTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);
    }

    /**
     * Creates the CHUNK message
     */
    public void run() {

        ChunkMessage chunkMessage = new ChunkMessage(this.header, this.chunk, this.peer.multicastDataRestoreAddress,
                this.peer.multicastDataRestorePort);

        byte[] messageInBytes = chunkMessage.convertToBytes();

        while (true) {
            if (!this.peer.recievedChunkMessage) { // FIXME: should it have the host ID so I know which host im
                                                   // "overloading"?
                sendChunkMessage(messageInBytes);
                break;
            }
        }
    }

    /**
     * Sends CHUNK message to the multicast channel
     * 
     * @param messageInBytes CHUNK message created in former function converted to
     *                       bytes
     * @return Runnable function
     */
    private Runnable sendChunkMessage(byte[] messageInBytes) {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(this.peer.multicastDataRestorePort);
            socket.setTimeToLive(1);
            socket.joinGroup(InetAddress.getByName(this.peer.multicastDataRestoreAddress));

            // sending request
            DatagramPacket chunkPacket = new DatagramPacket(messageInBytes, messageInBytes.length,
                    InetAddress.getByName(this.peer.multicastControlAddress), this.peer.multicastControlPort);

            socket.send(chunkPacket);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}