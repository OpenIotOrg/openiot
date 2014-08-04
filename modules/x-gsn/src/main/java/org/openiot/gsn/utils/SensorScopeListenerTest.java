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
 * @author Sofiane Sarni
*/

package org.openiot.gsn.utils;
import java.net.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SensorScopeListenerTest
{
    public static final String CONF_LOG4J_SENSORSCOPE_PROPERTIES = "conf/log4j_sensorscope.properties";
    private static transient Logger logger = Logger.getLogger(SensorScopeListenerTest.class);
    public SensorScopeListenerTest(int port)
    {
        ServerSocket server;

        try
        {
            server = new ServerSocket(port);

            while(true)
            {
                try
                {
                    Socket socket = server.accept();

                    if(socket != null)
                        new SensorScopeListenerClient(socket);
                }
                catch(Exception e)
                {
                    System.out.println("Error while accepting a new client: " + e);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Could not create the server: " + e);
        }
    }

    public static void main(String args[])
    {
        PropertyConfigurator.configure(CONF_LOG4J_SENSORSCOPE_PROPERTIES);
        new SensorScopeListenerTest(1234);
    }
}

