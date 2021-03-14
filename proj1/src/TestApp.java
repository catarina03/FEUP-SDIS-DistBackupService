/**
 * Class that represents the client that can communicate with a peer and test its services
 */
public class TestApp {
    /**
     * Main method.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // check arguments
        if (args.length > 4 || args.length < 2) {
            System.err.println("Invalid number of arguments, correct usage:\njava TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            System.exit(1);
        }

        String remote_object_name = args[0];

        try{
            Registry registry = LocateRegistry.getRegistry();
            RemoteInterface server = (RemoteInterface) registry.lookup(remote_object_name);

            switch (args[1]) {
                case "BACKUP":
                    if (args.length != 4) {
                        System.err.println("Invalid number of arguments for BACKUP protocol,\njava TestApp " + args[0] + " BACKUP <filepath> <desired replication degree>");
                        System.exit(2);
                    }
                    System.out.println(String.format("Requesting backup of file: %s with a replication degree of %d", args[2], Integer.parseInt(args[3])));
                    server.backUp(args[2], Integer.parseInt(args[3]));
                    break;
                case "RESTORE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RESTORE protocol,\njava TestApp " + args[0] + " RESTORE <original filepath>");
                        System.exit(3);
                    }
                    System.out.println(String.format("Requesting restoration of file: %s", args[2]));
                    server.restore(args[2]);
                    break;
                case "DELETE":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for DELETE protocol,\njava TestApp " + args[0] + " DELETE <original filepath>");
                        System.exit(4);
                    }
                    System.out.println(String.format("Requesting deletion of file: %s", args[2]));
                    server.delete(args[2]);
                    break;
                case "RECLAIM":
                    if (args.length != 3) {
                        System.err.println("Invalid number of arguments for RECLAIM protocol,\njava TestApp " + args[0] + " RESTORE <maximum storage space>");
                        System.exit(5);
                    }
                    System.out.println(String.format("Changing available space to: %d KB", Integer.parseInt(args[2])));
                    server.reclaim(Integer.parseInt(args[2]));
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
        } catch(RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
