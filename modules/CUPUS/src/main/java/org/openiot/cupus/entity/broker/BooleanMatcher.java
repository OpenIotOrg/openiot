/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.cupus.entity.broker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.openiot.cupus.common.SubscriptionDataStructure;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;
import org.openiot.cupus.message.internal.ElasticityReplyMessage;
import org.openiot.cupus.message.internal.ErrorMessage;
import org.openiot.cupus.message.internal.InfoMessage;
import org.openiot.cupus.message.internal.MergeBooleanMatcherMessage;
import org.openiot.cupus.message.internal.SplitBooleanMatcherMessage;
import org.openiot.cupus.message.internal.SubscriptionStructureMessage;
import org.openiot.cupus.subscriptionforest.ActiveSubscriptionForest;
import org.openiot.cupus.subscriptionforest.ActiveSubscriptionNode;
import org.openiot.cupus.util.LogWriter;

/**
 * Matcher is a component of a cloud-broker whose job is to keep and maintain a
 * part of the subscription structure and perform operations on it.
 *
 * @author Eugen Rozic, Aleksandar Antonic
 *
 */
public class BooleanMatcher {

    private int matcherID;

    protected BrokerComm intercomm = null;
    private UDPMatchingResultsManager toDeliveryService = null;

    protected HashMap<UUID, Long> activeSubscribers = new HashMap<UUID, Long>();
    //protected Cnode treeRoot;
    protected SubscriptionDataStructure treeRoot;

    private boolean testing = false;
    private boolean logWriting = false;
    protected LogWriter log = null;

    //elasticity monitoring 
    private boolean elasticity;
    private double splitThreshold; //maximal percentage of idle time when a split event is triggered
    private double mergeThreshold; //minimal percentage of idle time when a merge event is triggered
    private int checkThreshold; //number of received messages after which elasticity check is triggered
    protected long measurementStartTime;
    protected long idleTime;
    protected long messageCounter;
    protected InternalMessage sentMessage;

    /**
     * Constructs the matcher and starts it's internal communication thread.
     */
    protected BooleanMatcher(int matcherID, int deliveryServiceInternalUDPPort,
            boolean testing, boolean logWriting, boolean elasticity, double splitThreshold, double mergeThreshold, int checkThreshold,
            ObjectInputStream in, ObjectOutputStream out) {

        this.matcherID = matcherID;

        this.toDeliveryService = new UDPMatchingResultsManager(deliveryServiceInternalUDPPort);

        this.testing = testing;
        this.logWriting = logWriting;

        intercomm = new BrokerComm(in, out);

        this.treeRoot = new ActiveSubscriptionForest();

        this.log = new LogWriter("Matcher_" + matcherID + ".log", logWriting, false);

        this.elasticity = elasticity;
        this.splitThreshold = splitThreshold;
        this.mergeThreshold = mergeThreshold;
        this.checkThreshold = checkThreshold;
        measurementStartTime = System.currentTimeMillis();
        idleTime = 0;
        messageCounter = 0;
        
        new Thread(intercomm).start();
    }

    /**
     * Kills the (sub)process (which will automatically close the output streams
     * to the parent and children processes which will in turn cause their input
     * streams to throw EOFException which will cause them to call their
     * shutdown.
     */
    public void shutdown() {
        System.exit(-1);
    }

    public void checkElasticityMeasurement(long startIdle, long endIdle) {
        idleTime += endIdle - startIdle;
        messageCounter++;
        if (messageCounter > checkThreshold) {
            float idle = (idleTime) / ((float) (System.currentTimeMillis() - measurementStartTime));
            if (idle < splitThreshold) {//split matcher
                if (sentMessage == null) {
                    informBroker("Matcher " + matcherID + " SPLITTING " + idle, false);
                    List<ActiveSubscriptionNode> list = ((ActiveSubscriptionForest) treeRoot).toList();
                    List<Subscription> toDelivery = new LinkedList<>();
                    for (int i = 0; i < list.size() / 2; i++) {//take half of the available subscriptions
                        toDelivery.add(list.get(i).getData());
                    }
                    sentMessage = new SplitBooleanMatcherMessage(toDelivery);
                    sendInternalMessage(sentMessage);
                }
            } else if (idle > mergeThreshold) {//merge matcher
                if (sentMessage == null) {
                    informBroker("Matcher " + matcherID + " MERGING " + idle, false);
                    List<ActiveSubscriptionNode> list = ((ActiveSubscriptionForest) treeRoot).toList();
                    List<Subscription> toDelivery = new LinkedList<>();
                    for (int i = 0; i < list.size(); i++) {//take half of the available subscriptions
                        toDelivery.add(list.get(i).getData());
                    }
                    sentMessage = new MergeBooleanMatcherMessage(toDelivery);
                    sendInternalMessage(sentMessage);
                }
            }
            resetElasticityMeasurement();
        }
    }

    public void resetElasticityMeasurement() {
        idleTime = 0;
        messageCounter = 0;
        measurementStartTime = System.currentTimeMillis();
    }

    /**
     * Returns null if publication not instance of HashtablePublication,
     * otherwise a set of subscribers that should be notified about this
     * publication (can be an empty set).
     */
    public Set<UUID> findMatchingSubscribers(PublishMessage msg) {
        Publication pub = ((ActivePublication) msg.getPublication()).getPublication();
        if (!(pub instanceof HashtablePublication)) {
            return null;
        }
        Set<UUID> matched = treeRoot.findMatchingSubscribers((HashtablePublication) pub);
        return matched;
    }

    public Set<Subscription> findMatchingSubscriptions(ActiveAnnouncement actAnn) {
        Announcement ann = actAnn.getAnnouncement();
        if (!(ann instanceof TripletAnnouncement)) {
            return null;
        }
        Set<Subscription> matched = treeRoot.findMatchingSubscriptions((TripletAnnouncement) ann);
        return matched;
    }

    public int addSubscription(ActiveSubscription subscription) {
        if (!(subscription.getSubscription() instanceof TripletSubscription)) {
            informBroker("Forest (adding) can only work with instances of TripletSubscription, not " + subscription.getSubscription().getClass() + ".", true);
        }
        int retval = treeRoot.addSubscription(subscription);
        if (retval == SubscriptionDataStructure.SUB_ADDED) {
            Long value = activeSubscribers.get(subscription.getSubscriberID());
            if (value == null) {
                activeSubscribers.put(subscription.getSubscriberID(), 1L);
            } else {
                activeSubscribers.put(subscription.getSubscriberID(), value + 1);
            }
        }
        return retval;
    }

    public int removeSubscription(ActiveSubscription subscription) {
        if (!(subscription.getSubscription() instanceof TripletSubscription)) {
            informBroker("Forest (remove) can only work with instances of TripletSubscription, not " + subscription.getSubscription().getClass() + ".", true);
        }
        int retval = treeRoot.removeSubscription(subscription);
        if (retval == SubscriptionDataStructure.SUB_REMOVED) {
            Long value = activeSubscribers.get(subscription.getSubscriberID());
            if (value == 1) {
                activeSubscribers.remove(subscription.getSubscriberID());
            } else {
                activeSubscribers.put(subscription.getSubscriberID(), value - 1);
            }
        }
        return retval;
    }

    public void deleteSubscriber(SubscriberUnregisterMessage msg) {
        treeRoot.deleteSubscriber(msg.getEntityID());
        activeSubscribers.remove(msg.getEntityID());
    }

    public void elasticityReply(ElasticityReplyMessage msg) {
        //informBroker("REPLY " + msg.getSuccess(), false);
        if (msg.getSuccess() && sentMessage != null) {//request for split/merge successfull
            if (sentMessage instanceof SplitBooleanMatcherMessage) {
                List<Subscription> deliveredSubscriptions = ((SplitBooleanMatcherMessage) sentMessage).getSubscriptions();
                for (int i = 0; i < deliveredSubscriptions.size(); i++) {
                    treeRoot.removeSubscription((ActiveSubscription) deliveredSubscriptions.get(i));
                }
                sentMessage = null;
            } else if (sentMessage instanceof MergeBooleanMatcherMessage) {
                shutdown();
            }
        } else {
            sentMessage = null;
        }
    }

    /**
     * Convinience method that sends an ErrorMessage or an InfoMessage to the
     * CloudBroker, depending if the reporting flag is set or not. It also logs
     * the message to this Matcher's log file (if logging is on).
     */
    public void informBroker(String msg, boolean error) {
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
    protected void sendInternalMessage(Object msg) {
        try {
            intercomm.out.writeObject(msg);
            intercomm.out.flush();
        } catch (IOException e) {
            //if component found terminated shut everything down...
            this.shutdown();
        }
    }

    public int getUDPPort() {
        return toDeliveryService.deliveryServiceInternalUDPPort;
    }

    public boolean isTesting() {
        return testing;
    }

    public boolean isLogWriting() {
        return logWriting;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * For communicating with the starting process (parent/CloudBroker)
     */
    protected class BrokerComm implements Runnable {

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        /**
         * Sets the stream and reads from in and sets the singleton classes.
         *
         */
        public BrokerComm(ObjectInputStream in, ObjectOutputStream out) {
            this.in = in;
            this.out = out;

        }

        @Override
        public void run() {
            long startIdle = 0;
            long endIdle = 0;
            while (true) {
                Object objIn = null;
                try {
                    if (elasticity) {
                        checkElasticityMeasurement(startIdle, endIdle);
                    }
                    startIdle = System.currentTimeMillis();
                    objIn = in.readObject();
                    endIdle = System.currentTimeMillis();
                } catch (Exception e) {
                    BooleanMatcher.this.shutdown();
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof ElasticityReplyMessage) {
                        elasticityReply((ElasticityReplyMessage) objIn);
                    } else {
                        informBroker("Matcher: Unexpected internal message received from parent - " + objIn.getClass().getName(), true);
                    }
                } else if (objIn instanceof Message) {
                    if (objIn instanceof SubscriberUnregisterMessage) {

                        deleteSubscriber((SubscriberUnregisterMessage) objIn);

                    } else if (objIn instanceof SubscribeMessage) {
                        SubscribeMessage msg = (SubscribeMessage) objIn;
                        ActiveSubscription actSub = (ActiveSubscription) msg.getSubscription();
                        if (msg.isUnsubscribe()) {
                            int retval = removeSubscription(actSub);
                            switch (retval) {
                                case SubscriptionDataStructure.SUB_REMOVED:
                                    informBroker("Subscription successfully removed!", false);
                                    break;
                                case SubscriptionDataStructure.SUB_NOT_REMOVED:
                                    break;
                            }
                        } else {
                            int retval = addSubscription(actSub);
                            switch (retval) {
                                case SubscriptionDataStructure.SUB_ADDED:
                                    informBroker("Subscription successfully added!" + actSub.getSubscription().toString(), false);
                                    break;
                                case SubscriptionDataStructure.SUB_NOT_ADDED:
                                    informBroker("Subscription not added for unknown reason!", false);
                                    break;
                            }
                        }
                    } else if (objIn instanceof PublishMessage) {

                        PublishMessage msg = (PublishMessage) objIn;
                        if (!msg.isUnpublish()) {
                            Set<UUID> matched = findMatchingSubscribers(msg);
                            if (matched != null && !matched.isEmpty()) {
                                toDeliveryService.send(msg, matched);
                            }
                        }
                    } else if (objIn instanceof AnnounceMessage) {

                        AnnounceMessage msg = (AnnounceMessage) objIn;
                        ActiveAnnouncement actAnn = (ActiveAnnouncement) msg.getAnnouncement();
                        Set<Subscription> matched = findMatchingSubscriptions(actAnn);
                        if (matched != null && !matched.isEmpty()) {
                            toDeliveryService.send(msg, matched, actAnn.getMobileBrokerID());
                        }

                    } else {
                        informBroker("Matcher: Unexpected external message received from parent - " + objIn.getClass().getName(), true);
                    }
                } else {
                    String errMsg = "Matcher: Received message is not of class Message"
                            + " or InternalMessage! (" + objIn.getClass() + " instead). Ignoring...";
                    informBroker(errMsg, true);
                }
            }
        }
    }

    /**
     * For establishing, managing and using the UDP connection with the
     * DeliveryService that is used for notifing the DeliveryService about the
     * results of matching a publication on a Matcher.
     */
    private class UDPMatchingResultsManager {

        private int deliveryServiceInternalUDPPort = -1;
        private DatagramSocket socket = null;
        private ByteArrayOutputStream baseOut = null;

        public UDPMatchingResultsManager(int port) {
            this.deliveryServiceInternalUDPPort = port;

            try {
                socket = new DatagramSocket(); //bound to any local port
                baseOut = new ByteArrayOutputStream(64 * 1000); //64KB, max for IP packet
            } catch (Exception e) {
                sendInternalMessage(new ErrorMessage("Matcher couldn't start because it couldn't open a UDP socket."));
                BooleanMatcher.this.shutdown();
            }
        }

        /**
         * Sends a UDP packet to the DeliveryService containing the
         * PublishMessage and the set of subscriber IDs.
         */
        synchronized public void send(PublishMessage msg, Set<UUID> subIDs) {
            try {
                baseOut.reset();
                ObjectOutputStream oos = new ObjectOutputStream(baseOut); //has to write a new header each time
                oos.writeObject(msg);
                oos.writeObject(subIDs);
                oos.flush();
                //IP address of DeliveryService has to be Loopback (same maschine)
                DatagramPacket packet = new DatagramPacket(baseOut.toByteArray(), baseOut.size(),
                        InetAddress.getLoopbackAddress(), deliveryServiceInternalUDPPort);
                socket.send(packet);
            } catch (Exception e) {
                //TODO - try to repeat this or something...?!?
            }
        }

        /**
         * Sends a UDP packet to the DeliveryService containing the
         * Subscriptions and the mobile broker ID.
         */
        synchronized public void send(AnnounceMessage msg, Set<Subscription> subs, UUID mbID) {
            try {
                baseOut.reset();
                ObjectOutputStream oos = new ObjectOutputStream(baseOut); //has to write a new header each time
                oos.writeObject(msg);
                oos.writeObject(subs);
                oos.writeObject(mbID);
                oos.flush();
                //IP address of DeliveryService has to be Loopback (same maschine)
                DatagramPacket packet = new DatagramPacket(baseOut.toByteArray(), baseOut.size(),
                        InetAddress.getLoopbackAddress(), deliveryServiceInternalUDPPort);
                socket.send(packet);
            } catch (Exception e) {
                //TODO - try to repeat this or something...?!?
            }
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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

        int matcherID = -1;
        int deliveryServiceInternalUDPPort = -1;
        boolean testing = false;
        boolean logWriting = false;
        boolean elasticity = false;
        double splitThreshold = 0.0;
        double mergeThreshold = 0.0;
        int checkThreshold = 0;
        try {
            matcherID = Integer.parseInt(args[0]);
            deliveryServiceInternalUDPPort = Integer.parseInt(args[1]);
            if (deliveryServiceInternalUDPPort <= 1024 || deliveryServiceInternalUDPPort > 49151) {
                throw new NumberFormatException("Given port number is <1024 or >49151 !");
            }
            testing = Boolean.parseBoolean(args[2]);
            logWriting = Boolean.parseBoolean(args[3]);
            elasticity = Boolean.parseBoolean(args[4]);
            splitThreshold = Double.valueOf(args[5]);
            mergeThreshold = Double.valueOf(args[6]);
            checkThreshold = Integer.parseInt(args[7]);
        } catch (IndexOutOfBoundsException e) {
            String errMsg = e.getMessage() + " Not enough arguments sent when starting Matcher! (8 needed)";
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

        //create a new Matcher, a thread listening on System.in is
        //automatically started and keeps the process alive
        new BooleanMatcher(matcherID, deliveryServiceInternalUDPPort,
                testing, logWriting, elasticity, splitThreshold, mergeThreshold, checkThreshold, in, out);

        sendObject(new InfoMessage("Matcher " + matcherID + " created!"), out);
    }

    /**
     * convinience method for sending object and terminating process if not
     * successfull.
     */
    protected static void sendObject(Object o, ObjectOutputStream out) {
        try {
            out.writeObject(o);
            out.flush();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
