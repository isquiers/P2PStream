// //Questions for Sean about Java RMI.
// //1. Differnt interface for node and master ##
//   //dont really need to
// //2. questions abut different java files
// //3. remote interface vs interface.
// //4. client server on same Node
//     //threads will work for this.
// //5. data structure for branching chain
//   //Do a wierd tree.
// //7. suggestions on modeling bitrate both from client and for "streaming"
//
// //package com.technicalkeeda.app;
// import java.net.InetAddress;
// import java.lang.*;
// import java.util.*;
// public class Node {
//   ArrayList<ArrayList<Long>> dataBlock;
//   ArrayList<String> nodeIndex;
//   Integer maxSize = 250000;
//   Integer dataBlockSize;
//   Integer cacheSize;
//   String masterIp;
//   String selfIp;
//   String currProvider;
//   String filename;
//   boolean isMaster = false;
//   Integer currOffset = 0;
//
//   public static void main(String[] args) {
//     // [dataBlockSize][cacheSize][MasterIp][*Optional Content filename]
//     //
//     this.dataBlockSize = Integer.ParseInt(args[0]);
//     this.cacheSize = Integer.ParseInt(args[1]);
//     this.masterIp = Integer.ParseInt(args[2]);
//     InetAddress inetAddress = InetAddress.getLocalHost();
//     this.selfIp = inetAddress.getHostAddress();
//
//     if (args.length == 4) {
//       isMaster = true;
//       filename = Integer.ParseInt(args[3]);
//       Thread t1 = new Thread(createNodeIndex);
//       t1.start();
//       // start thread that loadscache
//     } else {
//       this.currProvider = join();
//       Thread t2 = new Thread(startRequestingData);
//       t2.start();
//     }
//     Thread t3 = new Thread(playFromCache);
//     t3.start();
//
//     try{
//       NodeInterface stub=new NodeInterfaceRemote();
//       Naming.rebind("rmi://localhost:8696/sonoo",stub);
//     }
//       catch(Exception e){System.out.println(e);}
//     }
//
//   // viewer function: loop while the currProvier is still responding
//   //                  if theres is error or stopage of response we need to
//   //                  contact the master and get an new reciever, also
//   //                  maybe respond to new requests with a wait status...
//   public void startRequestingData() {
//     //first data request
//     while(this.currProvider != "") {
//       try {
//         NodeFunctions stub = (NodeFunctions)Naming.lookup(this.currProvider);
//         DataBlock nextDB = stub.requestData(currOffset);
//         // put DB into cache
//       }
//       catch(Exception e) {
//         System.out.println(e);
//         System.out.println("Error with Provider, resetting");
//         this.currProvider = "";
//       }
//     }
//   }
//
// // master function: get IP of a dead node and reassign the currnode provided
// //                  a new servicer (for now going one higher up the tree)
//   public String updateMaster() {
//   }
//
// // viewer function: play the
//   public void playFromCache() {
//
//   }
//
//   public DataBlock requestData(String feederIP, int dataOffset) throws RemoteException;
//
//   public String updateMaster(String masterIP, String deadNodeIP) throws RemoteException;
//
//   void createNodeIndex() {
//     this.nodeIndex = new ArrayList<String>();
//   //only for masterIP
//   void loadCache() {
//     // can use this.cache
//   }
//
//   public Arraylist<String> updateIndexCache(String masterIp);
//
//   public Arraylist<String> requestCache();
//
//   public play();
//
//   public setDataBlockSize(int dataBlockSize){
//     this.dataBlockSize = dataBlockSize;
//   }
//
//   public setCacheSize(int cahceSize){
//     this.cacheSize = cacheSize;
//   }
//
//   void createDataBlock(){
//     dataBlock = new ArrayList<ArrayList<Long>>(dataBlockSize);
//     for(ArrayList<Long> array: dataBlock){
//       array = new ArrayList<Long>(maxSize);
//     }
//   }
//
// }
