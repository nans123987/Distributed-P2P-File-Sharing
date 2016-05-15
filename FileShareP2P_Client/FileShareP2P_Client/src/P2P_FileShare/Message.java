/*
   @Author

   Nandan Thareja
   Chetan Khullar
   Aditya Kadam
   Sanket Bhoirkar

*/
package P2P_FileShare;

import java.io.Serializable;
import java.net.InetAddress;

import java.util.ArrayList;


public class Message implements Serializable{
    
    private static final long serialVersionUID = 1L;
    public String type, sender, content, recipient;
    public ArrayList<String> dataList;
    InetAddress senderIp, receiverIp;
    public String FileName;
    public byte[] File;
    
    
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
     public Message(String type, String sender, String content, String recipient,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.senderIp=tsenderIp;
        this.receiverIp=treceiverIp;
    } 
     
    public Message(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.dataList=tdataStirng;
    }
    
    public Message(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.dataList=tdataStirng;
        this.senderIp=tsenderIp;
        this.receiverIp=treceiverIp;
    }
    
    public Message(String type, String sender, String content, String recipient, String filename){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient; this.FileName = filename;
    }
    
   public Message(String type, String sender, String content, String recipient, String filename, byte[] file){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient; this.FileName = filename;
        this.File = file;
    }
     
    
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
}

