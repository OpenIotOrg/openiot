package org.openiot.gsn;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class GSNStop {
  
  public static void main(String[] args) {
    stopGSN(Integer.parseInt(args[0]));
  }
  public static void stopGSN(int gsnControllerPort){
    try {
//      Socket socket = new Socket(InetAddress.getLocalHost().getLocalHost(), org.openiot.gsn.GSNController.GSN_CONTROL_PORT);
      Socket socket = new Socket(InetAddress.getByName("localhost"), gsnControllerPort);
      PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
      writer.println(org.openiot.gsn.GSNController.GSN_CONTROL_SHUTDOWN);
      writer.flush();
      System.out.println("[Done]");
    }catch (Exception e) {
      System.out.println("[Failed: "+e.getMessage()+ "]");
    }
  }
}
