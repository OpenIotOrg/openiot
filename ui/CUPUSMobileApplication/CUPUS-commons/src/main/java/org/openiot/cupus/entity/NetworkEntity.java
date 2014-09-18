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

package org.openiot.cupus.entity;

import org.openiot.cupus.common.UniqueObject;

/**
 * This abstract class is a base for everything that is an entity that is
 * visible and accessible via the network, meaning it can be addressed, it can
 * send and/or receive (TCP) messages etc.
 * 
 * @author Eugen
 * 
 */
public abstract class NetworkEntity extends UniqueObject {

	private static final long serialVersionUID = 1L;

	protected String myIP;
	protected int myPort;
	protected String myName;

	public NetworkEntity(String myName, String ipAddress, int myPort) {
		super();
		this.myName = myName;
		this.myIP = ipAddress;
		this.myPort = myPort;
	}

	public String getName() {
		return myName;
	}

	public String getIP() {
		return myIP;
	}

	public int getPort() {
		return myPort;
	}

	@Override
	public String toString() {
		return myName + " (" + myIP + ":" + myPort + ")";
	}
}
