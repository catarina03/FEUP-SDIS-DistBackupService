package peer;

import rmi.*;

import java.rmi.RemoteException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.net.*;

public class Peer implements RemoteInterface{
    
    public static String version;
    public Protocol protocol;
    public static int id;
    public static DiskState storage;

    public Peer() {
        version="1.0";
        id=0;
    }

    public static void main(String args[]) throws RemoteException, IOException {

        if (args.length != 9) {
            System.out.println("Usage: java Peer <protocol_version> <peer_id> <remote_object_name> <MC_Address> <MC_Port> <MDB_Address> <MDB_Port> <MDR_Address> <MDR_Port>");
            return;
        }

        version = args[0];
        //protocol = new Protocol(version);   // initiate protocol according to version
        id = Integer.parseInt(args[1]);
        String remoteObjName = args[2];
        Peer serverObj = new Peer();

        String multicastControlAddress = args[3];
        String multicastControlPort = args[4];
        String multicastDataBackupAddress = args[5];
        String multicastDataBackupPort = args[6];
        String multicastDataRestoreAddress = args[7];
        String multicastDataRestorePort = args[8];

        // start diskState
        storage = new DiskState(id);

        // TODO: check state and update every few secs to keep storage updated



        // connect to RMI
        Registry registry = getRegistry();

        try {

            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(serverObj, 4000);

            registry.rebind(remoteObjName, stub);

            System.out.println("Peer is running. " + remoteObjName + " listening at port 4000.\n");

        } catch (Exception e) {
            System.err.println("Peer Exception : " + e.toString());
            e.printStackTrace();
        }


        //connect to MC channel
        PeerMultiThreadControl multichannelscontrol = new PeerMultiThreadControl(args[1], version, multicastControlAddress, multicastControlPort,10);
        new Thread(multichannelscontrol).start();

        //connect to MDB channel
        PeerMultiThreadBackup multichannelsbackup = new PeerMultiThreadBackup(args[1], version, multicastDataBackupAddress,
                multicastDataBackupPort,10);
        new Thread(multichannelsbackup).start();

        //connect to MDR channel
        PeerMultiThreadRestore multichannelsrestore = new PeerMultiThreadRestore(args[1], version, multicastDataRestoreAddress,
                multicastDataRestorePort,10);
        new Thread(multichannelsrestore).start();
    }


    @Override
    public String backUp(String pathname, String degree){

        String result="Peer"+ this.id + ": received BACKUP request.";

        File file = new File(pathname);

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
