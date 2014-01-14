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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.ActiveAnnouncement;
import org.openiot.cupus.artefact.ActivePublication;
import org.openiot.cupus.artefact.ActiveSubscription;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.UniqueObject;
import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.PublisherRegisterMessage;
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
 * MessageReceiver is a component of a cloud-broker whose job is to receive
 * all messages and requests from publishers and subscribers.
 * It maintains a list of all regisered subscribers and publishers
 * and has a thread for each of them that manages the communication with it
 * and delegates the received requests to other broker components (via
 * internal connection to CloudBroker superprocess) for processing.
 * 
 * @author Eugen Rozic, Aleksandar Antonic
 *
 */
public class MessageReceiver extends NetworkEntity {

	private InternalCommunicationsThread intercomm = null;

	private volatile Map<UUID, PublisherForBroker> registeredPublishers = new HashMap<UUID, PublisherForBroker>();
	private volatile Map<UUID, SubscriberForBroker> registeredSubscribers = new HashMap<UUID, SubscriberForBroker>();
        private volatile Map<UUID, MobileBrokerForBroker> registeredMobileBrokers = new HashMap<UUID, MobileBrokerForBroker>();

	private volatile Set<ActivePublication> activePublications = new HashSet<ActivePublication>();
        private volatile Set<ActiveAnnouncement> activeAnnouncements = new HashSet<ActiveAnnouncement>();

	private Object mutexPublisherList = new Object();
	private Object mutexSubscriberList = new Object();
        private Object mutexMobileBroekrList = new Object();
	private Object mutexActivePublicationsList = new Object();
        private Object mutexActiveAnnouncementsList = new Object();

	protected boolean isRunning = false;
	private boolean testing = false;
	@SuppressWarnings("unused")
	private boolean logWriting = false;
	private LogWriter log = null;

	/**
	 * Constructs the message receiver and starts it's internal communication
	 * thread.
	 */
	private MessageReceiver(String brokerName, String brokerIP, int brokerPort,
			boolean testing, boolean logWriting,
			ObjectInputStream in, ObjectOutputStream out) {
		super(brokerName, brokerIP, brokerPort);
		
		this.testing = testing;
		this.logWriting = logWriting;
		this.log = new LogWriter(brokerName+"_messageReceiver.log", logWriting, false);
		
		this.intercomm = new InternalCommunicationsThread(in, out);
		new Thread(intercomm).start();
	}

	/**
	 * Starts a new BrokerListenerThread to accept incoming connections and
	 * spawn new BrokerServingThreads to process each of the connections.
	 */
	private void start(){
		new Thread(new BrokerListenerThread()).start();
		this.isRunning = true;
		informBroker("MessageReceiver started!", false);
	}

	/**
	 * Kills the (sub)process (which will automatically close all the queue
	 * connections and close the stream to the CloudBroker whose
	 * input stream will in turn throw an EOFException that will start the
	 * shutdown of the CloudBroker and all it's children processes.
	 */
	private void shutdown(){
		isRunning = false; //not really necessary
		System.exit(-1);
	}
	
	/**
	 * Removes the publisher from the registeredPublishers map and informs the
	 * CloudBroker.
	 */
	protected void removePublisher(UUID publisherID){
		PublisherForBroker pub;
		synchronized (mutexPublisherList) {
			pub = registeredPublishers.remove(publisherID);
		}
		informBroker("Publisher "+pub+" removed (disconnected) from broker.", false);
	}
	
	/**
	 * Removes the publisher from the registeredPublishers map and informs the
	 * CloudBroker.
	 */
	protected void removeSubscriber(SubscriberUnregisterMessage msg){
		synchronized (mutexSubscriberList) {
			registeredSubscribers.remove(msg.getEntityID());
		}
		sendInternalMessage(msg);
	}
        
        /**
	 * Removes the mobile broker from the registeredMobileBroker map and informs the
	 * CloudBroker.
	 */
	protected void removeMobileBroker(MobileBrokerDisconnectMessage msg){
		synchronized (mutexMobileBroekrList) {
			registeredMobileBrokers.remove(msg.getEntityID());
		}
                synchronized (mutexSubscriberList) {
			registeredSubscribers.remove(msg.getEntityID());
		}
                synchronized (mutexPublisherList) {
			registeredPublishers.remove(msg.getEntityID());
		}
                sendInternalMessage(msg);
	}
	
	/**
	 * Checks if the carried publication is valid and passes the Attributes
	 * check. If it does it is processed and forwarded to the CloudBroker.
	 *
	 * @param pubID ID of entity that publishes
	 * @param msg The received PublishMessage carrying the publication
	 */
	protected void publish(UUID pubID, PublishMessage msg) {


		PublisherForBroker publisher = registeredPublishers.get(pubID);
		Publication publication = msg.getPublication();
		
		if (publication==null || !publication.isValid()){
			return;
		}

		if (publication instanceof HashtablePublication){
			if (msg.isUnpublish()){
				unpublishBoolean(msg, publisher);
			} else {
				publishBoolean(msg, publisher);
			}
		} else {
			informBroker("Unknown publication type received! ("+publication.getClass().getName()+")", true);
			return;
		}

	}

	/**
	 * Adds the (hashtable) publication to the set of active publications and forwards the
	 * PublishMessage to the inside of the broker for further processing.
	 */
	private void publishBoolean(PublishMessage msg, PublisherForBroker publisher){

		Publication publication = msg.getPublication();
		ActivePublication pubPair = new ActivePublication(publisher.pubID, publication);

		msg.setPublication(pubPair);
		sendInternalMessage(msg); //forward to broker for processing...
		
		synchronized (this.mutexActivePublicationsList) {
			if(!activePublications.add(pubPair)){
				informBroker("Publication "+publication+" from publisher "+publisher+" already present on broker!", false);
				return; //do nothing if the broker already had this publication...
			}
		}
	}
	
	/**
	 * Removes the (hashtable) publication from the set of active publications and forwards the
	 * PublishMessage to the inside of the broker for further processing.
	 */
	private void unpublishBoolean(PublishMessage msg, PublisherForBroker publisher){

		Publication publication = msg.getPublication();
		ActivePublication pubPair = new ActivePublication(publisher.pubID, publication);

		msg.setPublication(pubPair);
		sendInternalMessage(msg); //forward to broker for processing...
		
		synchronized (this.mutexActivePublicationsList) {
			if (!this.activePublications.remove(pubPair)){
				informBroker("(unpublish) Publication "+publication+" from publisher "+publisher+" not found on broker!", true);
				return;
			}
		}
	}

	/**
	 * Checks if the carried subscription is valid and passes the Attributes
	 * check. If it does it is processed and forwarded to the CloudBroker.
	 *
	 * @param subID ID of entity that subscribes
	 * @param msg The received SubscribeMessage carrying the subscription
	 */
	protected void subscribe(UUID subID, SubscribeMessage msg) {

		SubscriberForBroker subscriber = registeredSubscribers.get(subID);
		Subscription subscription = msg.getSubscription();
		
		if (subscription==null || !subscription.isValid()){
			return;
		} 

		if (subscription instanceof TripletSubscription){
			if (msg.isUnsubscribe()){
				unsubscribeBoolean(msg, subscriber);
			} else {
				subscribeBoolean(msg, subscriber);
			}
		} else {
			informBroker("Unknown subscription type received! ("+subscription.getClass().getName()+")", true);
			return;
		}
	}

	/**
	 * Forwards the msg to the CloudBroker for further processing...
	 * (replacing the Subscription in it with an ActiveSubscription beforehand).<br>
	 * After forwarding the msg it checks which of the activePublications are 
	 * covered by the new subscription (removing the expired ones in the process)
	 * and sends all of them that do to the DeliveryService to be delivered to
	 * the subscriber. 
	 */
	private void subscribeBoolean(SubscribeMessage msg, SubscriberForBroker subscriber){

		Subscription subscription = msg.getSubscription();
		ActiveSubscription subPair = new ActiveSubscription(subscriber.subID, subscription);

		msg.setSubscription(subPair);
		sendInternalMessage(msg); //forward to broker for processing...
		
		//removing expired publications from the activePublications set...
		List<Publication> toDeliver = new ArrayList<Publication>();
		synchronized (this.mutexActivePublicationsList) { //deliver active publications that fit to the new subscription to its subscriber
			Iterator<ActivePublication> actPubIter = activePublications.iterator();
			while (actPubIter.hasNext()){
				ActivePublication pub = actPubIter.next();
				if (pub.isValid()) {
					if (subscription.coversPublication(pub.getPublication())) {
						toDeliver.add(pub.getPublication());
					}
				} else {
					actPubIter.remove();
				}
			}
		}
		sendInternalMessage(new InitialMatchesMessage(subscriber.subID, toDeliver));
                
                //removing expired announcements from the activeAnnouncement set...
		Set<UUID> mobileBrokerstoDeliver = new HashSet<UUID>();
		synchronized (this.mutexActiveAnnouncementsList) { //deliver subscription that fit to the active announcement to its mobile broker
			Iterator<ActiveAnnouncement> actAnnIter = activeAnnouncements.iterator();
			while (actAnnIter.hasNext()){
				ActiveAnnouncement ann = actAnnIter.next();
				if (ann.isValid()) {
					if (ann.coversSubscription(subscription)) {
						mobileBrokerstoDeliver.add(ann.getMobileBrokerID());
					}
				} else {
					actAnnIter.remove();
				}
			}
		}
		sendInternalMessage(new InitialAnnouncementMatchesMessage(msg, mobileBrokerstoDeliver));
	}

	/**
	 * Forwards the msg to the CloudBroker for further processing...
	 * (replacing the Subscription in it with an ActiveSubscription beforehand)
	 */
	private void unsubscribeBoolean(SubscribeMessage msg, SubscriberForBroker subscriber){
		ActiveSubscription subPair = new ActiveSubscription(subscriber.subID, msg.getSubscription());
		msg.setSubscription(subPair);
		sendInternalMessage(msg); //forward to broker for processing...
	}	
	
        /**
	 * Checks if the carried announcement is valid and passes the Attributes
	 * check. If it does it is processed and forwarded to the CloudBroker.
	 *
	 * @param mbID ID of mobile broker entity
	 * @param msg The received SubscribeMessage carrying the subscription
	 */
        
	protected void announce(UUID mbID, AnnounceMessage msg) {

		MobileBrokerForBroker mobileBroker = registeredMobileBrokers.get(mbID);
		Announcement announcement = msg.getAnnouncement();
                
		if (announcement==null || !announcement.isValid()){
			return;
		}
                
		if (announcement instanceof TripletAnnouncement){
			if (msg.isRevokeAnnouncement()){
				revokeAnnouncementBoolean(msg, mobileBroker);
			} else {
				announceBoolean(msg, mobileBroker);
			}
		} else {
			informBroker("Unknown announcement type received! ("+announcement.getClass().getName()+")", true);
			return;
		}
	}

	/**
	 * Forwards the msg to the CloudBroker for further processing...
	 * (replacing the Announcement in it with an ActiveAnnouncement beforehand).<br>
	 */
	private void announceBoolean(AnnounceMessage msg, MobileBrokerForBroker mobileBroker){

		Announcement announcement = msg.getAnnouncement();
		ActiveAnnouncement annPair = new ActiveAnnouncement(mobileBroker.mbID, announcement);

		msg.setAnnouncement(annPair);
		sendInternalMessage(msg); //forward to broker for processing...
                
                synchronized (this.mutexActiveAnnouncementsList) {
			if(!activeAnnouncements.add(annPair)){
				informBroker("Announcement "+announcement+" from mobile broker "+mobileBroker+" already present on broker!", false);
				return; //do nothing if the broker already had this publication...
			}
		}
	}

	/**
	 * Forwards the msg to the CloudBroker for further processing...
	 * (replacing the Announcement in it with an ActiveSubscription beforehand)
	 */
	private void revokeAnnouncementBoolean(AnnounceMessage msg, MobileBrokerForBroker mobileBroker){
		ActiveAnnouncement annPair = new ActiveAnnouncement(mobileBroker.getId(), msg.getAnnouncement());
		msg.setAnnouncement(annPair);
		sendInternalMessage(msg); //forward to broker for processing...
                
                synchronized (this.mutexActiveAnnouncementsList) {
			if (!this.activeAnnouncements.remove(annPair)){
				informBroker("Revoke announcement "+annPair.getAnnouncement()+" from mobile broker "+mobileBroker+" not found on broker!", true);
				return;
			}
		}
	}
        
	/**
	 * Convinience method that sends an ErrorMessage or an InfoMessage
	 * to the CloudBroker, depending if the reporting flag is set or not.
	 * It also logs the message to this MessageReceiver's log file
	 * (if logging is on).
	 */
	protected void informBroker(String msg, boolean error){
		if (error){
			log.writeToLog("ERROR: "+msg);
			if (testing)
				sendInternalMessage(new ErrorMessage(msg));
		} else {
			log.writeToLog(msg);
			if (testing)
				sendInternalMessage(new InfoMessage(msg));
		}
	}
	
	/**
	 * Convinience method to send a message to the CloudBroker.
	 * It has to be synchronized because multiple threads may want to send
	 * something at the same time.
	 */
	synchronized protected void sendInternalMessage(Object msg){
		try {
			intercomm.out.writeObject(msg);
			intercomm.out.flush();
		} catch (IOException e){
			//if component found terminated shut everything down...
			this.shutdown();
		}
	}

	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * For communicating with the starting process (CloudBroker)
	 */
	private class InternalCommunicationsThread implements Runnable {

		ObjectInputStream in = null;
		ObjectOutputStream out = null;

		/**
		 * Sets the stream and reads from in and sets the singleton class.
		 * 
		 */
		public InternalCommunicationsThread(ObjectInputStream in, ObjectOutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			while (true){
				Object objIn = null;
				try {
					objIn = in.readObject();
				} catch (Exception e){
					MessageReceiver.this.shutdown();
				}

				if (objIn instanceof InternalMessage){
					if (objIn instanceof StartComponentMessage){
						//MessageReceiver.this.start();
					} else {
						informBroker("MessageReceiver: Unexpected internal message received from CloudBroker - "+objIn.getClass().getName(), true);
					}
				} else if (objIn instanceof Message){
					if (objIn instanceof SubscriberDisconnectMessage){
						UUID subID = ((SubscriberDisconnectMessage)objIn).getEntityID();
						SubscriberForBroker sub = registeredSubscribers.get(subID);
						sub.terminateConnection();
					} else {
						informBroker("MessageReceiver: Unexpected external message received from CloudBroker - "+objIn.getClass().getName(), true);
					}
				} else {
					String errMsg = "MessageReceiver: Received message is not of class Message" +
							" or InternalMessage! ("+objIn.getClass()+" instead). Ignoring...";
					informBroker(errMsg, true);
				}
			}
		}

	}

	/**
	 * This class is used for accepting requests from new users and
	 * creating serving threads that will serve them.
	 */
	private class BrokerListenerThread implements Runnable {

		private MessageReceiver broker = MessageReceiver.this;
		private ServerSocket serverSocket;

		/**
		 * Primary run method that upon request creates new serving thread
		 */
		public void run(){

			boolean invalidPort = true;
			int tries = 0;
			while (invalidPort) { //crating new server socket (listener)
				try {
					serverSocket = new ServerSocket(broker.myPort, 100);
					serverSocket.setReuseAddress(true);
					invalidPort = false;
				} catch (IOException e) {
					broker.myPort++;
					tries++;
					if (tries>=10) {
						String errMsg = "Port(s) in use. Stopping trying to start the broker.";
						sendInternalMessage(new ErrorMessage(errMsg));
						MessageReceiver.this.shutdown();
					}
				}
			}

			sendInternalMessage(new InfoMessage("Listener Created on port "+ serverSocket.getLocalPort()));

			while (broker.isRunning) {
				Socket s = null;
				try {
					s = serverSocket.accept();
					new Thread(new BrokerServingThread(s)).start();
				} catch (SecurityException e){
					String errMsg = "Not allowed to accept a connection from: "+s.getInetAddress().getHostAddress()+":"+s.getPort();
					informBroker(errMsg, true);
				} catch (Exception e){
					String errMsg = "Unmanagable ServerSocket exception occured. Shutting the broker down...";
					sendInternalMessage(new ErrorMessage(errMsg));
					MessageReceiver.this.shutdown();
				}
			}
		}
	}

	/**
	 * This class is used for handling the connection initiation
	 */
	private class BrokerServingThread implements Runnable {

		private MessageReceiver broker = MessageReceiver.this;
		private Socket socket;

		private Message message;

		ObjectInputStream inFromClient;
		ObjectOutputStream outToClient;

		/**
		 * @param socket Socket for communicating with client (entity - publisher/subscriber)
		 */
		public BrokerServingThread(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Primary thread method, it waits for user input (via message),
		 * recognizes what type message is and calls appropriate method.
		 */
		@Override
		public void run() {

			if (!broker.isRunning)
				return;

			try {
				outToClient = new ObjectOutputStream(socket.getOutputStream());
				outToClient.flush();
				inFromClient = new ObjectInputStream(socket.getInputStream());
			} catch (Exception e1){
				try {
					socket.close();
				} catch (Exception e2){}
				return;
			}

			Object objIn = null;

			try {
				objIn = inFromClient.readObject();
			} catch (Exception e1) {
				try {
					socket.close();
				} catch (Exception e2) {}
				return;
			}

			if (!(objIn instanceof Message)){
				try {
					socket.close();
				} catch (Exception e2) {}
				return;
			} else {
				message = (Message)objIn;
			}
			if (message instanceof SubscriberRegisterMessage) {
                registerSubscriber((SubscriberRegisterMessage)message);
			} else if (message instanceof PublisherRegisterMessage) {
				connectPublisher((PublisherRegisterMessage)message);
			} else if (message instanceof MobileBrokerRegisterMessage) {
				connectMobileBroker((MobileBrokerRegisterMessage)message);			
			} else {
				//TODO send some sort of NACK...
			}
		}

		/**
		 * Used for registering a new subscriber
		 */
		private void registerSubscriber(SubscriberRegisterMessage msg) {

			SubscriberForBroker subscriber = broker.registeredSubscribers.get(msg.getEntityID());

			if (subscriber!=null){
				informBroker("Reconnecting a previously registered subscriber "+subscriber+".", false);

				if (subscriber.setSocketAndStreams(socket, inFromClient, outToClient)){
					new Thread(subscriber).start(); //start communication thread with subscriber
				} else {
					//this should never happen...
					informBroker("Subscriber "+subscriber+" was already connected?!", false);
				}
			} else {
				subscriber = new SubscriberForBroker(msg.getEntityName(),
						socket.getInetAddress().getHostAddress(), socket.getPort(),
						msg.getEntityID(), broker);
				synchronized (mutexSubscriberList) {
					broker.registeredSubscribers.put(msg.getEntityID(), subscriber);
				}
				subscriber.setSocketAndStreams(socket, inFromClient, outToClient);
				new Thread(subscriber).start();
				informBroker("Subscriber "+subscriber+" registered.", false);
			}
			//no need for ACK sending - establishing the return connection
			//from deliveryService to subscriber will be like ACK
			sendInternalMessage(msg); //forward to DeliveryService for it to setup the queue and connection beck to subscriber etc.
		}
                
                /**
		 * Used for registering a new mobile broker
		 */
		private void connectMobileBroker(MobileBrokerRegisterMessage msg) {
                    MobileBrokerForBroker mobileBroker = broker.registeredMobileBrokers.get(msg.getEntityID());
			
                        if (mobileBroker!=null){
				informBroker("Reconnecting a previously registered mobile broker "+mobileBroker+".", false);

				if (mobileBroker.setSocketAndStreams(socket, inFromClient, outToClient)){
					new Thread(mobileBroker).start(); //start communication thread with mobile broker
				} else {
					//this should never happen...
					informBroker("Mobile broker "+mobileBroker+" was already connected?!", false);
				}
			} else {
				mobileBroker = new MobileBrokerForBroker(msg.getEntityName(),
						socket.getInetAddress().getHostAddress(), socket.getPort(),
						msg.getEntityID(), broker);
				synchronized (mutexMobileBroekrList) {
					broker.registeredMobileBrokers.put(msg.getEntityID(), mobileBroker);
				}
                                mobileBroker.setSocketAndStreams(socket, inFromClient, outToClient);
                               
                                SubscriberForBroker subscriber = new SubscriberForBroker(msg.getEntityName(),
						socket.getInetAddress().getHostAddress(), socket.getPort(),
						msg.getEntityID(), broker);
                                synchronized (mutexSubscriberList) {
					broker.registeredSubscribers.put(msg.getEntityID(), subscriber);
				}
                                subscriber.setSocketAndStreams(socket, inFromClient, outToClient);
                                
                                PublisherForBroker publisher = new PublisherForBroker(msg.getEntityName(),
						socket.getInetAddress().getHostAddress(), socket.getPort(),
						msg.getEntityID(), broker);
				synchronized (mutexPublisherList) {
					broker.registeredPublishers.put(msg.getEntityID(), publisher);
				}
				publisher.setSocketAndStreams(socket, inFromClient, outToClient);
				
				new Thread(mobileBroker).start();
				informBroker("Mobile Broker "+mobileBroker+" registered.", false);
			}
			//no need for ACK sending - establishing the return connection
			//from deliveryService to subscriber will be like ACK
			sendInternalMessage(msg); //forward to DeliveryService for it to setup the queue and connection beck to mobile broker etc.
		}

		/**
		 * Used for registering a new publisher
		 * (with publishers there is no difference between registering and connecting)
		 */
		private void connectPublisher(PublisherRegisterMessage msg) {
			informBroker("Received a publisher register message...", false);

			PublisherForBroker publisher = broker.registeredPublishers.get(msg.getEntityID());

			if (publisher!=null){ //should not happen
				if (publisher.isRunning()){
					informBroker("Publisher "+publisher+" already connected?!", true);
					return;
				} else {
					informBroker("WARNING: Publisher "+publisher+" was found registered on broker but unconnected?!", true);
					publisher.setSocketAndStreams(socket, inFromClient, outToClient);
					new Thread(publisher).start();
				}
			} else {
				publisher = new PublisherForBroker(msg.getEntityName(),
						socket.getInetAddress().getHostAddress(), socket.getPort(),
						msg.getEntityID(), broker);
				synchronized (mutexPublisherList) {
					broker.registeredPublishers.put(msg.getEntityID(), publisher);
				}
				publisher.setSocketAndStreams(socket, inFromClient, outToClient);
				new Thread(publisher).start();
				informBroker("Publisher "+publisher+" connected.", false);
			}
			//TODO send some osrt of ACK
		}
	}

	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * Used for starting of the component.
	 * It creates an instance with the received arguments and starts a thread
	 * that listens on the input stream.
	 */
	public static void main(String[] args) {

		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(System.out);
			out.flush();
			in = new ObjectInputStream(System.in);
		} catch (Exception e){
			//don't write anything, just kill the (sub)process.
			System.exit(-1);
		}

		String brokerName = null;
		String brokerIP = null;
		int brokerPort = -1;
		boolean testing = false;
		boolean logWriting = false;
		try {
			brokerName = args[0];
			brokerIP = args[1];
			if (!brokerIP.equals(UniqueObject.getLocalIP())){
				throw new Exception("IP's don't match!");
			}
			brokerPort = Integer.parseInt(args[2]);
			if (brokerPort<=1024 || brokerPort>49151){
				throw new NumberFormatException("Given port number is <1024 or >49151!");
			}
			testing = Boolean.parseBoolean(args[3]);
			logWriting = Boolean.parseBoolean(args[4]);
		} catch (IndexOutOfBoundsException e){
			String errMsg = e.getMessage()+" Not enough argument sent when starting DeliveryService! (5 needed)";
			sendObject(new ErrorMessage(errMsg), out);
			System.exit(-1);
		} catch (NumberFormatException e){
			String errMsg = e.getMessage();
			sendObject(new ErrorMessage(errMsg), out);
			System.exit(-1);
		} catch (Exception e){
			sendObject(new ErrorMessage(e.getMessage()), out);
			System.exit(-1);
		}

		//create a new MessageReceiver, a thread listening on System.in is
		//automatically started and keeps the process alive
		MessageReceiver mr = new MessageReceiver(brokerName, brokerIP, brokerPort, testing, logWriting, in, out);
		mr.start();
                
		sendObject(new InfoMessage("MessageReceiver created!"), out);
	}

	/**
	 * convinience method for sending object and terminating process if
	 * not successfull.
	 */
	private static void sendObject(Object o, ObjectOutputStream out){
		try {
			out.writeObject(o);
			out.flush();
		} catch (Exception e){
			System.exit(-1);
		}
	}

}
