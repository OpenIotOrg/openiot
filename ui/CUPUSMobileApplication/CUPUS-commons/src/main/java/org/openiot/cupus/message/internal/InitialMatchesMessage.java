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
import java.util.UUID;

import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.message.InternalMessage;

public class InitialMatchesMessage implements InternalMessage {

	private static final long serialVersionUID = 2267723676888859927L;

	private UUID subscriberID;
	private List<Publication> initialMatches;

	public InitialMatchesMessage(UUID subID, List<Publication> pubs) {
		this.subscriberID = subID;
		this.initialMatches = pubs;
	}

	public UUID getSubscriberID() {
		return subscriberID;
	}

	public List<Publication> getInitialMatches() {
		return initialMatches;
	}

	@Override
	public UUID getID() {
		return subscriberID;
	}

}
