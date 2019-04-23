// import java.rmi.*;
//
// public class Viewer{
//
//   public static void main(String args[]){
//     // Server Code
//     try{
//
//     NodeFunctions stub=new NodeFunctionsRemote();
//     Naming.rebind("rmi://localhost:5000/sonoo",stub);
//
//     }
//     catch(Exception e){
//       System.out.println(e);
//     }
//
//     // Client Code
//     try{
//
//       NodeFunctions stub=(NodeFunctions)Naming.lookup("rmi://localhost:5000/sonoo");
//       System.out.println(stub.add(34,4));
//
//     }
//     catch(Exception e){
//       System.out.println(e);
//     }
//   }
// }
