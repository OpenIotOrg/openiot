/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.cupus.artefact;

import java.io.Serializable;

/**
 * An abstract class representing the sekelton of topKW subscription.
 * It has the parameters K and W and it's coverPublication and coversSubscription
 * methods are not implemented, instead the calculateRelevance should be implemented
 * by the classes that extends this abstract class.
 * 
 * @author Eugen
 *
 */
public abstract class TopKWSubscription extends Subscription implements Serializable{

	private static final long serialVersionUID = 1L;

	/**Limits the number of matching publications restricting it to top-k objects relevant to user
	interests over a sliding time window .*/
	protected int K;
	/**Sliding time window size.*/
	protected long W;

	public TopKWSubscription(int K, long W, long validity, long startTime) {
		super(validity, startTime);
		this.K = K;
		this.W = W;
	}

	public int getK() {
		return K;
	}

	public long getW() {
		return W;
	}

	/**
         * coversPublication method of TopKWSubscription subscriptions should not be used, 
         * except in the mobile broker, to check coverage of a new publication with received subscriptions
         */
        @Override
	public abstract boolean coversPublication(Publication pub);
		


        @Override
	public boolean coversSubscription(Subscription sub) {
		System.err.println("coversSubscription method of TopKWSubscription subscriptions"
				+" should not be used!");
		return false;
	}

	/**
	 * Calculates the relevance of the given pulication to this subscription
	 * in order for it to be ranked and decided if it will be delivered to the subscriber
	 * or not.
	 */
	public abstract double calculateRelevance(Publication p);

}
