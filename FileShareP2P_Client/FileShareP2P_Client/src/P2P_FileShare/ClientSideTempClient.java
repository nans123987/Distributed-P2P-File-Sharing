
/*
   @Author

   Nandan Thareja
   Chetan Khullar
   Aditya Kadam
   Sanket Bhoirkar

*/
 
package P2P_FileShare;

//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 *
 * @author nandan
 */
public class ClientSideTempClient  implements Runnable{
    public static Socket socket;
   
    
   
    public ClientSideTempClient(String FileName,InetAddress receiverIp, InetAddress senderIp)
    {
        try
        {
              
              System.out.println("trying to connect"+ receiverIp.getHostAddress());
              String ipAddress = receiverIp.getHostAddress();
              SocketAddress addr = new InetSocketAddress(ipAddress, 25000);
              socket = new Socket();
              socket.connect(addr,10000);
              ClientSideTempServer tempConn = new ClientSideTempServer(socket);
              tempConn.sendMessageToLocalServer(new Message("Download","none",FileName,"none",senderIp,receiverIp));
              Thread tempConnThread  = new Thread(tempConn);
              tempConnThread.start();
            
        }
        catch(Exception ex){
            System.out.println("Exception in ClientSideTempClient" + ex.getMessage());
        }
    }

     
    @Override
    public void run() {
        
        
    }

}
