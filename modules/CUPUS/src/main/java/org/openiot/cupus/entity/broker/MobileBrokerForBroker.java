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
import java.net.Socket;
import java.util.UUID;

import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.AnnounceMessage;
import org.openiot.cupus.message.external.MobileBrokerDisconnectMessage;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.SubscribeMessage;

/**
 * This class is a representation of a mobile broker connected with a broker on
 * the broker it is connected with. It is used for communication with the mobile
 * broker and identification of the mobile broker on the broker (via UUID).
 *
 * @author Aleksandar NOTE: STO JE NAKON DISCONNECTA S
 * subscriberom/publisherom/announceom!!!!
 */
public class MobileBrokerForBroker extends NetworkEntity implements Runnable {

    protected UUID mbID;
    protected MessageReceiver broker;
    protected Socket socket;
    ObjectInputStream inFromClient;
    ObjectOutputStream outToClient;
    private Message message;
    private boolean isRunning = false;

    /**
     * Constructor
     *
     * @param myName Name of mobile broker it represents
     * @param mobileBrokerOriginalID original UUID of connected mobile broker
     * @param myIP IP address of connected mobile broker
     * @param myPort Port number of connected mobile broker
     */
    public MobileBrokerForBroker(String myName, String myIP, int myPort,
            UUID mobileBrokerOriginalID, MessageReceiver myBroker) {
        super(myName, myIP, myPort);
        this.mbID = mobileBrokerOriginalID;
        this.broker = myBroker;
    }

    @Override
    public void run() {
        if (isRunning || !broker.isRunning) {
            return;
        }
        isRunning = true;

        while (isRunning) {

            Object objIn = null;
            try {
                objIn = inFromClient.readObject();
            } catch (Exception e) {
                terminateConnection();
                broker.sendInternalMessage(
                        new MobileBrokerDisconnectMessage(myName, getId()));
                return;
            }

            if (!(objIn instanceof Message)) {
                //TODO send some sort of NACK
                continue;
            } else {
                message = (Message) objIn;
            }

            if (message instanceof MobileBrokerDisconnectMessage) {
                terminateConnection();
                broker.sendInternalMessage(message);
                //TODO send some sort of ACK
                return;
                /*} else if (message instanceof SubscriberUnregisterMessage) {
                 terminateConnection();
                 broker.removeSubscriber((SubscriberUnregisterMessage)message);
                 //TODO send some sort of ACK
                 return;*/
            } else if (message instanceof SubscribeMessage) {
                broker.subscribe(mbID, (SubscribeMessage) message);
                //TODO send some sort of ACK
            } else if (message instanceof PublishMessage) {
                broker.publish(mbID, (PublishMessage) message);
                //TODO send some sort of ACK
            } else if (message instanceof AnnounceMessage) {
                broker.announce(mbID, (AnnounceMessage) message);
                //TODO send some sort of ACK
            }
        }
        terminateConnection(); //just in case
    }

    /**
     * Used for terminating the connection to the mobile broker
     */
    public void terminateConnection() {

        if (!isRunning && socket == null) {
            return;
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            //ignoring...
        }
        socket = null;
        this.isRunning = false;
    }

    /**
     * Used for (re)connecting a previously registered mobile broker...
     *
     * @param socket
     */
    public boolean setSocketAndStreams(Socket socket,
            ObjectInputStream in, ObjectOutputStream out) {
        if (isRunning) {
            return false;
        }
        this.socket = socket;
        this.inFromClient = in;
        this.outToClient = out;
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public UUID getMobileBrokerID() {
        return mbID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mbID == null) ? 0 : mbID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MobileBrokerForBroker)) {
            return false;
        }
        MobileBrokerForBroker other = (MobileBrokerForBroker) obj;
        if (mbID == null) {
            if (other.mbID != null) {
                return false;
            }
        } else if (!mbID.equals(other.mbID)) {
            return false;
        }
        return true;
    }
}
