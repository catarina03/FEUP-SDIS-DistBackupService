package peer;

public class TerminatorThread implements Runnable{

    public Peer peer;
    
    public TerminatorThread(Peer peer){
        this.peer = peer;
    }

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
