package rmi;

import java.rmi.RemoteException;

import java.rmi.Remote;

public interface RemoteInterface extends Remote{


    public String backUp(String pathname, String degree) throws RemoteException;

    public String restore(String pathname) throws RemoteException;

    public String delete(String pathname) throws RemoteException;

    public String reclaim(int maxDiskSpace) throws RemoteException;

    public String state() throws RemoteException;
    

}