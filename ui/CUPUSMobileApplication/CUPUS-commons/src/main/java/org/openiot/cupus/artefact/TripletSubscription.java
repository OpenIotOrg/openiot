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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;

/**
 * This class is a conjuction-of-predicates implementation of a boolean
 * subscription. A predicate is represented by a Triplet object, an they are
 * grouped by the attribute over which they are defined in a map. Each map entry
 * is a list of predicated related to that attribute.
 * 
 * @author Eugen
 */
public class TripletSubscription extends Subscription implements Serializable,
		Comparable<TripletSubscription> {

	private static final long serialVersionUID = 1L;

	private HashMap<String, Set<Triplet>> predicateMap = new HashMap<String, Set<Triplet>>();

	/**
	 * This map is for internal use in matching only. It should be null outside
	 * of the broker/matcher to minimize the serialised size of the subscription
	 * for sending through networks. The keys are the identifiers(names) of
	 * string attributes that have predicates defined over them in this
	 * subscription, and the value is a triplet that holds the information about
	 * the lowest and highest index (BETWEEN) of the allowed values for the
	 * attribute that match the predicates over the attribute in this
	 * subscription. The information is used in the CdirBucket class to check
	 * for enclosement in an efficient way and it should be set by the
	 * Attributes class on initial check when the subscription enters the
	 * broker.
	 */
	public HashMap<String, Triplet> stringAttributeBorders = null;

	public TripletSubscription(long validity, long startTime) {
		super(validity, startTime);
	}

	/**
	 * Adds a predicate (constraint) to this subscription.
	 * 
	 * @param triplet
	 *            (attribute(key), value, operator)
	 */
	public void addPredicate(Triplet triplet) {
		Set<Triplet> set = predicateMap.get(triplet.getKey());
		if (set != null) {
			set.add(triplet);
		} else {
			set = new HashSet<Triplet>();
			set.add(triplet);
			predicateMap.put(triplet.getKey(), set);
		}
	}

	public Set<String> attributes() {
		return predicateMap.keySet();
	}

	public Set<Triplet> attributePredicates(String attribute) {
		return predicateMap.get(attribute);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof TripletSubscription) {
			TripletSubscription subscription = (TripletSubscription) other;
			if (super.equals(subscription))
				return true;
			if (this.validity == subscription.validity
					&& this.startTime == subscription.startTime) {
				return predicateMap.equals(subscription.predicateMap);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + (predicateMap != null ? predicateMap.hashCode() : 0);
		return hash;
	}

	/**
	 * Method for checking whether subscription covers other subscription.
	 * 
	 * @param sub
	 *            subscription to confront
	 **/
	@Override
	public boolean coversSubscription(Subscription sub) {
		if (sub instanceof TripletSubscription) {
			TripletSubscription tripSub = (TripletSubscription) sub;
			for (String attr : this.attributes()) {
				for (Triplet tempTriplet1 : this.attributePredicates(attr)) {
					boolean satisfaction = false;
					Set<Triplet> attrPreds = tripSub.attributePredicates(attr);
					if (attrPreds == null)
						return false;
					for (Triplet tempTriplet2 : attrPreds) { // only the
																// predicates
																// over attr are
																// needed...
						if (tempTriplet1.covers(tempTriplet2)) {
							satisfaction = true;
							break;
						}
					}
					if (!satisfaction) {
						return false;
					}
				}
			}
			return true;
		} else if (sub instanceof ActiveSubscription) {
			return this.coversSubscription(((ActiveSubscription) sub)
					.getSubscription());
		}
		return false;
	}

	/**
	 * Method for checking whether subscription covers publication.
	 * 
	 * @param pub
	 *            publication
	 **/
	@Override
	public boolean coversPublication(Publication pub) {

		if (pub instanceof HashtablePublication) {
			HashtablePublication hashPub = (HashtablePublication) pub;
			for (String attr : this.attributes()) { // for each attribute in the
													// subscription...
				Object value = hashPub.getProperties().get(attr);
				if (value == null) {
					return false;
				}
				Triplet tempPubTriplet = new Triplet(attr, value,
						Operator.EQUAL);
				for (Triplet tempSubTriplet : this.attributePredicates(attr)) { // ...for
																				// each
																				// triplet
																				// over
																				// the
																				// attribute
					if (!tempSubTriplet.covers(tempPubTriplet)) {
						return false;
					}
				}
			}
			return true;
		} else if (pub instanceof ActivePublication) {
			return this.coversPublication(((ActivePublication) pub)
					.getPublication());
		}
		return false;
	}

	@Override
	public int compareTo(TripletSubscription other) {
		if (this.equals(other)) {
			return 0;
		} else if (this.coversSubscription(other)) {
			return 1;
		} else if (other.coversSubscription(this)) {
			return -1;
		} else {
			return -2;
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("( ");
		for (String key : predicateMap.keySet()) {
			for (Triplet triplet : predicateMap.get(key)) {
				str.append(triplet.toString() + " , ");
			}
		}
		str.replace(str.length() - 2, str.length(), ")"); // replacing last ", "
															// with ")"
		return str.toString();
	}

}
