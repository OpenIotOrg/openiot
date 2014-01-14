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

import java.util.UUID;

/**
 * Functions as a container for information about active subscriptions and their
 * publishers. It is used by Broker.
 * 
 * @author Eugen, Aleksandar
 * 
 */
public class ActiveAnnouncement extends Announcement {

	private static final long serialVersionUID = 1L;

	private UUID mobileBrokerID;
	private Announcement announcement;

	public ActiveAnnouncement(UUID subscriberID, Announcement announcement) {
		super(announcement.validity, announcement.startTime);
		this.mobileBrokerID = subscriberID;
		this.announcement = announcement;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public UUID getMobileBrokerID() {
		return mobileBrokerID;
	}

	/*
	 * @Override public boolean equals(Object o) { if (this==o) return true; if
	 * (o instanceof ActiveAnnouncement) { ActiveAnnouncement other =
	 * (ActiveAnnouncement)o; return
	 * this.mobileBrokerID.equals(other.mobileBrokerID) &&
	 * this.announcement.equals(other.announcement); } else { return false; } }
	 */
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 11
				* hash
				+ (this.mobileBrokerID != null ? this.mobileBrokerID.hashCode()
						: 0);
		hash = 11
				* hash
				+ (this.announcement != null ? this.announcement.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return announcement.toString() + " from: " + mobileBrokerID;
	}

	@Override
	public boolean coversSubscription(Subscription data) {
		return this.announcement.coversSubscription(data);

	}
}
