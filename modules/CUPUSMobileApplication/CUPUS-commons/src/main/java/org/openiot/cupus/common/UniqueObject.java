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

package org.openiot.cupus.common;

import java.util.UUID;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UniqueObject - every object has its own unique ID which is generated here.
 * 
 * 
 */
public abstract class UniqueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	public UniqueObject() {
		this.id = UUID.randomUUID();
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UniqueObject)) {
			return false;
		}
		final UniqueObject other = (UniqueObject) obj;
		if (this.id != other.id
				&& (this.id == null || !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	/**
	 * Returns the current local IP address or an empty string in error case /
	 * when no network connection is up.
	 * <p>
	 * The current machine could have more than one local IP address so might
	 * prefer to use getAllLocalIPs() or getAllLocalIPs(java.lang.String).
	 * <p>
	 * If you want just one IP, this is the right method and it tries to find
	 * out the most accurate (primary) IP address. It prefers addresses that
	 * have a meaningful dns name set for example.
	 * 
	 * @return Returns the current local IP address or an empty string in error
	 *         case.
	 * @since 0.1.0
	 */
	public static String getLocalIP() {
		String ipOnly = "";
		try {
			Enumeration<NetworkInterface> nifs = NetworkInterface
					.getNetworkInterfaces();
			if (nifs == null) {
				return "";
			}
			while (nifs.hasMoreElements()) {
				NetworkInterface nif = nifs.nextElement();
				// We ignore subinterfaces - as not yet needed.

				if (!nif.isLoopback() && nif.isUp() && !nif.isVirtual()) {
					Enumeration<InetAddress> adrs = nif.getInetAddresses();
					while (adrs.hasMoreElements()) {
						InetAddress adr = adrs.nextElement();
						if (adr != null
								&& !adr.isLoopbackAddress()
								&& (nif.isPointToPoint() || !adr
										.isLinkLocalAddress())) {
							String adrIP = adr.getHostAddress();
							String adrName;
							if (nif.isPointToPoint()) // Performance issues
														// getting hostname for
														// mobile internet
														// sticks
							{
								adrName = adrIP;
							} else {
								adrName = adr.getCanonicalHostName();
							}

							if (!adrName.equals(adrIP)) {
								return adrIP;
							} else {
								ipOnly = adrIP;
							}
						}
					}
				}
			}
			if (ipOnly.length() == 0) {
				Logger.getLogger(UniqueObject.class.getName()).log(
						Level.WARNING, "No IP address available");
			}
			return ipOnly;
		} catch (SocketException ex) {
			Logger.getLogger(UniqueObject.class.getName()).log(Level.WARNING,
					"No IP address available", ex);
			return "";
		}
	}
}
