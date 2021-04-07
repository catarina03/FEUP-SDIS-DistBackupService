import rmi.RemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class that represents the client that can communicate with a peer and test its services
 */
public class TestApp {
    /**
     * Main method.
     * @param args command line arguments
     * @throws NotBoundException
     */
    public static void main(String[] args) throws NotBoundException {
        // check arguments
        if (args.length > 4 || args.length < 2) {
            System.err.println("Invalid number of arguments, correct usage:\njava TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            System.exit(1);
        }

        String remote_object_name = args[0].trim();

        try{
            Registry registry = getRegistry("localhost");
            RemoteInterface server = (RemoteInterface) registry.lookup(remote_object_name);

            String serverResponse = "";

            // TODO: change strings
            switch (args[1]) {
                case "BACKUP":
                    if (args.length != 4) {
                        System.err.println("Invalid number of arguments for BACKUP protocol,\njava TestApp " + args[0] + " BACKUP <filepath> <desired replication degree>");
                        System.exit(2);
                    }
                    System.out.println(String.format("Requesting backup of file: %s with a replication degree of %d", args[2], Integer.parseInt(args[3])));
                    serverResponse = server.backUp(args[2], args[3]);
                    break;
                case "RESTORE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RESTORE protocol,\njava TestApp " + args[0] + " RESTORE <original filepath>");
                        System.exit(3);
                    }
                    System.out.println(String.format("Requesting restoration of file: %s", args[2]));
                    serverResponse = server.restore(args[2]);
                    break;
                case "DELETE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for DELETE protocol,\njava TestApp " + args[0] + " DELETE <original filepath>");
                        System.exit(4);
                    }
                    System.out.println(String.format("Requesting deletion of file: %s", args[2]));
                    serverResponse = server.delete(args[2]);
                    break;
                case "RECLAIM":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RECLAIM protocol,\njava TestApp " + args[0] + " RESTORE <maximum storage space>");
                        System.exit(5);
                    }
                    System.out.println(String.format("Changing available space to: %d KB", Integer.parseInt(args[2])));
                    serverResponse = server.reclaim(Integer.parseInt(args[2]));
                    break;
                case "STATE":
                    if (args.length != 2) {
                        System.err.println("Invalid number of arguments for STATE protocol,\njava TestApp " + args[0] + " STATE");
                        System.exit(6);
                    }
                    System.out.println(server.state());
                    break;
                default:
                    System.err.println("Invalid action:" + args[2]);

            }

            System.out.println(serverResponse);

        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    private static Registry getRegistry(String host) {
        Registry registry = null;

        try {
            boolean execute = true;
            boolean notExecute = false;

            if (execute) {
                registry = LocateRegistry.getRegistry(host);
            }

            if (notExecute) {
                registry = LocateRegistry.createRegistry(3000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return registry;
    }
}
