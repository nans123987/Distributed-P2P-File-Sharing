/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_FileShare;


import java.net.Socket;
import java.net.ServerSocket;


/**
 *
 * @author nandan
 */
public class ServerThread {
  
    //public static PeerHandler peer_handler;
    public static void main(String[] args) {
       try
       {
            final int serverPort = 8080;
           
            ServerSocket server_socket  = new ServerSocket(8080);
           
            
            while(true)
            {
                
                Socket client_socket = server_socket.accept();
                System.out.println("connection Accepted");
                P2P_Server newConn = new P2P_Server(client_socket);
                Thread clientThread = new Thread(newConn);
                clientThread.start();
            }
       }
       catch(Exception e)
       {
           System.out.println(e);
       }
    }
    
}
