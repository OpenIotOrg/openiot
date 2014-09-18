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

import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.message.Message;

/**
 * This class is a subtype of class message and it is used for sending
 * subscriptions to broker.
 * 
 * @author Eugen
 * 
 */
public class SubscribeMessage implements Message {

	private static final long serialVersionUID = 9102903090295068239L;

	private Subscription subscription;
	private boolean unsubscribe;

	/**
	 * Constructor
	 * 
	 * @param subscription
	 *            Subscription to be subscribed
	 * @param unsubscribe
	 *            true == unsubscribe
	 */
	public SubscribeMessage(Subscription subscription, boolean unsubscribe) {
		this.subscription = subscription;
		this.unsubscribe = unsubscribe;
	}

	public boolean isUnsubscribe() {
		return unsubscribe;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	@Override
	public UUID getID() {
		return subscription.getId();
	}
}
