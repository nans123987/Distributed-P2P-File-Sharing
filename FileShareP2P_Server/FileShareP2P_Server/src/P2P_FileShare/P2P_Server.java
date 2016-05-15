/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_FileShare;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nandan
 */
public class P2P_Server implements Runnable {

    public static ArrayList<Socket> peerSockets = new ArrayList<Socket>();
    public static ArrayList<String> recipientLst = new ArrayList<String>();

    public static ArrayList<ObjectInputStream> inputStreams = new ArrayList<ObjectInputStream>();
    public static ArrayList<ObjectOutputStream> outputStreams = new ArrayList<ObjectOutputStream>();

    public static HashMap<String, ArrayList<String>> peerFileDetail = new HashMap<String, ArrayList<String>>();

    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public Message message;
    public String UserName;

    public Socket socketObj;
    public String newPeer;
    public boolean UserSignedIn = false;
    public ArrayList<String> FileListOnStartUP = new ArrayList<String>();

    public P2P_Server(Socket tsock) {
        this.socketObj = tsock;

        try {
            objIn = new ObjectInputStream(socketObj.getInputStream());
            objOut = new ObjectOutputStream(socketObj.getOutputStream());

            peerSockets.add(socketObj);
            inputStreams.add(objIn);
            outputStreams.add(objOut);

        } catch (Exception e) {
            Logger.getLogger(P2P_Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    if (objIn.available() < 0) {
                        return;
                    }

                    message = (Message) objIn.readObject();
                    System.out.println(message);

                    if (message.type.equals("connection")) {

                        //Message m1 = new Message("connection", "server", "true", "test");
                        Message m1;
                        System.err.println("number of objects in socket array:" + peerSockets.size());
                        if (peerSockets.size() == 1) {
                            m1 = new Message("connection", "server", "true", "test", socketObj.getInetAddress(), socketObj.getInetAddress());
                        } else {

                            int numberOfSocket = peerSockets.size();
                            Socket lastObj = peerSockets.get(numberOfSocket - 2);
                            m1 = new Message("connection", "server", "true", "test", socketObj.getInetAddress(), lastObj.getInetAddress());
                        }
                        send(m1);
                    } else if (message.type.equals("Join")) {
                        Message m1;
                        
                        newPeer = message.sender;
                        if (!recipientLst.contains(newPeer)) {
                            recipientLst.add(newPeer);
                            UserSignedIn = true;
                        } else {
                            m1 = new Message("Join", "server", "false", "false");
                            send(m1);
                            int index = peerSockets.indexOf(socketObj);
                            inputStreams.remove(index);
                            outputStreams.remove(index);

                        }
                        if (UserSignedIn) {
                            for (int i = 0; i < peerSockets.size(); i++) {
                                m1 = new Message("Join", "server", newPeer, recipientLst.get(i), recipientLst);
                                Socket tempSockets = (Socket) peerSockets.get(i);
                                ObjectOutputStream tempOS = outputStreams.get(i);
                                tempOS.writeObject(m1);
                                tempOS.flush();
                            }
                            UserSignedIn = false;
                        }

                    } else if (message.type.equals("AddFile")) {
                        Message m1;
                        ArrayList<String> fileList;
                        newPeer = message.sender;
                        if (!peerFileDetail.containsKey(newPeer)) {
                            fileList = new ArrayList<String>();
                            fileList.add(message.content);
                            peerFileDetail.put(newPeer, fileList);
                        } else if (peerFileDetail.containsKey(newPeer)) {
                            peerFileDetail.get(newPeer).add(message.content);
                        }
                        m1 = new Message("AddFile", "server", newPeer, newPeer, message.content);
                        send(m1);
                        for (int i = 0; i < peerSockets.size(); i++) {
                            m1 = new Message("AddFile", "server", "true", recipientLst.get(i), message.content);
                            Socket tempSockets = (Socket) peerSockets.get(i);
                            ObjectOutputStream tempOS = outputStreams.get(i);
                            tempOS.writeObject(m1);
                            tempOS.flush();
                        }

                    } else if (message.type.equals("Leave")) {
                        Message m1;
                        ArrayList<String> fileList = new ArrayList<String>();
                        newPeer = message.sender;
                        if (peerFileDetail.containsKey(newPeer)) {
                            for (int k = 0; k < peerFileDetail.get(newPeer).size(); k++) {
                                fileList.add(peerFileDetail.get(newPeer).get(k));
                            }
                        }
                        objOut.writeObject(new Message("Leave", "server", message.sender, message.sender));
                        objOut.flush();
                        int index = peerSockets.indexOf(socketObj);
                        peerSockets.remove(index);
                        recipientLst.remove(index);

                        for (int i = 0; i < peerSockets.size(); i++) {
                            m1 = new Message("Leave", "server", message.sender, recipientLst.get(i), fileList);
                            Socket tempSockets = (Socket) peerSockets.get(i);
                            ObjectOutputStream tempOS = outputStreams.get(i);
                            tempOS.writeObject(m1);
                            tempOS.flush();
                        }

                    } 
                    else if (message.type.equals("GetFiles")) {
                        Message m1;
                        newPeer = message.sender;
                        if (message.content.equals("true")) {
                            
                            for(Entry<String,ArrayList<String>> eachEntry : peerFileDetail.entrySet())
                            {
                                for(String filename : eachEntry.getValue()){
                                     FileListOnStartUP.add(filename);
                                }
                                
                            }
                        }
                        m1 = new Message("GetFiles","server","true",newPeer,FileListOnStartUP);
                        send(m1);
                    }
                    else if(message.type.equals("Query")){
                        Message m1;
                        int index ;
                        Socket tempSock;
                        newPeer = message.sender;
                        String peerNameForSelectedFile = new String();
                        for(Entry<String,ArrayList<String>> eachEntry : peerFileDetail.entrySet()){
                            for(String filename : eachEntry.getValue()){
                                  if(filename.equals(message.content))
                                  {
                                     peerNameForSelectedFile = eachEntry.getKey();
                                     break;
                                  }
                            }
                        }
                        index = recipientLst.indexOf(peerNameForSelectedFile);
                        tempSock = peerSockets.get(index);
                        m1 = new Message("OpenPortForTransfer","server","true",peerNameForSelectedFile,socketObj.getInetAddress(),tempSock.getInetAddress());
                        ObjectOutputStream tempOS = outputStreams.get(index);
                        tempOS.writeObject(m1);
                        tempOS.flush();
                        Thread.sleep(5000);
                        m1 = new Message("Query","server",message.content,message.sender,socketObj.getInetAddress(),tempSock.getInetAddress());
                        send(m1);
                        
                        
                    }
                }

            } finally {

            }

        } catch (Exception ex) {
            System.out.println("Exception on the Server Side:" + ex.getMessage());
        }
    }

    public void send(Message msg) {
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing Message to Client: " + msg.toString());
        } catch (Exception e) {
            System.out.println("Exception in P2P client:" + e);
        }

    }
}
