package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{

    /**
     * Back Up protocol Initiator Interface
     * @param pathname path of file to backup
     * @param replication_degree replication degree desired for file
     * @return result string
     * @throws RemoteException if any RMI error occurs, throws exception
     */
    public String backUp(String pathname, String replication_degree) throws RemoteException;

    /**
     * Restore Protocol Initiator Interface
     * @param pathname path of file to restore
     * @return result string
     * @throws RemoteException if any RMI error occurs, throws exception
     */
    public String restore(String pathname) throws RemoteException;

    /**
     * Delete Protocol Initiator Interface
     * @param pathname path of file to delete
     * @return result string
     * @throws RemoteException if any RMI error occurs, throws exception
     */
    public String delete(String pathname) throws RemoteException;

    /**
     * Reclaim Protocol Initiator Interface
     * @param maxDiskSpace maximum space limit in KBytes allowed for peer
     * @return result string
     * @throws RemoteException if any RMI error occurs, throws exception
     */
    public String reclaim(int maxDiskSpace) throws RemoteException;

    /**
     * State Protocol Initiator Interface
     * @return result string containing state
     * @throws RemoteException if any RMI error occurs, throws exception
     */
    public String state() throws RemoteException;
}