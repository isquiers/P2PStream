/* This is a node that implements a simulate live stream by passsing arbitrary datablocks
 * at real time from a Node that considers itself the Master/Streamer.
 * Created By Sean Cork & Ian Squires
 *
 */

 /* This is a node that implements a simulate live stream by passsing arbitrary datablocks
  * at real time from a Node that considers itself the Master/Streamer.
  * Created By Sean Cork & Ian Squires
  *
  */

 import java.rmi.registry.Registry;
 import java.rmi.registry.LocateRegistry;
 import java.rmi.RemoteException;
 import java.rmi.server.UnicastRemoteObject;
 import java.net.InetAddress;
 import java.lang.*;
 import java.util.*;


 /* This class both acts as a Streamer and a viewer.
  *It dirrerentiats itself based on the command line arguments
  */

 public class Server implements Node {
   ArrayList<ArrayList<String>> nodeIndex = new ArrayList<ArrayList<String>>();
   public static String selfIp;

   //whose currently providing data
   public static String currProvider;
   public static boolean isMaster = false;
   public static String masterIp = "54.209.66.61";

   // chain in which the current node resides
   public static int currChainIndex = 0;
   public static String currDb;

   //logical clock value to see where in the datablock it is requesting.
   public static int logClock = 0;
   public static int currLogVal = 0;

   //might have some sort of cache dont know yet.
   public static Deque<String> dataQueue;
   public static int currClock;

   //Tolerable Threshold or maximum amount of time we are willing to have as the
   //delay from the orginal stream
   public static int msThreshold = 5000;
   public static int maxCache = 5;
   public static int missedDbs = 0;
   public static int startVal = 0;


   public Server() {}

 /*
 ######################### BEGIN REMOTE FUNCTIONS ###########################
 */

     //testing to simulate data being live streamed
     public String checkCounter() {
       String response;
       if(!selfIp.equals(masterIp)){
         if(dataQueue.size() > 1){
            response = dataQueue.peekLast();
          }
          else{
            response = dataQueue.peek();
          }
       }
       else{
         response = currDb;
       }
       //hello
       return response;
     }

     public void updateChain (int newChain) {
       currChainIndex = newChain;
     }

     /* Purpose: This function is executed once a Nodes delay from the live stream
      * passes a certain threshold. This node moves the node and any follwing nodes attached
      * to it to a new chain.
      * Parameters: The Ip of the node that wants to be moved and the Current chain index
      * Return Value: The string of the new IP that the chain is supposed to be representing.
      */
     public synchronized String moveNode(String mover, int currChain) {
       System.out.println("Node " + mover + " Requesting Move from Chain " + currChain);

       // index of node that is requesting a new Chain
       int moveMe = nodeIndex.get(currChain).indexOf(mover);
       if (moveMe == -1) {
         //return Failure
         System.out.println("Failed lookup when moving node");
       }
       //increment the currChain to indicate were are looking for a enw chain
       int newChainIndex = currChain + 1;

       //newchain will return null so we create new chain
       if (nodeIndex.size() == newChainIndex) {
         //add an arraylist and allocate new memory
         nodeIndex.add(new ArrayList<String>());
         //add the master Ip to the first value in the list
         nodeIndex.get(newChainIndex).add(masterIp);
         System.out.println("this is here");
       }

       //If we have already requested a new chain add the node requesting a move to the chain.
       String newProvider = nodeIndex.get(newChainIndex).get(nodeIndex.get(newChainIndex).size() - 1);
       System.out.println(newProvider + " this is new provider");
       String movingNode = nodeIndex.get(currChain).get(moveMe);

       System.out.println("This is moving node " + movingNode);

       // move all nodes downstream of requestor
       while (moveMe != nodeIndex.get(currChain).size()) {
         //add the first value to the new chain
         nodeIndex.get(newChainIndex).add(movingNode);
         //updates the current Chain index
         newChainAlert(movingNode, newChainIndex);
         nodeIndex.get(currChain).remove(moveMe); // remove them from the old chain
       }
       printIndex();
       return newProvider;
     }

     //Purpose: called imediatly after a node that is not the Master is activated.
     //this function joins a node to a chain.
     // Parameters: the ip of the node that wishes to join the stream.
     // return value: The Ip of the master or the node that is providing this new node
     // with its data
     public synchronized String join(String newIp) {
       System.out.println("Servicing join ################################ from " + newIp);

       //if it is the master
       if (nodeIndex.size() == 0) {
         //create first chain and add yourself to the nodeIndex
         ArrayList<String> newChain = new ArrayList<String>();
         nodeIndex.add(newChain);
         nodeIndex.get(0).add(newIp);
         printIndex();
         return masterIp;
       }
       // join always initiates you at the end of chain 0,
       //assuming you will request a change if it's necessary
       String newProvider = nodeIndex.get(0).get(nodeIndex.get(0).size()-1);
       nodeIndex.get(0).add(newIp);
       //print the chains and their contents
       printIndex();
       return newProvider;
     }

     //Purpose: removeNode that is in the chainList and is unresponsive
     //Parameters: a string that is the IP of the dead node, and the chain
     // it is located in
     // return value: A string representing the node that the new node will
     // connect to.
     public synchronized String removeNode(String deadNode, int chain) {
       //Debug
       System.out.println("Node removal requested");
       //index of node that we want to remove
       int removal = nodeIndex.get(chain).indexOf(deadNode);
       //if the node is actually going to get removed
       if (removal != -1) {
         System.out.println("removing node " + nodeIndex.get(chain).get(removal));
         nodeIndex.get(chain).remove(removal);
         //this means that remove node is getting called from master
         if(removal == 0) {
           return masterIp;
         }
         System.out.println("returing node " + nodeIndex.get(chain).get(removal - 1));
         printIndex();
         return nodeIndex.get(chain).get(removal - 1);
       } else {
         System.out.println("Node not Found");
         return "Error";
       }
     }


 /*
 ######################### END REMOTE FUNCTIONS ###########################
 */

     //Purpose: This is the main loop that goes thru and starts the necessary
     // functions for the node to be either a streamer or viewer
     //Parameters: command line arguments
     //Return value: none
     public static void main(String args[]) {

       //if the 2 Ip adresses given are the same its the master
       if (args[0].toString().equals(args[1].toString())) {
         System.err.println("is master");
         isMaster = true;
       }

       // command line args [masterIp][selfIp]
       masterIp = args[0].toString();
       selfIp = args[1].toString();

       //it starts the server
       startServer();

       //If the node is the master start the stream and add yourself to
       //the index of nodes that are currently runnign
       if (isMaster) {
         requestJoin();
         //thread to run in backround
         Thread t1 = new Thread(new Runnable() {
           public void run() {
             createAndStream();
           }
         });
         //updateFromFile();
         t1.start();
       } else {
         //You are a viewer and you want to join the stream
         createQueue();
         requestJoin();
         requestData();
       }
     }


     //Purpose: Changes the currChain index in the master
     //Parameters: the recieving node, and the new chain index
     //return value: none
     private static void newChainAlert(String recievingNode, int newChainIndex) {
       try {
         Registry registry = LocateRegistry.getRegistry(recievingNode, 8699);
         Node stub = (Node) registry.lookup("Node");
         stub.updateChain(newChainIndex);
       } catch (Exception e) {
           System.err.println("Client exception: " + e.toString());
           e.printStackTrace();
       }
     }


     //Purpose: lets the viewer request a new content provider if their current
     // one is unresponsive
     //Parameters: none
     //Return value: none
     private static void requestNewProvider() {
       System.out.println("Requesting new Provider other than " + currProvider);
       try {
         Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
         Node stub = (Node) registry.lookup("Node");

         //remove yourself and reset current provider.
         String response = stub.removeNode(currProvider, currChainIndex);
         currProvider = response;
         System.out.println("New Provider Accepted By Master, Provider Set To: " + currProvider);
       } catch (Exception e) {
         masterFailure();
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
       }
     }
     //Purpose: request a new chain if youre latency is to high
     //parameters: none
     //return value: none
     private static void requestNewChain() {
       try {
         Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
         Node stub = (Node) registry.lookup("Node");
         //move the node and children to new chain
         String response = stub.moveNode(selfIp, currChainIndex);
         currProvider = response;

       } catch (Exception e) {
         masterFailure();
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
       }
     }



     //Purpose: Create and Stream data datablock
     //Parameters: none
     //Return value: whether it succeded or not
     private static int createAndStream() {
       TimerTask repeatedTask = new TimerTask() {
         public void run() {
           currDb = createDb();
           System.err.println(currDb.substring(0,20));
         }
       };
       Timer timer = new Timer();
       timer.scheduleAtFixedRate(repeatedTask, 1000, 1000);
       return 1;
     }


     //Purpose: ask the provider for the data in the stream
     //Parameters: none
     //Return Value: none
     private static void requestData() {
       //time the requesting of data every second.
       TimerTask repeatedTask = new TimerTask() {
         public void run() {
           String host = currProvider;
           try {
             Registry registry = LocateRegistry.getRegistry(host, 8699);
             Node stub = (Node) registry.lookup("Node");
             //get the arbitrary data block
             System.out.println("here");
             String dBlock = stub.checkCounter();
             if (dataQueue.size() > maxCache) {
               dataQueue.removeFirst();
             }
             dataQueue.add(dBlock);
             //get timestamp and offset
             String[] timestamp = dBlock.split(",");
             Long timestampMills = Long.parseLong(timestamp[1]);
             int logVal = Integer.parseInt(timestamp[0]);
             Long offset = System.currentTimeMillis() - timestampMills;
             printCache();
             System.out.println("Curr Log Value " + logVal + " -- Offset in milleseconds: " + offset + "\n\n\n");
             if (startVal == 0) {
               startVal = logVal;
               currLogVal = logVal;
             }
             else {
               if (logVal != currLogVal + 1) {
                 System.out.println("MISSED DATABLOCK");
                 missedDbs += 1;
               }
             }
             currLogVal = logVal;
             //if the offset or latency is to bad request a new chain
             if ((offset > msThreshold) && (!currProvider.equals(masterIp))) {
               requestNewChain();
             }
           } catch (Exception e) {
               System.err.println("Client exception: " + e.toString());
               // e.printStackTrace();
               System.out.println("request Data failed, requesting new Provider");
               requestNewProvider();
           }
         }
       };
       Timer timer = new Timer();
       timer.scheduleAtFixedRate(repeatedTask, 1000, 1000);
     }
     //Purpose: request to join to server is used to execute join on the master
     //Parameters: none
     //Return value: none
     private static void requestJoin() {
       System.out.println("Requesting Join");
       try {
         Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
         Node stub = (Node) registry.lookup("Node");
         //executre join on masterIp
         String response = stub.join(selfIp);
         //set your current provider
         currProvider = response;
         //if your current provider is yourself delete the node and get a new provider
         //this happens whena node leaves and rejoins the stream
         if(currProvider.equals(selfIp) && !currProvider.equals(masterIp)) {
           requestNewProvider();
         }
         System.out.println("Join Accepted By Master, Provider Set To: " + currProvider);
       }
       catch (Exception e) {
         masterFailure();
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
       }
     }


     private static Server obj;
     //Purpose: starts server
     private static void startServer() {
       try {
           obj = new Server();
           Node stub = (Node) UnicastRemoteObject.exportObject(obj, 8699);
           // Bind the remote object's stub in the registry
           Registry registry = LocateRegistry.createRegistry(8699);
           registry.bind("Node", stub);
           System.err.println("Server ready");
       } catch (Exception e) {
           System.err.println("Server exception: " + e.toString());
           e.printStackTrace();
       }
     }

     public static void setMaster(boolean a) {
       isMaster = a;
     }

     //Purpose: print the chain index to help with visualization of whats going on.
     public void printIndex() {
       for (int i = 0; i < nodeIndex.size(); i++) {
         System.out.println(" Chain index " + i + ": ");
         for (int j = 0; j < nodeIndex.get(i).size(); j++) {
           System.out.print(" Node index " + j + ": ");
           System.out.print(nodeIndex.get(i).get(j) + " ");
         }
         System.out.print("\n");
       }
     }

     public static void masterFailure() {
       System.err.println("FATAL ERROR: Master Failure To Respond");
       int time = currLogVal - startVal;
       System.out.println("misssed " + missedDbs + " out of " + time);
       double missrate = (double) missedDbs / time;
       System.out.println("MISSRATE = " + (missedDbs/time));
       System.exit(0);
     }

     public static void createQueue(){
       dataQueue = new LinkedList<String>();
     }

     //Purpose: creating arbitrary block to be able to send
     public static String createDb() {
       logClock += 1;
       Long time = System.currentTimeMillis();
       char[] garbage = new char[2000000];
       Arrays.fill(garbage, 'a');
       String Db = new String(garbage);
       return logClock + "," + time + "," + Db;
     }

     public static void printCache() {
       System.out.println("in herrrrrr");
       for (String db: dataQueue) {
         System.out.println(db.substring(0,20));
       }
     }
 }
