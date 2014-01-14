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
import org.openiot.cupus.message.external.SubscribeMessage;
import org.openiot.cupus.message.external.SubscriberDisconnectMessage;
import org.openiot.cupus.message.external.SubscriberUnregisterMessage;

/**
 * This class is a representation of a subscriber connected with a broker
 * on the broker it is connected with.
 * It is used for communication with the subscriber and identification
 * of the subscriber on the broker (via UUID).
 * 
 * @author Eugen
 * 
 */
public class SubscriberForBroker extends NetworkEntity implements Runnable {
	
	protected UUID subID;
	
	protected MessageReceiver broker;
    protected Socket socket;
	ObjectInputStream inFromClient;
	ObjectOutputStream outToClient;
	
	private Message message;
    
    private boolean isRunning = false;

    /**
     * Constructor
     * @param myName Name of subscriber it represents
     * @param subscriberOriginalID original UUID of connected subscriber
     * @param myIP IP address of connected subscriber
     * @param myPort Port number of connected subscriber
     */
    public SubscriberForBroker(String myName, String myIP, int myPort,
    		UUID subscriberOriginalID, MessageReceiver myBroker) {
        super(myName, myIP, myPort);
        this.subID = subscriberOriginalID;
        this.broker = myBroker;
    }
    
    @Override
    public void run() {
    	if (isRunning || !broker.isRunning)
    		return;
    	isRunning = true;

    	while (isRunning){

    		Object objIn = null;
			try {
				objIn = inFromClient.readObject();
			} catch (Exception e) {
				terminateConnection();
				broker.sendInternalMessage(
						new SubscriberDisconnectMessage(myName, getId()));
				return;
			}

			if (!(objIn instanceof Message)){
				//TODO send some sort of NACK
				continue;
			} else {
				message = (Message)objIn;
			}
    		
			if (message instanceof SubscriberDisconnectMessage){
				terminateConnection();
				broker.sendInternalMessage(message);
				//TODO send some sort of ACK
				return;
			} else if (message instanceof SubscriberUnregisterMessage) {
				terminateConnection();
				broker.removeSubscriber((SubscriberUnregisterMessage)message);
				//TODO send some sort of ACK
				return;
    		} else if (message instanceof SubscribeMessage) {
				broker.subscribe(subID, (SubscribeMessage)message);
    			//TODO send some sort of ACK
    		}
    	}
    	terminateConnection(); //just in case
    }
    
    /**
	 * Used for terminating the connection to the subscriber
	 */
	public void terminateConnection() {
		
		if (!isRunning && socket==null)
			return;
		
		try {
			if (socket!=null)
				socket.close();
		} catch (IOException e) {
			//ignoring...
		}
		socket = null;
		this.isRunning = false;
	}
    
    /**
     * Used for (re)connecting a previously registered subscriber...
     * @param socket
     */
    public boolean setSocketAndStreams(Socket socket,
    		ObjectInputStream in, ObjectOutputStream out) {
    	if (isRunning){
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
    
    public UUID getSubID() {
		return subID;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (
        		(subID == null) ? 0 : subID.hashCode());
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
        if (!(obj instanceof SubscriberForBroker)) {
            return false;
        }
        SubscriberForBroker other = (SubscriberForBroker) obj;
        if (subID == null) {
            if (other.subID != null) {
                return false;
            }
        } else if (!subID.equals(other.subID)) {
            return false;
        }
        return true;
    }
}
