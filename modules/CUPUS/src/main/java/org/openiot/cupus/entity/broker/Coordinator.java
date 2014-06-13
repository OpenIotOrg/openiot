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

import com.google.android.gcm.server.Sender;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openiot.cupus.artefact.ActiveSubscription;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TopKWSubscription;

import org.openiot.cupus.common.UniqueObject;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterGCMMessage;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberDisconnectMessage;
import org.openiot.cupus.message.external.SubscriberRegisterMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;
import org.openiot.cupus.message.internal.ElasticityReplyMessage;
import org.openiot.cupus.message.internal.ErrorMessage;
import org.openiot.cupus.message.internal.InfoMessage;
import org.openiot.cupus.message.internal.InitialAnnouncementMatchesMessage;
import org.openiot.cupus.message.internal.InitialMatchesMessage;
import org.openiot.cupus.message.internal.MergeBooleanMatcherMessage;
import org.openiot.cupus.message.internal.MergeTopKWMatcherMessage;
import org.openiot.cupus.message.internal.SplitBooleanMatcherMessage;
import org.openiot.cupus.message.internal.SplitTopKWMatcherMessage;
import org.openiot.cupus.message.internal.StartComponentMessage;
import org.openiot.cupus.message.internal.SubscriptionStructureMessage;
import org.openiot.cupus.util.LogWriter;

/**
 * This is the main cloud-broker class. It represents the cloud-broker which is
 * instantiated, started and stopped through it. The CloudBroker object starts
 * the three broker components (MessageReceiver, DeliveryService and (initial)
 * Matcher) as separate processes and relays the communication between them by
 * forwarding from input streams to the proper output streams (for example
 * reading the in from MessageReceiver and sending it to the out of the
 * DeliveryService for communication between those two components)
 *
 * @author Eugen Rozic
 *
 */
public class Coordinator {

    private String brokerName;
    private String brokerIP;
    private int brokerPort;
    private int internalUDPPort;

    private int queueCapacity;
    private String APIKey;

    private LogWriter log;
    private boolean logWriting = true;
    private boolean testing = false;

    private String classpath = null;

    private Process messageReceiver = null;
    private MessageReceiverRelay messageReceiverRelay = null;

    private Process deliveryService = null;
    private DeliveryServiceRelay deliveryServiceRelay = null;

    private int maxNumberOfMatchers;
    private int topKWMaxNumberOfMatchers;
    private int numberOfMatchers;
    private int topKWNumberOfMatchers;
    private Process[] matchers;
    private Process[] topKWMatchers;
    private MatcherRelay[] matcherRelays;
    private MatcherRelay[] topKWMatcherRelays;
    private int matcherRoundRobin = 0;
    private int topKWMatcherRoundRobin = 0;

    private boolean elasticity = false;
    private double splitThreshold;
    private double mergeThreshold;
    private int checkThreshold;

    private final Object mutexMatcher = new Object();

    /**
     * The main and only constructor. It takes in a configuration file that
     * specifies all information the broker needs, the broker name and port.
     *
     * It then creates the needed components and starts the threads that control
     * the information flow from and to them.
     *
     * @param configFile
     */
    public Coordinator(File configFile, String classpath) {

        this.classpath = classpath;

        //reads properties and instantiates and sets everything...
        try {
            Properties brokerProps = new Properties();
            FileInputStream fileIn = new FileInputStream(configFile);
            brokerProps.load(fileIn);
            fileIn.close();

            this.brokerName = brokerProps.getProperty("brokerName");
            if (this.brokerName == null) {
                throw new NullPointerException("Name must be defined!");
            }
            this.brokerPort = Integer.parseInt(brokerProps.getProperty("brokerPort"));
            this.internalUDPPort = Integer.parseInt(
                    brokerProps.getProperty("internalUDPPort"));
            this.brokerIP = UniqueObject.getLocalIP();

            this.queueCapacity = Integer.parseInt(brokerProps.getProperty("queueCapacity"));
            this.APIKey = brokerProps.getProperty("apikey");
            
            if (brokerProps.getProperty("testing", "false").toLowerCase().equals("false")) {
                this.testing = false;
            } else if (brokerProps.getProperty("testing").toLowerCase().equals("true")) {
                this.testing = true;
            } else {
                System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
                this.testing = false;
            }
            if (brokerProps.getProperty("logWriting", "true").toLowerCase().equals("true")) {
                this.logWriting = true;
            } else if (brokerProps.getProperty("logWriting").toLowerCase().equals("false")) {
                this.logWriting = false;
            } else {
                System.err.println("Config param \"logWriting\" should be either true or false! Setting to default true.");
                this.logWriting = true;
            }
            if (brokerProps.getProperty("elasticity", "false").toLowerCase().equals("false")) {
                this.elasticity = false;
            } else if (brokerProps.getProperty("elasticity").toLowerCase().equals("true")) {
                this.elasticity = true;
            } else {
                System.err.println("Config param \"elasticity\" should be either true or false! Setting to default false.");
                this.elasticity = false;
            }

            this.maxNumberOfMatchers = numberOfMatchers = Integer.parseInt(brokerProps.getProperty("numberOfBooleanMatchers"));
            this.topKWMaxNumberOfMatchers = topKWNumberOfMatchers = Integer.parseInt(brokerProps.getProperty("numberOfTopKWMatchers"));
            if (this.elasticity) {
                numberOfMatchers = 1;
            }

            this.splitThreshold = Double.parseDouble(brokerProps.getProperty("splitThreshold"));
            this.mergeThreshold = Double.parseDouble(brokerProps.getProperty("mergeThreshold"));
            this.checkThreshold = Integer.parseInt(brokerProps.getProperty("checkThreshold"));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        log = new LogWriter(this.brokerName + ".log", logWriting, testing);
        
        try {
            initMessageReceiver();
            initDeliveryService();
            initMatchers();
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }

        log.writeToLog("Broker name: " + this.brokerName, true);
        log.writeToLog("Broker port: " + this.brokerPort, true);

        if (brokerIP.equals("")) {
            log.writeToLog("Broker IP address: unidentifiable?!", true);
        } else {
            log.writeToLog("Broker IP address: " + this.brokerIP, true);
        }
        log.writeToLog("", true); //empty line
    }

    /**
     * Sends the start messages to the MessageReceiver and the DeliveryService
     * components of the broker so they can start accepting accepting and
     * processing requests and sending answers to them.
     */
    public void start() {
        messageReceiverRelay.sendStartMessage();
        deliveryServiceRelay.sendStartMessage();
    }

    /**
     * Kills the broker. It does so by just terminating the process which will
     * automatically cause all output streams to close which will in turn cause
     * all input streams on the receiving side to throw an EOFException which
     * will cause the starting of shutdown of all the components (subprocesses)
     * this CloudBroker is connected to.
     */
    public void shutdown() {
        log.writeToLog("Shutting down all Broker components!", true);
        System.exit(-1);
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Starts the MessageReceiver process and a thread to manage it's
     * input/output.
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
     * Starts the DeliveryService process and a thread to manage it's
     * input/output.
     */
    private void initDeliveryService() throws IOException {
        String[] cmd = new String[]{"java", "-cp", classpath,
            DeliveryService.class.getName(),
            brokerName, brokerIP, Integer.toString(internalUDPPort),
            Integer.toString(queueCapacity),
            Boolean.toString(testing), Boolean.toString(logWriting), APIKey};
        ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
        deliveryService = builder.start();

        deliveryServiceRelay = new DeliveryServiceRelay();
        new Thread(deliveryServiceRelay).start();
    }

    /**
     * Starts the Matcher processes and a thread for each to manage it's
     * input/output.
     */
    private void initMatchers() throws IOException {
        matchers = new Process[numberOfMatchers];
        matcherRelays = new MatcherRelay[numberOfMatchers];

        for (int matcherID = 0; matcherID < numberOfMatchers; matcherID++) {
            String[] cmd = new String[]{"java", "-cp", classpath,
                BooleanMatcher.class.getName(), Integer.toString(matcherID),
                Integer.toString(internalUDPPort),
                Boolean.toString(testing), Boolean.toString(logWriting),
                Boolean.toString(elasticity), Double.toString(splitThreshold), Double.toString(mergeThreshold), Integer.toString(checkThreshold)};
            ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
            matchers[matcherID] = builder.start();

            MatcherRelay matcherRelay = new MatcherRelay(matchers[matcherID], matcherID);
            matcherRelays[matcherID] = matcherRelay;
            new Thread(matcherRelay).start();

            matcherRelay.out.flush();
        }

        topKWMatchers = new Process[topKWNumberOfMatchers];
        topKWMatcherRelays = new MatcherRelay[topKWNumberOfMatchers];

        for (int matcherID = 0; matcherID < topKWNumberOfMatchers; matcherID++) {
            String[] cmd = new String[]{"java", "-cp", classpath,
                TopKWMatcher.class.getName(), Integer.toString(matcherID),
                Integer.toString(internalUDPPort),
                Boolean.toString(testing), Boolean.toString(logWriting),
                Boolean.toString(elasticity), Double.toString(splitThreshold), Double.toString(mergeThreshold), Integer.toString(checkThreshold)};
            ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
            topKWMatchers[matcherID] = builder.start();

            MatcherRelay matcherRelay = new MatcherRelay(topKWMatchers[matcherID], matcherID + 100);
            topKWMatcherRelays[matcherID] = matcherRelay;
            new Thread(matcherRelay).start();

            matcherRelay.out.flush();
        }

    }

    /**
     * Starts the additional Matcher process and a thread for each to manage
     * it's input/output.
     */
    public int initAdditionalBooleanMatcher() throws IOException {
        synchronized (mutexMatcher) {
            if (numberOfMatchers + topKWNumberOfMatchers + 1 < maxNumberOfMatchers) {
                Process[] newMatchers = new Process[numberOfMatchers + 1];
                MatcherRelay[] newRelays = new MatcherRelay[numberOfMatchers + 1];
                for (int i = 0; i < numberOfMatchers; i++) {
                    newMatchers[i] = matchers[i];
                    newRelays[i] = matcherRelays[i];
                }

                int matcherID = numberOfMatchers;
                String[] cmd = new String[]{"java", "-cp", classpath,
                    BooleanMatcher.class.getName(), Integer.toString(matcherID),
                    Integer.toString(internalUDPPort),
                    Boolean.toString(testing), Boolean.toString(logWriting),
                    Boolean.toString(elasticity), Double.toString(splitThreshold), Double.toString(mergeThreshold), Integer.toString(checkThreshold)};
                ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
                newMatchers[matcherID] = builder.start();

                MatcherRelay matcherRelay = new MatcherRelay(newMatchers[matcherID], matcherID);
                newRelays[matcherID] = matcherRelay;
                new Thread(matcherRelay).start();

                matcherRelay.out.flush();

                matchers = newMatchers;
                matcherRelays = newRelays;
                numberOfMatchers++;
                return matcherID;
            } else {
                return -1;
            }
        }
    }

    /**
     * Starts the additional Matcher process and a thread for each to manage
     * it's input/output.
     */
    public int initAdditionalTopKWMatcher() throws IOException {
        synchronized (mutexMatcher) {
            if (numberOfMatchers + topKWNumberOfMatchers + 1 < topKWMaxNumberOfMatchers) {
                Process[] newMatchers = new Process[topKWNumberOfMatchers + 1];
                MatcherRelay[] newRelays = new MatcherRelay[topKWNumberOfMatchers + 1];
                for (int i = 0; i < topKWNumberOfMatchers; i++) {
                    newMatchers[i] = topKWMatchers[i];
                    newRelays[i] = topKWMatcherRelays[i];
                }

                int matcherID = topKWNumberOfMatchers;
                String[] cmd = new String[]{"java", "-cp", classpath,
                    TopKWMatcher.class.getName(), Integer.toString(matcherID),
                    Integer.toString(internalUDPPort),
                    Boolean.toString(testing), Boolean.toString(logWriting),
                    Boolean.toString(elasticity), Double.toString(splitThreshold), Double.toString(mergeThreshold), Integer.toString(checkThreshold)};
                ProcessBuilder builder = new ProcessBuilder(cmd).directory(new File(".")).redirectErrorStream(true);
                newMatchers[matcherID] = builder.start();

                MatcherRelay matcherRelay = new MatcherRelay(newMatchers[matcherID], matcherID);
                newRelays[matcherID] = matcherRelay;
                new Thread(matcherRelay).start();

                matcherRelay.out.flush();

                topKWMatchers = newMatchers;
                topKWMatcherRelays = newRelays;
                topKWNumberOfMatchers++;
                return matcherID;
            } else {
                return -1;
            }
        }
    }

    /**
     * Removes the Matcher process
     */
    public boolean removeBooleanMatcher(int matcherID) {
        //synchronized (mutexMatcher) {
        if (numberOfMatchers > 1) {
            Process[] newMatchers = new Process[numberOfMatchers - 1];
            MatcherRelay[] newRelays = new MatcherRelay[numberOfMatchers - 1];
            for (int i = 0; i < matcherID; i++) {
                newMatchers[i] = matchers[i];
                newRelays[i] = matcherRelays[i];
            }
            for (int i = matcherID + 1; i < numberOfMatchers; i++) {
                newMatchers[i - 1] = matchers[i];
                newRelays[i - 1] = matcherRelays[i];
                newRelays[i - 1].setMatcherId(i - 1);
            }

            matchers = newMatchers;
            matcherRelays = newRelays;
            numberOfMatchers--;
            matcherRoundRobin = matcherRoundRobin % numberOfMatchers;
            return true;
        } else {
            return false;
        }
        //}
    }

    /**
     * Removes the Matcher process
     */
    public boolean removeTopKWMatcher(int matcherID) {
        synchronized (mutexMatcher) {
            if (topKWNumberOfMatchers > 1) {
                Process[] newMatchers = new Process[topKWNumberOfMatchers - 1];
                MatcherRelay[] newRelays = new MatcherRelay[topKWNumberOfMatchers - 1];
                for (int i = 0; i < matcherID; i++) {
                    newMatchers[i] = topKWMatchers[i];
                    newRelays[i] = topKWMatcherRelays[i];
                }
                for (int i = matcherID + 1; i < topKWNumberOfMatchers; i++) {
                    newMatchers[i - 1] = topKWMatchers[i];
                    newRelays[i - 1] = topKWMatcherRelays[i];
                    newRelays[i - 1].setMatcherId(i - 1);
                }

                topKWMatchers = newMatchers;
                topKWMatcherRelays = newRelays;
                topKWNumberOfMatchers--;
                topKWMatcherRoundRobin = topKWMatcherRoundRobin % topKWNumberOfMatchers;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Convinience method to send a message to an output stream...
     */
    private void sendMessage(Object msg, ObjectOutputStream out) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            log.error("Sending message failed: " + e.getMessage());
            this.shutdown();
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                Coordinator.this.shutdown();
            }
        }

        @Override
        public void run() {
            while (true) {
                Object objIn = null;
                try {
                    objIn = in.readObject();
                } catch (Exception e) {
                    System.out.println(e.getClass());
                    for (StackTraceElement s : e.getStackTrace()) {
                        System.out.println(s.toString());
                    }
                    log.error("Unable to read from MessageReceiver's input stream! " + e.getMessage());
                    Coordinator.this.shutdown();
                    return;
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof InitialMatchesMessage) {
                        //forward to deliveryService to send the initial notify msgs
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof InitialAnnouncementMatchesMessage) {
                        //forward to deliveryService to send the initial announcement msgs
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof ErrorMessage) {
                        log.error(((ErrorMessage) objIn).getContents());
                    } else if (objIn instanceof InfoMessage) {
                        log.writeToLog(((InfoMessage) objIn).getContents());
                    } else {
                        log.error("Unexpected internal message received from MessageReceiver - " + objIn.getClass().getName());
                    }
                } else if (objIn instanceof Message) {
                    //System.out.println("CloudBroker!" + objIn.getClass());
                    if (objIn instanceof SubscriberRegisterMessage) {
                        //forward to deliveryService to establish a connection
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof SubscriberDisconnectMessage) {
                        //forward to deliveryService to kill the queue
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof SubscriberUnregisterMessage) {
                        //forward to deliveryService to kill and remove the queue
                        sendMessage(objIn, deliveryServiceRelay.out);
                        //forward to all Matchers to remove all subscribers subscriptions
                        for (int i = 0; i < numberOfMatchers; i++) {
                            sendMessage(objIn, matcherRelays[i].out);
                        }
                    } else if (objIn instanceof MobileBrokerRegisterMessage) {
                        //forward to deliveryService to establish a connection
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof MobileBrokerRegisterGCMMessage) {
                        //forward to deliveryService to establish a connection
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof MobileBrokerDisconnectMessage) {
                        //forward to deliveryService to kill the queue
                        sendMessage(objIn, deliveryServiceRelay.out);
                    } else if (objIn instanceof SubscribeMessage) {
                        synchronized (mutexMatcher) {
                            SubscribeMessage msg = (SubscribeMessage) objIn;
                            log.writeToLog("Received "
                                    + (msg.isUnsubscribe() ? "unsubscription " : "subscription ")
                                    + msg.getSubscription() + ".");

                            //forward to a matcher to process...
                            if (msg.getSubscription() instanceof TopKWSubscription) {
                                sendMessage(objIn, topKWMatcherRelays[topKWMatcherRoundRobin].out);
                                topKWMatcherRoundRobin = (topKWMatcherRoundRobin + 1) % topKWNumberOfMatchers;
                            } else {
                                sendMessage(objIn, matcherRelays[matcherRoundRobin].out);
                                matcherRoundRobin = (matcherRoundRobin + 1) % numberOfMatchers;
                            }
                        }
                    } else if (objIn instanceof PublishMessage) {
                        synchronized (mutexMatcher) {
                            PublishMessage msg = (PublishMessage) objIn;
                            log.writeToLog("Received "
                                    + (msg.isUnpublish() ? "unpublication " : "publication ")
                                    + msg.getPublication() + ".");
                            //forward to all matchers to process...
                            for (int i = 0; i < numberOfMatchers; i++) {
                                sendMessage(objIn, matcherRelays[i].out);
                            }

                            for (int i = 0; i < topKWNumberOfMatchers; i++) {
                                sendMessage(objIn, topKWMatcherRelays[i].out);
                            }
                        }
                    } else if (objIn instanceof AnnounceMessage) {
                        synchronized (mutexMatcher) {
                            AnnounceMessage msg = (AnnounceMessage) objIn;
                            log.writeToLog("Received "
                                    + (msg.isRevokeAnnouncement() ? "revoke announcement " : "announcement ")
                                    + msg.getAnnouncement() + ".");
                            //forward to all matchers to process...
                            for (int i = 0; i < numberOfMatchers; i++) {
                                sendMessage(objIn, matcherRelays[i].out);
                            }

                            for (int i = 0; i < topKWNumberOfMatchers; i++) {
                                sendMessage(objIn, topKWMatcherRelays[i].out);
                            }
                        }
                    } else {
                        log.error("Unexpected external message received from MessageReceiver - " + objIn.getClass().getName());
                    }
                } else {
                    log.error("Object received from the MessageReceiver is not of class Message"
                            + " or InternalMessage! (" + objIn.getClass().getName() + " instead: " + objIn.toString() + " ). Ignoring...");
                }
            }
        }

        /**
         * Sends a start message to the MessageReceiver components of the cloud
         * broker. It should make the MessageReceiver start the thread that
         * listens for incoming connection requests from subscribers and
         * publishers.
         */
        void sendStartMessage() {
            try {
                out.writeObject(new StartComponentMessage());
                out.flush();
            } catch (IOException e) {
                //if messageReceiver found terminated shut everything down...
                log.error("MessageReceiver found dead while sending the start message.");
                Coordinator.this.shutdown();
            }
        }
    }

    /**
     * Manages the in/out streams of the deliveryService subprocess...
     */
    private class DeliveryServiceRelay implements Runnable {

        ObjectInputStream in;
        ObjectOutputStream out;
        InputStreamReader ba;
        public DeliveryServiceRelay() {
            try {
                // catches both the stdout and stderr of the subprocess
                out = new ObjectOutputStream(
                        deliveryService.getOutputStream());
                out.flush();
                in = new ObjectInputStream(
                        deliveryService.getInputStream());
                 //ba = new InputStreamReader(deliveryService.getInputStream());
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                Coordinator.this.shutdown();
            }
        }

        @Override
        public void run() {
            while (true) {
                Object objIn = null;
                try {
                    objIn = in.readObject();
                    //char[] a = new char[2000];
                    //ba.read(a);
                    //System.out.println(a);
                } catch (Exception e) {
                    try {
                        while (in.available() > 0) {
                            System.out.println(in.read());
                        }
                    } catch (IOException ex) {

                    }
                    System.out.println(e.toString());
                    for (StackTraceElement s : e.getStackTrace()) {
                        System.out.println(s.toString());
                    }
                    log.error("Unable to read from DeliveryService's input stream! " + e.getMessage());
                    Coordinator.this.shutdown();
                    return;
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof ErrorMessage) {
                        log.error(((ErrorMessage) objIn).getContents());
                    } else if (objIn instanceof InfoMessage) {
                        log.writeToLog(((InfoMessage) objIn).getContents());
                    } else {
                        log.error("Unexpected internal message received from DeliveryService - " + objIn.getClass().getName());
                    }
                } else if (objIn instanceof Message) {
                    if (objIn instanceof SubscriberDisconnectMessage) {
                        log.writeToLog("Subscriber " + ((SubscriberDisconnectMessage) objIn).getEntityName()
                                + " disconnected from broker!");
                        //forward to messageReceiver to kill the SubForBroker
                        sendMessage(objIn, messageReceiverRelay.out);
                    } else if (objIn instanceof MobileBrokerDisconnectMessage) {
                        log.writeToLog("Mobile broker " + ((MobileBrokerDisconnectMessage) objIn).getEntityName()
                                + " disconnected from broker!");
                        //forward to messageReceiver to kill the SubForBroker
                        sendMessage(objIn, messageReceiverRelay.out);
                    } else {
                        log.error("Unexpected external message received from DeliveryService - " + objIn.getClass().getName());
                    }
                } else {
                    log.error("Object received from the DeliveryService is not of class Message"
                            + " or InternalMessage! (" + objIn.getClass().getName() + " instead). Ignoring...");
                }
            }
        }

        /**
         * Sends a start message to the DeliveryService components of the cloud
         * broker. It should make the DeliveryService start the thread that
         * listens for incoming connection requests from subscribers.
         */
        void sendStartMessage() {
            try {
                out.writeObject(new StartComponentMessage());
                out.flush();
            } catch (IOException e) {
                //if deliveryService found terminated shut everything down...
                log.error("DeliveryService found dead while sending the start message.");
                Coordinator.this.shutdown();
            }
        }
    }

    /**
     * Manages the in/out streams of the rootMatcher subprocess...
     */
    private class MatcherRelay implements Runnable {

        ObjectInputStream in;
        ObjectOutputStream out;

        InternalMessage elasticityMessage = null;

        private int matcherID;
        boolean running = true;

        public MatcherRelay(Process matcher, int matcherID) {
            this.matcherID = matcherID;
            try {
                // catches both the stdout and stderr of the subprocess
                out = new ObjectOutputStream(
                        matcher.getOutputStream());
                out.flush();
                in = new ObjectInputStream(
                        matcher.getInputStream());
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                Coordinator.this.shutdown();
            }
        }

        @Override
        public void run() {

            while (running) {
                Object objIn = null;
                try {
                    objIn = in.readObject();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    for (StackTraceElement s : e.getStackTrace()) {
                        System.out.println(s.toString());
                    }
                    log.error("Unable to read from Matcher " + matcherID + "'s input stream! " + e.getMessage());
                    Coordinator.this.shutdown();
                    return;
                }

                if (objIn instanceof InternalMessage) {
                    if (objIn instanceof ErrorMessage) {
                        log.error(((ErrorMessage) objIn).getContents());
                    } else if (objIn instanceof InfoMessage) {
                        log.writeToLog(((InfoMessage) objIn).getContents());
                    } else if (objIn instanceof SplitBooleanMatcherMessage) {
                        long start = System.currentTimeMillis();
                        try {
                            int success = initAdditionalBooleanMatcher();
                            if (success != -1) {
                                synchronized (mutexMatcher) {
                                    MatcherRelay newRelay = matcherRelays[success];
                                    List<Subscription> subs = ((SplitBooleanMatcherMessage) objIn).getSubscriptions();
                                    //System.out.println("PRETPLATE=" + subs.size() + " MatcherID=" + (numberOfMatchers - 1));
                                    for (int i = 0; i < subs.size(); i++) {
                                        SubscribeMessage subMess = new SubscribeMessage(subs.get(i), false);
                                        sendMessage(subMess, newRelay.out);
                                    }
                                    sendMessage(new ElasticityReplyMessage(true), out);
                                    //System.out.println("SPLIT;" + matcherID + ";" + (System.currentTimeMillis() - start) + ";" + matcherRelays.length + ";true;" + System.currentTimeMillis() / 1000);
                                }
                            } else {
                                sendMessage(new ElasticityReplyMessage(false), out);
                                //System.out.println("SPLIT;" + matcherID + ";" + (System.currentTimeMillis() - start) + ";" + matcherRelays.length + ";false;" + System.currentTimeMillis() / 1000);
                            }
                        } catch (Exception e) {
                            sendMessage(new ElasticityReplyMessage(false), out);
                            //System.out.println("SPLIT;" + matcherID + ";" + (System.currentTimeMillis() - start) + ";" + matcherRelays.length + ";false;" + System.currentTimeMillis() / 1000);
                        }
                    } else if (objIn instanceof MergeBooleanMatcherMessage) {
                        long start = System.currentTimeMillis();
                        synchronized (mutexMatcher) {
                            if (removeBooleanMatcher(matcherID)) {
                                List<Subscription> subs = ((MergeBooleanMatcherMessage) objIn).getSubscriptions();

                                for (int i = 0; i < subs.size(); i++) {
                                    SubscribeMessage subMess = new SubscribeMessage(subs.get(i), false);
                                    sendMessage(subMess, matcherRelays[matcherRoundRobin].out);
                                    matcherRoundRobin = (matcherRoundRobin + 1) % numberOfMatchers;
                                }
                                sendMessage(new ElasticityReplyMessage(true), out);
                                try {
                                    in.close();
                                    out.close();
                                    running = false;
                                    log.writeToLog("Matcher " + matcherID + "merged with other matchers ");
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    for (StackTraceElement s : e.getStackTrace()) {
                                        System.out.println(s.toString());
                                    }
                                    log.error("During merge of the matcher " + matcherID + "connections shutted down" + e.getMessage());
                                }
                                
                                //System.out.println("MERGE;" + matcherID + ";" + (System.currentTimeMillis() - start) + ";" + matcherRelays.length + ";true;" + System.currentTimeMillis() / 1000);
                            } else {
                                sendMessage(new ElasticityReplyMessage(false), out);
                                //System.out.println("MERGE;" + matcherID + ";" + (System.currentTimeMillis() - start) + ";" + matcherRelays.length + ";false;" + System.currentTimeMillis() / 1000);
                            }
                        }
                    } else if (objIn instanceof SplitTopKWMatcherMessage) {
                        try {
                            int success = initAdditionalTopKWMatcher();
                            if (success != -1) {
                                MatcherRelay newRelay = topKWMatcherRelays[success];
                                List<Subscription> subs = ((SplitTopKWMatcherMessage) objIn).getSubscriptions();
                                //System.out.println("PRETPLATE=" + subs.size() + " MatcherID=" + (topKWNumberOfMatchers - 1));
                                for (int i = 0; i < subs.size(); i++) {
                                    SubscribeMessage subMess = new SubscribeMessage(subs.get(i), false);
                                    sendMessage(subMess, newRelay.out);
                                }
                                sendMessage(new ElasticityReplyMessage(true), out);
                            } else {
                                sendMessage(new ElasticityReplyMessage(false), out);
                            }
                        } catch (Exception e) {
                            sendMessage(new ElasticityReplyMessage(false), out);
                        }
                    } else if (objIn instanceof MergeTopKWMatcherMessage) {
                        if (removeTopKWMatcher(matcherID)) {
                            List<Subscription> subs = ((MergeTopKWMatcherMessage) objIn).getSubscriptions();
                            synchronized (mutexMatcher) {
                                for (int i = 0; i < subs.size(); i++) {
                                    SubscribeMessage subMess = new SubscribeMessage(subs.get(i), false);
                                    sendMessage(subMess, topKWMatcherRelays[topKWMatcherRoundRobin].out);
                                    topKWMatcherRoundRobin = (topKWMatcherRoundRobin + 1) % topKWNumberOfMatchers;
                                }
                            }
                            try {
                                in.close();
                                out.close();
                                running = false;
                                log.writeToLog("TopKWMatcher " + matcherID + "merged with other matchers ");
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                for (StackTraceElement s : e.getStackTrace()) {
                                    System.out.println(s.toString());
                                }
                                log.error("During merge of the topKW matcher " + matcherID + "connections shutted down" + e.getMessage());
                            }
                        } else {
                            sendMessage(new ElasticityReplyMessage(false), out);
                        }
                    } else {
                        log.error("Unexpected internal message received from Matcher - " + objIn.getClass().getName());
                    }
                } else if (objIn instanceof Message) {
                    log.error("Unexpected external message received from Matcher - " + objIn.getClass().getName());
                } else {
                    log.error("Object received from the Matcher is not of class Message"
                            + " or InternalMessage! (" + objIn.getClass().getName() + " instead). Ignoring...");
                }
            }
        }

        public void setMatcherId(int id) {
            matcherID = id;
        }
    }

}
