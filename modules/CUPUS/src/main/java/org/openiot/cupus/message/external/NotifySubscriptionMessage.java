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

import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.message.Message;

/**
 * This class is a subtype of class message and it is used for sending new
 * subscription to mobile broker.
 * 
 * @author Aleksandar
 */
public class NotifySubscriptionMessage implements Message {

	private static final long serialVersionUID = -1353926341869057708L;

	private Subscription subscription;
	private boolean revokeAnnouncement;

	/**
	 * Constructor
	 * 
	 * @param publication
	 *            Publication
	 * @param revoke
	 *            true == revoke
	 */
	public NotifySubscriptionMessage(Subscription subscription, boolean revoke) {
		this.subscription = subscription;
		this.revokeAnnouncement = revoke;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public boolean isRevoke() {
		return revokeAnnouncement;
	}

	public void setRevoke(boolean revoke) {
		this.revokeAnnouncement = revoke;
	}

	@Override
	public UUID getID() {
		return subscription.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NotifySubscriptionMessage))
			return false;
		NotifySubscriptionMessage other = (NotifySubscriptionMessage) obj;

		return this.subscription.equals(other.subscription)
				&& this.revokeAnnouncement == other.revokeAnnouncement;
	}

	@Override
	public int hashCode() {
		int hashCode = subscription.hashCode();
		if (revokeAnnouncement)
			return ~hashCode;
		else
			return hashCode;
	}
}
