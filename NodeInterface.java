import java.rmi.*;
public interface NodeInterface extends Remote {

  // public void leave()

  // public DataBlock requestData(int dataOffset) throws RemoteException;

  // master recieves node to remove and returns the new provider in the chain
  // public String updateMaster(String senderIp, String deadNodeIP) throws RemoteException;
  //
  // // public createNetwork()
  //
  public String join(String myIp) throws RemoteException;
  //
  // public ArrayList<String> updateIndexCache(String masterIp);
  //
  // public ArrayList<String> requestCache();

}
