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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

import org.openiot.cupus.entity.NetworkEntity;
import org.openiot.cupus.message.Message;
import org.openiot.cupus.message.external.PublishMessage;
import org.openiot.cupus.message.external.PublisherDisconnectMessage;

/**
 * This class is a representation of a publisher connected with a broker on the
 * broker it is connected with. It is used for communication with the publisher
 * and identification of the publisher on the broker (via UUID).
 *
 * @author Eugen
 *
 */
public class PublisherForBroker extends NetworkEntity implements Runnable {

    protected UUID pubID;

    protected MessageReceiver broker;
    protected Socket socket;

    private Message message;

    ObjectInputStream inFromClient;
    ObjectOutputStream outToClient;

    private boolean isRunning = false;

    /**
     * Constructor
     *
     * @param myName Name of publisher it represents
     * @param publisherOriginalID original UUID of connected publisher
     * @param myIP IP address of connected publisher
     * @param myPort Port number of connected publisher
     */
    public PublisherForBroker(String myName, String myIP, int myPort,
            UUID publisherOriginalID, MessageReceiver myDeliveryService) {
        super(myName, myIP, myPort);
        this.pubID = publisherOriginalID;
        this.broker = myDeliveryService;
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
                Message m = (Message) inFromClient.readObject();
                objIn = m;
            } catch (Exception e) {
                for (StackTraceElement ee : e.getStackTrace()) {
                    broker.informBroker(ee.toString(), false);
                }
                break;
            }
            
            if (!(objIn instanceof Message)) {
                //TODO send some sort of NACK
                continue;
            } else {
                message = (Message) objIn;
            }

            if (message instanceof PublisherDisconnectMessage) {
                //TODO send some sort of ACK
                break;
            } else if (message instanceof PublishMessage) {
                broker.publish(pubID, (PublishMessage) message);
                //TODO send some sort of ACK
            }
        }
        terminateConnection();
        broker.removePublisher(pubID);
    }

    /**
     * Used for unregistering (disconnecting) a publisher
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
     * Used for (re)connecting a previously registered subscriber...
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((pubID == null) ? 0 : pubID.hashCode());
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
        if (!(obj instanceof PublisherForBroker)) {
            return false;
        }
        PublisherForBroker other = (PublisherForBroker) obj;
        if (pubID == null) {
            if (other.pubID != null) {
                return false;
            }
        } else if (!pubID.equals(other.pubID)) {
            return false;
        }
        return true;
    }
}
