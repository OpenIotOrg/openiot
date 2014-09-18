package org.openiot.cupus.artefact;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;

/**
 * An implementation of a TopKWSubscription that is based on a structured
 * continuous query and notifies about maximum value of the requested parameter.
 *
 * @author Aleksandar
 *
 */
public class MaxTopKWSubscription extends TopKWSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Triplet> data;

    public MaxTopKWSubscription(int K, long W, long validity, long startTime, String parameter, String area) {
        super(K, W, validity, startTime);
        data = new HashSet<Triplet>();
        data.add(new Triplet(parameter, 0, Operator.GREATER_THAN));
        data.add(new Triplet("Area", area, Operator.EQUAL));
    }

    /**
     * Removes the given triplet from the data (conditions) of this subscription
     *
     * @return true if removed, false otherwise (wasn't present)
     */
    public boolean removeTriplet(Triplet triplet) {
        return data.remove(triplet);
    }

    public void clearTriplets() {
        data.clear();
    }

    public boolean containsTriplet(Triplet triplet) {
        return data.contains(triplet);
    }

    public boolean hasNoTriplets() {
        return data.isEmpty();
    }

    public Set<Triplet> getData() {
        return data;
    }

    @Override
    public double calculateRelevance(Publication p) {
        if (!(p instanceof HashtablePublication)) {
            System.err.println("Cannot compare a structured subsciption (TripletTopKW) with a non structured publication (not a HashtablePublication)!");
            return Double.NEGATIVE_INFINITY;
        }
        double ret_val = 0.0;
        HashMap<String, Object> publication = ((HashtablePublication) p).getProperties();
        // for every condition (triplet)...
        for (Triplet t : data) {
            //check if exists
            Object pub_value = publication.get(t.getKey());
            if (pub_value == null) {
                return Double.NEGATIVE_INFINITY;
            }
            //if string...
            if (t.getValue().getClass() == String.class) {
                if (pub_value.getClass() != String.class) {
                    System.err.println("Same key (" + t.getKey() + ") different types: String != " + pub_value.getClass());
                    return Double.NEGATIVE_INFINITY;
                }
                String thisVal = ((String) t.getValue()).toLowerCase(Locale.ENGLISH);
                String thatVal = ((String) pub_value).toLowerCase(Locale.ENGLISH);
                //check every possible string operator (has to satisfy)
                switch (t.getOperator()) {
                    case EQUAL:
                        if (!thatVal.equals(thisVal)) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        break;
                    default:
                        System.err.println("Triplet with string value and numeric operator?!");
                        return Double.NEGATIVE_INFINITY;
                }
            } else { //if not strings then they have to be numbers of some kind
                double thisVal, thatVal;
                try {
                    thisVal = Double.parseDouble(t.getValue().toString());
                    thatVal = Double.parseDouble(pub_value.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Triplet with non-string, non-numeric value?!");
                    return Double.NEGATIVE_INFINITY;
                }
                //for every possible numerical operator
                switch (t.getOperator()) {
                    case GREATER_THAN:
                        ret_val += (thatVal - thisVal);
                        break;
                    default:
                        System.err.println("Triplet with numerical value and string operator?!");
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

        StringBuilder retVal = new StringBuilder("K=" + K + ",W=" + W + " ");

        Iterator<Triplet> iterator = data.iterator();
        retVal.append("(" + iterator.next().toString());
        while (iterator.hasNext()) {
            retVal.append("," + iterator.next().toString());
        }
        return retVal.append(")").toString();
    }

}
