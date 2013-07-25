package org.openiot.gsn.acquisition2.wrappers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public class MigMessageWrapper2  extends AbstractWrapper2 implements net.tinyos1x.message.MessageListener, net.tinyos.message.MessageListener {

	private static int threadCounter = 0;

	protected MigMessageParameters parameters = null;

	protected net.tinyos1x.message.MoteIF moteIFTinyOS1x = null;
	protected net.tinyos.message.MoteIF moteIFTinyOS2x = null;

	protected net.tinyos1x.message.Message messageTemplateTinyOS1x = null;
	protected net.tinyos.message.Message messageTemplateTinyOS2x = null;
	
	protected static Map<String,Semaphore> moteIFList1xSemaphore = new HashMap<String,Semaphore> () ;
	protected static Map<String,net.tinyos1x.message.MoteIF> moteIFList1x = new HashMap<String,net.tinyos1x.message.MoteIF> () ;
	protected static Map<String,net.tinyos1x.packet.PhoenixSource> phoenixSourceList1x = new HashMap<String, net.tinyos1x.packet.PhoenixSource> () ;

	protected static Map<String,Semaphore> moteIFList2xSemaphore = new HashMap<String,Semaphore>();
	protected static Map<String,net.tinyos.message.MoteIF> moteIFList2x = new HashMap<String,net.tinyos.message.MoteIF>();
	protected static Map<String,net.tinyos.packet.PhoenixSource> phoenixSourceList2x = new HashMap<String, net.tinyos.packet.PhoenixSource> () ;

	private final transient Logger logger = Logger.getLogger( MigMessageWrapper2.class );

	@Override
	public boolean initialize() {
		logger.warn("tinyos wrapper initialize started...");
		try {
			parameters = new MigMessageParameters();
			parameters.initParameters(getActiveAddressBean());
			if (parameters.getTinyosVersion() == MigMessageParameters.TINYOS_VERSION_1) {
				// create a semaphore for each source/address
				Semaphore sem;
				synchronized (moteIFList1xSemaphore) {
					if (moteIFList1xSemaphore.containsKey(parameters.getTinyosSource())) {
						sem = moteIFList1xSemaphore.get(parameters.getTinyosSource());
					}
					else {
						sem = new Semaphore(1);
						moteIFList1xSemaphore.put(parameters.getTinyosSource(), sem);
					}
				}
				synchronized (phoenixSourceList1x) {
					// Create the source
					if ( ! phoenixSourceList1x.containsKey(parameters.getTinyosSource())) {
						// Create the source
						logger.debug("Create new source >" + parameters.getTinyosSource() + "<.");
						net.tinyos1x.packet.PhoenixSource phoenixSourceTinyOS1x = net.tinyos1x.packet.BuildSource.makePhoenix(parameters.getTinyosSource(), net.tinyos1x.util.PrintStreamMessenger.err);
						if (phoenixSourceTinyOS1x == null) throw new IOException ("The source >" + parameters.getTinyosSource() + "< is not valid.");
						phoenixSourceList1x.put(parameters.getTinyosSource(), phoenixSourceTinyOS1x);
						phoenixSourceTinyOS1x.setResurrection();
					}
				}
			}
			else {
				// create a semaphore for each source/address
				Semaphore sem;
				synchronized (moteIFList2xSemaphore) {
					if (moteIFList2xSemaphore.containsKey(parameters.getTinyosSource())) {
						sem = moteIFList2xSemaphore.get(parameters.getTinyosSource());
					}
					else {
						sem = new Semaphore(1);
						moteIFList2xSemaphore.put(parameters.getTinyosSource(), sem);
					}
				}
				synchronized (phoenixSourceList2x) {
					// Create the source
					if ( ! phoenixSourceList2x.containsKey(parameters.getTinyosSource())) {
						// Create the source
						logger.debug("Create new source >" + parameters.getTinyosSource() + "<.");
						net.tinyos.packet.PhoenixSource phoenixSourceTinyOS2x = net.tinyos.packet.BuildSource.makePhoenix(parameters.getTinyosSource(), net.tinyos.util.PrintStreamMessenger.err);
						if (phoenixSourceTinyOS2x == null) throw new IOException ("The source >" + parameters.getTinyosSource() + "< is not valid.");
						phoenixSourceList2x.put(parameters.getTinyosSource(), phoenixSourceTinyOS2x);
						phoenixSourceTinyOS2x.setResurrection();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		logger.warn("tinyos wrapper initialize completed ...");
		setName( "TinyOSWrapper-Thread:" + ( ++threadCounter ) );
		return true;
	}

	@Override
	public void run() {
		try {
		//
		logger.debug("Connecting to " + parameters.getTinyosSource());
		if (parameters.getTinyosVersion() == MigMessageParameters.TINYOS_VERSION_1) {

			Semaphore sem = moteIFList1xSemaphore.get(parameters.getTinyosSource());
			
			// Build the Mote interface
			sem.acquire();
			if (! moteIFList1x.containsKey(parameters.getTinyosSource())) {
				net.tinyos1x.packet.PhoenixSource phoenixSourceTinyOS1x = phoenixSourceList1x.get(parameters.getTinyosSource());
				moteIFTinyOS1x = new net.tinyos1x.message.MoteIF(phoenixSourceTinyOS1x);
				moteIFList1x.put(parameters.getTinyosSource(), moteIFTinyOS1x);
			}
			else {
				logger.debug("Reusing source >" + parameters.getTinyosSource() + "<.");
				moteIFTinyOS1x = (net.tinyos1x.message.MoteIF) moteIFList1x.get(parameters.getTinyosSource());
			}
			sem.release();

			// Register to the message type
			logger.debug("Register message >" + parameters.getTinyosMessageName() + "< to source.");
			Class<?> messageClass = Class.forName(parameters.getTinyosMessageName());
			
			if (parameters.getTinyOSMessageLength() == -1) {
				messageTemplateTinyOS1x = (net.tinyos1x.message.Message) messageClass.newInstance();
			}
			else {
				Constructor<?> messageConstructor = messageClass.getConstructor(int.class);
				messageTemplateTinyOS1x = (net.tinyos1x.message.Message) messageConstructor.newInstance(parameters.getTinyOSMessageLength());
			}
			moteIFTinyOS1x.registerListener(messageTemplateTinyOS1x, this);

		}
		else {
			
			Semaphore sem = moteIFList2xSemaphore.get(parameters.getTinyosSource());
			
			// Build the Mote interface
			sem.acquire();
			if (! moteIFList2x.containsKey(parameters.getTinyosSource())) {
				net.tinyos.packet.PhoenixSource phoenixSourceTinyOS2x = phoenixSourceList2x.get(parameters.getTinyosSource());
				moteIFTinyOS2x = new net.tinyos.message.MoteIF(phoenixSourceTinyOS2x);
				moteIFList2x.put(parameters.getTinyosSource(), moteIFTinyOS2x);
			}
			else {
				logger.debug("Reusing source >" + parameters.getTinyosSource() + "<.");
				moteIFTinyOS2x = (net.tinyos.message.MoteIF) moteIFList2x.get(parameters.getTinyosSource());
			}
			sem.release();

			// Register to the message type
			logger.debug("Register message >" + parameters.getTinyosMessageName() + "< to source.");
			Class<?> messageClass = Class.forName(parameters.getTinyosMessageName());
			
			if (parameters.getTinyOSMessageLength() == -1) {
				messageTemplateTinyOS2x = (net.tinyos.message.Message) messageClass.newInstance();
			}
			else {
				Constructor<?> messageConstructor = messageClass.getConstructor(int.class);
				messageTemplateTinyOS2x = (net.tinyos.message.Message) messageConstructor.newInstance(parameters.getTinyOSMessageLength());
			}
			moteIFTinyOS2x.registerListener(messageTemplateTinyOS2x, this);
			
		}
		logger.debug("Connected to >" + parameters.getTinyosSource() + "<");
		}
		catch (ClassNotFoundException e) {
			logger.error("Unable to find the >" + parameters.getTinyosMessageName() + "< class.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void dispose() {
		if (moteIFTinyOS1x != null && messageTemplateTinyOS1x != null) moteIFTinyOS1x.deregisterListener(messageTemplateTinyOS1x, this);
		if (moteIFTinyOS2x != null && messageTemplateTinyOS2x != null) moteIFTinyOS2x.deregisterListener(messageTemplateTinyOS2x, this);
		threadCounter--;
	}

	@Override
	public String getWrapperName() {
		return "TinyOS packet wrapper";
	}

	public MigMessageParameters getParameters() {
		return parameters;
	}

	public void messageReceived(int to, net.tinyos1x.message.Message tosmsg) {
		logger.debug("TinyOS 1.x Message received");
		postStreamElement(tosmsg.dataGet(), System.currentTimeMillis( ));
	}

	public void messageReceived(int to, net.tinyos.message.Message tosmsg) {
		logger.debug("TinyOS 2.x Message received");
		postStreamElement(tosmsg.dataGet(), System.currentTimeMillis( ));
	}
}
