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

import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.message.Message;

/**
 * This class is a subtype of class message and it is used for sending
 * subscriptions to broker.
 * 
 * @author Eugen & Aleksandar
 * 
 */
public class AnnounceMessage implements Message {

	private static final long serialVersionUID = 9102903090295068239L;

	private Announcement announcement;
	private boolean revokeAnnouncement;

	/**
	 * Constructor
	 * 
	 * @param announcement
	 *            Announcement to be sent
	 * @param unsubscribe
	 *            true == revoke announcement
	 */
	public AnnounceMessage(Announcement announcement, boolean revokeAnnouncement) {
		this.announcement = announcement;
		this.revokeAnnouncement = revokeAnnouncement;
	}

	public boolean isRevokeAnnouncement() {
		return revokeAnnouncement;
	}

	public Announcement getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(Announcement announcement) {
		this.announcement = announcement;
	}

	@Override
	public UUID getID() {
		return announcement.getId();
	}
}
