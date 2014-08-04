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

package org.openiot.cupus.message.external;

import java.util.UUID;

import org.openiot.cupus.message.Message;

/**
 * This class is a subtype of class message and it is used for sending
 * information during subscriber registration process.
 * 
 * @author Eugen
 * 
 */
public class SubscriberRegisterMessage implements Message {

	private static final long serialVersionUID = 8716233142831946666L;

	private String entityName;
	private UUID entityID;
	private String subscriberIP;
	private int subscriberListeningPort;

	/**
	 * Constructor
	 * 
	 * @param entityName
	 *            Name of entity sending message
	 * @param entityID
	 *            ID of entity sending message
	 * @param subscriberListeningPort
	 *            TCP port on which the subscriber is listening for the incoming
	 *            connection from the broker (deliveryService) that will be used
	 *            to deliver the publication that are matched.
	 */
	public SubscriberRegisterMessage(String entityName, UUID entityID,
			String subscriberIP, int subscriberListeningPort) {
		this.entityName = entityName;
		this.entityID = entityID;
		this.subscriberIP = subscriberIP;
		this.subscriberListeningPort = subscriberListeningPort;
	}

	/**
	 * @return subscriberIP - IP adress of the subscriber on which it is waiting
	 *         for a connection request from the DeliveryService of the
	 *         cloud-broker
	 */
	public String getIP() {
		return subscriberIP;
	}

	/**
	 * @return subscriberListeningPort - a port on which the subscriber is
	 *         listening for an incoming connection from the broker
	 *         (DeliveryService)
	 */
	public int getPort() {
		return subscriberListeningPort;
	}

	public String getEntityName() {
		return entityName;
	}

	public UUID getEntityID() {
		return entityID;
	}

	@Override
	public UUID getID() {
		return entityID;
	}

	@Override
	public String toString() {
		return entityName + "(" + subscriberIP + ":" + subscriberListeningPort
				+ ")";
	}
}
