

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
  //ArrayList<String> nodeIndex;
  //Integer maxSize = 250000;
  //Integer dataBlockSize;
  //Integer cacheSize;
  //String masterIp
  public static String selfIp;
  public static String currProvider;
  // //String filename;
  public static boolean isMaster = false;
  // //Integer currOffset = 0;
  public static Integer testcounter = 0;

    public Server() {}
/*
######################### BEGIN REMOTE FUNCTIONS ###########################
*/
    public String sayHello() {
      return "Hello, world!";
    }

    public String checkCounter() {
      String response = testcounter.toString();
      return response;
    }

/*
######################### END REMOTE FUNCTIONS ###########################
*/
    public static void main(String args[]) {
      if (args[0].toString().equals("1")) {
        System.err.println("is master");
        isMaster = true;
      }
      currProvider = args[1];
      try{
        InetAddress inetAddress = InetAddress.getLocalHost();
        selfIp = inetAddress.getHostAddress();
        String instanceId = EC2MetadataUtils.getInstanceId();
        AmazonEC2 awsEC2client = AmazonEC2ClientBuilder.defaultClient();
        selfIp = awsEC2client.describeInstances(new DescribeInstancesRequest()
                      .withInstanceIds(instanceId))
                        .getReservations()
                        .stream()
                        .map(Reservation::getInstances)
                        .flatMap(List::stream)
                        .findFirst()
                        .map(Instance::getPublicIpAddress)
                        .orElse(null);
        System.out.println(selfIp);
      } catch (Exception e){
        System.err.println("Server exception: " + e.toString());
        e.printStackTrace();
      }

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
        requestData();
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
      String host = currProvider;
      try {
          Registry registry = LocateRegistry.getRegistry(host, 8699);
          Hello stub = (Hello) registry.lookup("Hello");
          String response = stub.checkCounter();
          while(response != null){
            System.out.println(response);
            //response = stub.sayHello();
            response = stub.checkCounter();
            testcounter = Integer.parseInt(response);
          }
        } catch (Exception e) {
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

    public static void setMaster(boolean a){
      isMaster = a;
    }
}
