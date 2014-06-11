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

import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.MobileBrokerRegisterMessage;
import org.openiot.cupus.message.external.NotifyMessage;
import org.openiot.cupus.message.external.NotifySubscriptionMessage;

import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aleksandar
 */
public class TCPMobileBroker extends MobileBrokerOutgoingTCP {

    /**
     * Socket for incoming notifications (about matched publications) from the
     * Broker
     */
    private Socket receivingSocket = null;
    private ObjectInputStream receivingIn = null;

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

    @Override
    protected void startIncomingConnection() {
        ServerSocket brokerBackConnectSpot = null;
        try {
            brokerBackConnectSpot = new ServerSocket(0);
            brokerBackConnectSpot.setSoTimeout(10000);
        } catch (Exception e1) {
            String message = "Unable to open ServerSocket for broker back-connection!";
            log.error(message);
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
        sendMessageInBackGround(connectMessage);

        //wait for 'response' - in the form of connect request from Broker's DeliveryService
        try {
            receivingSocket = brokerBackConnectSpot.accept();
            receivingIn = new ObjectInputStream(receivingSocket.getInputStream());
        } catch (Exception e1) {
            String message = "Unable to establish back-connection from broker!";
            log.error(message);
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
    }

    @Override
    protected void terminateIncomingConnectionInBackground() {
        receivingSocket = null;
        receivingIn = null;
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
                    TCPMobileBroker.this.notify(msg.getPublication(), msg.isUnpublish());
                } else if (objIn instanceof NotifySubscriptionMessage) {
                	NotifySubscriptionMessage msg = (NotifySubscriptionMessage) objIn;
                    announcement(msg.getSubscription(), msg.isRevoke());
                } else {
                    log.writeToLog("Unkown request/response received from broker (type = "
                            + objIn.getClass().getName() + "). Ignoring...");
                }
            }
        }


    }
}
