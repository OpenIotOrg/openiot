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

package org.openiot.cupus.mobile.entity.mobilebroker;

import android.content.Context;
import android.os.AsyncTask;

import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.NotifyMessage;
import org.openiot.cupus.message.external.NotifySubscriptionMessage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Aleksandar
 */
public class TCPMobileBroker extends AbstractMobileBroker {

    /**
     * Socket for sending messages (subscriptions and connect/disconnect/etc.)
     * to the Broker
     */
    private Socket sendingSocket = null;
    private ObjectOutputStream sendingOut = null;
    /**
     * Socket for incoming notifications (about matched publications) from the
     * Broker
     */
    private Socket receivingSocket = null;
    private ObjectInputStream receivingIn = null;

    private AnnouncementListener announcementListener;

    private Set<String> distinctSubscriptionAttributes = new HashSet<String>();

    public TCPMobileBroker(String myName, String myBrokerIP, int myBrokerPort, Context context) {
        super(myName, myBrokerIP, myBrokerPort, context);
    }

    /**
     * Constructor - subscriber can be created via configuration file or
     * directly
     */
    public TCPMobileBroker(File configFile, Context context) {
        super(configFile, context);
    }


    public class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            connectInBackGround();
            return null;
        }
    }

    /**
     * Used for connecting mobile broker to broker
     */
    @Override
    public void connect() {
        new Connection().execute();
    }


    private void connectInBackGround() {
        if (connected) {
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
            try {
                sendingSocket.close();
            } catch (Exception e) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        } catch (IOException ex) {
            log.writeToLog("Failed to open stream to the Broker: " + ex);
            try {
                sendingSocket.close();
            } catch (Exception e) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        }

        ServerSocket brokerBackConnectSpot = null;
        try {
            brokerBackConnectSpot = new ServerSocket(0);
            brokerBackConnectSpot.setSoTimeout(10000);
        } catch (Exception e1) {
            log.error("Unable to open ServerSocket for broker back-connection!");
            try {
                sendingSocket.close();
            } catch (Exception e2) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        }
        int brokerBackConnectPort = brokerBackConnectSpot.getLocalPort();

        //send the register message
        Message connectMessage = new MobileBrokerRegisterMessage(myName, this.getId(), myIP, brokerBackConnectPort);
        this.sendMessageInBackGround(connectMessage);

        //wait for 'response' - in the form of connect request from Broker's DeliveryService
        try {
            receivingSocket = brokerBackConnectSpot.accept();
            receivingIn = new ObjectInputStream(receivingSocket.getInputStream());
        } catch (Exception e1) {
            log.error("Unable to establish back-connection from broker!");
            log.error("Exception: " + e1.getMessage());
            try {
                sendingSocket.close();
            } catch (Exception e2) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        }
        this.connected = true;
        log.writeToLog("Connected to Broker " + myBrokerIP + ":" + myBrokerPort);

        //create broker listener that will handle incoming notify or subscription messages
        Thread brokerListener = new Thread(new TCPMobileBroker.MobileBrokerListenerThread());
        brokerListener.start();

        //processing subscriptions, publications and announcements that were added while disconnected...
        Iterator<Subscription> iteratorSub = outboxSubs.iterator();
        while (iteratorSub.hasNext()) {
            subscribe(iteratorSub.next());
            iteratorSub.remove();
        }
        Iterator<Publication> iteratorPublication = outboxPubs.iterator();
        while (iteratorPublication.hasNext()) {
            publish(iteratorPublication.next());
            iteratorPublication.remove();
        }
        Iterator<Announcement> iteratorAnn = outboxAnnouncements.iterator();
        while (iteratorAnn.hasNext()) {
            announce(iteratorAnn.next());
            iteratorAnn.remove();
        }
    }


    private class TerminateConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            terminateConnectionInBackground();
            return null;
        }
    }

    /**
     * For terminating the connection... closes to outSocket and sets everything
     * to null
     */
    public void terminateConnection() {
        new TerminateConnection().execute();
    }


    private void terminateConnectionInBackground() {
        try {
            sendingSocket.close();
            //the receiving scket will be closed from the server side
        } catch (Exception e) {
            //ignore
        }
        sendingSocket = null;
        sendingOut = null;
        receivingSocket = null;
        receivingIn = null;
        this.connected = false;
    }


    private class SendMessage extends AsyncTask {

        private Message message;

        public SendMessage(Message sendMsg) {
            this.message = sendMsg;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            sendMessageInBackGround(message);
            return null;
        }
    }

    /**
     * Used for sending messages to broker
     *
     * @param sendMsg message to be sent
     */
    protected void sendMessage(Message sendMsg) {
        new SendMessage(sendMsg).execute();
    }


    private void sendMessageInBackGround(Message sendMsg) {
        try {
            sendingOut.writeObject(sendMsg);
            sendingOut.flush();
        } catch (Exception e1) {
            log.error("Message " + sendMsg + " not sent. Disconnecting because of connection problems.");
            e1.printStackTrace();
            terminateConnection();
        }
    }

    public void setAnnouncementListener(AnnouncementListener announcementListener) {
        this.announcementListener = announcementListener;
    }

    /**
     * Thread for receiving notify messages from the Broker's DeliveryService
     * component.
     */
    private class MobileBrokerListenerThread implements Runnable {

        @Override
        public void run() {

            while (connected) {
                Object objIn = null;
                try {
                    objIn = receivingIn.readObject();
                } catch (Exception e) {
                    log.error("Error on input stream from Broker. Terminating connection...");
                    terminateConnection();
                    log.writeToLog("Disconnected from Broker.");
                    return;
                }

                if (objIn instanceof NotifyMessage) {                	
                    NotifyMessage msg = (NotifyMessage) objIn;
                    notify(msg.getPublication(), msg.isUnpublish());
                } else if (objIn instanceof NotifySubscriptionMessage) {                	
                	NotifySubscriptionMessage msg = (NotifySubscriptionMessage) objIn;
                    announcement(msg.getSubscription(), msg.isRevoke());
                } else {
                    log.writeToLog("Unkown request/response received from broker (type = "
                            + objIn.getClass().getName() + "). Ignoring...");
                }
            }
        }

        /**
         * Used for handling notifications about new publications
         *
         * @param publication New Publication
         */
        public void notify(Publication publication, boolean unpublish) {
            synchronized (publicationListMutex) {
                if (!unpublish) {
                    publicationList.add(publication);
                } else {
                    publicationList.remove(publication);
                }
            }
            if (!unpublish) {
                log.writeToLog("Received a publication from broker (" + publication.getId() + ")");
                notificationListener.notify(getId(), myName, publication);
            } else {
                log.writeToLog("Received an unpublication from broker (" + publication.getId() + ")");
            }
        }

        /**
         * Used for handling new subscriptions from the broker
         *
         * @param subscription New Subscription
         */
        public void announcement(Subscription subscription, boolean unsubscribe) {
            synchronized (subscriptionListMutex) {
                if (!unsubscribe) {
                    TripletSubscription tripletSubscription = (TripletSubscription) subscription;
                    if (!distinctSubscriptionAttributes.containsAll(tripletSubscription.attributes())) {
                        distinctSubscriptionAttributes.addAll(tripletSubscription.attributes());
                        boolean sendSubscriptions = false;
                        for(Announcement announcement : activeAnnouncements) {
                            TripletAnnouncement tripletAnnouncement = (TripletAnnouncement) announcement;
                            if (tripletAnnouncement.coversSubscription(tripletSubscription)) {
                                sendSubscriptions = true;
                            }
                        }
                        if (sendSubscriptions) {
                            announcementListener.announcement(tripletSubscription.attributes(), unsubscribe);
                        }
                    }

                    brokerSubs.add(subscription);
                    log.writeToLog("Received subscription from broker (" + subscription.getId() + ")");

                } else {
                    brokerSubs.remove(subscription);
                    TripletSubscription tripletSubscription = (TripletSubscription) subscription;
                    distinctSubscriptionAttributes.removeAll(tripletSubscription.attributes());
                    announcementListener.announcement(tripletSubscription.attributes(), unsubscribe);
                    log.writeToLog("Received unsubscription from broker (" + subscription.getId() + ")");

                }

            }
        }
    }
}
