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

import java.io.Serializable;

import org.openiot.cupus.common.UniqueObject;

/**
 * Abstract Subscription class
 * 
 */
public abstract class Subscription extends UniqueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	protected long validity;
	protected long startTime;

	public Subscription(long validity, long startTime) {
		super();
		this.validity = validity;
		this.startTime = startTime;
	}

	public long getValidity() {
		return validity;
	}

	public long getStartTime() {
		return startTime;
	}

	/**
	 * Checks if validity is bigger than current time in millis or if it is -1.
	 * If either of those is true then true is returned, else the publication is
	 * invalid.
	 */
	public boolean isValid() {
		return (validity >= System.currentTimeMillis())
				|| (validity == -1);
	}

	/**
	 * Method returns TRUE if this subscription event matches publication pub.
	 * 
	 * @param pub
	 *            Publication to compare to.
	 */
	public abstract boolean coversPublication(Publication pub);

	/**
	 * Returns whether this subscription covers specified subscripion sub.<br>
	 * Subscription A covers subscription B if A.equals(B) or (Type of A covers
	 * Type of B and properties of A cover properties of B). Properties of A
	 * cover croperties of B only if B contains all properties of A.
	 * 
	 * @param sub
	 *            Subccription to compare to.
	 * 
	 * @return True if this sub covers specified sub, else false.
	 */
	public abstract boolean coversSubscription(Subscription sub);
}