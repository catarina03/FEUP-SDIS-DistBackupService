
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;


public class Server implements RemoteInterface {

    private DNSService dnsService = null;

    public Server() {
        dnsService = new DNSService();
    }

    @Override
    public String register(Request r) throws RemoteException{

        System.out.println("The register method was invoked with " + r.name + " and " + r.ip_address + " .\n");
        String response = dnsService.addEntry(r);
        System.out.println(response);
        return response;

    }


    @Override
    public String lookup(Request r){
        
        System.out.println("The lookup method was invoked with " + r.name + " .\n");
        String response = dnsService.getEntry(r);
        System.out.println(response);
        return response;

    }


    public static void main(String args[]) throws RemoteException{

        if(args.length != 1){
            System.out.println("Usage: java Server <remote_object_name>");
            return;
        }

        String remoteObjName = args[0];

        Server serverObj = new Server();

        setVMProperties();

        Registry registry = getRegistry();

        try{

            RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(serverObj, 4000);

            registry.rebind(remoteObjName, stub);

            System.out.println("Server is running. " + remoteObjName + " listening at port 4000.\n");

        }catch (Exception e){
            System.err.println("Server Exception : " + e.toString());
            e.printStackTrace();
        }
    }


    // Private Methods

    private static void setVMProperties(){
        setPolicy();

        // apenas necessário se corrermos o rmiregistry fora do diretório de execução aka. o rmiregistry assume que o diretório em que corre é a codebase
        // String codebase = RemoteInterface.class.getProtectionDomain().getCodeSource().getLocation().toString();     //FIXME: no permissions
        String codebase = "file:///C:/Users/shit/OneDrive/Documentos/Faculdade/3ºano/SDIS/MIEIC-SDIS/lab3/classDir/";
        System.setProperty("java.rmi.server.codebase", codebase);

        String useCodebaseOnly = "false";
        System.setProperty("java.rmi.server.useCodebaseOnly", useCodebaseOnly);
    }

    private static void setPolicy(){

        String policyFilePath = "file:///C:/temp/rmicodebase/my.policy";

        // System.setProperty("java.security.policy", policyFilePath);
        if(System.getSecurityManager()==null){
            System.setSecurityManager(new SecurityManager());
        }
    }

    private static Registry getRegistry(){
        Registry registry = null;

        try{
            boolean execute = true;
            boolean notExecute = false;

            if(execute){
                registry = LocateRegistry.getRegistry();
            }

            if(notExecute){
                registry = LocateRegistry.createRegistry(3000);
            }

        }catch(RemoteException e){
            e.printStackTrace();
        }

        return registry;
    }
}