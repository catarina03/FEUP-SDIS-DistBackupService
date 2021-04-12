package tasks;

import messages.RemovedMessage;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import peer.Header;

public class RemoveAllTask extends Task {
    private Header header;
    private RemovedMessage message;

    /**
     * Constructor of RemoveAllTask
     * 
     * @param peer Peer that will run the task
     */
    public RemoveAllTask(Peer peer) {
        this.peer = peer;

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    /**
     * Removes all chunks from a peer and sends REMOVED messages
     */
    public void run() {

        try {
            if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed) {
                for (String id : this.peer.storage.backedUpChunks.keySet()) {

                    this.peer.storage.occupiedSpace -= this.peer.storage.backedUpChunks.get(id).getSize();

                    // make message
                    this.header = new Header(this.peer.version, "REMOVED", this.peer.id,
                            this.peer.storage.backedUpChunks.get(id).fileId,
                            this.peer.storage.backedUpChunks.get(id).chunkNo);
                    this.message = new RemovedMessage(header, this.peer.multicastControlAddress,
                            this.peer.multicastControlPort);

                    // APAGAR O FICHEIRO LOCAL
                    this.peer.storage.chunksLocation.remove(id);
                    this.peer.storage.backedUpChunks.remove(id);

                    byte[] messageInBytes = message.convertToBytes();

                    InetAddress group = InetAddress.getByName(message.address);
                    MulticastSocket socket = new MulticastSocket(message.port);
                    socket.setTimeToLive(1);
                    socket.joinGroup(group);

                    // sending request
                    DatagramPacket removedPacket = new DatagramPacket(messageInBytes, messageInBytes.length,
                            InetAddress.getByName(message.address), message.port);
                    socket.send(removedPacket);

                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
