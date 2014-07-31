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

package org.openiot.cupus.entity.publisher;

import org.openiot.cupus.artefact.Publication;

/**
 * Application interface for publisher
 * 
 * @author Aleksandar, Eugen
 */
public interface PublisherInterface {

	public void connect();

	void disconnectFromBroker();

	void publish(Publication publication);

	void unpublish(Publication publication);

	public void reconnect();

	public void reconnect(String brokerIP, int brokerPort);
}
