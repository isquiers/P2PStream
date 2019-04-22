import java.rmi.*;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface NodeInterface extends Remote {


  // public void join(String masterIP)

  // public void leave()

  // public DataBlock requestData(int dataOffset) throws RemoteException;

  // master recieves node to remove and returns the new provider in the chain
  // public String updateMaster(String senderIp, String deadNodeIP) throws RemoteException;
  //
  // // public createNetwork()
  //
  // public String join(String myIp) throws RemoteException;
  //
  // public ArrayList<String> updateIndexCache(String masterIp);
  //
  // public ArrayList<String> requestCache();

  public int add(int x,int y)throws RemoteException;


}
