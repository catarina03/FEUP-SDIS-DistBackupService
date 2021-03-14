import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {
    }

    public static void main(String[] args) {

        if (args.length > 5) {
            System.out.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd>*");
            return;
        }

        String host = args[0];
        String remote_object_name = args[1];

        Request r = parser(args);
        if (r.operation.equals("")) {
            System.out.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd>*");
            return;
        }

        try {
            /*
            String policyFilePath = "file:///C:/temp/rmicodebase/my.policy";
            System.setProperty("java.security.policy", policyFilePath);

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            */

            Registry rmiRegistry = getRegistry(host);

            RemoteInterface stub = (RemoteInterface) rmiRegistry.lookup(remote_object_name);

            String response = null;
            if (r.operation.equals("REGISTER")) {
                System.out.println("Client : " + r.operation + " " + r.name + " " + r.ip_address + " .\n");
                response = stub.register(r.name, r.ip_address);
            }
            if (r.operation.equals("LOOKUP")) {
                System.out.println("Client : " + r.operation + " " + r.name + " .\n");
                response = stub.lookup(r.name);
            }

            System.out.println("Server response: " + response + ". \n\n");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static Request parser(String[] args) {
        Request r = new Request();
        if (args[2].equals("REGISTER")) { // REGISTER operation
            r.operation = args[2];
            r.name = args[3];
            r.ip_address = args[4];
            return r;
        } else if (args[2].equals("LOOKUP")) { // LOOKUP operation
            r.operation = args[2];
            r.name = args[3];
            return r;
        } else {
            r.operation = "";
            return r;
        }
    }

    // Private Methods

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