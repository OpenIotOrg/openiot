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

package org.openiot.cupus.entity.broker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import org.openiot.cupus.common.UniqueObject;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberDisconnectMessage;
import org.openiot.cupus.message.external.SubscriberRegisterMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;
import org.openiot.cupus.message.internal.ErrorMessage;
import org.openiot.cupus.message.internal.InfoMessage;
import org.openiot.cupus.message.internal.InitialAnnouncementMatchesMessage;
import org.openiot.cupus.message.internal.InitialMatchesMessage;
import org.openiot.cupus.message.internal.StartComponentMessage;
import org.openiot.cupus.util.LogWriter;

/**
 * This is the main cloud-broker class. It represents the cloud-broker which is
 * instantiated, started and stopped through it.
 * The CloudBroker object starts the three broker components (MessageReceiver,
 * DeliveryService and (initial) Matcher) as separate processes and relays the
 * communication between them by forwarding from input streams to the proper
 * output streams (for example reading the in from MessageReceiver and sending it to
 * the out of the DeliveryService for communication between those two components)
 * 
 * @author Eugen Rozic
 *
 */
public class CloudBroker {

	private String brokerName;
	private String brokerIP;
	private int brokerPort;
	private int internalUDPPort;

	private int queueCapacity;

	private LogWriter log;
	private boolean logWriting = true;
	private boolean testing = false;
	
	private String classpath = null;

	private Process messageReceiver = null;
	private MessageReceiverRelay messageReceiverRelay = null;

	private Process deliveryService = null;
	private DeliveryServiceRelay deliveryServiceRelay = null;

	private int numberOfMatchers;
	private Process[] matchers;
	private MatcherRelay[] matcherRelays;
	private int matcherRoundRobin = 0;

	/**
	 * The main and only constructor. It takes in a configuration file
	 * that specifies all information the broker needs, the broker name and port.
	 * 
	 * It then creates the needed components and starts the threads that control
	 * the information flow from and to them.
	 * 
	 * @param configFile
	 */
	public CloudBroker(File configFile, String classpath) {

		this.classpath = classpath;
		
		//reads properties and instantiates and sets everything...
		try {
			Properties brokerProps = new Properties();
			FileInputStream fileIn = new FileInputStream(configFile); 
			brokerProps.load(fileIn);
			fileIn.close();

			this.brokerName = brokerProps.getProperty("brokerName");
			if (this.brokerName==null)
				throw new NullPointerException("Name must be defined!");
			this.brokerPort = Integer.parseInt(brokerProps.getProperty("brokerPort"));
			this.internalUDPPort = Integer.parseInt(
					brokerProps.getProperty("internalUDPPort"));
			this.brokerIP = UniqueObject.getLocalIP();

			this.queueCapacity = Integer.parseInt(brokerProps.getProperty("queueCapacity"));

			this.numberOfMatchers = Integer.parseInt(
					brokerProps.getProperty("numberOfMatchers"));

			if (brokerProps.getProperty("testing", "false").toLowerCase().equals("false")){
				this.testing = false;
			} else if (brokerProps.getProperty("testing").toLowerCase().equals("true")){
				this.testing = true;
			} else {
				System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
				this.testing = false;
			}
			if (brokerProps.getProperty("logWriting", "true").toLowerCase().equals("true")){
				this.logWriting = true;
			} else if (brokerProps.getProperty("logWriting").toLowerCase().equals("false")){
				this.logWriting = false;
			} else {
				System.err.println("Config param \"logWriting\" should be either true or false! Setting to default true.");
				this.logWriting = true;
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}

		log = new LogWriter(this.brokerName + ".log", logWriting, testing);

		try {
			initMessageReceiver();
			initDeliveryService();
			initMatchers();
		} catch (Exception e){
			e.printStackTrace();
			shutdown();
		}

		log.writeToLog("Broker name: " + this.brokerName, true);
		log.writeToLog("Broker port: " + this.brokerPort, true);

		if (brokerIP.equals("")) {
			log.writeToLog("Broker IP address: unidentifiable?!", true);
		} else {
			log.writeToLog("Broker IP address: "+this.brokerIP, true);
		}
		log.writeToLog("",true); //empty line
	}

	/**
	 * Sends the start messages to the MessageReceiver and the DeliveryService
	 * components of the broker so they can start accepting accepting and processing
	 * requests and sending answers to them.
	 */
	public void start(){
		messageReceiverRelay.sendStartMessage();
		deliveryServiceRelay.sendStartMessage();
	}

	/**
	 * Kills the broker.
	 * It does so by just terminating the process which will automatically
	 * cause all output streams to close which will in turn cause all input
	 * streams on the receiving side to throw an EOFException which will
	 * cause the starting of shutdown of all the components (subprocesses)
	 * this CloudBroker is connected to.
	 */
	public void shutdown(){
		log.writeToLog("Shutting down all Broker components!", true);
		System.exit(-1);
	}

	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * Starts the MessageReceiver process and a thread to manage it's input/output.
	 */
	private void initMessageReceiver() throws IOException {
		String[] cmd = new String[]{"java", "-cp", classpath,
				MessageReceiver.class.getName(),
				brokerName, brokerIP, Integer.toString(brokerPort),
				Boolean.toString(testing), Boolean.toString(logWriting)};
		ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
		messageReceiver = builder.start();

		messageReceiverRelay = new MessageReceiverRelay();
		new Thread(messageReceiverRelay).start();

		messageReceiverRelay.out.flush();
	}

	/**
	 * Starts the DeliveryService process and a thread to manage it's input/output.
	 */
	private void initDeliveryService() throws IOException {
		String[] cmd = new String[]{"java", "-cp", classpath,
				DeliveryService.class.getName(),
				brokerName, brokerIP, Integer.toString(internalUDPPort),
				Integer.toString(queueCapacity),
				Boolean.toString(testing), Boolean.toString(logWriting)};
		ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
		deliveryService = builder.start();

		deliveryServiceRelay = new DeliveryServiceRelay();
		new Thread(deliveryServiceRelay).start();
	}

	/**
	 * Starts the Matcher processes and a thread for each to manage it's input/output.
	 */
	private void initMatchers() throws IOException {
		matchers = new Process[numberOfMatchers];
		matcherRelays = new MatcherRelay[numberOfMatchers];
		
		for (int matcherID=0; matcherID<numberOfMatchers; matcherID++){
			String[] cmd = new String[]{"java", "-cp", classpath,
					Matcher.class.getName(), Integer.toString(matcherID),
					Integer.toString(internalUDPPort),
					Boolean.toString(testing), Boolean.toString(logWriting)};
			ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
			matchers[matcherID] = builder.start();

			MatcherRelay matcherRelay = new MatcherRelay(matcherID);
			matcherRelays[matcherID] = matcherRelay;
			new Thread(matcherRelay).start();

			matcherRelay.out.flush();
		}
	}

	/**
	 * Convinience method to send a message to an output stream...
	 */
	private void sendMessage(Object msg, ObjectOutputStream out){
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e){
			log.error("Sending message failed: "+e.getMessage());
			this.shutdown();
		}
	}

	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * Manages the in/out streams of the messageReceiver subprocess...
	 */
	private class MessageReceiverRelay implements Runnable {

		ObjectInputStream in;
		ObjectOutputStream out;

		public MessageReceiverRelay() {
			try {
				// catches both the stdout and stderr of the subprocess
				out = new ObjectOutputStream(
						messageReceiver.getOutputStream());
				out.flush();
				in = new ObjectInputStream(messageReceiver.getInputStream());
			} catch (Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
				CloudBroker.this.shutdown();
			}
		}

		@Override
		public void run() {
			while (true){
				Object objIn = null;
				try {
					objIn = in.readObject();
				} catch (Exception e){
					log.error("Unable to read from MessageReceiver's input stream! "+e.getMessage());
					CloudBroker.this.shutdown();
					return;
				}

				if (objIn instanceof InternalMessage){
					if (objIn instanceof InitialMatchesMessage){
						//forward to deliveryService to send the initial notify msgs
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof InitialAnnouncementMatchesMessage){
						//forward to deliveryService to send the initial announcement msgs
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof ErrorMessage){
						log.error(((ErrorMessage)objIn).getContents());
					} else if (objIn instanceof InfoMessage) {
						log.writeToLog(((InfoMessage)objIn).getContents());
					} else {
						log.error("Unexpected internal message received from MessageReceiver - "+objIn.getClass().getName());
					}
				} else if (objIn instanceof Message){
                                    //System.out.println("CloudBroker!" + objIn.getClass());
					if (objIn instanceof SubscriberRegisterMessage){
						//forward to deliveryService to establish a connection
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof SubscriberDisconnectMessage){
						//forward to deliveryService to kill the queue
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof SubscriberUnregisterMessage){
						//forward to deliveryService to kill and remove the queue
						sendMessage(objIn, deliveryServiceRelay.out);
						//forward to all Matchers to remove all subscribers subscriptions
						for (int i=0; i<numberOfMatchers; i++){
							sendMessage(objIn, matcherRelays[i].out);
						}
					} else if (objIn instanceof MobileBrokerRegisterMessage){
						//forward to deliveryService to establish a connection
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof MobileBrokerDisconnectMessage){
						//forward to deliveryService to kill the queue
						sendMessage(objIn, deliveryServiceRelay.out);
					} else if (objIn instanceof SubscribeMessage){
						SubscribeMessage msg = (SubscribeMessage)objIn;
						log.writeToLog("Received "
								+(msg.isUnsubscribe()?"unsubscription ":"subscription ")
								+ msg.getSubscription()+".");

						//forward to a matcher to process...
						sendMessage(objIn, matcherRelays[matcherRoundRobin].out);
						matcherRoundRobin = (matcherRoundRobin+1)%numberOfMatchers;

					} else if (objIn instanceof PublishMessage){

						PublishMessage msg = (PublishMessage)objIn;
						log.writeToLog("Received "
								+(msg.isUnpublish()?"unpublication ":"publication ")
								+ msg.getPublication()+".");
						//forward to all matchers to process...
						for (int i=0; i<numberOfMatchers; i++){
							sendMessage(objIn, matcherRelays[i].out);
						}
						
					} else if (objIn instanceof AnnounceMessage){
						
						AnnounceMessage msg = (AnnounceMessage)objIn;
						log.writeToLog("Received "
								+(msg.isRevokeAnnouncement()?"revoke announcement ":"announcement ")
								+ msg.getAnnouncement()+".");
						//forward to all matchers to process...
						for (int i=0; i<numberOfMatchers; i++){
							sendMessage(objIn, matcherRelays[i].out);
						}
					} else {
						log.error("Unexpected external message received from MessageReceiver - "+objIn.getClass().getName());
					}
				} else {
					log.error("Object received from the MessageReceiver is not of class Message" +
							" or InternalMessage! ("+objIn.getClass().getName()+" instead: "+objIn.toString()+" ). Ignoring...");
				}
			}
		}

		/**
		 * Sends a start message to the MessageReceiver components of the cloud broker.
		 * It should make the MessageReceiver start the thread that listens for
		 * incoming connection requests from subscribers and publishers.
		 */
		void sendStartMessage(){
			try {
				out.writeObject(new StartComponentMessage());
				out.flush();
			} catch (IOException e){
				//if messageReceiver found terminated shut everything down...
				log.error("MessageReceiver found dead while sending the start message.");
				CloudBroker.this.shutdown();
			}
		}
	}

	/**
	 * Manages the in/out streams of the deliveryService subprocess...
	 */
	private class DeliveryServiceRelay implements Runnable {

		ObjectInputStream in;
		ObjectOutputStream out;

		public DeliveryServiceRelay() {
			try {
				// catches both the stdout and stderr of the subprocess
				out = new ObjectOutputStream(
						deliveryService.getOutputStream());
				out.flush();
				in = new ObjectInputStream(
						deliveryService.getInputStream());
			} catch (Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
				CloudBroker.this.shutdown();
			}
		}

		@Override
		public void run() {
			while (true){
				Object objIn = null;
				try {
					objIn = in.readObject();
				} catch (Exception e){
					log.error("Unable to read from DeliveryService's input stream! "+e.getMessage());
					CloudBroker.this.shutdown();
					return;
				}

				if (objIn instanceof InternalMessage){
					if (objIn instanceof ErrorMessage){
						log.error(((ErrorMessage)objIn).getContents());
					} else if (objIn instanceof InfoMessage) {
						log.writeToLog(((InfoMessage)objIn).getContents());
					} else {
						log.error("Unexpected internal message received from DeliveryService - "+objIn.getClass().getName());
					}
				} else if (objIn instanceof Message){
					if (objIn instanceof SubscriberDisconnectMessage){
						log.writeToLog("Subscriber "+((SubscriberDisconnectMessage)objIn).getEntityName()+
								" disconnected from broker!");
						//forward to messageReceiver to kill the SubForBroker
						sendMessage(objIn, messageReceiverRelay.out);
					} else {
						log.error("Unexpected external message received from DeliveryService - "+objIn.getClass().getName());
					}
				} else {
					log.error("Object received from the DeliveryService is not of class Message" +
							" or InternalMessage! ("+objIn.getClass().getName()+" instead). Ignoring...");
				}
			}
		}

		/**
		 * Sends a start message to the DeliveryService components of the cloud broker.
		 * It should make the DeliveryService start the thread that listens for
		 * incoming connection requests from subscribers.
		 */
		void sendStartMessage(){
			try {
				out.writeObject(new StartComponentMessage());
				out.flush();
			} catch (IOException e){
				//if deliveryService found terminated shut everything down...
				log.error("DeliveryService found dead while sending the start message.");
				CloudBroker.this.shutdown();
			}
		}
	}

	/**
	 * Manages the in/out streams of the rootMatcher subprocess...
	 */
	private class MatcherRelay implements Runnable {

		ObjectInputStream in;
		ObjectOutputStream out;

		private int matcherID;

		public MatcherRelay(int matcherID) {
			this.matcherID = matcherID;
			try {
				// catches both the stdout and stderr of the subprocess
				out = new ObjectOutputStream(
						matchers[matcherID].getOutputStream());
				out.flush();
				in = new ObjectInputStream(
						matchers[matcherID].getInputStream());
			} catch (Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
				CloudBroker.this.shutdown();
			}
		}

		@Override
		public void run() {
			while (true){
				Object objIn = null;
				try {
					objIn = in.readObject();
				} catch (Exception e){
					log.error("Unable to read from Matcher "+matcherID+"'s input stream! "+e.getMessage());
					CloudBroker.this.shutdown();
					return;
				}

				if (objIn instanceof InternalMessage){
					if (objIn instanceof ErrorMessage){
						log.error(((ErrorMessage)objIn).getContents());
					} else if (objIn instanceof InfoMessage) {
						log.writeToLog(((InfoMessage)objIn).getContents());
					} else {
						log.error("Unexpected internal message received from RootMatcher - "+objIn.getClass().getName());
					}
				} else if (objIn instanceof Message){
					log.error("Unexpected external message received from root Matcher - "+objIn.getClass().getName());
				} else {
					log.error("Object received from the root Matcher is not of class Message" +
							" or InternalMessage! ("+objIn.getClass().getName()+" instead). Ignoring...");
				}
			}
		}

	}

}
