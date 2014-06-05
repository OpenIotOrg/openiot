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

import com.google.android.gcm.server.Sender;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.ActivePublication;
import org.openiot.cupus.artefact.ActiveSubscription;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TopKWSubscription;
import org.openiot.cupus.common.UniqueObject;
import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterGCMMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.NotifyMessage;
import org.openiot.cupus.message.external.NotifySubscriptionMessage;
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
 * DeliveryService is a component of a cloud-broker whose job is to keep and
 * maintain a directory of subscriber queues and to receive (UDP) pulish
 * messages from Matcher processes and put them in their respectivy queues.
 *
 * @author Eugen Rozic
 *
 */
public class DeliveryService extends NetworkEntity {

    private InternalCommunicationsThread intercomm = null;

    private HashMap<UUID, SubscriberQueue> queueDirectory = null;
    private int queueCapacity = -1;

    private boolean testing = false;
    @SuppressWarnings("unused")
    private boolean logWriting = false;
    private LogWriter log = null;

    protected boolean isRunning = false;
    
    
    private Sender gcmSender;
    private String APIkey = "AIzaSyADsn6N5Rq6an73KnSaEt3geI-BHy5nBdY";
    
    //testing variables
    private UUID mb;
    private UUID sub;
    /**
     * Constructs the delivery service and starts it's internal communication
     * thread.
     *
     * @param UDPport The port on which this will listen and wait for
     * connections
     */
    private DeliveryService(String brokerName, String brokerIP, int UDPport,
            int queueCapacity, boolean testing, boolean logWriting, String ApiKey,
            ObjectInputStream in, ObjectOutputStream out) {
        super(brokerName, brokerIP, UDPport);

        queueDirectory = new HashMap<UUID, SubscriberQueue>();
        this.queueCapacity = queueCapacity;

        this.testing = testing;
        this.logWriting = logWriting;
        log = new LogWriter(brokerName + "_deliveryService.log", logWriting, false);
        
        intercomm = new InternalCommunicationsThread(in, out);
        new Thread(intercomm).start();
        
        this.APIkey = ApiKey;
        if (this.APIkey != null && !this.APIkey.isEmpty()) {
            this.gcmSender = new Sender(APIkey);
        }
    }

    /**
     * Creates a UDP socket a thread that manages all incoming packets to it.
     * All notifications from all Matchers about matched subscribers are sent
     * through it.
     */
    private void start() {
        new Thread(new NotifyReceiverThread()).start();
        isRunning = true;
        informBroker("DeliveryService started!", false);
    }

    /**
     * Kills the (sub)process (which will automatically close all the
     * connections to the pubs and subs and close the stream to the CloudBroker
     * whose input stream will in turn throw an EOFException that will start the
     * shutdown of the CloudBroker and all it's children processes.
     */
    private void shutdown() {
        isRunning = false; //not really necessary
        System.exit(-1);
    }

    /**
     * Puts a NotifyMessage with a publication in each of the subscribers queues
     * (a check for duplicates is done before adding to the queue).
     */
    private void notifySubscribers(PublishMessage pubMsg, Set<UUID> subscribers) {
        Publication pub = null;
        if (pubMsg.getPublication() instanceof ActivePublication) {
        pub = ((ActivePublication) pubMsg.getPublication()).getPublication();
        } else if (pubMsg.getPublication() instanceof HashtablePublication) {
         pub = pubMsg.getPublication();
        }
        for (UUID subscriber : subscribers) {
        	sub = subscriber;
            SubscriberQueue queue = queueDirectory.get(subscriber);
            NotifyMessage msg = new NotifyMessage(pub, false);
            if (pubMsg.isUnpublish()) {
                boolean found = queue.remove(msg);
                if (!found) { //if not found locally must have already been sent to subscriber, in which case he should be notified that the publication was unpublished... 
                    msg.setUnpublish(true);
                    queue.put(msg);
                }
            } else {
                queue.put(msg);
            }
        }
    }

    /**
     * Puts a NotifyMessage in the subscriber's queue (the one identified by the
     * given UUID) for each of the publications in the pubs list.<br>
     * Used for notifing a subscriber about the initial matches to a
     * subscription he just subscribed (the matches to the beforehand published
     * but still active publications).
     */
    private void bulkNotify(UUID subscriber, List<Publication> pubs) {
        SubscriberQueue queue = queueDirectory.get(subscriber);
        for (Publication pub : pubs) {
            NotifyMessage msg = new NotifyMessage(pub, false);
            queue.put(msg);
        }
    }
    
    private void notifyMobileBroker(UUID mbID, Set<Subscription> subs) {
    	mb = mbID;
        SubscriberQueue queue = queueDirectory.get(mbID);
        for (Subscription sub : subs) {
            NotifySubscriptionMessage msg = new NotifySubscriptionMessage(sub, false);
            queue.put(msg);
        }
    }
    
    private void notifyMobileBrokers(SubscribeMessage subscription, Set<UUID> mobileBrokerIDs) {
        Subscription subForSending = null;
        if (subscription.getSubscription() instanceof ActiveSubscription) {
            subForSending = ((ActiveSubscription)subscription.getSubscription()).getSubscription();
        } else if (subscription.getSubscription() instanceof MemorySubscription) {
            subForSending = ((MemorySubscription)subscription.getSubscription()).getSubscription();
        } else {
            subForSending = subscription.getSubscription();
        }
        for (UUID mb : mobileBrokerIDs) {
            SubscriberQueue queue = queueDirectory.get(mb);
            NotifySubscriptionMessage msg = new NotifySubscriptionMessage(subForSending, false);
            if (subscription.isUnsubscribe()) {
                boolean found = queue.remove(msg);
                if (!found) { //if not found locally must have already been sent to subscriber, in which case he should be notified that the publication was unpublished... 
                    msg.setRevoke(true);
                    queue.put(msg);
                }
            } else {
                queue.put(msg);
            }
        }
    }

    /**
     * Convinience method that sends an ErrorMessage or an InfoMessage to the
     * CloudBroker, depending if the reporting flag is set or not. It also logs
     * the message to this DeliveryService's log file (if logging is on).
     */
    protected void informBroker(String msg, boolean error) {
        if (error) {
            log.writeToLog("ERROR: " + msg);
            if (testing) {
                sendInternalMessage(new ErrorMessage(msg));
            }
        } else {
            log.writeToLog(msg);
            if (testing) {
                sendInternalMessage(new InfoMessage(msg));
            }
        }
    }

    /**
     * Convinience method to send a message to the CloudBroker. It has to be
     * synchronized because multiple threads may want to send something at the
     * same time.
     */
    synchronized protected void sendInternalMessage(Object msg) {
        try {
            intercomm.out.writeObject(msg);
            intercomm.out.flush();
        } catch (IOException e) {
            //if component found terminated shut everything down...
            this.shutdown();
        }
    }
    
    synchronized protected Sender getSender() {
        return gcmSender;
    }

	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * For communicating with the starting process (CloudBroker)
     */
    private class InternalCommunicationsThread implements Runnable {

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        public InternalCommunicationsThread(ObjectInputStream in, ObjectOutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            while (true) {
                Object objIn = null;
                try {
                    objIn = in.readObject();
                } catch (Exception e) {
                    DeliveryService.this.shutdown();
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof StartComponentMessage) {
                        //DeliveryService.this.start();
                    } else if (objIn instanceof InitialMatchesMessage) {
                        //notify the subscriber about all of the publications
                        bulkNotify(((InitialMatchesMessage) objIn).getSubscriberID(),
                                ((InitialMatchesMessage) objIn).getInitialMatches());
                    } else if (objIn instanceof InitialAnnouncementMatchesMessage) {
                        //notify the subscriber about all of the publications
                        notifyMobileBrokers(((InitialAnnouncementMatchesMessage) objIn).getSubscriptionMessage(),
                                ((InitialAnnouncementMatchesMessage) objIn).getMobileBrokerIDs());
                    } else {
                        informBroker("DeliveryService: Unexpected internal message received from CloudBroker - " + objIn.getClass().getName(), true);
                    }
                } else if (objIn instanceof Message) {
                    if (objIn instanceof SubscriberDisconnectMessage) {
                        UUID subID = ((SubscriberDisconnectMessage) objIn).getEntityID();
                        SubscriberQueue queue = queueDirectory.get(subID);
                        if (queue != null){
                            queue.terminateConnection();
                        }
                        informBroker("Subscriber " + ((SubscriberDisconnectMessage) objIn).getEntityName()
                                + " disconnected from broker!", false);
                    } else if (objIn instanceof SubscriberUnregisterMessage) {
                        UUID subID = ((SubscriberUnregisterMessage) objIn).getEntityID();
                        SubscriberQueue queue = queueDirectory.remove(subID);
                        if (queue != null){
                            queue.terminateConnection();
                        }
                        informBroker("Subscriber " + ((SubscriberUnregisterMessage) objIn).getEntityName()
                                + " unregistered from broker!", false);
                    } else if (objIn instanceof SubscriberRegisterMessage) {
                        SubscriberRegisterMessage msg = (SubscriberRegisterMessage) objIn;
                        SubscriberQueue queue = new SubscriberQueue(msg.getEntityID(), queueCapacity, DeliveryService.this, false);
                        queueDirectory.put(msg.getEntityID(), queue);
                        Socket socket = null;
                        
                        try {
                            socket = new Socket(msg.getIP(), msg.getPort());
                            queue.setConnection(socket);
                        } catch (Exception e) {
                            //if not able to make connection...
                            queue.terminateConnection(); //just in case
                            sendInternalMessage(new SubscriberDisconnectMessage(
                                    msg.getEntityName(), msg.getEntityID()));
                            continue; //go wait for next message
                        }
                        
                        //if connection successfully made...
                        new Thread(queue).start(); //start communication thread
                        informBroker("Subscriber " + ((SubscriberRegisterMessage) objIn).getEntityName()
                                + " connected to broker!", false);
                    } else if (objIn instanceof MobileBrokerDisconnectMessage) {
                        UUID mbID = ((MobileBrokerDisconnectMessage) objIn).getEntityID();
                        SubscriberQueue queue = queueDirectory.remove(mbID);
                       if (queue != null){
                            queue.terminateConnection();
                        }
                        informBroker("Mobile broker " + ((MobileBrokerDisconnectMessage) objIn).getEntityName()
                                + " unregistered from broker!", false);
                    } else if (objIn instanceof MobileBrokerRegisterMessage) {
                        MobileBrokerRegisterMessage msg = (MobileBrokerRegisterMessage) objIn;
                        SubscriberQueue queue = new SubscriberQueue(msg.getEntityID(), queueCapacity, DeliveryService.this, false);
                        queueDirectory.put(msg.getEntityID(), queue);
                        Socket socket = null;
                        try {
                            socket = new Socket(msg.getIP(), msg.getPort());
                            queue.setConnection(socket);
                        } catch (Exception e) {
                            //if not able to make connection...
                            queue.terminateConnection(); //just in case
                            sendInternalMessage(new MobileBrokerDisconnectMessage(
                                    msg.getEntityName(), msg.getEntityID()));
                            continue; //go wait for next message
                        }
                        //if connection successfully made...
                        new Thread(queue).start(); //start communication thread
                        informBroker("Mobile broker " + ((MobileBrokerRegisterMessage) objIn).getEntityName()
                                + " connected to broker!", false);
                    } else if (objIn instanceof MobileBrokerRegisterGCMMessage) {
                        MobileBrokerRegisterGCMMessage msg = (MobileBrokerRegisterGCMMessage) objIn;
                        
                        SubscriberQueue queue = new SubscriberQueue(msg.getEntityID(), queueCapacity, DeliveryService.this, true);
                        queueDirectory.put(msg.getEntityID(), queue);
                        if (msg.getRegistrationID() != null && !msg.getRegistrationID().isEmpty()) {
                            //informBroker("User RegId="+msg.getRegistrationID(), false);
                            queue.setGCMId(msg.getRegistrationID());
                        } else {
                            if (msg.getRegistrationID() == null || msg.getRegistrationID().isEmpty()) {
                                informBroker("GCM user " + msg.getEntityName() + " did not provide GCM registration ID", false);
                            }
                            //if a user did not send a registration key
                            queue.terminateConnection(); //just in case
                            sendInternalMessage(new MobileBrokerDisconnectMessage(
                                    msg.getEntityName(), msg.getEntityID()));
                            continue; //go wait for next message
                        }
                        //if connection successfully made...
                        new Thread(queue).start(); //start communication thread
                        informBroker("Mobile broker " + ((MobileBrokerRegisterGCMMessage) objIn).getEntityName()
                                + " connected to broker!", false);
                    } else {
                        informBroker("DeliveryService: Unexpected external message received from CloudBroker - " + objIn.getClass().getName(), true);
                    }
                } else {
                    String errMsg = "DeliveryService: Received message is not of class Message"
                            + " or InternalMessage! (" + objIn.getClass() + " instead). Ignoring...";
                    informBroker(errMsg, true);
                }
            }
        }
    }

    /**
     * For receiving UDP messages from Matchers that carry a publication and a
     * list of subscriberID's to which the publication needs to be sent.
     */
    private class NotifyReceiverThread implements Runnable {

        private DatagramSocket UDPsocket = null;

        public NotifyReceiverThread() {
            try {
                UDPsocket = new DatagramSocket(DeliveryService.this.myPort);
            } catch (Exception e) {
                DeliveryService.this.shutdown();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            byte[] data = new byte[64 * 1000]; //64 KB, max IP packet
            DatagramPacket packet = new DatagramPacket(data, data.length);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);

            while (true) {
                try {
                    //read the next packet...
                    UDPsocket.receive(packet);
                } catch (Exception e) {
                    if (UDPsocket.isClosed()) {
                        informBroker("DeliveryService crashed because the UDPSocket got closed (somehow, unexpectedly).", true);
                        DeliveryService.this.shutdown();
                    } else {
                        continue;
                    }
                }
                bais.reset(); //sets the stream to start reading from 0
                try {
                    ObjectInputStream ois = new ObjectInputStream(bais); //has to read the stream header each time
                    Object messageType = ois.readObject();
                    if (messageType instanceof PublishMessage) {
                        PublishMessage pubMsg = (PublishMessage) messageType;
                        Set<UUID> subscriberIDs = (Set<UUID>) ois.readObject();
                        notifySubscribers(pubMsg, subscriberIDs);
                    } else if (messageType instanceof AnnounceMessage) {
                        Set<Subscription> subscriptions = (Set<Subscription>) ois.readObject();
                        UUID mbID = (UUID) ois.readObject();
                        notifyMobileBroker(mbID, subscriptions);
                    } else {
                        //TODO send some sort of NACK
                    }
                } catch (Exception e) {
                    //TODO send some sort of NACK
                    continue;
                }
               
                
            }
        }

    }

	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Used for starting of the component. It creates an instance with the
     * received arguments and starts a thread that listens on the input stream.
     */
    public static void main(String[] args) {

        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(System.out);
            out.flush();
            in = new ObjectInputStream(System.in);
        } catch (Exception e) {
            //don't write anything, just kill the (sub)process.
            System.exit(-1);
        }

        String brokerName = null;
        String brokerIP = null;
        int port = -1;
        int queueCapacity = -1;
        boolean testing = false;
        boolean logWriting = false;
        String api="";
        try {
            brokerName = args[0];
            brokerIP = args[1];
            if (!brokerIP.equals(UniqueObject.getLocalIP())) {
                throw new Exception("IP's don't match!");
            }
            port = Integer.parseInt(args[2]);
            if (port <= 1024 || port > 49151) {
                throw new NumberFormatException("Given port number is <1024 or >49151 !");
            }
            queueCapacity = Integer.parseInt(args[3]);
            if (queueCapacity <= 0) {
                throw new NumberFormatException("Given queue capacity is <=0 !");
            }
            testing = Boolean.parseBoolean(args[4]);
            logWriting = Boolean.parseBoolean(args[5]);
            api = args[6];
        } catch (IndexOutOfBoundsException e) {
            String errMsg = e.getMessage() + " Not enough argument sent when starting DeliveryService! (7 needed)";
            sendObject(new ErrorMessage(errMsg), out);
            System.exit(-1);
        } catch (NumberFormatException e) {
            String errMsg = e.getMessage();
            sendObject(new ErrorMessage(errMsg), out);
            System.exit(-1);
        } catch (Exception e) {
            sendObject(new ErrorMessage(e.getMessage()), out);
            System.exit(-1);
        }
        //Sender sender = new Sender(api);
		//create a new DeliveryService, a thread listening on System.in is
        //automatically started and keeps the process alive
        DeliveryService ds = new DeliveryService(brokerName, brokerIP, port, queueCapacity, testing, logWriting, api, in, out);
        ds.start();

        sendObject(new InfoMessage("DeliveryService created!"), out);
    }

    /**
     * convinience method for sending object and terminating process if not
     * successfull.
     */
    private static void sendObject(Object o, ObjectOutputStream out) {
        try {
            out.writeObject(o);
            out.flush();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
