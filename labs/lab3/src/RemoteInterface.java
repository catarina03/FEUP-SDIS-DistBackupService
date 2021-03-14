import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
    
    String register(String name, String ip) throws RemoteException;
    String lookup(String name) throws RemoteException;
    
}
