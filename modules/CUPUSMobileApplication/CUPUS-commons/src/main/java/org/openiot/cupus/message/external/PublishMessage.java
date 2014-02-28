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
import org.openiot.cupus.message.Message;

/**
 * This class is a subtype of class message and it is used for sending
 * publication to broker.
 * 
 * @author Eugen
 * 
 */
public class PublishMessage implements Message {

	private static final long serialVersionUID = 7182223620427767811L;

	private Publication publication;
	private boolean unpublish;

	/**
	 * Constructor
	 * 
	 * @param publication
	 *            Publication
	 * @param unpublish
	 *            true == unpublish
	 */
	public PublishMessage(Publication publication, boolean unpublish) {
		this.publication = publication;
		this.unpublish = unpublish;
	}

	public boolean isUnpublish() {
		return unpublish;
	}

	public Publication getPublication() {
		return publication;
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	@Override
	public UUID getID() {
		return publication.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PublishMessage))
			return false;
		PublishMessage other = (PublishMessage) obj;

		return this.publication.equals(other.publication)
				&& this.unpublish == other.unpublish;
	}

	@Override
	public int hashCode() {
		int hashCode = publication.hashCode();
		if (unpublish)
			return ~hashCode;
		else
			return hashCode;
	}
}
