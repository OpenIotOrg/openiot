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

package org.openiot.cupus.message.internal;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.message.InternalMessage;
import org.openiot.cupus.message.external.SubscribeMessage;

public class InitialAnnouncementMatchesMessage implements InternalMessage {

	private static final long serialVersionUID = 2267723676888859927L;

	private SubscribeMessage subscription;
	private Set<UUID> mobileBrokerIDS;

	public InitialAnnouncementMatchesMessage(SubscribeMessage subscription,
			Set<UUID> mbIDs) {
		this.subscription = subscription;
		this.mobileBrokerIDS = mbIDs;
	}

	public SubscribeMessage getSubscriptionMessage() {
		return subscription;
	}

	public Set<UUID> getMobileBrokerIDs() {
		return mobileBrokerIDS;
	}

	@Override
	public UUID getID() {
		return subscription.getID();
	}

}
