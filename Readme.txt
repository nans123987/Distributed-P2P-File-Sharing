1. Open both the projects in netbeans IDE
2. Right click each project and click on resolve problems -> resolve -> browse the mysql-connector-java-5.1.18.jar file sent.
3. open the cleint project on other systems as well.
4. Make the following change in P2P_Client.java
        a. Find the statement [String clientIP = InetAddress.getLocalHost().getHostAddress();] - line 45
        b. change it to [String clientIP = "<IP Address>"] -  <IP Address> - > IP address of the peer running the server.
5. Connected all the peers to the same network using phone hotspot or run hotspot on the system running the server.
6. Run the FileShareP2P_Server project.
7. Run the FIleShareP2P_Client project.
