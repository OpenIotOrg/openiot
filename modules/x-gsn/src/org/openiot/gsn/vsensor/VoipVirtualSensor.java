/*
 * This virtual sensor implements a phone call notification. The virtual sensor makes a phone call when a condition 
 * in the virtual sensor query is triggered, e.g. <query>SELECT temp FROM source1 WHERE temp >= 54 </query>
 * 
 * The virtual sensor is configured to work with Asterisk [1]. So, you will need to install or have an existing asterisk 
 * server running [2]. Once the virtual sensor is deployed, it automatically creates a dial plan in asterisk and registers 
 * extensions needed to make the phone calls, minimal configuration is required. You need to add the following line to 
 * the 'extensions_custom.conf' file in asterisk (/etc/asterisk/extensions_custom.conf):
 * 
 * #include extensions_gsn.conf
 *  
 * This will include the any extensions created by the virtual sensor in the file 'extensions_gsn.conf'. Depending on the
 * number of times the virtual sensor is deployed, an extension with the virtual sensor name, e.g. from the <virtual-sensor name="phone"> tag
 * and a random extension number (internal to asterisk) will be created. 
 * 
 * The virtual sensor uses the Manager API to connect (login) to asterisk and execute remote commands (e.g. load dial plan, 
 * make phone call, call forward). To loging to the asterisk manager, you have to create an account and add your IP address in the 'manager.conf'
 * file in asterisk (/etc/asterisk/manager.conf):
 * 
 * [testuser]
 * secret = mypassword
 * permit=192.168.12.101/255.255.0.0
 * read = system,call,log,verbose,command,agent,user
 * write = system,call,log,verbose,command,agent,user
 * 
 * Where, 'testuser' is the username and 'secret' is the password. In 'permit' add your IP address (otherwise asterisk will block you).
 * As part of asterisk, you will also need to install the 'festival' text to speech system [3] and the 'sox' utility [4]. These programs are 
 * used by the virtual sensor to convert the notification message (string) to speech (audio) to be played by asterisk. If using linux/ubuntu,
 * do a 'apt-get install festival sox'.
 * 
 * References
 * 
 * [1] http://www.asterisk.org/
 * [2] http://www.trixbox.org/downloads
 * [3] http://www.cstr.ed.ac.uk/projects/festival/ 
 * [2] http://sox.sourceforge.net/
 */

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;

import java.io.IOException;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;

/**
 * @author GSN Team
 */
public class VoipVirtualSensor extends AbstractVirtualSensor
{
  private final static transient Logger logger = Logger.getLogger(VoipVirtualSensor.class);
  
  private ManagerConnection managerConnection;
  private OriginateAction originateAction;
  private ManagerResponse originateResponse;
  private boolean connected = false;
  private String vs_name;
  private String vs_ext;
  private String phone_no;
  private final int CALL_TIMEOUT = 17000; // in milliseconds
  
  // contains the extensions created by the virtual sensor, this file has to be added in the 'extensions_custom.conf' asterisk configuration
  private final String DIAL_PLAN = "extensions_gsn.conf";
  
  // The default configuratoin can only made SIP calls to internal SIP/IP phones registered with asterisk. 
  // To make external PSTN calls, change the SIP trunk name to match the VoIP account provider. 
  private final String SIP_TRUNK = "from-internal"; 
  
  private static int vs_counter = 0;
  private int NumberOfCalls = 0;
  private boolean CallAnswered = false;
  private final int NUMBER_OF_RETRY_CALLS = 50; 
  
  
  public void dataAvailable(String inputStreamName, StreamElement data)
  {    
    if (connected)
    {
      // post the stream data produced
      dataProduced(data);
      
      // the query in the virtual sensor was satisfied make a phone call
      logger.info("Query Satisfied - Calling phone number " + phone_no);
      logger.info("CallAnswered=" + CallAnswered + " NumberOfCalls=" + NumberOfCalls);
      
      try
      {
          // to prevent multiple calls 
          if (CallAnswered == false || NumberOfCalls == NUMBER_OF_RETRY_CALLS)
          {  
  	        // generate the call and wait for answer or timeout
  	        originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
          
  	        if(originateResponse.getResponse().equals("Success"))
  	        {
  	          logger.info("Call answered.");
  	          
  	          CallAnswered = true;
  	        }
  	        else if (originateResponse.getResponse().equals("Error"))
  	        {
  	          logger.error("Call Error - Wrong phone number or extension.");
  	          
  	          CallAnswered = false;
  	        }
  	        
  	        NumberOfCalls = 0;
          }
          
      } catch (TimeoutException e)
      {
        logger.warn("Call timeout (-" + CALL_TIMEOUT + ") milliseconds.");
        NumberOfCalls = 0;
      } catch (IOException e)
      {
        logger.warn(e);
      }
      
      NumberOfCalls++;
    }
  }
  
  public boolean initialize()
  {
    VSensorConfig vsensor = getVirtualSensorConfiguration();
    TreeMap<String, String> params = vsensor.getMainClassInitialParams();
    
    ManagerConnectionFactory factory = new ManagerConnectionFactory(params.get("host"), params.get("username"), params.get("password"));
    
    managerConnection = factory.createManagerConnection();
    
    // get the name of the virtual sensor from the vsd
    vs_name = new String(vsensor.getName());
    
    // generate a random extension number between 9000-10000
    Random random = new Random();    
    Integer ext =  (int)((long)(1001 * random.nextDouble()) + 9000);
    vs_ext = ext.toString();
    
    try
    {
      // connect to Asterisk and log in
      managerConnection.login();
      
      connected = true;
      
      // delete previous configuration, e.g. dial plan and config files
      cleanConfig();
      
      // create the text-to-speech ulaw file
      text2speech_low(params.get("message"));
      
      // create the dial plan in the asterisk server
      createDialPlan(vs_name, vs_ext);
      
      // settings for making the actual phone call
      originateAction = new OriginateAction();
      
      phone_no = new String(params.get("number"));
      
      originateAction.setChannel("SIP/" + phone_no + "@" + SIP_TRUNK);
      originateAction.setContext(vs_name);
      originateAction.setExten(vs_ext);
      
      originateAction.setPriority(new Integer(1));
      originateAction.setCallerId("GSN Notification");
      
    } catch (Exception e)
    {
      connected = false;
      logger.error("connection state is " + managerConnection.getState() + "    "+ e);
    }
    
    vs_counter++;
    
    logger.info("Virtual Sensor [" + vs_name + "]" + " added to GSN with extension " + vs_ext +  " running instance @" + vs_counter);
    
    return connected;
  }  
   
  public void dispose()
  {
    if (connected)
    {
      try
      {
        // delete the audio files from asterisk server and logoff from the manager.
        originateAction = new OriginateAction();
        originateAction.setChannel("Local/1004@dummy-wait");
        originateAction.setApplication("System");
        
        if (vs_counter == 0)
        {
          originateAction.setData("rm -rf /etc/asterisk/" + DIAL_PLAN);
          
          originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
          
          logger.info("dispose() : Removed " + DIAL_PLAN + " from Asterisk configuration. " + originateResponse.getResponse());
          
          // and finally log off and disconnect
          managerConnection.logoff();
          
        } else
        {
          // delete only the ulaw files for the virtual sensor
          originateAction.setData("rm -rf /tmp/gsn_tmp /tmp/" + vs_name + ".ulaw" + " /tmp/" + vs_name + ".gsm");
          
          originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
          
          logger.info("dispose() : Removed /tmp/gsn_tmp /tmp/" + vs_name + ".ulaw files." + " " + originateResponse.getResponse());
          
          // and finally log off and disconnect
          managerConnection.logoff();
          
          // decrease wrapper vs_counter
          vs_counter--;
        }
    } catch (Exception e)
    {
      logger.error("Error removing the virtual sensor." + e);
    }
    
    logger.info("dispose() : Virtual Sensor removed [" + vs_name + "].");
    }
  }
  
  public void createDialPlan(String vs_name, String vs_ext)
  {   
    try
    {
      originateAction = new OriginateAction();
      originateAction.setChannel("Local/1004@dummy-wait");
      originateAction.setApplication("System");
      originateAction.setData("echo -e '[" + vs_name + "]\nexten => " + vs_ext + ",1,Answer\nexten => " + vs_ext + ",2,Wait(1)\nexten => " + vs_ext + ",3,Playback(/tmp/" + vs_name
          + ", skip)\nexten => " + vs_ext + ",4,Wait(1)\nexten => " + vs_ext + ",5,Hangup\n' >> /etc/asterisk/" + DIAL_PLAN + " ; /usr/sbin/asterisk -rx reload");
      
      originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
      
      logger.info("createDialPlan() : " + originateResponse.getResponse());
      
    } catch (Exception e)
    {
      logger.warn(e);
    }
  }
  
  public void text2speech_low(String msg)
  {
    originateAction = new OriginateAction();
    originateAction.setChannel("Local/1004@dummy-wait");
    originateAction.setApplication("System");
    originateAction.setData("echo '" + msg + "' | text2wave -scale 4 -o /tmp/" + vs_name + ".ulaw -otype ulaw");
    
    try
    {
      originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
      
      logger.info("text2speech_low() : " + originateResponse.getResponse());
      
    } catch (Exception e)
    {
      logger.warn(e);
    }
  }
  
  public void cleanConfig()
  {
    try
    {
      // delete the audio files from asterisk server and logoff from the manager.
      originateAction = new OriginateAction();
      originateAction.setChannel("Local/1004@dummy-wait");
      originateAction.setApplication("System");
      
      if (vs_counter == 0)
      {
        originateAction.setData("rm -rf /tmp/*.ulaw /etc/asterisk/" + DIAL_PLAN);
        
        originateResponse = managerConnection.sendAction(originateAction, CALL_TIMEOUT);
        
        logger.info("cleanConfig() : Removed previous " + DIAL_PLAN + " and ulaw files from Asterisk configuration. " + originateResponse.getResponse());
        
      }
      
    } catch (Exception e)
    {
      logger.error("Error cleaning previous configuration for the virtual sensor. " + e);
    }
  }
  

}
