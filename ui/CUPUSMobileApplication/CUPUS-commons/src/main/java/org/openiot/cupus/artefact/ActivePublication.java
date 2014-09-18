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
 * Functions as a container for information about active publications and their
 * publishers. It is used by Broker.
 * 
 * @author Aleksandar, Eugen
 * 
 */
public class ActivePublication extends Publication {

	private static final long serialVersionUID = 1L;

	private UUID publisherID;
	private Publication publication;
	private long validityPeriod;

	public ActivePublication(UUID publisherID, Publication publication) {
		super(publication.validity, publication.startTime);
		this.publisherID = publisherID;
		this.publication = publication;
	}

	public Publication getPublication() {
		return publication;
	}

	public UUID getPublisherID() {
		return publisherID;
	}

	public long getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(long validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ActivePublication) {
			ActivePublication other = (ActivePublication) o;
			return (this.publisherID.equals(other.publisherID))
					&& (this.publication.equals(other.publication));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash
				+ (this.publisherID != null ? this.publisherID.hashCode() : 0);
		hash = 73 * hash
				+ (this.publication != null ? this.publication.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return publication.toString() + " from: " + publisherID;
	}
}
