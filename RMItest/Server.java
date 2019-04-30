

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.lang.*;
import java.util.*;

// String selfIp;
// String currProvider;
// //String filename;
// Boolean isMaster = false;
// //Integer currOffset = 0;
// Integer testcounter = 0;

public class Server implements Hello {
  //ArrayList<ArrayList<Long>> dataBlock;
  ArrayList<ArrayList<String>> nodeIndex = new ArrayList<ArrayList<String>>();
  public static Integer MAXCHAINLEN = 6;
  //Integer maxSize = 250000;
  //Integer dataBlockSize;
  //Integer cacheSize;
  public static String selfIp;
  public static String currProvider;
  // //String filename;
  public static boolean isMaster = false;
  // //Integer currOffset = 0;
  public static Integer testcounter = 1;

  public static String masterIp = "54.209.66.61";

  //refers to how many chains from the Master there currently are
  public static int chainIndex = 0;

  public Server() {}
/*
######################### BEGIN REMOTE FUNCTIONS ###########################
*/
    public String sayHello() {
      return "Hello, world!";
    }

    //testing to simulate 
    public String checkCounter() {
      String response = testcounter.toString();
      return response;
    }

    // master only
    public synchronized String join(String newIp) {


      System.out.println("Servicing join################################ from " + newIp);
      if (nodeIndex.size() == 0) {
        System.out.println("Here1");
        ArrayList<String> newChain = new ArrayList<String>();
        nodeIndex.add(newChain);
        nodeIndex.get(0).add(newIp);
        System.out.println("BLAHHHH" + nodeIndex.get(0).get(0));
        printIndex();
        return masterIp;
      }
      System.out.println("Here2");
      String newProvider = nodeIndex.get(chainIndex).get(nodeIndex.get(chainIndex).size()-1);
      nodeIndex.get(chainIndex).add(newIp);
      printIndex();
      return newProvider;
    }

    public synchronized String removeNode(String deadNode) {
      for (int i = 0; i < nodeIndex.size(); i++) {
        int removal = nodeIndex.get(i).indexOf(deadNode);
        if (removal != -1) {
          System.out.println("removing node " + nodeIndex.get(i).get(removal));
          nodeIndex.remove(removal);
          if(removal == 0) {
            return masterIp;
          }
          System.out.println("returing node " + nodeIndex.get(1).get(removal - 1));
          printIndex();
          return nodeIndex.get(i).get(removal - 1);
        }
      }
      System.out.println("NODE TO REMOVE NOT FOUND");
      return "CATS";
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

      String cat = createDb();
      System.out.println(cat.length());
      System.out.println(cat);


      //if (true) { return; }
      startServer();

      if (isMaster) {
        Thread t1 = new Thread(new Runnable() {
          public void run() {
            updateCounter();
          }
        });
        //updateFromFile();
        t1.start();
      } else {
        requestJoin();
        requestData();
      }
    }

    private static void requestNewProvider() {
      try {
        Registry registry = LocateRegistry.getRegistry(masterIp, 8699);
        Hello stub = (Hello) registry.lookup("Hello");
        String response = stub.removeNode(currProvider);
        currProvider = response;
        System.out.println("New Provider Accepted By Master, Provider Set To: " + currProvider);
        // TODO:test for failure
      } catch (Exception e) {
        System.err.println("Master Failure");
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }

    private static Integer updateCounter() {
      TimerTask repeatedTask = new TimerTask() {
        public void run() {
          testcounter++;
          System.err.println(testcounter);
        }
      };
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(repeatedTask, 1000, 1000);
      return 1;
    }

    private static void requestData() {
      TimerTask repeatedTask = new TimerTask() {
        public void run() {
          System.out.println("Requesting Data from Node: " + currProvider);
          String host = currProvider;
          try {
            Registry registry = LocateRegistry.getRegistry(host, 8699);
            Hello stub = (Hello) registry.lookup("Hello");
            String response = stub.checkCounter();
            testcounter = Integer.parseInt(response);
            System.out.println(testcounter);
          } catch (Exception e) {
              System.err.println("Client exception: " + e.toString());
              e.printStackTrace();
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
      String host = masterIp;
      try {
          Registry registry = LocateRegistry.getRegistry(host, 8699);
          Hello stub = (Hello) registry.lookup("Hello");
          String response = stub.join(selfIp);
          currProvider = response;
          System.out.println("Join Accepted By Master, Provider Set To: " + currProvider);
          // TODO:test for failure
      }
      catch (Exception e) {
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

    public static String createDb() {
      Long time = System.currentTimeMillis();
      char[] garbage = new char[2000000];
      Arrays.fill(garbage, 'a');
      String Db = new String(garbage);
      return time + "," + Db;
    }

    public void printIndex() {
      for (int i = 0; i < nodeIndex.size(); i++) {
        System.out.print(" Chain num " + i);
        for (int j = 0; j < nodeIndex.get(i).size(); j++) {
          System.out.print(" Node num " + j + " ");
          System.out.print(nodeIndex.get(i).get(j) + " ");
        }
        System.out.print("\n");
      }
    }
}
