import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{


    String backUp(String pathname, String degree) throws RemoteException;

    String restore(String pathname) throws RemoteException;

    String delete(String pathname) throws RemoteException;

    String reclaim(int maxDiskSpace) throws RemoteException;

    String state() throws RemoteException;
    

}