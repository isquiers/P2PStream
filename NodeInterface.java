import java.rmi.*;
public interface Node extends Remote{

// public void join(String masterIP)

// public void leave()

public DataBlock requestData(String feederIP, int dataOffset) throws RemoteException;

public String updateMaster(String masterIP, String deadNodeIP) throws RemoteException;

// public createNetwork()

public Arraylist<String> updateIndexCache(String masterIp);

public Arraylist<String> requestCache();

public play();

}
