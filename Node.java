//Questions for Sean about Java RMI.
//1. Differnt interface for node and master
//2. questions abut different java files
//3. remote interface vs interface.
//4. client server on same Node
//5. data structure for branching chain
//6. linked list acceptable
//7. suggestions on modeling bitrate both from client and for "streaming"
//8 . client server on different threads or different processes.

package com.technicalkeeda.app;
import java.net.InetAddress;

public class Node extends UnicastRemoteObject implements NodeInterface{

  Integer dataBlockSize;
  Integer cacheSize;
  ArrayList<Object> cache;
  String masterIp;
  String selfIp;
  String filename;
  boolean isMaster = false;

  public static void main(String[] args) {
    // [dataBlockSize][cacheSize][MasterIp][*Optional Content filename]
    //
    this.dataBlockSize = Integer.ParseInt(args[0]);
    this.cacheSize = Integer.ParseInt(args[1]);
    this.masterIp = Integer.ParseInt(args[2]);
    InetAddress inetAddress = InetAddress.getLocalHost();
    this.selfIp = inetAddress.getHostAddress();

    if (args.length == 4) {
      isMaster = true;
      filename = Integer.ParseInt(args[3]);
      Thread thread1(loadCache);
      // start thread that loadscace
    } else {
      Thread thread2(startRequestingData);
    }
    Thread thread2(playFromCache);

    try{
      NodeInterface stub=new NodeInterfaceRemote();
      Naming.rebind("rmi://localhost:5000/sonoo",stub);
    }
      catch(Exception e){System.out.println(e);}
    }

  }

  public void startRequestingData() {

  }

  public void playFromCache() {

  }

  public DataBlock requestData(String feederIP, int dataOffset) throws RemoteException;

  public String updateMaster(String masterIP, String deadNodeIP) throws RemoteException;

  //only for masterIP
  void loadCache() {
    // can use this.cache
  }

  public Arraylist<String> updateIndexCache(String masterIp);

  public Arraylist<String> requestCache();

  public play();

  public setDataBlockSize(int dataBlockSize){
    this.dataBlockSize = dataBlockSize;
  }

  public setCacheSize(int cahceSize){
    this.cacheSize = cacheSize;
  }

  public setMasterIp(String masterIp){
    this.masterIp = masterIp;
  }



}
