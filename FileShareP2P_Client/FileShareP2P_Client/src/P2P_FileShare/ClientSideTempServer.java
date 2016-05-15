/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package P2P_FileShare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.Calendar;
import javax.swing.JFileChooser;

/**
 *
 * @author nandan
 */
public class ClientSideTempServer implements Runnable {

    Socket clientSocket;
    ObjectOutputStream objOut;
    ObjectInputStream objIn;
    String FilePath;
    File requestedFile;
    byte[] fileArray;

    public BufferedOutputStream bos = null;
    public BufferedInputStream bis = null;
    public FileInputStream fis;
    public FileOutputStream fos;
    public static String ThroughputTime;
    public static int responseSeconds;
    public static Calendar responseTime;
    

    public ClientSideTempServer(Socket cliSocket) throws IOException {

        try {
            this.clientSocket = cliSocket;

            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objOut.flush();
            objIn = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception ex) {
            System.out.println("Exception in ClientSideServer:" + ex.getMessage());
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
                    Message message = (Message) objIn.readObject();
                    System.out.print(message);

                    if (message.type.equals("Download")) {
                        SendFile(message.content);
                    } else if (message.type.equals("Save")) {
                        SaveFile(message.FileName, message.File);
                    }

                }
            } finally {
                clientSocket.close();
            }
        } catch (Exception ex) {
            System.out.println("ClientSideTempServer Exception : " + ex.getMessage());
        }
    }

    public void SendFile(String FileName) throws IOException {
        Message m1;
        try {

            FilePath = Filedirectory.filedirectory.get(FileName);
            requestedFile = new File(FilePath);
            fileArray = new byte[(int) requestedFile.length()];
            try {
                fis = null;
                fis = new FileInputStream(requestedFile);
                fis.read(fileArray);
            } catch (FileNotFoundException ex) {
                System.err.println("File Can not be read" + ex.getMessage());

            }
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
        }
        m1 = new Message("Save", "none", "none", "none", FileName, fileArray);
        objOut.writeObject(m1);
        objOut.flush();
        

    }

    public void SaveFile(String FileName, byte[] FileArray) {
        JFileChooser chooser = new JFileChooser();
        
        responseTime = Calendar.getInstance();
        responseSeconds = responseTime.get(Calendar.SECOND);
        int option = chooser.showSaveDialog(P2P_Client.FileShareWindowUI);
       
        try {
            try {
                if (option == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    chooser.setSelectedFile(requestedFile);
                    File file = new File(path);
                    fos = new FileOutputStream(file);
                    fos.write(FileArray);
                    fos.close();
                    
                    ThroughputTime = Integer.toString(responseSeconds - P2P_Client.requestSeconds);
                    P2P_Client.FileShareWindowUI.SetDownloadElapsedTime(ThroughputTime + " Seconds");
                    
                }
                
            } catch (FileNotFoundException ex) {
                System.out.println("File Not Found for writing : "+ex.getMessage());
            }
        } 
        catch (Exception ex) {
            System.out.println("Error saving the file : " + ex.getMessage());
        }

    }

    public void sendMessageToLocalServer(Message msg) {
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing message in clientSideTempClient: " + msg);
        } catch (Exception ex) {
            System.out.println("Exception in P2P_client:" + ex);
        }

    }
}
