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
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.openiot.cupus.common.Triplet;
import static org.openiot.cupus.common.enums.Operator.CONTAINS_STRING;
import static org.openiot.cupus.common.enums.Operator.ENDS_WITH_STRING;
import static org.openiot.cupus.common.enums.Operator.EQUAL;
import static org.openiot.cupus.common.enums.Operator.GREATER_OR_EQUAL;
import static org.openiot.cupus.common.enums.Operator.GREATER_THAN;
import static org.openiot.cupus.common.enums.Operator.LESS_OR_EQUAL;
import static org.openiot.cupus.common.enums.Operator.LESS_THAN;
import static org.openiot.cupus.common.enums.Operator.STARTS_WITH_STRING;

/**
 * An implementation of a TopKWSubscription that is based on a
 * structured continuous query.
 * 
 * TODO this is primitive... (especially equals)
 * 
 * The subscription is a set of triples that are consisted of a key a value and
 * an operator. The relevance is calculated in the following manner:
 * for numeric values the operator is checked - if the operator is
 * > or >= then relevance is value-condition (meaning if value is 100, and
 * condition is x>=5 then relevance is 100-5 = 95 and is value is -100 then
 * relevance is -100-5 = -105); analogous for < and <= operators.
 * For "equals" the relevance is the negative distance.
 * For string operators and values only the satisfiability is checked. Meaning
 * that the conditions must be satisfied (if not relevance -oo), and that
 * them being satisfied gives 0 to relevance, meaning that it doesn't spoil it
 * but it can't improve it, it is merely a necessety (because it is
 * pretty much incomparable, and for textual stuff nonstructured subscriptions
 * should be used).
 * If the publication doens't have one or more keys that the subscription
 * mandates the relevance is automatically set to negative infinity.
 * 
 * @author Eugen
 *
 */
public class TripletTopKWSubscription extends TopKWSubscription implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Set<Triplet> data; 
	
	public TripletTopKWSubscription(int K, long W, long validity, long startTime) {
		super(K, W, validity, startTime);
		data = new HashSet<Triplet>();
	}
	
	/**
	 * Adds the triplet to the data (conditions) of this subscription.
	 * @return true if added, false otherwise (was already present)
	 */
	public boolean addTriplet(Triplet triplet){
		return data.add(triplet);
	}
	
	/**
	 * Removes the given triplet from the data (conditions) of this subscription
	 * @return true if removed, false otherwise (wasn't present)
	 */
	public boolean removeTriplet(Triplet triplet){
		return data.remove(triplet);
	}
	
	public void clearTriplets(){
		data.clear();
	}
	
	public boolean containsTriplet(Triplet triplet){
		return data.contains(triplet);
	}
	
	public boolean hasNoTriplets(){
		return data.isEmpty();
	}
	
	public Set<Triplet> getData() {
		return data;
	}
	
	@Override
	public double calculateRelevance(Publication p) {
		if (!(p instanceof HashtablePublication)){
			//System.err.println("Cannot compare a structured subsciption (TripletTopKW) with a non structured publication (not a HashtablePublication)!");
			return Double.NEGATIVE_INFINITY;
		}
		double ret_val = 0.0;
		HashMap<String, Object> publication = ((HashtablePublication)p).getProperties();
		// for every condition (triplet)...
		for (Triplet t : data){
			//check if exists
			Object pub_value = publication.get(t.getKey());
			if (pub_value==null)
				return Double.NEGATIVE_INFINITY;
			//if string...
			if (t.getValue().getClass()==String.class){
				if (pub_value.getClass()!=String.class){
					//System.err.println("Same key ("+t.getKey()+") different types: String != "+pub_value.getClass());
					return Double.NEGATIVE_INFINITY;
				}
				String thisVal = ((String)t.getValue()).toLowerCase(Locale.ENGLISH);
				String thatVal = ((String)pub_value).toLowerCase(Locale.ENGLISH);
				//check every possible string operator (has to satisfy)
				switch (t.getOperator()){
				case CONTAINS_STRING:
					if (!thatVal.contains(thisVal))
						return Double.NEGATIVE_INFINITY;
					break;
				case STARTS_WITH_STRING:
					if (!thatVal.startsWith(thisVal))
						return Double.NEGATIVE_INFINITY;
					break;
				case ENDS_WITH_STRING:
					if (!thatVal.endsWith(thisVal))
						return Double.NEGATIVE_INFINITY;
					break;
				case EQUAL:
					if (!thatVal.equals(thisVal))
						return Double.NEGATIVE_INFINITY;
					break;
				default:
					//System.err.println("Triplet with string value and numeric operator?!");
					return Double.NEGATIVE_INFINITY;
				}
			} else { //if not strings then they have to be numbers of some kind
				double thisVal,thatVal;
				try {
					thisVal = Double.parseDouble(t.getValue().toString());
					thatVal = Double.parseDouble(pub_value.toString());
				} catch (NumberFormatException e){
					//System.err.println("Triplet with non-string, non-numeric value?!");
					return Double.NEGATIVE_INFINITY;
				}
				//for every possible numerical operator
				switch (t.getOperator()){
				case GREATER_OR_EQUAL:
				case GREATER_THAN:
					ret_val += (thatVal-thisVal);
					break;
				case LESS_OR_EQUAL:
				case LESS_THAN:
					ret_val += (thisVal-thatVal);
					break;
				case EQUAL:
					ret_val -= Math.abs(thatVal-thisVal);
					break;
				//case LESS_OR_GREATER_OR_EQUAL:
				//	break;
				default:
					//System.err.println("Triplet with numerical value and string operator?!");
					return Double.NEGATIVE_INFINITY;
				}
			}
		}
		return ret_val;
	}
	
        @Override
        public boolean coversPublication(Publication pub) {
            if (pub instanceof HashtablePublication) {
                HashtablePublication hp = (HashtablePublication) pub;
                for (Triplet t : data) {
                    if (hp.getProperties().containsKey(t.getKey())) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        
	@Override
	public String toString() {
		
		StringBuilder retVal = new StringBuilder("K="+K+",W="+W+" ");
		
		Iterator<Triplet> iterator = data.iterator();
		retVal.append("(" + iterator.next().toString());
		while (iterator.hasNext()) {
			retVal.append("," + iterator.next().toString());
		}
		return retVal.append(")").toString();
	}

}
