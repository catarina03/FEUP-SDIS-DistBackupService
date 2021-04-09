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


public class RemovedTask extends Task{
    private int tries;
    private int space;
    private Header header;
    private RemovedMessage message;

    public RemovedTask(Peer peer, int space) {
        //super(peer,null);
        this.peer = peer;
        this.tries = 0;
        this.space=space;
        

        this.scheduler = new ScheduledThreadPoolExecutor(NUMBER_OF_WORKERS);
    }


    private ArrayList<String> sortingAlgorithm(){

        Map<String, Integer> extraReplicationMap = new LinkedHashMap<>();
        for (ConcurrentHashMap.Entry<String, ConcurrentSkipListSet<Integer>> e : this.peer.storage.chunksLocation.entrySet()) {
            extraReplicationMap.put(e.getKey(), e.getValue().size() - this.peer.storage.backedUpChunks.get(e.getKey()).getDesiredReplicationDegree());
        }
        
        List<Map.Entry<String, Integer>> extraReplicationList = new LinkedList<>(extraReplicationMap.entrySet());
        Collections.sort(extraReplicationList, new Comparator<Map.Entry<String, Integer>>() {
          public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o2.getValue() - o1.getValue();
          }
        });

        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> e : extraReplicationList) {
            result.add(e.getKey());
        }
    
        return result;
    }

/*
    public void prepare(){

        if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed){

            ArrayList<String> sortedChunkIds = new ArrayList<>();
            //Algoritmo
            sortedChunkIds = sortingAlgorithm();

            this.peer.storage.occupiedSpace -= this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).getSize();
            this.peer.storage.backedUpChunks.remove(sortedChunkIds.get(0));

            this.peer.storage.chunksReplicationDegree.replace(sortedChunkIds.get(0), 
                                                this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)), 
                                                this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)) + 1);
            this.peer.storage.chunksLocation.get(sortedChunkIds.get(0)).remove(this.peer.id);

            // APAGAR O FICHEIRO LOCAL


            this.header = new Header(this.peer.version, "REMOVED", this.peer.id, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).fileId, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).chunkNo);
            this.message = new RemovedMessage(header, this.peer.multicastControlAddress, this.peer.multicastControlPort);

/*
                
            if (this.tries < 3) {
                Random rand = new Random();
                int upperbound = 401;
                int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400


                scheduler.schedule(this, randomDelay, TimeUnit.MILLISECONDS);


                //Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

                //this.workerService.execute(processMessage);
            }

            this.tries++;
            
        
        
        }
    }

*/


    public void run() {


        System.out.println("MAX Storage capacity: " + this.peer.storage.maxCapacityAllowed);
        System.out.println("CURRENT Storage capacity: " + this.peer.storage.occupiedSpace);
        
        try {
            if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed){
                System.out.println("IN REMOVED TASK");

                ArrayList<String> sortedChunkIds = new ArrayList<>();

                sortedChunkIds = sortingAlgorithm();

                this.peer.storage.occupiedSpace -= this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).getSize();

                // this.peer.storage.chunksReplicationDegree.replace(sortedChunkIds.get(0), 
                // this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)), 
                // this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)) - 1);  //TODO:+ OU -, also why are we doing this?
                // this.peer.storage.chunksReplicationDegree.computeIfPresent(sortedChunkIds.get(0), (k, v) -> v - 1);
                // this.peer.storage.chunksLocation.get(sortedChunkIds.get(0)).remove(this.peer.id);
                
                // make message
                this.header = new Header(this.peer.version, "REMOVED", this.peer.id, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).fileId, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).chunkNo);
                this.message = new RemovedMessage(header, this.peer.multicastControlAddress, this.peer.multicastControlPort);
                
                // APAGAR O FICHEIRO LOCAL
                this.peer.storage.chunksLocation.remove(sortedChunkIds.get(0));
                this.peer.storage.chunksReplicationDegree.remove(sortedChunkIds.get(0));
                this.peer.storage.backedUpChunks.remove(sortedChunkIds.get(0));
    /*
                if (this.tries < 3) {
                    Random rand = new Random();
                    int upperbound = 401;
                    int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400


                    scheduler.schedule(this, randomDelay, TimeUnit.MILLISECONDS);


                    //Runnable processMessage = () -> this.messageHandler.handle(packet, packetAddress, packetPort);

                    //this.workerService.execute(processMessage);
                }

                this.tries++;
                */

                byte[] messageInBytes = message.convertToBytes();
    
                // MulticastSocket socket = new MulticastSocket(message.port);
                // socket.setTimeToLive(1);
                // socket.joinGroup(InetAddress.getByName(message.address));

                InetAddress group = InetAddress.getByName(message.address);
                MulticastSocket socket = new MulticastSocket(message.port);
                socket.setTimeToLive(1);
                socket.joinGroup(group);
    
                // sending request
                DatagramPacket removedPacket = new DatagramPacket(messageInBytes, messageInBytes.length,
                        InetAddress.getByName(message.address), message.port);
                socket.send(removedPacket);
    
                socket.close();
    
                //if (this.tries < 3) {
                    //Random rand = new Random();
                    //int upperbound = 401;
                    //int randomDelay = rand.nextInt(upperbound);   //generate random values from 0-400
    
                    //scheduler.schedule(this, randomDelay, TimeUnit.MILLISECONDS);
                //}

                if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed){
                    this.run();
                }
            }

            /*
            if (this.peer.storage.occupiedSpace > this.peer.storage.maxCapacityAllowed){
    
                ArrayList<String> sortedChunkIds = new ArrayList<>();
                //Algoritmo
                sortedChunkIds = sortingAlgorithm();
    
                this.peer.storage.occupiedSpace -= this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).getSize();
                this.peer.storage.backedUpChunks.remove(sortedChunkIds.get(0));
    
                this.peer.storage.chunksReplicationDegree.replace(sortedChunkIds.get(0), 
                                                    this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)), 
                                                    this.peer.storage.chunksReplicationDegree.get(sortedChunkIds.get(0)) + 1);
                this.peer.storage.chunksLocation.get(sortedChunkIds.get(0)).remove(this.peer.id);

                // APAGAR O FICHEIRO LOCAL


                Header header = new Header(this.peer.version, "REMOVED", this.peer.id, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).fileId, this.peer.storage.backedUpChunks.get(sortedChunkIds.get(0)).chunkNo);
                RemovedMessage message = new RemovedMessage(header, this.peer.multicastControlAddress, this.peer.multicastControlPort);


                */

    
                //this.tries++;




            //}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}