import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{


    String backUp(Request r) throws RemoteException;

    String restore(Request r) throws RemoteException;

    String delete(Request r) throws RemoteException;

    String reclaim(Request r) throws RemoteException;

    String state() throws RemoteException;

}