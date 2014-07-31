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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.ActiveAnnouncement;
import org.openiot.cupus.artefact.ActivePublication;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TopKWSubscription;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;
import org.openiot.cupus.message.internal.ElasticityReplyMessage;
import org.openiot.cupus.message.internal.ErrorMessage;
import org.openiot.cupus.message.internal.InfoMessage;
import org.openiot.cupus.message.internal.MergeTopKWMatcherMessage;
import org.openiot.cupus.message.internal.SplitTopKWMatcherMessage;
import org.openiot.cupus.topkw.SASubscription;
import org.openiot.cupus.topkw.SkybandProcessor;
import org.openiot.cupus.topkw.TopKWProcessor;
import org.openiot.cupus.util.LogWriter;

/**
 * Matcher is a component of a cloud-broker whose job is to keep and maintain a
 * part of the subscription structure and perform operations on it.
 *
 * @author Eugen Rozic, Aleksandar Antonic
 *
 */
public class TopKWMatcher {

    private int matcherID;

    protected BrokerComm intercomm = null;
    private UDPMatchingResultsManager toDeliveryService = null;

    protected HashMap<UUID, Long> activeSubscribers = new HashMap<UUID, Long>();
    protected TopKWProcessor processor;

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
    protected TopKWMatcher(int matcherID, int deliveryServiceInternalUDPPort,
            boolean testing, boolean logWriting, boolean elasticity, double splitThreshold, double mergeThreshold, int checkThreshold,
            ObjectInputStream in, ObjectOutputStream out) {

        this.matcherID = matcherID;

        this.toDeliveryService = new UDPMatchingResultsManager(deliveryServiceInternalUDPPort);

        this.testing = testing;
        this.logWriting = logWriting;

        intercomm = new BrokerComm(in, out);

        this.processor = new SkybandProcessor(this);

        this.log = new LogWriter("TopKWMatcher_" + matcherID + ".log", logWriting, false);

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

    public Set<Subscription> findMatchingSubscriptions(ActiveAnnouncement actAnn) {
        Announcement ann = actAnn.getAnnouncement();
        if (!(ann instanceof TripletAnnouncement)) {
            return null;
        }
        Set<Subscription> matched = processor.findMatchingSubscriptions((TripletAnnouncement) ann);
        return matched;
    }

    public boolean addSubscription(MemorySubscription subscription) {
        if (!(subscription.getSubscription() instanceof TopKWSubscription)) {
            informBroker("Top K/W processor (adding) can only work with instances of TopKWSubscription, not " + subscription.getSubscription().getClass() + ".", true);
        }
        subscription.setNotifier(toDeliveryService);
        subscription.setPrint(this);
        boolean retval = processor.add(subscription);
        if (retval) {
            Long value = activeSubscribers.get(subscription.getSubscriber());
            if (value == null) {
                activeSubscribers.put(subscription.getSubscriber(), 1L);
            } else {
                activeSubscribers.put(subscription.getSubscriber(), value + 1);
            }
        }
        return retval;
    }

    public boolean removeSubscription(MemorySubscription subscription) {
        if (!(subscription.getSubscription() instanceof TopKWSubscription)) {
            informBroker("Top K/W processor (remove) can only work with instances of TopKWSubscription, not " + subscription.getSubscription().getClass() + ".", true);
        }
        boolean retval = processor.remove(subscription);
        if (retval) {
            Long value = activeSubscribers.get(subscription.getSubscriber());
            if (value == 1) {
                activeSubscribers.remove(subscription.getSubscriber());
            } else {
                activeSubscribers.put(subscription.getSubscriber(), value - 1);
            }
        }
        return retval;
    }

    public void deleteSubscriber(SubscriberUnregisterMessage msg) {
        processor.deleteSubscriber(msg.getEntityID());
        activeSubscribers.remove(msg.getEntityID());
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

    public void checkElasticityMeasurement(long startIdle, long endIdle) {
        idleTime += endIdle - startIdle;
        messageCounter++;
        if (messageCounter > checkThreshold) {
            float idle = (idleTime) / ((float) (System.currentTimeMillis() - measurementStartTime));
            if (idle < splitThreshold) {//split matcher
                if (sentMessage == null) {
                    informBroker("TopKWMatcher " + matcherID + " SPLITTING " + idle, false);
                    List<MemorySubscription> list = processor.getSubscriptions();
                    List<Subscription> toDelivery = new LinkedList<>();
                    for (int i = 0; i < list.size() / 2; i++) {//take half of the available subscriptions
                        toDelivery.add(list.get(i));
                    }
                    sentMessage = new SplitTopKWMatcherMessage(toDelivery);
                    sendInternalMessage(sentMessage);
                }
            } else if (idle > mergeThreshold) {//merge matcher
                if (sentMessage == null) {
                    informBroker("TopKWMatcher " + matcherID + " MERGING " + idle, false);
                    List<MemorySubscription> list = processor.getSubscriptions();
                    List<Subscription> toDelivery = new LinkedList<>();
                    for (int i = 0; i < list.size(); i++) {//take half of the available subscriptions
                        toDelivery.add(list.get(i));
                    }
                    sentMessage = new MergeTopKWMatcherMessage(toDelivery);
                    sendInternalMessage(sentMessage);
                }
            }
            resetElasticityMeasurement();
        }
    }

    public void elasticityReply(ElasticityReplyMessage msg) {
        if (msg.getSuccess() && sentMessage != null) {//request for split/merge successfull
            if (sentMessage instanceof SplitTopKWMatcherMessage) {
                List<Subscription> deliveredSubscriptions = ((SplitTopKWMatcherMessage) sentMessage).getSubscriptions();
                for (int i = 0; i < deliveredSubscriptions.size(); i++) {
                    processor.remove((MemorySubscription) deliveredSubscriptions.get(i));
                }
                sentMessage = null;
            } else if (sentMessage instanceof MergeTopKWMatcherMessage) {
                shutdown();
            }
        } else {
            sentMessage = null;
        }
    }

    public void resetElasticityMeasurement() {
        idleTime = 0;
        messageCounter = 0;
        measurementStartTime = System.currentTimeMillis();
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
                        //checkElasticityMeasurement(startIdle, endIdle);
                    }
                    startIdle = System.currentTimeMillis();
                    objIn = in.readObject();
                    endIdle = System.currentTimeMillis();
                } catch (Exception e) {
                    TopKWMatcher.this.shutdown();
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof ElasticityReplyMessage) {
                        elasticityReply((ElasticityReplyMessage) objIn);
                    } else {
                        informBroker("TopKWMatcher: Unexpected internal message received from parent - " + objIn.getClass().getName(), true);
                    }
                } else if (objIn instanceof Message) {
                    if (objIn instanceof SubscriberUnregisterMessage) {

                        deleteSubscriber((SubscriberUnregisterMessage) objIn);

                    } else if (objIn instanceof SubscribeMessage) {
                        SubscribeMessage msg = (SubscribeMessage) objIn;
                        SASubscription actSub = (SASubscription) msg.getSubscription();
                        if (msg.isUnsubscribe()) {
                            boolean retval = removeSubscription(actSub);
                            if (retval) {
                                informBroker("TopKWSubscription successfully removed!", false);
                            }
                        } else {
                            boolean retval = addSubscription(actSub);
                            if (retval) {
                                informBroker("TopKWSubscription successfully added!" + actSub.getSubscription().toString(), false);
                            }
                        }
                    } else if (objIn instanceof PublishMessage) {

                        PublishMessage msg = (PublishMessage) objIn;
                        if (msg.isUnpublish()) {
                            ActivePublication actPub = (ActivePublication) msg.getPublication();
                            processor.unpublish(actPub.getPublication());
                        } else {
                            ActivePublication actPub = (ActivePublication) msg.getPublication();
                            processor.process(actPub.getPublication(), actPub.getPublisherID(), actPub.getPublication().getValidity());
                        }

                    } else if (objIn instanceof AnnounceMessage) {

                        AnnounceMessage msg = (AnnounceMessage) objIn;
                        ActiveAnnouncement actAnn = (ActiveAnnouncement) msg.getAnnouncement();
                        Set<Subscription> matched = findMatchingSubscriptions(actAnn);
                        if (matched != null && !matched.isEmpty()) {
                            toDeliveryService.send(msg, matched, actAnn.getMobileBrokerID());
                        }

                    } else {
                        informBroker("TopKWMatcher: Unexpected external message received from parent - " + objIn.getClass().getName(), true);
                    }
                } else {
                    String errMsg = "TopKWMatcher: Received message is not of class Message"
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
    private class UDPMatchingResultsManager extends Notifier {

        private int deliveryServiceInternalUDPPort = -1;
        private DatagramSocket socket = null;
        private ByteArrayOutputStream baseOut = null;

        public UDPMatchingResultsManager(int port) {
            this.deliveryServiceInternalUDPPort = port;

            try {
                socket = new DatagramSocket(); //bound to any local port
                baseOut = new ByteArrayOutputStream(64 * 1000); //64KB, max for IP packet
            } catch (Exception e) {
                sendInternalMessage(new ErrorMessage("TopKWMatcher couldn't start because it couldn't open a UDP socket."));
                TopKWMatcher.this.shutdown();
            }
        }

        public void send(Publication msg, UUID subscriber) {
            Set<UUID> subID = new HashSet<>();
            subID.add(subscriber);
            this.send(new PublishMessage(msg, false), subID);
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
            String errMsg = e.getMessage() + " Not enough arguments sent when starting TopKWMatcher! (8 needed)";
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
        new TopKWMatcher(matcherID, deliveryServiceInternalUDPPort,
                testing, logWriting, elasticity, splitThreshold, mergeThreshold, checkThreshold, in, out);

        sendObject(new InfoMessage("TopKWMatcher " + matcherID + " created!"), out);
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
