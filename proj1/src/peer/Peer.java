package peer;

import files.BackupChunk;
import files.BackupFile;
import files.FileManager;
import messages.PutchunkMessage;
import rmi.RemoteInterface;
import tasks.PutchunkTask;

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
    public Protocol protocol;
    public int id;
    public DiskState storage;
    public FileManager fileManager;

    public int MAX_SIZE_CHUNK = 64000; // in bytes
    // public int MAX_SIZE_CHUNK = 5000; //in bytes

    public static PeerMultiThreadControl multichannelscontrol;
    public static PeerMultiThreadBackup multichannelsbackup;
    public static PeerMultiThreadRestore multichannelsrestore;

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

        this.storage = new DiskState(this.id);
        this.storage.recoverState();
        this.storage.updateState();

        this.fileManager = new FileManager(this);

        this.multicastControlAddress = mcAddress;
        this.multicastControlPort = Integer.parseInt(mcPort.trim());
        this.multicastDataBackupAddress = mdbAddress;
        this.multicastDataBackupPort = Integer.parseInt(mdbPort.trim());
        this.multicastDataRestoreAddress = mdrAddress;
        this.multicastDataRestorePort = Integer.parseInt(mdrPort.trim());

        try {
            // connect to MC channel
            // PeerMultiThreadControl multichannelscontrol = new
            // PeerMultiThreadControl(args[1], version, multicastControlAddress,
            // multicastControlPort,10);
            multichannelscontrol = new PeerMultiThreadControl(this, version, this.multicastControlAddress,
                    this.multicastControlPort, 10);
            new Thread(multichannelscontrol).start();

            // connect to MDB channel
            // PeerMultiThreadBackup multichannelsbackup = new
            // PeerMultiThreadBackup(args[1], version, multicastDataBackupAddress,
            // multicastDataBackupPort,10);
            multichannelsbackup = new PeerMultiThreadBackup(this, version, this.multicastDataBackupAddress,
                    this.multicastDataBackupPort, 10);
            new Thread(multichannelsbackup).start();

            // connect to MDR channel
            // PeerMultiThreadRestore multichannelsrestore = new
            // PeerMultiThreadRestore(args[1], version, multicastDataRestoreAddress,
            // multicastDataRestorePort,10);
            multichannelsrestore = new PeerMultiThreadRestore(this, version, this.multicastDataRestoreAddress,
                    this.multicastDataRestorePort, 10);
            new Thread(multichannelsrestore).start();
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

        //version = args[0];
        // protocol = new Protocol(version); // initiate protocol according to version
        String remoteObjName = args[2];

        Peer peer = new Peer(args[0], Integer.parseInt(args[1]), args[3], args[4], args[5], args[6], args[7], args[8]);

        // TODO: check state and update every few secs to keep storage updated

        // connect to RMI
        Registry registry = getRegistry();

        try {

            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(peer, 0);

            registry.rebind(remoteObjName, stub);

            System.out.println("Peer is running. " + remoteObjName + " listening at port 4000.\n");

        } catch (Exception e) {
            System.err.println("Peer Exception : " + e.toString());
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
        
        storage.saveFileToDirectory(this.id, systemFile);
        fileManager.readFileIntoChunks(absolutePath, systemFile);
        
        this.storage.files.putIfAbsent(systemFile.fileId, systemFile);
        
        return result;
    }

    public void sendPutChunk(BackupChunk chunk, Header header) {

        PutchunkMessage message = new PutchunkMessage(header, chunk, multichannelsbackup.getMulticastAddress(),
                multichannelsbackup.getMulticastPort());
        PutchunkTask backupTask = new PutchunkTask(this, message);
        backupTask.run();

        System.out.println(message.header.toString());
    }

    @Override
    public String delete(String pathname) {

        String result = "Peer id-" + this.id + ": received DELETE request.";

        return result;

    }

    @Override
    public String restore(String pathname) {

        String result = "Peer" + this.id + ": received RESTORE request.";

        return result;

    }

    @Override
    public String reclaim(int maxDiskSpace) {

        String result = "Peer" + this.id + ": received RECLAIM request.";

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
