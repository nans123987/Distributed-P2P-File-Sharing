/*
   @Author

   Nandan Thareja
   Chetan Khullar
   Aditya Kadam
   Sanket Bhoirkar

*/
package P2P_FileShare;

import java.util.HashMap;

/**
 *
 * @author nandan
 */
public class Filedirectory {
    
    public static HashMap<String, String> filedirectory;

    public Filedirectory() {
        this.filedirectory = new HashMap<String, String>();
    }
    
    public void addFileInfo(String FileName, String FilePath)
    {
         filedirectory.put(FileName, FilePath);
    }
    
   
   
}
