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

package org.openiot.gsn.wrappers.general;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.utils.KeyValueImp;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * Modified to used RXTX (http://users.frii.com/jarvi/rxtx/) which a LGPL
 * replacement for javacomm. The Easiest way to install RXTX is from the binary
 * distribution which is available at
 * http://users.frii.com/jarvi/rxtx/download.html Links GSN to a sensor network
 * through serial port. <p/> The only needed parameter is the serial port
 * address, provided through xml. Default connection settings are 9600 8 N 1
 * Optional parameters for the XML file: - inputseparator: if set, use this as
 * divider between data 'packets' - baudrate: set serialport baudrate (default:
 * 9600) - flowcontrolmode: set serialport flowcontrol mode possible values are: -
 * FLOWCONTROL_NONE: Flow control off. - FLOWCONTROL_RTSCTS_IN: RTS/CTS flow
 * control on input. - FLOWCONTROL_RTSCTS_OUT: RTS/CTS flow control on output. -
 * FLOWCONTROL_XONXOFF_IN: XON/XOFF flow control on input. -
 * FLOWCONTROL_XONXOFF_OUT: XON/XOFF flow control on output. - databits: set
 * serialport databits (5, 6, 7 or 8) default is 8 - stopbits: set serialport
 * stopbits (1, 2 or 1.5) default is 1 - parity: set serialport parity possiblie
 * values are: - PARITY_EVEN: EVEN parity scheme. - PARITY_MARK: MARK parity
 * scheme. - PARITY_NONE: No parity bit. (default) - PARITY_ODD: ODD parity
 * scheme. - PARITY_SPACE: SPACE parity scheme.
 */
public class SerialWrapper extends AbstractWrapper implements SerialPortEventListener {

	public static final String      RAW_PACKET    = "RAW_PACKET";

	private final transient Logger  logger        = Logger.getLogger( SerialWrapper.class );

	private SerialConnection        wnetPort;

	private int                     threadCounter = 0;

	public InputStream              is;

	private AddressBean             addressBean;

	private String                  serialPort;

	private String                  inputSeparator;

	private boolean                 useInputSeparator;

	private int                     flowControlMode;

	private int                     baudRate      = 9600;

	private int                     dataBits      = SerialPort.DATABITS_8;

	private int                     stopBits      = SerialPort.STOPBITS_1;

	private int                     parity        = SerialPort.PARITY_NONE;

	private  DataField [] dataField     ;

	private int output_format;

	private int packet_length = 100;

	/*
	 * Needs the following information from XML file : serialport : the name of
	 * the serial port (/dev/ttyS0...) Optional parameters for the XML file: -
	 * inputseparator: if set, use this as divider between data 'packets' -
	 * baudrate: set serialport baudrate (default: 9600) - flowcontrolmode: set
	 * serialport flowcontrol mode possible values are: - FLOWCONTROL_NONE: Flow
	 * control off. - FLOWCONTROL_RTSCTS_IN: RTS/CTS flow control on input. -
	 * FLOWCONTROL_RTSCTS_OUT: RTS/CTS flow control on output. -
	 * FLOWCONTROL_XONXOFF_IN: XON/XOFF flow control on input. -
	 * FLOWCONTROL_XONXOFF_OUT: XON/XOFF flow control on output. - databits: set
	 * serialport databits (5, 6, 7 or 8) default is 8 - stopbits: set serialport
	 * stopbits (1, 2 or 1.5) default is 1 - parity: set serialport parity
	 * possiblie values are: - PARITY_EVEN: EVEN parity scheme. - PARITY_MARK:
	 * MARK parity scheme. - PARITY_NONE: No parity bit. (default) - PARITY_ODD:
	 * ODD parity scheme. - PARITY_SPACE: SPACE parity scheme.
	 */
	public boolean initialize ( ) {
		setName( "SerialWrapper-Thread" + ( ++threadCounter ) );
		addressBean = getActiveAddressBean( );
		serialPort = addressBean.getPredicateValue( "serialport" );
		if ( serialPort == null || serialPort.trim( ).length( ) == 0 ) {
			logger.warn( "The >serialport< parameter is missing from the SerialWrapper, wrapper initialization failed." );
			return false;
		}

		inputSeparator = addressBean.getPredicateValue( "inputseparator" );
		if ( inputSeparator == null ) 
			useInputSeparator = false;
		else 
			useInputSeparator = true;


		String representation = addressBean.getPredicateValue( "representation" );
		
		if ( representation == null || representation.equalsIgnoreCase("binary") ){ 
			output_format=0;
		}else if (representation.startsWith("string"))
			output_format=1;
		else {
			logger.error("The provided representation >"+representation+"< is not valid, possible values are binary , string");
			return false;
		}
		
		packet_length= addressBean.getPredicateValueAsInt("packet-length",100);

		String newBaudRate = addressBean.getPredicateValue( "baudrate" );
		if ( newBaudRate != null && newBaudRate.trim( ).length( ) > 0 ) {
			baudRate = Integer.parseInt( newBaudRate ); // TODO: check validity of
			// baudrate?
		}

		String newDataBits = addressBean.getPredicateValue( "databits" );
		if ( newDataBits != null && newDataBits.trim( ).length( ) > 0 ) {
			switch ( Integer.parseInt( newDataBits ) ) {
			case 5 :
				dataBits = SerialPort.DATABITS_5;
				break;
			case 6 :
				dataBits = SerialPort.DATABITS_6;
				break;
			case 7 :
				dataBits = SerialPort.DATABITS_7;
				break;
			case 8 :
				dataBits = SerialPort.DATABITS_8;
				break;
			}
		}

		String newStopBits = addressBean.getPredicateValue( "stopbits" );
		if ( newStopBits != null && newStopBits.trim( ).length( ) > 0 ) {
			float newstopbits = Float.parseFloat( newStopBits );

			if ( newstopbits == 1.0 ) stopBits = SerialPort.STOPBITS_1;
			if ( newstopbits == 2.0 ) stopBits = SerialPort.STOPBITS_2;
			if ( newstopbits == 1.5 ) stopBits = SerialPort.STOPBITS_1_5;
		}

		String newParity = addressBean.getPredicateValue( "parity" );
		if ( newParity != null && newParity.trim( ).length( ) > 0 ) {
			if ( newParity.equals( "PARITY_EVEN" ) ) parity = SerialPort.PARITY_EVEN;
			if ( newParity.equals( "PARITY_MARK" ) ) parity = SerialPort.PARITY_MARK;
			if ( newParity.equals( "PARITY_NONE" ) ) parity = SerialPort.PARITY_NONE;
			if ( newParity.equals( "PARITY_ODD" ) ) parity = SerialPort.PARITY_ODD;
			if ( newParity.equals( "PARITY_SPACE" ) ) parity = SerialPort.PARITY_SPACE;
		}

		String newflowControlMode = addressBean.getPredicateValue( "flowcontrolmode" );
		if ( newflowControlMode != null && newflowControlMode.trim( ).length( ) > 0 ) {
			flowControlMode = 0;

			String modes[] = newflowControlMode.split( "\\|" );

			for ( int i = 0 ; i < modes.length ; i++ ) {
				if ( modes[ i ].equals( "FLOWCONTROL_NONE" ) ) flowControlMode |= SerialPort.FLOWCONTROL_NONE;
				if ( modes[ i ].equals( "FLOWCONTROL_RTSCTS_IN" ) ) flowControlMode |= SerialPort.FLOWCONTROL_RTSCTS_IN;
				if ( modes[ i ].equals( "FLOWCONTROL_RTSCTS_OUT" ) ) flowControlMode |= SerialPort.FLOWCONTROL_RTSCTS_OUT;
				if ( modes[ i ].equals( "FLOWCONTROL_XONXOFF_IN" ) ) flowControlMode |= SerialPort.FLOWCONTROL_XONXOFF_IN;
				if ( modes[ i ].equals( "FLOWCONTROL_XONXOFF_OUT" ) ) flowControlMode |= SerialPort.FLOWCONTROL_XONXOFF_OUT;
			}

			if ( flowControlMode == 0 ) {
				flowControlMode = -1; // don't set flow control mode if it is
				// empty or is only composed of invalid
				// arguments
			}
		} else {
			flowControlMode = -1;
		}

		// TASK : TRYING TO CONNECT USING THE ADDRESS
		wnetPort = new SerialConnection( serialPort );
		if ( wnetPort.openConnection( ) == false ) { return false; }
		wnetPort.addEventListener( this );
		is = wnetPort.getInputStream( );

		if ( logger.isDebugEnabled( ) ) {
			logger.debug( "Serial port wrapper successfully opened port and registered itself as listener." );
		}

		inputBuffer = new byte [ MAXBUFFERSIZE ];
		dataField = new DataField[] { new DataField( RAW_PACKET , (output_format==0?"binary":"varchar")+"("+packet_length+")" , "The packet contains raw data from a sensor network." ) };
		return true;
	}

	/**
	 * A class that handles the details of the serial connection.
	 */

	public class SerialConnection {

		protected OutputStream     os;

		protected InputStream      is;

		private CommPortIdentifier portId;

		public SerialPort          sPort;

		private String             serialPort;

		private boolean            open;

		/**
		 * Creates a SerialConnection object and initialiazes variables passed in
		 * as params.
		 * 
		 * @param serialPort A SerialParameters object.
		 */
		public SerialConnection ( String serialPort ) {
			open = false;
			this.serialPort = serialPort;
		}

		/**
		 * Attempts to open a serial connection (9600 8N1). If it is unsuccesfull
		 * at any step it returns the port to a closed state, throws a
		 * <code>SerialConnectionException</code>, and returns. <p/> Gives a
		 * timeout of 30 seconds on the portOpen to allow other applications to
		 * reliquish the port if have it open and no longer need it.
		 */
		public boolean openConnection ( ) {
			// Obtain a CommPortIdentifier object for the port you want to open.
			try {
				portId = CommPortIdentifier.getPortIdentifier( serialPort );
			} catch ( NoSuchPortException e ) {
				logger.error( "Port doesn't exist : " + serialPort , e );
				return false;
			}
			// Open the port represented by the CommPortIdentifier object.
			// Give the open call a relatively long timeout of 30 seconds to
			// allow a different application to reliquish the port if the user
			// wants to.
			if ( portId.isCurrentlyOwned( ) ) {
				logger.error( "port owned by someone else" );
				return false;
			}
			try {
				sPort = ( SerialPort ) portId.open( "GSNSerialConnection" , 30 * 1000 );

				sPort.setSerialPortParams( baudRate , dataBits , stopBits , parity );
				if ( flowControlMode != -1 ) {
					sPort.setFlowControlMode( flowControlMode );
				}
			} catch ( PortInUseException e ) {
				logger.error( e.getMessage( ) , e );
				return false;
			} catch ( UnsupportedCommOperationException e ) {
				logger.error( e.getMessage( ) , e );
				return false;
			}

			// Open the input and output streams for the connection. If they
			// won't
			// open, close the port before throwing an exception.
			try {
				os = sPort.getOutputStream( );
				is = sPort.getInputStream( );
			} catch ( IOException e ) {
				sPort.close( );
				logger.error( e.getMessage( ) , e );
				return false;
			}
			sPort.notifyOnDataAvailable( true );
			sPort.notifyOnBreakInterrupt( false );

			// Set receive timeout to allow breaking out of polling loop
			// during
			// input handling.
			try {
				sPort.enableReceiveTimeout( 30 );
			} catch ( UnsupportedCommOperationException e ) {

			}
			open = true;
			return true;
		}

		/**
		 * Close the port and clean up associated elements.
		 */
		public void closeConnection ( ) {
			// If port is alread closed just return.
			if ( !open ) { return; }
			// Check to make sure sPort has reference to avoid a NPE.
			if ( sPort != null ) {
				try {
					os.close( );
					is.close( );
				} catch ( IOException e ) {
					System.err.println( e );
				}
				sPort.close( );
			}
			open = false;
		}

		/**
		 * Send a one second break signal.
		 */
		public void sendBreak ( ) {
			sPort.sendBreak( 1000 );
		}

		/**
		 * Reports the open status of the port.
		 * 
		 * @return true if port is open, false if port is closed.
		 */
		public boolean isOpen ( ) {
			return open;
		}

		public void addEventListener ( SerialPortEventListener listener ) {
			try {
				sPort.addEventListener( listener );
			} catch ( TooManyListenersException e ) {
				sPort.close( );
				logger.warn( e.getMessage( ) , e );
			}
		}

		/**
		 * Send a byte.
		 */
		 public void sendByte ( int i ) {
			 try {
				 os.write( i );
			 } catch ( IOException e ) {
				 System.err.println( "OutputStream write error: " + e );
			 }
		 }

		 public InputStream getInputStream ( ) {
			 return is;
		 }

		 public OutputStream getOutputStream ( ) {
			 return os;
		 }

	}

	public void run ( ) {

	}

	public boolean sendToWrapper ( Object dataItem ) throws OperationNotSupportedException {
		if ( logger.isDebugEnabled( ) ) logger.debug( "Serial wrapper received a serial port sending..." );
		if ( !wnetPort.isOpen( ) ) throw new OperationNotSupportedException( "The connection is closed." );
		try {
			if ( logger.isDebugEnabled( ) ) logger.debug( "Serial wrapper performing a serial port sending." );
			if ( dataItem instanceof byte [ ] )
				wnetPort.getOutputStream( ).write( ( byte [ ] ) dataItem );
			else { // general case, writes using the printwriter.
				PrintWriter pw = new PrintWriter( wnetPort.getOutputStream( ) );
				pw.write( dataItem.toString( ) );
				pw.flush( );
				pw.close( );
			}
			return true;
		} catch ( IOException e ) {
			logger.warn( "OutputStream write error. " , e );
			return false;
		}
	}

	public DataField [] getOutputFormat ( ) {
		return dataField;
	}

	public void dispose ( ) {
		wnetPort.closeConnection( );
		threadCounter--;
	}

	private static final int MAXBUFFERSIZE = 1024;

	private byte [ ]         inputBuffer;

	public void serialEvent ( SerialPortEvent e ) {
//		if ( logger.isDebugEnabled( ) ) logger.debug( "Serial wrapper received a serial port event, reading..." );
//		if ( !isActive( ) || listeners.isEmpty( ) ) {
//		if ( logger.isDebugEnabled( ) ) logger.debug( "Serial wrapper dropped the input b/c there is no listener there or the wrapper is inactive." );
//		return;
//		}
		// Determine type of event.
		switch ( e.getEventType( ) ) {
		// Read data until -1 is returned.
		case SerialPortEvent.DATA_AVAILABLE :
			/*
			 * int index = 0; while (newData != -1) { try { if (is == null) { if
			 * (logger.isDebugEnabled ()) logger.debug("SerialWrapper: Warning,
			 * is == null !"); is = wnetPort.getInputStream(); } else newData =
			 * is.read(); if (newData > -1 && newData < 256) {
			 * inputBuffer[index++] = (byte) newData; } } catch (IOException ex) {
			 * System.err.println(ex); return; } }
			 */
			try {
				is.read( inputBuffer );
			} catch ( IOException ex ) {
				logger.warn( "Serial port wrapper couldn't read data : " + ex );
				return;
			}
			break;
			// If break event append BREAK RECEIVED message.
		case SerialPortEvent.BI :
			// messageAreaIn.append("\n--- BREAK RECEIVED ---\n");
		}

		if ( logger.isDebugEnabled( ) ) 
			logger.debug( new StringBuilder( "Serial port wrapper processed a serial port event, stringbuffer is now : " ).append( new String(inputBuffer) ).toString( ) );
		if ( useInputSeparator ) {
			for ( String chunk : new String(inputBuffer).split( inputSeparator ) ) 
				if ( chunk.length( ) > 0 ) 
					post_item(chunk);
		} else { //without separator character.
			post_item(new String(inputBuffer) );
		}
	}

	
	private void post_item (String val){
		switch (output_format){
		case 0: // for binary data
			postStreamElement(  val.length()>packet_length?val.substring(0,packet_length).getBytes():val.getBytes() );
			break;
		case 1: // for strings
			postStreamElement(  val.length()>packet_length ? val.substring(0,packet_length):val );
			break;
		}
	}
	public static void main ( String [ ] args ) {
		Properties properties = new Properties( );
		properties.put( "log4j.rootLogger" , "DEBUG,console" );
		properties.put( "log4j.appender.console" , "org.apache.log4j.ConsoleAppender" );
		properties.put( "log4j.appender.console.Threshold" , "DEBUG" );
		properties.put( "log4j.appender.console.layout" , "org.apache.log4j.PatternLayout" );
		properties.put( "log4j.appender.console.layout.ConversionPattern" , "%-6p[%d] [%t] (%13F:%L) %3x - %m%n" );
		PropertyConfigurator.configure( properties );
		Logger logger = Logger.getLogger( SerialWrapper.class );
		logger.info( "SerialWrapper Test Started" );
		SerialWrapper serialWrapper = new SerialWrapper( );
		ArrayList < KeyValueImp > predicates = new ArrayList < KeyValueImp >( );
		predicates.add( new KeyValueImp( "serialport" , "/dev/ttyUSB0" ) );
		predicates.add( new KeyValueImp( "inputseparator" , "(\n|\r|\f)" ) );
		predicates.add( new KeyValueImp( "baudrate" , "57600" ) );
		predicates.add( new KeyValueImp( "flowcontrolmode" , "FLOWCONTROL_NONE" ) );
		predicates.add( new KeyValueImp( "databits" , "8" ) );
		predicates.add( new KeyValueImp( "stopbits" , "1" ) );
		predicates.add( new KeyValueImp( "parity" , "PARITY_NONE" ) );
		predicates.add( new KeyValueImp( "host" , "localhost" ) );
		predicates.add( new KeyValueImp( "port" , "22001" ) );
		serialWrapper.setActiveAddressBean( new AddressBean( "SerialWrapper" , predicates.toArray(new KeyValueImp[] {}) ) );
		if ( !serialWrapper.initialize( ) ) {
			System.out.println( "initialization failed\n" );
		} else {
			System.out.println( "initialization successful\n" );
		}
	}
	public String getWrapperName() {
		return "serial port wrapper rs-232";
	}

}
