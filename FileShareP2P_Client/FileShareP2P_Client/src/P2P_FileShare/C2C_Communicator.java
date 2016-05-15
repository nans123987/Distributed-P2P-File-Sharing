/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_FileShare;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author nandan
 */
public class C2C_Communicator {

    final int port = 25000;
    public static ServerSocket server_socket = null;
    public static Socket client_socket;
   

    public C2C_Communicator() {
        try {
            
           if(server_socket == null){
            server_socket = new ServerSocket(25000);
           }
            while (true) {
                client_socket = server_socket.accept();
                System.err.println(port + " : opened for file transfer with" + client_socket.getInetAddress().getHostAddress());
                ClientSideTempServer tempConn = new ClientSideTempServer(client_socket);
                Thread commThread = new Thread(tempConn);
                commThread.start();
                
            }

        } catch (Exception ex) {
            System.out.println("Exception in C2C Communicator" + ex.getMessage());

        }
    }
    
   

}
