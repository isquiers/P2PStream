

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.lang.*;
import java.util.*;

public class Server implements Hello {
  //ArrayList<ArrayList<Long>> dataBlock;
  ArrayList<ArrayList<String>> nodeIndex = new ArrayList<ArrayList<String>>();
  public static String selfIp;
  public static String currProvider;
  public static boolean isMaster = false;
  public static String masterIp = "54.209.66.61";
  public static int currChainIndex = 0; // chain in which the current node resides
  public static String currDb;
  public static int logClock = 0;
  public static Queue<String> dataQueue;
  public static int currClock;
  public static int msThreshold = 1000; // offset in milleseconds

  public Server() {}
/*
######################### BEGIN REMOTE FUNCTIONS ###########################
*/

    //testing to simulate data being live streamed
    public String checkCounter() {
      String response = currDb;
      return response;
    }

    public void updateChain (int newChain) {
      currChainIndex = newChain;
    }

    // executed by master
    // returns new Provider
    public synchronized String moveNode(String mover, int currChain) {
      System.out.println("Node " + mover + " Requesting Move from Chain " + currChain);
      int moveMe = nodeIndex.get(currChain).indexOf(mover);
      if (moveMe == -1) {
        //return Failure
        System.out.println("Failed lookup when moving node");
      }
      int newChainIndex = currChain + 1;
      if (nodeIndex.size() == newChainIndex) { //newchain will return null so we create new chain
        nodeIndex.add(new ArrayList<String>());
        nodeIndex.get(newChainIndex).add(masterIp);
        System.out.println("this is here");
      }
      String newProvider = nodeIndex.get(newChainIndex).get(nodeIndex.get(newChainIndex).size() - 1);
      System.out.println(newProvider + " this is new provider");
      String movingNode = nodeIndex.get(currChain).get(moveMe);

      while (movingNode != null) { // move all nodes downstream of requestor
        nodeIndex.get(newChainIndex).add(movingNode); // add them to new chain
        newChainAlert(movingNode, newChainIndex); // update them
        nodeIndex.get(currChain).remove(moveMe); // remove them from the old chain
        movingNode = nodeIndex.get(currChain).get(moveMe);
      }
      return newProvider;
    }

    //executed by master
    //this is called imediatly after a node that is not the Master is activated.
    public synchronized String join(String newIp) {
      System.out.println("Servicing join ################################ from " + newIp);

      //if it is the master
      // we could also do this in the master main loop, to take out this block
      if (nodeIndex.size() == 0) {
        System.out.println("Here1");
        ArrayList<String> newChain = new ArrayList<String>();
        nodeIndex.add(newChain);
        nodeIndex.get(0).add(newIp);
        printIndex();
        return masterIp;
      }
      // join always initiates you at the end of chain 0, assuming you will request a change if it's necessary
      System.out.println("Here2");
      String newProvider = nodeIndex.get(0).get(nodeIndex.get(0).size()-1);
      nodeIndex.get(0).add(newIp);
      printIndex();
      return newProvider;
    }

    public synchronized String removeNode(String deadNode, int chain) {
      System.out.println("Node removal requested");
      int removal = nodeIndex.get(chain).indexOf(deadNode);
      if (removal != -1) {
        System.out.println("removing node " + nodeIndex.get(chain).get(removal));
        nodeIndex.get(chain).remove(removal);
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

    // command line args [masterIp][selfIp]
    public static void main(String args[]) {
      if (args[0].toString().equals(args[1].toString())) {
        System.err.println("is master");
        isMaster = true;
      }
      masterIp = args[0].toString();
      selfIp = args[1].toString();

      startServer();

      if (isMaster) {
        requestJoin();
        Thread t1 = new Thread(new Runnable() {
          public void run() {
            updateCounter();
          }
        });
        //updateFromFile();
        t1.start();
      } else {
        createQueue();
        requestJoin();
        requestData();
      }
    }

    private static void newChainAlert(String recievingNode, int newChainIndex) {
      try {
        Registry registry = LocateRegistry.getRegistry(recievingNode, 8699);
        Hello stub = (Hello) registry.lookup("Hello");
        stub.updateChain(newChainIndex);
      } catch (Exception e) {
          System.err.println("Client exception: " + e.toString());
          e.printStackTrace();
      }
    }

    private static void requestNewProvider() {
      System.out.println("Requesting new Provider other than " + currProvider);
      try {
        Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
        Hello stub = (Hello) registry.lookup("Hello");
        String response = stub.removeNode(currProvider, currChainIndex);
        currProvider = response;
        System.out.println("New Provider Accepted By Master, Provider Set To: " + currProvider);
      } catch (Exception e) {
        masterFailure();
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }

    private static void requestNewChain() {
      try {
        Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
        Hello stub = (Hello) registry.lookup("Hello");
        String response = stub.moveNode(selfIp, currChainIndex);
        currProvider = response;

      } catch (Exception e) {
        masterFailure();
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }

    private static int updateCounter() {
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

    private static void requestData() {
      TimerTask repeatedTask = new TimerTask() {
        public void run() {
          // System.out.println("Requesting Data from Node: " + currProvider);
          String host = currProvider;
          try {
            Registry registry = LocateRegistry.getRegistry(host, 8699);
            Hello stub = (Hello) registry.lookup("Hello");

            String dBlock = stub.checkCounter();
            currDb = dBlock;
            String[] timestamp = dBlock.split(",");
            Long timestampMills = Long.parseLong(timestamp[1]);
            Long offset = System.currentTimeMillis() - timestampMills;
            System.out.println("This is time differnce milleseconds: " + offset);
            if (offset > msThreshold) {
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

    private static void requestJoin() {
      System.out.println("Requesting Join");
      try {
        Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
        Hello stub = (Hello) registry.lookup("Hello");
        String response = stub.join(selfIp);
        currProvider = response;
        if(currProvider.equals(selfIp) && !currProvider.equals(masterIp)) {
          requestNewProvider();
        }
        System.out.println("Join Accepted By Master, Provider Set To: " + currProvider);
        // TODO:test for failure
      }
      catch (Exception e) {
        masterFailure();
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }


    private static Server obj;
    private static void startServer() {
      try {
          obj = new Server();
          Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 8699);
          // Bind the remote object's stub in the registry
          Registry registry = LocateRegistry.createRegistry(8699);
          registry.bind("Hello", stub);
          System.err.println("Server ready");
      } catch (Exception e) {
          System.err.println("Server exception: " + e.toString());
          e.printStackTrace();
      }
    }

    public static void setMaster(boolean a) {
      isMaster = a;
    }

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
      System.exit(0);
    }

    public static void createQueue(){
      dataQueue = new LinkedList<String>();
    }

    public static String createDb() {
      logClock += 1;
      Long time = System.currentTimeMillis();
      char[] garbage = new char[2000000];
      Arrays.fill(garbage, 'a');
      String Db = new String(garbage);
      return logClock + "," + time + "," + Db;
    }
}
