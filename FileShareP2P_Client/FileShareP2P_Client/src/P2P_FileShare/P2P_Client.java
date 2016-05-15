/*
   @Author

   Nandan Thareja
   Chetan Khullar
   Aditya Kadam
   Sanket Bhoirkar

*/



package P2P_FileShare;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class P2P_Client implements Runnable {

    public Socket socket;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public ArrayList<String> peerList;
    public JoinNetworkScreen JoinScreenUI;
    public static FileShare_Peer_Window FileShareWindowUI;
    public String Username;
    public static Calendar downloadRequestTime;
    public FileShare_Peer_Window peerWindow;
    public Timestamp currentTimeStamp;
    public InetAddress clientSideServerIp;
    public InetAddress clientIp;
    public static int requestSeconds;
    ClientSideTempClient clientTemp;
    public ArrayList<String> connectedPeers;

    public P2P_Client(JoinNetworkScreen frame) throws Exception {

        try {
            JoinScreenUI = frame;
            String clientIP = InetAddress.getLocalHost().getHostAddress();
            socket = new Socket(clientIP, 8080);
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush();
            objIn = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            Logger.getLogger(P2P_Client.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Message message = (Message) objIn.readObject();
                    System.out.println("Received msg:" + message);
                    Date date = new Date();
                    currentTimeStamp = new Timestamp(date.getTime());
                    switch (message.type) {
                        case "connection":
                            if (message.content.equals("true")) {
                                System.out.println("connection successful");
                                InetAddress senderIp = message.senderIp;
                                clientIp = senderIp;
                                InetAddress receiverIp = message.receiverIp;
                                System.err.println(senderIp + "------" + receiverIp);
                                if (senderIp.getHostAddress().equals(receiverIp.getHostAddress())) {
                                    //first member of ring topology
                                    System.err.println("first member");
                                } else {
                                    System.err.println("not the first member");

                                }
                            } else {
                                System.out.println("something went wrong");
                            }
                            break;

                        case "Leave":
                            if (message.content.equals(Username)) {
                                peerWindow.dispose();
                                peerWindow = null;
                                socket.close();
                                String[] args = {};
                            } else {
                                peerWindow.addRemovePeerElement('R', message.content);
                                if (!message.dataList.isEmpty()) {
                                    peerWindow.removeFileElement(message.dataList);
                                }
                                peerWindow.addNotification(currentTimeStamp.toString() + ": " + message.content + ": Left the File Share Network");
                            }

                            break;
                        case "Join":
                            if (message.content.equals(Username)) {
                                System.out.println("Add a new FileShare window for new User");
                                connectedPeers = message.dataList;
                                
                                peerWindow = new FileShare_Peer_Window();
                                FileShareWindowUI = peerWindow;
                                peerWindow.UISetup(message.recipient);
                                peerWindow.attributeSetup(this, Username);
                                //getConnectedPeers(new Message("GetPeers",Username,"true", "server"));
                                peerWindow.addPeerToList(connectedPeers);
                                peerWindow.setVisible(true);
                                peerWindow.addNotification(currentTimeStamp.toString() + ":  You Joined the network");
                                JoinScreenUI.setVisible(false);
                                JoinScreenUI = null;
                            } else if (message.content.equals("false")) {
                                JOptionPane.showMessageDialog(JoinScreenUI, "Username has been already taken");

                            } else {
                                peerWindow.addRemovePeerElement('A', message.content);

                                peerWindow.addNotification(currentTimeStamp.toString() + ": " + message.content + ": Joined the network");

                            }

                            break;

                        case "AddFile":
                            if (message.content.equals("true")) {
                                peerWindow.addFileElement(message.FileName);
                            }
                            break;
                            
                        case "GetFiles":
                            if (message.content.equals("true")){
                                peerWindow.UpdateFileList(message.dataList);
                            }
                            break;
                        case "Query":
                            if(message.recipient.equals(Username)){
                                    getFileFromClient(message.content,message.receiverIp,message.senderIp);
                            }
                            break;
						case "OpenPortForTransfer":
                            if(message.content.equals("true")){
                               C2C_Communicator communicator = new C2C_Communicator();
                            }
                            break;	
                    }

                } catch (Exception e) {
                    System.out.println("Connection Issue: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.print("Connection Issue:" + ex.getMessage());
        }
    }

    public void joinP2PNetwork(JoinNetworkScreen frame, Message msg) {
        JoinScreenUI = frame;
        this.Username = msg.sender;
        send(msg);
    }

    public void LeaveFileShareNetwork() {
        send(new Message("Leave", Username, "true", "server"));
    }

    public void addFileToServer(String FileName) {
        System.err.println("Uploading File To server" + FileName);
        send(new Message("AddFile", Username, FileName, "server"));
    }
    
    public void GetFileList()
    {
        downloadRequestTime = Calendar.getInstance();
        requestSeconds = downloadRequestTime.get(Calendar.SECOND);
        send(new Message("GetFiles",Username,"true","server"));
    }

    public void QueryServerForPeer(String FileName)
    {
        send(new Message("Query",Username,FileName,"server"));
    }
    
   public void getFileFromClient(String FileName, InetAddress receiverIp,InetAddress senderIp ){
            clientTemp = new ClientSideTempClient(FileName, receiverIp, senderIp);
   
   }
    public void send(Message msg) {
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing Message to Server: " + msg.toString());
        } catch (Exception e) {
            System.out.println("Exception in P2P client:" + e);
        }

    }
}
