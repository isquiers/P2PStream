public class Node {

  Integer dataBlockSize;
  Integer cacheSize;
  ArrayList<Object> Cache;
  String masterIp;

  public static void main(String args[]) {
    // [dataBlockSize][cacheSize][MasterIp][Optional Content filename]
    //
    int dataBlockSize = Integer.ParseInt(args[0]);
    int cacheSize = Integer.ParseInt(args[1]);

  }

  public void playFromCache(ArrayList<Object E>) {

  }




  public void listenForReq() {
    try{

      NodeInterface stub=new NodeInterfaceRemote();
      Naming.rebind("rmi://localhost:5000/sonoo",stub);

    }
      catch(Exception e){System.out.println(e);}
    }
  }

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
