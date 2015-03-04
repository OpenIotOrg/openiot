package org.openiot.gsn.wrappers;


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

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.wrappers.AbstractWrapper;

/**
 * @author Salma Abdulaziz e-mail: salma.abdulaziz@insight-centre.org
 */

public class ArduinoWrapper extends AbstractWrapper implements SerialPortEventListener  {
	SerialPort serialPort;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = {
			"/dev/ttyUSB0", // Linux
	};
	
	private DataField[] collection = new DataField[] { new DataField("light", "double", "light value from arduino") };
	private final transient Logger logger = Logger.getLogger(ArduinoWrapper.class);
	private int counter;
	private AddressBean params;
	private long rate = 1000;
	
	
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
    private BufferedReader in;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	private String port = null;
	
	private int messagesize = 2;
	
	public boolean initialize() {
	    
		CommPortIdentifier portId = null;		
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
	
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements() && port!=null) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println("Ports in hte system" + currPortId.getName());
		//		if (currPortId.getName().equals(port)) {
		//			portId = currPortId;
		//			break;
		//	}
		}

		// Obtain a CommPortIdentifier object for the port you want to open.
		try {
			portId = CommPortIdentifier.getPortIdentifier( "/dev/tty.usbmodem1411" );
		} catch ( NoSuchPortException e ) {
			logger.error( "Port doesn't exist : " + serialPort , e );
			return false;
		}
		//if (portId == null) {
		//	logger.error("Could not find COM port.");
		//	return false;
		//}
		
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
		
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		
			//set flow control and RTS
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                    | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			
			serialPort.setRTS(true);
			
			// open the streams
			//input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			//output = serialPort.getOutputStream();
			
		
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			return true;
		} catch (Exception e) {
			System.err.println(e.toString());
			return false;
		}
	}
	
	
	@Override
    
    public void serialEvent(SerialPortEvent oEvent) {
        double lightvalue=0;
        
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                
               InputStream inputStream = serialPort.getInputStream();
            //read value from file
            try {
                in = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                String str;
                if ((str = in.readLine()).length() > 0) {
                    logger.debug(in.readLine());
                     lightvalue = Double.parseDouble(str);
                }
                in.close();
                // post the data to GSN
                if(lightvalue > 0){
                    System.out.println("I am sending this data to GSN -> " + lightvalue);
                    postStreamElement(new Serializable[] {lightvalue});
                }
                
            }
                catch (IOException ex) {
                    ex.printStackTrace();
                }

                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
          
        }
    }

	@Override
	public DataField[] getOutputFormat() {
		// TODO Auto-generated method stub
		return collection;
	}

	@Override
	public void dispose() {
		//TODO Auto-generated method stub
		if (serialPort != null) {
			serialPort.removeEventListener();
           serialPort.close();
    }
		counter --;
	}

	@Override
	public String getWrapperName() {
		// TODO Auto-generated method stub
		return "Arduino Wrapper";
	}

}
