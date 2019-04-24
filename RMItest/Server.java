/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.lang.*;
import java.util.*;

public class Server implements Hello {
  //ArrayList<ArrayList<Long>> dataBlock;
  //ArrayList<String> nodeIndex;
  //Integer maxSize = 250000;
  //Integer dataBlockSize;
  //Integer cacheSize;
  String masterIp;
  String selfIp;
  String currProvider;
  //String filename;
  Boolean isMaster = false;
  //Integer currOffset = 0;
  Integer testcounter = 0;

    public Server() {}
/*
######################### BEGIN REMOTE FUNCTIONS ###########################
*/
    public String sayHello() {
      return "Hello, world!";
    }

    public String checkCounter() {
      String response = this.testcounter.toString();
      return response;
    }

/*
######################### END REMOTE FUNCTIONS ###########################
*/
    public static void main(String args[]) {
      if (args[0] == "1") {
        this.isMaster = true;
      }
      this.currProvier = args[1];

      InetAddress inetAddress = InetAddress.getLocalHost();
      this.selfIp = inetAddress.getHostAddress();

      startServer();

      if (this.isMaster) {
        //updateFromFile();
        Thread t1 = new Thread(updateCounter());
        t1.start();
      } else {
        requestData();
      }
    }

    private static Integer updateCounter() {
      TimerTask repeatedTask = new TimerTask() {
        public void run() {
          this.testcounter++;
          System.out.println(this.testcounter);
        }
      };
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(repeatedTask, 1000, 1000);
      return 1;
    }

    private static void requestData() {
      String host = this.currProvier;
      try {
          Registry registry = LocateRegistry.getRegistry(host, 8699);
          Hello stub = (Hello) registry.lookup("Hello");
          String response = stub.sayHello();
          while(response != null){
            System.out.println(response);
            //response = stub.sayHello();
            response = stub.sayHello();
          }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }

    private static void startServer() {
      try {
          Server obj = new Server();
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
}
