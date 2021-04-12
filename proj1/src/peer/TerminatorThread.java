package peer;

public class TerminatorThread implements Runnable{

    public Peer peer;
    
    /**
     * Constructor of TerminatorThread
     * @param peer Peer that has the terminator thread
     */
    public TerminatorThread(Peer peer){
        this.peer = peer;
    }

    /**
     * Upon SIGINT saves the DiskState information to a .ser file and shutdown peer
     */
    @Override
    public void run() {
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                
                peer.fileManager.updateState(); //saves state before shutting down
                
                System.out.println("\nSaving information into local storage and shutting down ..."); 
            }
        });
    } 
}
