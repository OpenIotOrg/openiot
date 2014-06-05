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

package org.openiot.cupus.entity.subscriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.SubscriberInterface;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.NotifyMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberDisconnectMessage;
import org.openiot.cupus.message.external.SubscriberRegisterMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;
import org.openiot.cupus.util.LogWriter;
import org.openiot.cupus.util.ReadingWritingXML;

/**
 * Primary class for subscriber. It contains most functionality and contains
 * data needed for subscriber to function.
 *
 * @author Aleksandar, Eugen
 *
 */
public class Subscriber extends NetworkEntity implements SubscriberInterface {

	protected List<Publication> publicationList = new ArrayList<Publication>();
	//List of all Subscriptions
	protected List<Subscription> allSubs = new ArrayList<Subscription>();
	// List od all active subscriptions
	protected List<Subscription> activeSubs = new ArrayList<Subscription>();
	protected List<Subscription> outboxSubs = new ArrayList<Subscription>();
	private NotificationListener notificationListener;

	private boolean connected = false;
	private String myBrokerIP;
	private int myBrokerPort;
	
	private LogWriter log;
	private boolean logWriting = false;
	private boolean testing = true;

	/** Socket for sending messages (subscriptions and connect/disconnect/etc.) to the Broker */
	private Socket sendingSocket = null;
	private ObjectOutputStream sendingOut = null;
	//private ObjectInputStream sendingIn;
	
	/** Socket for incoming notifications (about matched publications) from the Broker */
	private Socket receivingSocket = null;
	//private ObjectOutputStream receivingOut;
	private ObjectInputStream receivingIn = null;

	
	private Object publicationListMutex = new Object();

	/**
	 * Constructor - subscriber can be created via configuration file or
	 * directly
	 *
	 * @param myName Subscriber's name
	 * @param myBrokerIP Publisher's connecting broker IP address
	 * @param myBrokerPort Publisher's connecting broker port
	 */
	public Subscriber(String myName, String myBrokerIP, int myBrokerPort) {
		super(myName, getLocalIP(), -1);
		
		if (this.myIP.equals("")) {
			log.writeToLog("Does not have correct IP address " + this.myIP, true);
			this.myIP = "localhost";
		}
		this.myBrokerIP = myBrokerIP;
		this.myBrokerPort = myBrokerPort;
		this.connected = false;

		log = new LogWriter(this.myName + "_subscriberLog.txt", logWriting, testing);
		log.writeToLog("Subscriber name: " + this.myName, true);
		log.writeToLog("Subscriber broker port: " + this.myBrokerPort, true);
		log.writeToLog("Subscriber broker IP: " + this.myBrokerIP, true);
		log.writeToLog("", true);
	}
	
	/**
	 * Constructor - subscriber can be created via configuration file or
	 * directly
	 */
	public Subscriber(File configFile) {
		super("", getLocalIP(), -1);
		
		if (this.myIP.equals("")) {
			log.writeToLog("Does not have correct IP address " + this.myIP, true);
			this.myIP = "localhost";
		}
		
		try {
			Properties subProps = new Properties();
			FileInputStream fileIn = new FileInputStream(configFile);
			subProps.load(fileIn);
			fileIn.close();
			
			this.myName = subProps.getProperty("subscriberName");
			if (this.myName==null)
				throw new NullPointerException("Name must be defined!");
			
			this.myBrokerIP = subProps.getProperty("brokerIP");
			if (this.myBrokerIP==null)
				throw new NullPointerException("BrokerIP must be defined!");
			
			this.myBrokerPort = Integer.parseInt(subProps.getProperty("brokerPort"));
			
			if (subProps.getProperty("testing", "false").toLowerCase().equals("false")){
				this.testing = false;
			} else if (subProps.getProperty("testing").toLowerCase().equals("true")){
				this.testing = true;
			} else {
				System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
				this.testing = false;
			}
			if (subProps.getProperty("logWriting", "true").toLowerCase().equals("true")){
				this.logWriting = true;
			} else if (subProps.getProperty("logWriting").toLowerCase().equals("false")){
				this.logWriting = false;
			} else {
				System.err.println("Config param \"logWriting\" should be either true or false! Setting to default true.");
				this.logWriting = true;
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}

		log = new LogWriter(this.myName + "_subscriberLog.txt", logWriting, testing);
		log.writeToLog("Subscriber name: " + this.myName, true);
		log.writeToLog("Subscriber broker port: " + this.myBrokerPort, true);
		log.writeToLog("Subscriber broker IP: " + this.myBrokerIP, true);
		log.writeToLog("", true);
	}

	/**
	 * Used for connecting subscriber to broker
	 */
	@Override
	public void connect() {
		if (connected){
			log.writeToLog("Connect request received while being connected. Ignored.");
			return;
		}
		try {
			sendingSocket = new Socket(this.myBrokerIP, this.myBrokerPort);
			this.myPort = sendingSocket.getLocalPort();
			sendingOut = new ObjectOutputStream(sendingSocket.getOutputStream());
			sendingOut.flush();
		} catch (UnknownHostException ex) {
			log.writeToLog("Connecting failed - Unknown Broker Host or Port: " + ex);
			try {sendingSocket.close();} catch (Exception e){}
			sendingSocket = null;
			sendingOut = null;
			return;
		} catch (IOException ex) {
			log.writeToLog("Failed to open stream to the Broker: " + ex);
			try {sendingSocket.close();} catch (Exception e){}
			sendingSocket = null;
			sendingOut = null;
			return;
		}
		
		ServerSocket brokerBackConnectSpot = null;
		try {
			brokerBackConnectSpot = new ServerSocket(0);
			brokerBackConnectSpot.setSoTimeout(10000);
		} catch (Exception e1){
			log.error("Unable to open ServerSocket for broker back-connection!");
			try {sendingSocket.close();} catch (Exception e2){}
			sendingSocket = null;
			sendingOut = null;
			return;
		}
		int brokerBackConnectPort = brokerBackConnectSpot.getLocalPort();

		//send the register message
		Message connectMessage = new SubscriberRegisterMessage(myName, this.getId(),
				myIP, brokerBackConnectPort);
                System.out.println(myIP+ " "+brokerBackConnectPort);
		this.sendMessage(connectMessage);
		
		//wait for 'response' - in the form of connect request from Broker's DeliveryService
		try {
			receivingSocket = brokerBackConnectSpot.accept();
			receivingIn = new ObjectInputStream(receivingSocket.getInputStream());
		} catch (Exception e1){
			log.error("Unable to establish back-connection from broker!");
			log.error("Exception: "+e1.getMessage());
			try {sendingSocket.close();} catch (Exception e2){}
			sendingSocket = null;
			sendingOut = null;
			return;
		}
		this.connected = true;
		log.writeToLog("Connected to Broker "+myBrokerIP+":"+myBrokerPort);

		//create broker listener that will handle incoming notify messages
		Thread brokerListener = new Thread(new SubscriberListenerThread());
		brokerListener.start();

		//processing subscriptions that were added while disconnected...
		Iterator<Subscription> iterator = outboxSubs.iterator();
		while (iterator.hasNext()) {
			subscribe(iterator.next());
			iterator.remove();
		}
	}

	/**
	 * Used for handling negative response message (reconnecting to broker)
	 *
	 * @param brokerIP IP address of reconnecting broker
	 * @param brokerPort Port number of reconnecting broker
	 */
	@Override
	public void reconnect(String brokerIP, int brokerPort) {
		if (connected){
			log.writeToLog("Disconnecting from broker (part of reconnect).");
			disconnectFromBroker();
		}
		this.myBrokerIP = brokerIP;
		this.myBrokerPort = brokerPort;
		this.connect();
	}

	/**
	 * Reconnect to last connected broker
	 */
	@Override
	public void reconnect() {
		if (connected){
			log.writeToLog("Disconnecting from broker (part of reconnect).");
			disconnectFromBroker();
		}
		this.connect();
	}

	/**
	 * Used for disconnecting from broker
	 */
	@Override
	public void disconnectFromBroker() {
		if (connected) {
			Message disconnectMessage = new SubscriberDisconnectMessage(myName, getId());
			this.sendMessage(disconnectMessage);
			//TODO FIXME no confirmation is waited for... it is just assumed the communication went ok
			terminateConnection();
			log.writeToLog("Disconnected from broker!");
		} else {
			log.writeToLog("Cannot disconnect from broker because not connected.");
		}
	}

	/**
	 * Used for unregistering from broker (meaning losing all subscriptions)
	 */
	@Override
	public void unregisterFromBroker() {
		if (connected) {
			Message unregisterMessage = new SubscriberUnregisterMessage(myName, getId());
			this.sendMessage(unregisterMessage);
			//TODO FIXME no confirmation is waited for... it is just assumed the communication went ok
			terminateConnection();
			activeSubs.clear(); //clearing them from here to be in sync with situation on broker
			outboxSubs.clear(); //same as above (because they wre made while still being registered to that broker)
			log.writeToLog("Unregistered from broker!");
		} else {
			log.writeToLog("Cannot unregister from broker because not connected.");
		}
	}
	
	/**
	 * For terminating the connection...
	 * closes to outSocket and sets everything to null
	 */
	private void terminateConnection(){
		try {
			sendingSocket.close();
			//the receiving scket will be closed from the server side
		} catch (Exception e){
			//ignore
		}
		sendingSocket = null;
		sendingOut = null;
		receivingSocket = null;
		receivingIn = null;
		this.connected = false;
	}

	/**
	 * Subscribing subscription from XML file
	 *
	 * @param fileName Name of input file
	 * @return returns subscription UUID
	 */
	public UUID subscribeFromXMLFile(String fileName) {
		return subscribe(fileName, "");
	}

	/**
	 * Subscribing subscription from string
	 *
	 * @param inputString string containing XML subscription
	 * @return returns subscription UUID
	 */
	public UUID subscribeFromXMLString(String inputString) {
		return subscribe("", inputString);
	}

	/**
	 * Subscribing subscription from XML file or content (not both - one has to
	 * be empty string (""))
	 *
	 * @param fileName Name of input file
	 * @param inputString Name of input String
	 * @return returns subscription UUID
	 */
	public UUID subscribe(String fileName, String inputString) {
		ReadingWritingXML input = new ReadingWritingXML(fileName, inputString);
		input.read();
		Subscription sub = input.createSubscription();
		this.subscribe(sub);
		return sub.getId();
	}

	/**
	 * Used for subscribing
	 *
	 * @param subscription Subscription to be subscribed
	 */
	public void subscribe(Subscription subscription) {
		if (connected) {
			Message sendMasg = new SubscribeMessage(subscription, false);
			this.sendMessage(sendMasg);
			log.writeToLog("Subscription "+subscription+" sent to broker.");
			//TODO no confirmation is waited for here...
			if (!allSubs.contains(subscription)) {
				allSubs.add(subscription);
				activeSubs.add(subscription);
			} else if (!activeSubs.contains(subscription)) {
				activeSubs.add(subscription);
			}
		} else {
			if (!allSubs.contains(subscription)) {
				outboxSubs.add(subscription);
				allSubs.add(subscription);
			} else if (!outboxSubs.contains(subscription)) {
				outboxSubs.add(subscription);
			}
			log.writeToLog("Subscription "+subscription+" put in outbox because not connected to broker.");
		}
	}

	/**
	 * Used for unsubscribing
	 *
	 * @param subscription Subscription to be unsubscribed
	 */
	public void unsubscribe(Subscription subscription) {

		if (activeSubs.contains(subscription)) {
			if (connected) {
				Message sendMsg = new SubscribeMessage(subscription, true);
				this.sendMessage(sendMsg);
				log.writeToLog("Unsubscription request sent to broker.");
			}
			activeSubs.remove(subscription);
		} else if (outboxSubs.contains(subscription)){
			outboxSubs.remove(subscription);
			log.writeToLog("Subscription unsubscribed from outbox. No need to contact the broker.");
		} else {
			log.writeToLog("Unsubscription impossible because subscription is no longer active.");
		}
	}

	/**
	 * Used for sending messages to broker
	 *
	 * @param sendMsg message to be sent
	 */
	protected void sendMessage(Message sendMsg) {
		try {
			sendingOut.writeObject(sendMsg);
			sendingOut.flush();
		} catch (Exception e1){
			log.error("Message "+sendMsg+" not sent. Disconnecting because of connection problems.");
			e1.printStackTrace();
			terminateConnection();
		}
	}

	public void setNotificationListener(
			NotificationListener notificationListener) {
		this.notificationListener = notificationListener;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean isLogWriting() {
		return logWriting;
	}

	public boolean isTesting() {
		return testing;
	}


	/**
	 * Thread for receiving notify messages from the Broker's
	 * DeliveryService component.
	 */
	private class SubscriberListenerThread implements Runnable {

		@Override
		public void run() {

			while (connected) {
				Object objIn = null;
				try {
					objIn = receivingIn.readObject();
				} catch (Exception e){
					log.error("Error on input stream from Broker. Terminating connection...");
					terminateConnection();
					log.writeToLog("Disconnected from Broker.");
					return;
				}

				if (objIn instanceof NotifyMessage) {
					NotifyMessage msg = (NotifyMessage)objIn;
					notify(msg.getPublication(), msg.isUnpublish());
				} else {
					log.writeToLog("Unkown request/response received from broker (type = "+
							objIn.getClass().getName()+"). Ignoring...");
				}
			}
		}

		/**
		 * Used for handling notifications about new publications
		 * @param publication New Publication
		 */
		public void notify(Publication publication, boolean unpublish) {
			synchronized (publicationListMutex) {
				if (!unpublish)
					publicationList.add(publication);
				else
					publicationList.remove(publication);
			}
			if (!unpublish){
				log.writeToLog("Received a publication from broker ("+publication.getId()+")");
				notificationListener.notify(getId(), myName, publication);
			} else {
				log.writeToLog("Received an unpublication from broker ("+publication.getId()+")");	
			}
		}
	}
}
