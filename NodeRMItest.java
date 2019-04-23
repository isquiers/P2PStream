//Questions for Sean about Java RMI.
//1. Differnt interface for node and master ##
  //dont really need to
//2. questions abut different java files
//3. remote interface vs interface.
//4. client server on same Node
    //threads will work for this.
//5. data structure for branching chain
  //Do a wierd tree.
//7. suggestions on modeling bitrate both from client and for "streaming"
import java.net.InetAddress;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;


public class NodeRMItest implements NodeInterface{
  ArrayList<ArrayList<Long>> dataBlock;
  ArrayList<String> nodeIndex;
  Integer maxSize = 250000;
  Integer dataBlockSize;
  Integer cacheSize;
  String masterIp;
  String selfIp;
  String currProvider;
  String filename;
  boolean isMaster = false;
  Integer currOffset = 0;

  NodeRMItest()throws RemoteException {
    super();
  }

  public String join(String newIp) {
    System.out.println("Here is the IP " + newIp);
    return newIp;
  }


  public static void main(String[] args) {
    // [dataBlockSize][cacheSize][MasterIp][*Optional Content filename]
    if (args[0] == "1") {
      try{
        NodeRMItest Node = new NodeRMItest();
        NodeInterface stub = (NodeInterface) UnicastRemoteObject.exportObject(Node, 0);
        Registry registry =  LocateRegistry.createRegistry(8005);
        registry.rebind("TestNode", stub);

        System.err.println("Server ready");

        //Naming.rebind("rmi://localhost:8005/sonoo",stub);
      }
        catch(Exception e) {
          System.out.println(e);
        }
    } else {
      try {
        // NodeInterface stub = (NodeInterface) Naming.lookup("rmi://54.209.66.61:8005/sonoo");
        // String cats = stub.join("cats cats");
        // System.out.println(cats);
        // put DB into cache
        Registry registry = LocateRegistry.getRegistry(8005);
        NodeInterface stub = (NodeInterface) registry.lookup("TestNode");
        String response = stub.join("cats cats");
        System.out.println("response: " + response);
      }
      catch(Exception e) {
        System.out.println(e);
        System.out.println("Error with Provider, resetting");
      }
    }
  }

}
