package test;

import rmi.RemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class TestApp {
    
    public static void main(String[] args) throws NotBoundException {
        // check arguments
        if (args.length > 4 || args.length < 2) {
            System.err.println("Invalid number of args.");
            System.err.println("Usage: java test.TestApp <peer_access_point> <sub_protocol> [<opnd_1> <opnd_2>]");
            System.exit(1);
        }

        String remote_object_name = args[0].trim();

        try{
            Registry registry = getRegistry("localhost");
            RemoteInterface server = (RemoteInterface) registry.lookup(remote_object_name);

            String serverResponse = "";

            switch (args[1]) {
                case "BACKUP":
                    if (args.length != 4) {
                        System.err.println("Invalid number of arguments for BACKUP protocol");
                        System.err.println("Usage: java test.TestApp " + args[0] + " BACKUP <filepath> <desired replication degree>");
                        System.exit(2);
                    }
                    System.out.println("Requesting backup of file: " + args[2] + " with a replication degree of " + args[3] + ".");
                    serverResponse = server.backUp(args[2], args[3]);
                    break;
                case "RESTORE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RESTORE protocol");
                        System.err.println("Usage: java test.TestApp " + args[0] + " RESTORE <original filepath>");
                        System.exit(3);
                    }
                    System.out.println("Requesting restoration of file: " + args[2] + ".");
                    serverResponse = server.restore(args[2]);
                    break;
                case "DELETE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for DELETE protocol");
                        System.err.println("Usage: java test.TestApp " + args[0] + " DELETE <original filepath>");
                        System.exit(4);
                    }
                    System.out.println("Requesting deletion of file: " + args[2] + ".");
                    serverResponse = server.delete(args[2]);
                    break;
                case "RECLAIM":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RECLAIM protocol");
                        System.err.println("Usage: java test.TestApp " + args[0] + " RESTORE <maximum storage space>");
                        System.exit(5);
                    }
                    System.out.println("Changing available space to: " + args[2] + " KB.");
                    serverResponse = server.reclaim(Integer.parseInt(args[2]));
                    break;
                case "STATE":
                    if (args.length != 2) {
                        System.err.println("Invalid number of arguments for STATE protocol");
                        System.err.println("Usage: java test.TestApp " + args[0] + " STATE");
                        System.exit(6);
                    }
                    System.out.println(server.state());
                    break;
                default:
                    System.err.println("Unknown command: " + args[2] + ". Known commands: BACKUP, DELETE, RESTORE, RECLAIM, STATE.");
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
