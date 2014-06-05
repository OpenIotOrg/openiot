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

package org.openiot.cupus.artefact;

import java.io.Serializable;
import java.util.UUID;

/**
 * Functions as a container for information about active subscriptions and their
 * publishers. It is used by Broker.
 * 
 * @author Aleksandar, Eugen
 * 
 */
public class ActiveSubscription extends Subscription implements Serializable{

	private static final long serialVersionUID = 1L;

	private UUID subscriberID;
	private Subscription subscription;

	public ActiveSubscription(UUID subscriberID, Subscription subscription) {
		super(subscription.validity, subscription.startTime);
		this.subscriberID = subscriberID;
		this.subscription = subscription;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public UUID getSubscriberID() {
		return subscriberID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ActiveSubscription) {
			ActiveSubscription other = (ActiveSubscription) o;
			return this.subscriberID.equals(other.subscriberID)
					&& this.subscription.equals(other.subscription);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 11
				* hash
				+ (this.subscriberID != null ? this.subscriberID.hashCode() : 0);
		hash = 11
				* hash
				+ (this.subscription != null ? this.subscription.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return subscription.toString() + " from: " + subscriberID;
	}

	@Override
	public boolean coversSubscription(Subscription data) {
		if (data instanceof ActiveSubscription) {
			return this.coversActiveSubscription((ActiveSubscription) data);
		} else {
			return this.subscription.coversSubscription(data);
		}
	}

	public boolean coversActiveSubscription(ActiveSubscription data) {
		if (!this.subscriberID.equals(data.subscriberID))
			return false;
		else
			return this.subscription.coversSubscription(data.getSubscription());
	}

	@Override
	public boolean coversPublication(Publication publication) {
		return this.subscription.coversPublication(publication);
	}
}
