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
public class TripletAnnouncement extends Announcement implements Serializable {

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

    public TripletAnnouncement(long validity, long startTime) {
        super(validity, startTime);
    }

    public void addNumericalPredicate(String name) {
        addPredicate(new Triplet(name, Double.NEGATIVE_INFINITY, Operator.GREATER_OR_EQUAL));
    }

    public void addNumericalPdredicate(String name, double minValue,
            double maxValue) {
        addPredicate(new Triplet(name, new Double[]{minValue, maxValue},
                Operator.BETWEEN));
    }

    public void addNumericalPdredicate(String name, double value,
            Operator operator) {
        addPredicate(new Triplet(name, value, operator));
    }

    public void addTextualPdredicate(String name) {
        addPredicate(new Triplet(name, "", Operator.CONTAINS_STRING));
    }

    public void addTextualPdredicate(String name, String value,
            Operator operator) {
        addPredicate(new Triplet(name, value, operator));
    }

    /**
     * Adds a predicate (constraint) to this subscription.
     *
     * @param triplet (attribute(key), value, operator)
     */
    private void addPredicate(Triplet triplet) {
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
        if (this == other) {
            return true;
        }
        if (other instanceof TripletAnnouncement) {
            TripletAnnouncement announcement = (TripletAnnouncement) other;
            if (super.equals(announcement)) {
                return true;
            }
            if (this.validity == announcement.validity
                    && this.startTime == announcement.startTime) {
                return predicateMap.equals(announcement.predicateMap);
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
     * @param sub subscription to confront
	 *
     */
    @Override
    public boolean coversSubscription(Subscription sub) {
        if (sub instanceof TripletSubscription) {
            TripletSubscription tripSub = (TripletSubscription) sub;
            for (String subAttribute : tripSub.attributes()) {
                if (this.predicateMap.get(subAttribute) == null) {
                    return false;
                } else {
                    Set<Triplet> subTriplets = tripSub.attributePredicates(subAttribute);
                    Set<Triplet> annTriplets = this.attributePredicates(subAttribute);
                    for (Triplet sT : subTriplets) {
                        boolean satisfaction = false;
                        for (Triplet aT : annTriplets) {
                            if (aT.partiallyCovers(sT)) {
                                satisfaction = true;
                                break;
                            }
                        }
                        if (!satisfaction) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else if (sub instanceof ActiveSubscription) {
            return this.coversSubscription(((ActiveSubscription) sub)
                    .getSubscription());
        } else if (sub instanceof MemorySubscription) {
            return this.coversSubscription(((MemorySubscription) sub).getSubscription());
        } else if (sub instanceof TripletTopKWSubscription) {
            TripletTopKWSubscription tripTopKW = (TripletTopKWSubscription) sub;

            for (Triplet subscriptionTriplet : tripTopKW.getData()) {
                if (!this.attributes().contains(subscriptionTriplet.getKey())) {
                    return false;
                }
            }
            return true;

        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("( ");
        for (String key : predicateMap.keySet()) {
            for (Triplet triplet : predicateMap.get(key)) {
                str.append(triplet.toString() + " , ");
            }
        }
        str.replace(str.length() - 2, str.length(), ")"); // replacing last ", " with ")"
        return str.toString();
    }

}
