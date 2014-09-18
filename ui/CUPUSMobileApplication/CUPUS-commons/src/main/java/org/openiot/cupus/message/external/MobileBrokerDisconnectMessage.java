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
 * This class is a subtype of class message and it is used for starting a
 * subscriber disconnect process. Disconnecting a subscriber is different from
 * unregistering a subscriber. Disconnecting means only breaking the link, not
 * removing the subscriptions from the broker. The notifications will pile up in
 * a queue and the subscriber can (re)connect at some later time and will be
 * delivered the publications that fit the subscriptions on the broker that
 * accumulated over time time the subscriber was disconnected.
 * 
 * @author Eugen & Aleksandar
 * 
 */
public class MobileBrokerDisconnectMessage implements Message {

	private static final long serialVersionUID = 2993337630853519724L;

	private String entityName;
	private UUID entityID;

	public MobileBrokerDisconnectMessage(String entityName, UUID entityID) {
		this.entityName = entityName;
		this.entityID = entityID;
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

}