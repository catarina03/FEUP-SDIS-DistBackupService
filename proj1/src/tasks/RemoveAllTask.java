package tasks;

import messages.RemovedMessage;
import peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import peer.Header;

public class RemoveAllTask extends Task {
    private Header header;
    private RemovedMessage message;

    public RemoveAllTask(Peer peer) {
        this.peer = peer;

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }

    public void run() {

        try {
            if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed) {
                System.out.println("IN REMOVE ALL TASK");

                for (String id : this.peer.storage.backedUpChunks.keySet()) {

                    this.peer.storage.occupiedSpace -= this.peer.storage.backedUpChunks.get(id)
                            .getSize();

                    // make message
                    this.header = new Header(this.peer.version, "REMOVED", this.peer.id,
                            this.peer.storage.backedUpChunks.get(id).fileId,
                            this.peer.storage.backedUpChunks.get(id).chunkNo);
                    this.message = new RemovedMessage(header, this.peer.multicastControlAddress,
                            this.peer.multicastControlPort);


                    System.out.println("BEFORE");
                    System.out.println("Chunks location: " + this.peer.storage.chunksLocation);
                    //System.out.println("Chunks rep degree: " + this.peer.storage.chunksReplicationDegree);
                    System.out.println("Chunks backed up: " + this.peer.storage.backedUpChunks);


                    System.out.println("Chunks location is there: " + this.peer.storage.chunksLocation.containsKey(id));
                    //System.out.println("Chunks rep degree is there: " + this.peer.storage.chunksReplicationDegree.containsKey(id));
                    System.out.println("Chunks backed up is there: " + this.peer.storage.backedUpChunks.containsKey(id));

                    // APAGAR O FICHEIRO LOCAL
                    this.peer.storage.chunksLocation.remove(id);
                    //this.peer.storage.chunksReplicationDegree.remove(id);
                    this.peer.storage.backedUpChunks.remove(id);




                    System.out.println("AFTER");
                    System.out.println("Chunks location: " + this.peer.storage.chunksLocation);
                   // System.out.println("Chunks rep degree: " + this.peer.storage.chunksReplicationDegree);
                    System.out.println("Chunks backed up: " + this.peer.storage.backedUpChunks);


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
                System.out.println("Entrei aqui & o márcio é lindo: " + this.peer.storage);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
