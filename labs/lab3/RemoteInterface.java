import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
    
    String register(Request r) throws RemoteException;
    String lookup(Request r) throws RemoteException;
    
}
