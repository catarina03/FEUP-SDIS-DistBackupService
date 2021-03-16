import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements RemoteInterface{
    
    public static String version;
    public static int id;

    public Peer() {
        version="1.0";
        id=0;
    }

    public static void main(String args[]) throws RemoteException {

        if (args.length != 3) {
            System.out.println("Usage: java Peer <protocol_version> <peer_id> <remote_object_name>");
            return;
        }

        version = args[0];
        id = Integer.parseInt(args[1]);
        String remoteObjName = args[2];
        Peer serverObj = new Peer();

        Registry registry = getRegistry();

        try {

            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(serverObj, 4000);

            registry.rebind(remoteObjName, stub);

            System.out.println("Peer is running. " + remoteObjName + " listening at port 4000.\n");

        } catch (Exception e) {
            System.err.println("Peer Exception : " + e.toString());
            e.printStackTrace();
        }
    }


    @Override
    public String backUp(String pathname, String degree){

        String result="Peer"+ this.id + ": received BACKUP request.";


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
