public class Peer implements RemoteInterface{
    
    public String version;
    public int id;

    public static void main(String args[]) throws RemoteException {

        if (args.length != 3) {
            System.out.println("Usage: java Peer <protocol_version> <peer_id> <remote_object_name>");
            return;
        }

        this.version = args[1];
        this.id=args[2];
        String remoteObjName = args[3];
        Server serverObj = new Server();

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
    public String backUp(Request r){

        String result="";


        return result;

    }

    @Override
    public String delete(Request r) {

        String result = "";

        return result;

    }

    @Override
    public String restore(Request r) {

        String result = "";

        return result;

    }


    @Override
    public String reclaim(Request r) {

        String result = "";

        return result;

    }

    @Override
    public String state() {

        String result = "";

        return result;

    }
}
