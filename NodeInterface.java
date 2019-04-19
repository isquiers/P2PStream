import java.rmi.*;
public interface Node extends Remote{

// public void join(String masterIP)

// public void leave()


public DataBlock requestData(String feederIP) throws RemoteException;

public void sendData(Object dataBlock) throws RemoteException;

public String updateMaster(String masterIP, String deadNodeIP) throws RemoteException;

public String getMasterIndex(String masterIp) throws RemoteException;

public play();

}
