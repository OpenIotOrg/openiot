package org.openiot.cupus.mobile.entity.mobilebroker;

import android.content.Context;

import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.entity.mobilebroker.MobileBrokerInterface;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.mobile.util.AndroidLogWriter;
import org.openiot.cupus.mobile.util.Utils;
import org.openiot.cupus.util.ReadingWritingXML;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by kpripuzic on 1/21/14.
 */
public abstract class AbstractMobileBroker extends NetworkEntity implements MobileBrokerInterface {
    //List of all locally created Publications
    protected ArrayList<Publication> allPubs = new ArrayList<Publication>();

    // List od all locally active Publications
    protected ArrayList<Publication> activePubs = new ArrayList<Publication>();
    protected ArrayList<Publication> outboxPubs = new ArrayList<Publication>();
    protected List<Subscription> allSubs = new ArrayList<Subscription>();

    // List od all active Subscriptions
    protected List<Subscription> activeSubs = new ArrayList<Subscription>();
    protected List<Subscription> outboxSubs = new ArrayList<Subscription>();
    protected NotificationListener notificationListener;
    protected List<Publication> publicationList = new ArrayList<Publication>();

    // List od all active Annoncements
    protected ArrayList<Announcement> allAnnouncements = new ArrayList<Announcement>();
    protected ArrayList<Announcement> activeAnnouncements = new ArrayList<Announcement>();
    protected ArrayList<Announcement> outboxAnnouncements = new ArrayList<Announcement>();

    // List od all active subscriptions received from the cloud broker
    protected List<Subscription> brokerSubs = new ArrayList<Subscription>();

    //List of configuration parameters
    protected String myBrokerIP;
    protected int myBrokerPort;
    protected boolean connected;
    protected AndroidLogWriter log;
    protected boolean logWriting = true;
    protected boolean testing = true;

    //List od locks
    protected Object publicationListMutex = new Object();
    protected Object subscriptionListMutex = new Object();

    /**
     * Constructor - mobile broker can be created via configuration file or
     * directly
     *
     * @param myName Mobile Broker's name
     * @param myBrokerIP Mobile Broker's connecting broker IP address
     * @param myBrokerPort Mobile Broker's connecting broker port
     */
    public AbstractMobileBroker(String myName, String myBrokerIP, int myBrokerPort, Context context) {
        super(myName, Utils.getIPAddress(true), -1);

        log = new AndroidLogWriter(this.myName + "_mobilebrokerLog.txt", logWriting, testing, context);

        if (this.myIP.equals("")) {
            log.writeToLog("Does not have correct IP address " + this.myIP, true);
            this.myIP = "localhost";

        }
        this.myBrokerIP = myBrokerIP;
        this.myBrokerPort = myBrokerPort;
        this.connected = false;

        log.writeToLog("Mobile Broker name: " + this.myName, true);
        log.writeToLog("Mobile Broker broker port: " + this.myBrokerPort, true);
        log.writeToLog("Mobile Broker broker IP: " + this.myBrokerIP, true);
        log.writeToLog("", true);
    }

    /**
     * Constructor - subscriber can be created via configuration file or
     * directly
     */
    public AbstractMobileBroker(File configFile, Context context) {
        super("", Utils.getIPAddress(true), -1);

        log = new AndroidLogWriter(this.myName + "_mobilebrokerLog.txt", logWriting, testing, context);

        if (this.myIP.equals("")) {
            this.myIP = "localhost";
        }

        try {
            Properties mbProps = new Properties();
            FileInputStream fileIn = new FileInputStream(configFile);
            mbProps.load(fileIn);
            fileIn.close();

            this.myName = mbProps.getProperty("mobilebrokerName");
            if (this.myName == null) {
                throw new NullPointerException("Name must be defined!");
            }

            this.myBrokerIP = mbProps.getProperty("brokerIP");
            if (this.myBrokerIP == null) {
                throw new NullPointerException("BrokerIP must be defined!");
            }

            this.myBrokerPort = Integer.parseInt(mbProps.getProperty("brokerPort"));

            if (mbProps.getProperty("testing", "false").toLowerCase().equals("false")) {
                this.testing = false;
            } else if (mbProps.getProperty("testing").toLowerCase().equals("true")) {
                this.testing = true;
            } else {
                System.err.println("Config param \"testing\" should be either true or false! Setting to default false.");
                this.testing = false;
            }
            if (mbProps.getProperty("logWriting", "true").toLowerCase().equals("true")) {
                this.logWriting = true;
            } else if (mbProps.getProperty("logWriting").toLowerCase().equals("false")) {
                this.logWriting = false;
            } else {
                System.err.println("Config param \"logWriting\" should be either true or false! Setting to default true.");
                this.logWriting = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        log.writeToLog("Mobile Broker name: " + this.myName, true);
        log.writeToLog("Mobile Broker broker port: " + this.myBrokerPort, true);
        log.writeToLog("Mobile Broker broker IP: " + this.myBrokerIP, true);
        log.writeToLog("", true);
    }

    /**
     * Used for connecting mobile broker to broker
     */
    @Override
    public abstract void connect();

    /**
     * Used for handling negative response message (reconnecting to broker)
     *
     * @param brokerIP IP address of reconnecting broker
     * @param brokerPort Port number of reconnecting broker
     */
    @Override
    public void reconnect(String brokerIP, int brokerPort) {
        if (connected) {
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
        if (connected) {
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
            Message disconnectMessage = new MobileBrokerDisconnectMessage(myName, getId());
            this.sendMessage(disconnectMessage);
            //TODO FIXME no confirmation is waited for... it is just assumed the communication went ok
            terminateConnection();
            log.writeToLog("Disconnected from broker!");
        } else {
            log.writeToLog("Cannot disconnect from broker because not connected.");
        }
    }

    /**
     * For terminating the connection... closes to outSocket and sets everything
     * to null
     */
    public abstract void terminateConnection();

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
            log.writeToLog("Subscription " + subscription + " sent to broker.");
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
            log.writeToLog("Subscription " + subscription + " put in outbox because not connected to broker.");
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
        } else if (outboxSubs.contains(subscription)) {
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
    protected abstract void sendMessage(Message sendMsg);

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
     * @return returns publication UUID
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
        for (Subscription subscritpion : brokerSubs) {
            if (!subscritpion.isValid()) {
                brokerSubs.remove(subscritpion);
                continue;
            }
            if (subscritpion.coversPublication(publication)) {
                if (connected) {
                    Message sendMsg = new PublishMessage(publication, false);
                    this.sendMessage(sendMsg);
                    log.writeToLog("Publication " + publication + " sent to broker.");
                    //TODO no confirmation is waited for here...
                    activePubs.add(publication);
                    allPubs.add(publication);
                } else {
                    outboxPubs.add(publication);
                    allPubs.add(publication);
                    log.writeToLog("Publication " + publication + " put in outbox because not connected to broker.");
                }
                return;
            }
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
        } else if (outboxPubs.contains(publication)) {
            outboxPubs.remove(publication);
            log.writeToLog("Publication unpublished from outbox. No need to contact the broker.");
        } else {
            log.writeToLog("Unpublication impossible because publication is not active.");
        }
    }

    /**
     * Announcing the data source from XML file
     *
     * @param fileName Name of input file
     * @return returns announcement UUID
     */
    public UUID announceFromXMLFile(String fileName) {
        return announce(fileName, "");
    }

    /**
     * Announcing the data source from string
     *
     * @param inputString string containing XML announcement
     * @return returns announcement UUID
     */
    public UUID announceFromXMLString(String inputString) {
        return announce("", inputString);
    }

    /**
     * Announcing the data soruce from XML file or content (not both - one has to
     * be empty string (""))
     *
     * @param fileName Name of input file
     * @param inputString Name of input String
     * @return returns announcement UUID
     */
    public UUID announce(String fileName, String inputString) {
        ReadingWritingXML input = new ReadingWritingXML(fileName, inputString);
        input.read();
        Announcement ann = input.createAnnouncement();
        this.announce(ann);
        return ann.getId();
    }

    /**
     * Used for announcing
     *
     * @param announcement Announcement to be published
     */
    public void announce(Announcement announcement) {
        if (connected) {
            Message sendMasg = new AnnounceMessage(announcement, false);
            this.sendMessage(sendMasg);
            log.writeToLog("Announcement " + announcement + " sent to broker.");
            //TODO no confirmation is waited for here...
            if (!allAnnouncements.contains(announcement)) {
                allAnnouncements.add(announcement);
                activeAnnouncements.add(announcement);
            } else if (!activeAnnouncements.contains(announcement)) {
                activeAnnouncements.add(announcement);
            }
        } else {
            if (!allAnnouncements.contains(announcement)) {
                allAnnouncements.add(announcement);
                activeAnnouncements.add(announcement);
            } else if (!activeAnnouncements.contains(announcement)) {
                activeAnnouncements.add(announcement);
            }
            log.writeToLog("Announcement " + announcement + " put in outbox because not connected to broker.");
        }
    }

    /**
     * Used for revoke of previously sent announcement
     *
     * @param announcement Announcement to be published
     */
    public void revokeAnnouncement(Announcement announcement) {
        if (activeAnnouncements.contains(announcement)) {
            if (connected) {
                Message sendMsg = new AnnounceMessage(announcement, true);
                this.sendMessage(sendMsg);
                log.writeToLog("Request for revoke of announcement sent to broker.");
            }
            activeAnnouncements.remove(announcement);
        } else if (outboxAnnouncements.contains(announcement)) {
            outboxAnnouncements.remove(announcement);
            log.writeToLog("Announcement removed from outbox. No need to contact the broker.");
        } else {
            log.writeToLog("Revoke of announcement impossible because announcement is no longer active.");
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

    @Override
    public void unregisterFromBroker() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
