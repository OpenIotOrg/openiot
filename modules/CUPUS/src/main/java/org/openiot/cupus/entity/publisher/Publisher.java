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

package org.openiot.cupus.entity.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;
import org.openiot.cupus.artefact.HashtablePublication;

import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.entity.publisher.PublisherInterface;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.PublisherDisconnectMessage;
import org.openiot.cupus.message.external.PublisherRegisterMessage;
import org.openiot.cupus.util.LogWriter;
import org.openiot.cupus.util.ReadingWritingXML;

/**
 * This is an implementation of a publisher that knows how to communicate with
 * the cloud-broker.
 *
 */
public class Publisher extends NetworkEntity implements PublisherInterface {

	//List of all published Publications
	private ArrayList<Publication> allPubs = new ArrayList<Publication>();
	// List od all active Publications
	private ArrayList<Publication> activePubs = new ArrayList<Publication>();
	private ArrayList<Publication> outboxPubs = new ArrayList<Publication>();
	private String myBrokerIP;
	private int myBrokerPort;
	private boolean connected;
	
	private LogWriter log;
	private boolean logWriting = false;
	private boolean testing = true;

	private Socket socket;
	private ObjectOutputStream out;

	/**
	 * Constructor - publisher can be created via configuration file or directly
	 *
	 * @param myName Publisher name
	 * @param myBrokerIP Publisher's connecting broker IP address
	 * @param myBrokerPort Publisher's connecting broker port
	 */
	public Publisher(String myName, String myBrokerIP, int myBrokerPort) {
		super(myName, getLocalIP(), -1);
		if (this.myIP.equals("")) {
			log.writeToLog("Does not have correct IP address " + this.myIP);
			this.myIP = "localhost";
		}
		this.myBrokerIP = myBrokerIP;
		this.myBrokerPort = myBrokerPort;
		this.connected = false;

		log = new LogWriter(this.myName + "_publisherLog.txt", logWriting, testing);
		log.writeToLog("Publisher name: " + this.myName);
		log.writeToLog("Publisher broker port: " + this.myBrokerPort);
		log.writeToLog("Publisher broker IP: " + this.myBrokerIP);
		log.writeToLog("");
	}
	
	/**
	 * Constructor - subscriber can be created via configuration file or
	 * directly
	 */
	public Publisher(File configFile) {
		super("", getLocalIP(), -1);
		
		if (this.myIP.equals("")) {
			log.writeToLog("Does not have correct IP address " + this.myIP, true);
			this.myIP = "localhost";
		}
		
		try {
			Properties pubProps = new Properties();
			FileInputStream fileIn = new FileInputStream(configFile);
			pubProps.load(fileIn);
			fileIn.close();
			
			this.myName = pubProps.getProperty("publisherName");
			if (this.myName==null)
				throw new NullPointerException("Name must be defined!");
			
			this.myBrokerIP = pubProps.getProperty("brokerIP");
			if (this.myBrokerIP==null)
				throw new NullPointerException("BrokerIP must be defined!");
			
			this.myBrokerPort = Integer.parseInt(pubProps.getProperty("brokerPort"));
			
			if (pubProps.getProperty("testing", "false").toLowerCase().equals("false")){
				this.testing = false;
			} else if (pubProps.getProperty("testing").toLowerCase().equals("true")){
				this.testing = true;
			} else {
				System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
				this.testing = false;
			}
			if (pubProps.getProperty("logWriting", "true").toLowerCase().equals("true")){
				this.logWriting = true;
			} else if (pubProps.getProperty("logWriting").toLowerCase().equals("false")){
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
		log.writeToLog("Publisher name: " + this.myName, true);
		log.writeToLog("Publisher broker port: " + this.myBrokerPort, true);
		log.writeToLog("Publisher broker IP: " + this.myBrokerIP, true);
		log.writeToLog("", true);
	}

	/**
	 * Used for connecting publisher to broker
	 */
	@Override
	public void connect() {
		if (connected){
			log.writeToLog("Connect request received while being connected. Ignored.");
			return;
		}
		try {
			socket = new Socket(this.myBrokerIP, this.myBrokerPort);
			this.myPort = socket.getLocalPort();
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
		} catch (UnknownHostException ex) {
			log.writeToLog("Connecting failed - Unknown Broker Host or Port: " + ex);
			try {socket.close();} catch (Exception e){}
			socket = null;
			out = null;
			return;
		} catch (IOException ex) {
			log.writeToLog("Failed to open stream to the Broker: " + ex);
			try {socket.close();} catch (Exception e){}
			socket = null;
			out = null;
			return;
		}

		Message connectMessage = new PublisherRegisterMessage(myName, this.getId());
		this.sendMessage(connectMessage);
		//TODO FIXME no confirmation is waited for... it is just assumed the connection is ok.
		log.writeToLog("Connected to Broker " + myBrokerIP + " " + myBrokerPort);
		this.connected = true;

		Iterator<Publication> iteratorPublication = outboxPubs.iterator();
		while (iteratorPublication.hasNext()) {
			publish(iteratorPublication.next());
			iteratorPublication.remove();
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
	 *
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
			Message disconnectMessage = new PublisherDisconnectMessage();
			this.sendMessage(disconnectMessage);
			//TODO FIXME no confirmation is waited for... it is just assumed the communication went ok
			terminateConnection();
			log.writeToLog("Disconnected from broker!");
		} else {
			log.writeToLog("Cannot disconnect from broker because not connected.");
		}
	}
	
	/**
	 * For terminating the connection...
	 * closes to outSocket and sets everything to null
	 */
	private void terminateConnection(){
		try {
			socket.close();
			//the receiving scket will be closed from the server side
		} catch (Exception e){
			//ignore
		}
		socket = null;
		out = null;
		this.connected = false;
	}

	/**
	 * Publishing publication from XML file
	 *
	 * @param fileName Name of input file
	 * @return returns publication UUID
	 */
	public UUID publishFromXMLFile(String fileName) {
		return publish(fileName, "");
	}

	/**
	 * Publishing publication from string
	 *
	 * @param inputString string containing XML publication
	 * @return  returns publication UUID
	 */
	public UUID publishFromXMLString(String inputString) {
		return publish("", inputString);
	}

	/**
	 * Publishing publication from XML file or content (not both - one has to be
	 * empty string (""))
	 *
	 * @param fileName Name of input file
	 * @param inputString Name of input String
	 * @return returns publication UUID 
	 */
	public UUID publish(String fileName, String inputString) {
		ReadingWritingXML input = new ReadingWritingXML(fileName, inputString);
		if (fileName.equalsIgnoreCase("")) {
			input.readString();
		} else {
			input.readFile();
		}
		Publication pub = input.createPublication();
		this.publish(pub);
		return pub.getId();
	}

	/**
	 * Used for publishing new publication
	 *
	 * @param publication Publication to be published
	 */
	public void publish(Publication publication) {
		if (connected) {
			Message sendMsg = new PublishMessage((HashtablePublication)publication, false);
			this.sendMessage(sendMsg);
			log.writeToLog("Publication "+publication+" sent to broker.");
			//TODO no confirmation is waited for here...
			activePubs.add(publication);
			allPubs.add(publication);
		} else {
			outboxPubs.add(publication);
			allPubs.add(publication);
			log.writeToLog("Publication "+publication+" put in outbox because not connected to broker.");
		}
	}

	/**
	 * Used for unpublishing old publication
	 *
	 * @param publication Publication to be unpublished
	 */
	public void unpublish(Publication publication) {

		if (activePubs.contains(publication)) {
			if (connected) {
				Message sendMsg = new PublishMessage(publication, true);
				this.sendMessage(sendMsg);
				log.writeToLog("Unpublication request sent to broker.");
			}
			activePubs.remove(publication);
		} else if (outboxPubs.contains(publication)){
			outboxPubs.remove(publication);
			log.writeToLog("Publication unpublished from outbox. No need to contact the broker.");
		} else {
			log.writeToLog("Unpublication impossible because publication is no longer active.");
		}
	}

	/**
	 * Used for sending messages to broker
	 *
	 * @param sendMasg message to be sent
	 */
	protected void sendMessage(Message sendMasg) {
		try {
			out.writeObject(sendMasg);
			out.flush();
		} catch (Exception e1){
			log.error("Message "+sendMasg+" not sent. Disconnecting because of connection problems.");
			e1.printStackTrace();
			terminateConnection();
		}
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

}
