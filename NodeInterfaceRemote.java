import java.rmi.*;
import java.rmi.server.*;

public class AdderRemote extends UnicastRemoteObject implements Adder{

AdderRemote()throws RemoteException{
super();
}

public String join(String newIp) {
  System.out.println("Here is the IP " + newIp);
}

}
