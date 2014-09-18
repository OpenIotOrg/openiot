package org.openiot.cupus.mobile.entity.mobilebroker;

import android.content.Context;
import android.os.AsyncTask;

import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.message.Message;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Marko on 6.6.2014..
 */
public abstract class MobileBrokerOutgoingTCP extends AbstractMobileBroker {

    /**
     * Socket for sending messages (subscriptions and connect/disconnect/etc.)
     * to the Broker
     */
    protected Socket sendingSocket = null;
    protected ObjectOutputStream sendingOut = null;

    protected AnnouncementListener announcementListener;

    protected Set<String> distinctSubscriptionAttributes = new HashSet<String>();

    public MobileBrokerOutgoingTCP(String myName, String myBrokerIP, int myBrokerPort, Context context){
        super(myName,myBrokerIP,myBrokerPort,context);
    }

    public MobileBrokerOutgoingTCP(File configFile, Context context){
        super(configFile,context);
    }

    /**
     * Used for connecting mobile broker to broker
     */
    @Override
    public void connect() {
        new EstablishConnection().execute();
    }

    /**
     * For terminating the connection... closes to outSocket and sets everything
     * to null
     */
    @Override
    public void terminateConnection() {
        new TerminateConnection().execute();
    }

    @Override
    public void setAnnouncementListener(AnnouncementListener announcementListener) {
        this.announcementListener = announcementListener;
    }

    /**
     * Used for sending messages to broker
     *
     * @param sendMsg message to be sent
     */
    @Override
    protected void sendMessage(Message sendMsg) {
        new SendMessage(sendMsg).execute();
    }

    /**
     * AsyncTask for establishing connection in both ways
     */
    public class EstablishConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            startOutgoingConnection();
            startIncomingConnection();
            processCollectedWhileDisconnected();
            return null;
        }
    }

    /**
     * AsyncTask for terminating both way connections
     */
    private class TerminateConnection extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            terminateOutgoingConnectionInBackground();
            terminateIncomingConnectionInBackground();
            MobileBrokerOutgoingTCP.this.connected = false;
            return null;
        }
    }

    /**
     * AsyncTask for sending message to server
     */
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
     * For establishing connection from user to server
     */
    private void startOutgoingConnection(){

        if (connected) {
            String message = "Connect request received while being connected. Ignored.";
            log.writeToLog(message);
            return;
        }
        try {
            sendingSocket = new Socket(this.myBrokerIP, this.myBrokerPort);
            this.myPort = sendingSocket.getLocalPort();
            sendingOut = new ObjectOutputStream(sendingSocket.getOutputStream());
            sendingOut.flush();
        } catch (UnknownHostException ex) {
            String message = "Connecting failed - Unknown Broker Host or Port: " + ex;
            log.writeToLog(message);
            try {
                sendingSocket.close();
            } catch (Exception e) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        } catch (IOException ex) {
            String message = "Failed to open stream to the Broker: " + ex;
            log.writeToLog(message);
            try {
                sendingSocket.close();
            } catch (Exception e) {
            }
            sendingSocket = null;
            sendingOut = null;
            return;
        }
    }

    /**
     * For establishing back connection from server to user
     */
    protected abstract void startIncomingConnection();

    /**
     * Processing collected data while been disconnected from server
     */
    private void processCollectedWhileDisconnected(){

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

    /**
     * Send msg to server
     * @param sendMsg
     */
    protected void sendMessageInBackGround(Message sendMsg) {
        try {
            sendingOut.writeObject(sendMsg);
            sendingOut.flush();
        } catch (Exception e1) {
            log.error("Message " + sendMsg + " not sent. Disconnecting because of connection problems.");
            e1.printStackTrace();
            terminateConnection();
        }
    }

    /**
     * Terminate outgoing connection
     */
    private void terminateOutgoingConnectionInBackground(){
        try {
            sendingSocket.shutdownInput();
            sendingSocket.shutdownOutput();
            sendingSocket.close();
            //the receiving socket will be closed from the server side
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendingSocket = null;
        sendingOut = null;
    }

    /**
     * Terminate incoming connection
     */
    protected abstract void terminateIncomingConnectionInBackground();

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
                log.writeToLog("Received subscription from broker (" + subscription + ")");

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
