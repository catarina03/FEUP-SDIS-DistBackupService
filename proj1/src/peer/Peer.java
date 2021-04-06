package peer;

import files.BackupChunk;
import files.Chunk;
import rmi.RemoteInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.util.Arrays;

public class Peer implements RemoteInterface{
    
    public static String version;
    public Protocol protocol;
    public int id;
    public DiskState storage;
    public int MAX_SIZE_CHUNK = 64000; //in bytes
    //public int MAX_SIZE_CHUNK = 5000; //in bytes

    public static PeerMultiThreadControl multichannelscontrol;
    public static PeerMultiThreadBackup multichannelsbackup;
    public static PeerMultiThreadRestore multichannelsrestore;

    public String multicastControlAddress;
    public int multicastControlPort;
    public String multicastDataBackupAddress;
    public int multicastDataBackupPort;
    public String multicastDataRestoreAddress;
    public int multicastDataRestorePort;

    /*
    public Peer() {
        version="1.0";
        id=0;
    }

     */

    public Peer(int id, String mcAddress, String mcPort, String mdbAddress, String mdbPort, String mdrAddress, String mdrPort){
        this.id = id;

        this.storage = new DiskState(this.id);

        this.multicastControlAddress = mcAddress;
        this.multicastControlPort = Integer.parseInt(mcPort.trim());
        this.multicastDataBackupAddress = mdbAddress;
        this.multicastDataBackupPort = Integer.parseInt(mdbPort.trim());
        this.multicastDataRestoreAddress = mdrAddress;
        this.multicastDataRestorePort = Integer.parseInt(mdrPort.trim());

        try {
            //connect to MC channel
            //PeerMultiThreadControl multichannelscontrol = new PeerMultiThreadControl(args[1], version, multicastControlAddress, multicastControlPort,10);
            multichannelscontrol = new PeerMultiThreadControl(this, version, this.multicastControlAddress, this.multicastControlPort,10);
            new Thread(multichannelscontrol).start();

            //connect to MDB channel
            //PeerMultiThreadBackup multichannelsbackup = new PeerMultiThreadBackup(args[1], version, multicastDataBackupAddress, multicastDataBackupPort,10);
            multichannelsbackup = new PeerMultiThreadBackup(this, version, this.multicastDataBackupAddress, this.multicastDataBackupPort,10);
            new Thread(multichannelsbackup).start();

            //connect to MDR channel
            //PeerMultiThreadRestore multichannelsrestore = new PeerMultiThreadRestore(args[1], version, multicastDataRestoreAddress, multicastDataRestorePort,10);
            multichannelsrestore = new PeerMultiThreadRestore(this, version, this.multicastDataRestoreAddress, this.multicastDataRestorePort,10);
            new Thread(multichannelsrestore).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws RemoteException, IOException {

        if (args.length != 9) {
            System.out.println("Usage: java Peer <protocol_version> <peer_id> <remote_object_name> <MC_Address> <MC_Port> <MDB_Address> <MDB_Port> <MDR_Address> <MDR_Port>");
            return;
        }

        version = args[0];
        //protocol = new Protocol(version);   // initiate protocol according to version

        //id = Integer.parseInt(args[1]);
        String remoteObjName = args[2];
        //Peer serverObj = new Peer();



        // start diskState

        Peer peer = new Peer(Integer.parseInt(args[1]), args[3], args[4], args[5], args[6], args[7], args[8]);

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


    private String generateFileId(String filePath) {
        String fileId = "";
        try {
            BasicFileAttributes fileMetadata = Files.readAttributes(Path.of(filePath), BasicFileAttributes.class);
            String fileOwner = Files.getOwner(Path.of(filePath)).toString();
            String hashMetadata = filePath + fileMetadata.creationTime() + fileMetadata.lastModifiedTime() + fileOwner;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(hashMetadata.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexStr = new StringBuilder();
            for(byte singleByte : encodedhash){
                String m = Integer.toHexString(0xff & singleByte);
                if(m.length() == 1)
                    hexStr.append('0');
                hexStr.append(m);
            }
            fileId = hexStr.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileId;
    }


    @Override
    public String backUp(String pathname, String degree){
        String result="Peer"+ id + ": received BACKUP request.";

        int replication_degree = Integer.parseInt(degree);
        if (replication_degree < 1 || replication_degree > 9) {
            throw new IllegalArgumentException("Invalid replication degree, must be 1 - 9.");
        }
        if (pathname.isEmpty()) {
            throw new IllegalArgumentException("Empty file path.");
        }

        File file = new File(pathname);
        if (!file.exists()){
            throw new IllegalArgumentException("Empty file path.");
        }

        //storage.fileManager.readFileIntoChunks(pathname);
        Path path = Paths.get(pathname);
        Path absolutePath = path.toAbsolutePath();

        int chunk_no = 0;
        Chunk chunk = new BackupChunk();
        //ConcurrentHashMap<String, Chunk> chunkMap = new ConcurrentHashMap<>();
        byte[] buffer = new byte[MAX_SIZE_CHUNK]; //TEM QUE SER MENOS

        String fileId = generateFileId(pathname);

        try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(String.valueOf(absolutePath)))) {
            int size;
            while((size = stream.read(buffer)) > 0){
                Header header = new Header("1.0", "PUTCHUNK", this.id, fileId, chunk_no, replication_degree);
                //chunk = new BackupChunk(Arrays.copyOf(buffer, size), size);
                chunk = new BackupChunk(fileId + chunk_no, size, replication_degree, 0, Arrays.copyOf(buffer, size));
                //chunkMap.put(fileId + "_" + chunk_no, chunk);

                //send chunk
                PutchunkMessage message = new PutchunkMessage(header, chunk, multichannelsbackup.getMulticastAddress(), multichannelsbackup.getMulticastPort());
                BackupTask backupTask = new BackupTask(message);
                backupTask.run();

                System.out.println(message.header.toString());

                chunk_no++;
                buffer = new byte[MAX_SIZE_CHUNK];
            }

            //check if needs 0 size chunk
            if(chunk.getSize() == MAX_SIZE_CHUNK) {
                // If the file size is a multiple of the chunk size, the last chunk has size 0.
                Header header = new Header("1.0", "PUTCHUNK", id, fileId, chunk_no, replication_degree);
                //chunk = new BackupChunk(new byte[0], 0);
                chunk = new BackupChunk(fileId + chunk_no, size, replication_degree, 0, Arrays.copyOf(buffer, size));

                //send chunk
                PutchunkMessage message = new PutchunkMessage(header, chunk, multichannelsbackup.getMulticastAddress(), multichannelsbackup.getMulticastPort());
                BackupTask backupTask = new BackupTask(message);
                backupTask.run();

                //chunkMap.put(fileId + "_" + chunk_no, chunk);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        return result;

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

        String result = "Peer" + this.id + ": received STATE request.";

        return result;

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
