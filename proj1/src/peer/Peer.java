package peer;

import files.BackupChunk;
import files.BackupFile;
import files.FileManager;
import messages.PutchunkMessage;
import messages.DeleteMessage;
import messages.GetChunkMessage;
import rmi.RemoteInterface;
import tasks.PutchunkTask;
import tasks.RemovedTask;
import tasks.DeleteTask;
import tasks.GetChunkTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements RemoteInterface {

    public String version;
    public int id;
    public DiskState storage;
    public FileManager fileManager;

    public int MAX_SIZE_CHUNK = 64000; // in bytes

    public static PeerMultiThreadControl multichannelscontrol;
    public static PeerMultiThreadBackup multichannelsbackup;
    public static PeerMultiThreadRestore multichannelsrestore;
    public static TerminatorThread terminator;

    public String multicastControlAddress;
    public int multicastControlPort;
    public String multicastDataBackupAddress;
    public int multicastDataBackupPort;
    public String multicastDataRestoreAddress;
    public int multicastDataRestorePort;

    public Peer(String version, int id, String mcAddress, String mcPort, String mdbAddress, String mdbPort, String mdrAddress,
            String mdrPort) {
        this.version = version;
        this.id = id;

        this.fileManager = new FileManager(this);

        this.storage = new DiskState(this.id);

        this.fileManager.recoverState();
        this.fileManager.updateState();


        this.multicastControlAddress = mcAddress;
        this.multicastControlPort = Integer.parseInt(mcPort.trim());
        this.multicastDataBackupAddress = mdbAddress;
        this.multicastDataBackupPort = Integer.parseInt(mdbPort.trim());
        this.multicastDataRestoreAddress = mdrAddress;
        this.multicastDataRestorePort = Integer.parseInt(mdrPort.trim());

        

        try {
            // connect to MC channel
            multichannelscontrol = new PeerMultiThreadControl(this, version, this.multicastControlAddress,
                    this.multicastControlPort, 10);
            new Thread(multichannelscontrol).start();

            // connect to MDB channel
            multichannelsbackup = new PeerMultiThreadBackup(this, version, this.multicastDataBackupAddress,
                    this.multicastDataBackupPort, 10);
            new Thread(multichannelsbackup).start();

            // connect to MDR channel
            multichannelsrestore = new PeerMultiThreadRestore(this, version, this.multicastDataRestoreAddress,
                    this.multicastDataRestorePort, 10);
            new Thread(multichannelsrestore).start();

            terminator = new TerminatorThread(this);
            new Thread(terminator).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String args[]) throws RemoteException, IOException {
 
        if (args.length != 9) {
            System.out.println(
                    "Usage: java Peer <protocol_version> <peer_id> <remote_object_name> <MC_Address> <MC_Port> <MDB_Address> <MDB_Port> <MDR_Address> <MDR_Port>");
            return;
        }

        Peer peer = new Peer(args[0], Integer.parseInt(args[1]), args[3], args[4], args[5], args[6], args[7], args[8]);
        
        String remoteObjName = args[2];

        // TODO: check state and update every few secs to keep storage updated

        // connect to RMI
        Registry registry = getRegistry();

        try {

            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(peer, 0);

            registry.rebind(remoteObjName, stub);

            System.out.println("Peer running, " + remoteObjName + " is listening...\n");

        } catch (Exception e) {
            System.err.println("Peer Exception: " + e.toString());
            e.printStackTrace();
        }

    }


    @Override
    public String backUp(String pathname, String degree) {
        String result = "Peer" + id + ": received BACKUP request.";

        // check replication degree errors
        int replication_degree = Integer.parseInt(degree);
        if (replication_degree < 1 || replication_degree > 9) {
            throw new IllegalArgumentException("Invalid replication degree, must be 1 - 9.");
        }

        // check path errors
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("Empty file path.");
        }

        File file = new File(pathname);
        if (!file.exists()) {
            throw new IllegalArgumentException("Empty file path.");
        }

        // create backup system file
        BackupFile systemFile = new BackupFile(pathname, replication_degree);
        
        Path path = Paths.get(pathname);
        Path absolutePath = path.toAbsolutePath();
        
        fileManager.saveFileToDirectory(this.id, systemFile);
        fileManager.readFileIntoChunks(absolutePath, systemFile);
        
        this.storage.files.putIfAbsent(systemFile.fileId, systemFile);
        
        return result;
    }


    public void sendPutChunk(BackupChunk chunk, Header header) {

        PutchunkMessage message = new PutchunkMessage(header, chunk, multichannelsbackup.getMulticastAddress(),
                multichannelsbackup.getMulticastPort());
        PutchunkTask backupTask = new PutchunkTask(this, message);
        backupTask.run();
    }


    @Override
    public String delete(String pathname) {
        String result = "Peer id-" + this.id + ": received DELETE request.";

        // check path errors
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("Empty file path.");
        }

        File file = new File(pathname);
        if (!file.exists()) {
            throw new IllegalArgumentException("Empty file path.");
        }
        
        BackupFile systemFile = new BackupFile(pathname, 0);
        Header header = new Header(this.version, "DELETE", this.id, systemFile.fileId);
        sendDelete(header);
        
        return result;
    }


    public void sendDelete(Header header) {
        DeleteMessage message = new DeleteMessage(header, multichannelscontrol.getMulticastAddress(), multichannelscontrol.getMulticastPort());
        DeleteTask deleteTask = new DeleteTask(this, message);
        deleteTask.run();
    }


    @Override
    public String restore(String pathname) {
        String result = "Peer" + this.id + ": received RESTORE request.";
        
        BackupFile fileToBeRestored = new BackupFile(pathname, 1);
        if(!storage.files.containsKey(fileToBeRestored.fileId)){
            throw new IllegalArgumentException("Requested file was not backed up from this Peer.");
        }

        long numberOfFileChunks = this.storage.files.get(fileToBeRestored.fileId).chunks.mappingCount();

        // send Getchunk for every file chunk
        for (int chunkNo = 0; chunkNo < numberOfFileChunks; chunkNo++){
            Header header = new Header(this.version, "GETCHUNK", this.id, fileToBeRestored.fileId, chunkNo);
            GetChunkMessage message = new GetChunkMessage(header, multichannelscontrol.getMulticastAddress(), multichannelscontrol.getMulticastPort());

            GetChunkTask getChunkTask = new GetChunkTask(this, message);
            getChunkTask.run();
        }
        
        return result;

    }


    @Override
    public String reclaim(int maxDiskSpace) {
        if (maxDiskSpace < 0){
            return "Invalid argument: maximum disk space has to be a positive integer or zero.";
        }

        
        String result = "Peer" + this.id + ": received RECLAIM request.";
        
        if (maxDiskSpace >= this.storage.maxCapacityAllowed){
            this.storage.maxCapacityAllowed = maxDiskSpace;
            System.out.println("Storage capacity upgraded to: " + maxDiskSpace);
        }
        else {
            //if maxdiskspace == 0 delete everything

            //CHECK PEER STORAGE TO REMOVE ENOUGH CHUNKS TO FREE SPACE 
            // (ALGORITHM: REMOVER O CHUNK (OU CHUNKS) MAIS PEQUENO QUE CONSIGA LIBERTAR O ESPAÇO PEDIDO, MINIMO NUMERO DE CHUNKS)
    
            //ATUALIZAR MAPAS E ESPAÇOS 
            //SEND REMOVED FOR EACH DELETED CHUNK

            System.out.println("Storage capacity downgraded to: " + maxDiskSpace);

            System.out.println("Before MAX Storage capacity: " + this.storage.maxCapacityAllowed);
            System.out.println("Before CURRENT Storage capacity: " + this.storage.occupiedSpace);

            this.storage.maxCapacityAllowed = maxDiskSpace;

            System.out.println("After MAX Storage capacity: " + this.storage.maxCapacityAllowed);
            System.out.println("After CURRENT Storage capacity: " + this.storage.occupiedSpace);

            RemovedTask task = new RemovedTask(this, maxDiskSpace);
            task.run();

            // ON RECEIVING REMOVED, PEER UPDATES MAPAS 
            // SE ALGUM CHUNK DROPS BELOW DESIRED REPLICATION DEGREE ENTAO MANDA-SE PUTCHUNK PARA ESSE CHUNK

            //this.storage.maxCapacityAllowed = maxDiskSpace;
        }
        return result;

    }

    
    @Override
    public String state() {
        return this.storage.toString();
    }

    private static Registry getRegistry() {
        Registry registry = null;

        try {
            boolean execute = true;
            boolean notExecute = false;

            if (execute) {
                registry = LocateRegistry.getRegistry();
            }

            if (notExecute) {
                registry = LocateRegistry.createRegistry(3000);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return registry;
    }



    
}
