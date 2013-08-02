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

