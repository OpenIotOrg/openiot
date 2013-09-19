/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

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
