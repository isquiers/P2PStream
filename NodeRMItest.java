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

package com.technicalkeeda.app;
import java.net.InetAddress;

public class Node {
  ArrayList<ArrayList<long>> dataBlock;
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

  public static void main(String[] args) {
    // [dataBlockSize][cacheSize][MasterIp][*Optional Content filename]
    //
    if (args[0] == "1") {
      try{
        NodeInterface stub=new NodeInterfaceRemote();
        Naming.rebind("rmi://localhost:5000/sonoo",stub);
      }
        catch(Exception e){System.out.println(e);}
      while (true) {

      }
    } else {
      try {
        NodeFunctions stub = (NodeFunctions)Naming.lookup("rmi://54.209.66.61:5000/sonoo");
        DataBlock nextDB = stub.join("cats cats");
        // put DB into cache
      }
      catch(Exception e) {
        System.out.println(e);
        System.out.println("Error with Provider, resetting")
      }
    }
  }

  // viewer function: loop while the currProvier is still responding
  //                  if theres is error or stopage of response we need to
  //                  contact the master and get an new reciever, also
  //                  maybe respond to new requests with a wait status...
