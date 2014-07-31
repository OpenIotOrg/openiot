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
package org.openiot.cupus.common;

import java.io.Serializable;
import java.util.Locale;

import org.openiot.cupus.common.enums.Operator;

/**
 * This class represents a single piece of information that publications and
 * subscriptions are made of. Publications will always have the operator "equal"
 * because they represent a point in a space, for example a=5 or s="lol", while
 * subscriptions can have any operator because they want to match any
 * publication that satisfies their condition, for example a>=4 or
 * s.contains("lol"). "a" or "s" is the key, "5" or "lol" is the value and "=",
 * ">=" or "contains" is the operator. All possible operators are enumerated in
 * the Operator enumeration.
 *
 * @author Eugen Rožić
 */
public class Triplet implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    /**
     * Can only possible be String, Double or Double[]
     */
    private Object value;
    private Operator operator;

    /**
     * Should not be used, created only for WS methods
     */
    public Triplet() {
    }

    public Triplet(String key, Object value, Operator operator) {
        this.key = key;
        this.operator = operator;
        // check if value matches operator!
        Double numValue = null;
        switch (operator) {
            // string operators...
            case CONTAINS_STRING:
            case STARTS_WITH_STRING:
            case ENDS_WITH_STRING:
                if (!(value instanceof String)) {
                    throw new RuntimeException("Non string with string operator!");
                }
                this.value = value;
                break;
            // numeric operators (one value)
            case GREATER_THAN:
            case GREATER_OR_EQUAL:
            case LESS_THAN:
            case LESS_OR_EQUAL:
                numValue = Double.parseDouble(value.toString()); // will throw
                // exception if
                // not
                // numeric...
                this.value = numValue;
                break;
            // numeric (two values)
            case BETWEEN:
                if (!(value instanceof Object[])) {
                    if (value.getClass().isArray()) {
                        throw new RuntimeException(
                                "Only object arrays can be used (Double[] for example), not primitive arrays (like double[])!");
                    } else {
                        throw new RuntimeException(
                                "Non-array with BETWEEN operator!");
                    }
                }
                Object[] arrayValue = (Object[]) value;
                if (arrayValue.length != 2) {
                    throw new RuntimeException(
                            "Array of size!=2 with BETWEEN operator!");
                }
                Double[] numArray = new Double[2];
                numArray[0] = Double.parseDouble(arrayValue[0].toString()); // will
                // throw
                // exception
                // if
                // not
                // numeric...
                numArray[1] = Double.parseDouble(arrayValue[1].toString()); // will
                // throw
                // exception
                // if
                // not
                // numeric...
                if (numArray[0] >= numArray[1]) {
                    throw new RuntimeException(
                            "First number of between has to be LESS THAN (<) the second number!");
                }
                this.value = numArray;
                break;
            // only one left...
            case EQUAL:
                if (value instanceof String) {
                    this.value = value;
                } else {
                    numValue = Double.parseDouble(value.toString()); // will throw
                    // exception
                    // if not
                    // numeric...
                    this.value = numValue;
                }
                break;
            default:
                throw new RuntimeException(
                        "Operator has to be one of the legal enum constants!");
        }
    }

    public String getKey() {
        return this.key;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }

    /**
     * Checks if this Triplet covers "that" Triplet.
     */
    public boolean covers(Triplet that) {

        // checking for extremes...
        if (!this.key.equals(that.key)) {
            return false;
        } else if (this.equals(that)) {
            return true;
        }

        // if this is string both have to be strings...
        if (this.value instanceof String) {
            if (!(that.value instanceof String)) {
                return false;
            }
            String thisVal = ((String) this.value).toLowerCase(Locale.ENGLISH);
            String thatVal = ((String) that.value).toLowerCase(Locale.ENGLISH);
            // for every possible this string operator check that.operator
            switch (this.operator) {
                case EQUAL:
                    switch (that.operator) {
                        case EQUAL:
                            return thisVal.equals(thatVal);
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                        case ENDS_WITH_STRING:
                            return false;
                    }
                case CONTAINS_STRING:
                    switch (that.operator) {
                        case EQUAL:
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                        case ENDS_WITH_STRING:
                            return thatVal.contains(thisVal);
                    }
                case STARTS_WITH_STRING:
                    switch (that.operator) {
                        case EQUAL:
                        case STARTS_WITH_STRING:
                            return thatVal.startsWith(thisVal);
                        case CONTAINS_STRING:
                        case ENDS_WITH_STRING:
                            return false;
                    }
                case ENDS_WITH_STRING:
                    switch (that.operator) {
                        case EQUAL:
                        case ENDS_WITH_STRING:
                            return thatVal.endsWith(thisVal);
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                            return false;
                    }
                default:
                    System.err
                            .println("Triplet.class: this shouldn't happen! (string)");
                    return false;
            }
        } else { // if not strings then they have to be numbers of some kind
            if (!(that.value instanceof Number)) {
                return false;
            }
            double thisVal, thatVal;
            Double[] thisArrayVal, thatArrayVal;
            // for every possible this numerical operator check that.operator
            switch (this.operator) {
                case EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal == thatVal;
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                        case BETWEEN:
                            return false;
                    }
                case GREATER_THAN:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                        case GREATER_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal < thatVal;
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return thisVal <= thatVal;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal < thatArrayVal[0];
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            return false;
                    }
                case GREATER_OR_EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return thisVal <= thatVal;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal <= thatArrayVal[0];
                        case LESS_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return (thisVal == Double.NEGATIVE_INFINITY)
                                    && (thatVal == Double.POSITIVE_INFINITY);
                        case LESS_THAN:
                            return false;
                    }
                case LESS_THAN:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                        case LESS_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal > thatVal;
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisVal >= thatVal;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal > thatArrayVal[1];
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            return false;
                    }
                case LESS_OR_EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisVal >= thatVal;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal >= thatArrayVal[1];
                        case GREATER_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return (thatVal == Double.NEGATIVE_INFINITY)
                                    && (thisVal == Double.POSITIVE_INFINITY);
                        case GREATER_THAN:
                            return false;
                    }
                case BETWEEN:
                    thisArrayVal = (Double[]) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return (thisArrayVal[0] <= thatVal)
                                    && (thisArrayVal[1] >= thatVal);
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return (thisArrayVal[0] <= thatArrayVal[0])
                                    && (thisArrayVal[1] >= thatArrayVal[1]);
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return (thisArrayVal[1] >= thatVal)
                                    && (thisArrayVal[0] == Double.NEGATIVE_INFINITY);
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return (thisArrayVal[0] <= thatVal)
                                    && (thisArrayVal[1] == Double.POSITIVE_INFINITY);
                    }
                default:
                    System.err
                            .println("Triplet.class: this shouldn't happen! (numeric)");
                    return false;
            }
        }
    }

    /**
     * Checks if this Triplet partially covers "that" Triplet, i.e. an
     * intersection of this triplet and that triplet is not an empty set.
     */
    public boolean partiallyCovers(Triplet that) {

        // checking for extremes...
        if (!this.key.equals(that.key)) {
            return false;
        } else if (this.equals(that)) {
            return true;
        }

        // if this is string both have to be strings...
        if (this.value instanceof String) {
            if (!(that.value instanceof String)) {
                return false;
            }
            String thisVal = ((String) this.value).toLowerCase(Locale.ENGLISH);
            String thatVal = ((String) that.value).toLowerCase(Locale.ENGLISH);
            // for every possible this string operator check that.operator
            switch (this.operator) {
                case EQUAL:
                    switch (that.operator) {
                        case EQUAL:
                            return thisVal.equals(thatVal);
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                        case ENDS_WITH_STRING:
                            return false;
                    }
                case CONTAINS_STRING:
                    switch (that.operator) {
                        case EQUAL:
                            return thatVal.contains(thisVal);
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                        case ENDS_WITH_STRING:
                            return true;
                    }
                case STARTS_WITH_STRING:
                    switch (that.operator) {
                        case EQUAL:
                        case STARTS_WITH_STRING:
                            return thatVal.startsWith(thisVal);
                        case CONTAINS_STRING:
                        case ENDS_WITH_STRING:
                            return true;
                    }
                case ENDS_WITH_STRING:
                    switch (that.operator) {
                        case EQUAL:
                        case ENDS_WITH_STRING:
                            return thatVal.endsWith(thisVal);
                        case CONTAINS_STRING:
                        case STARTS_WITH_STRING:
                            return true;
                    }
                default:
                    System.err
                            .println("Triplet.class: this shouldn't happen! (string)");
                    return false;
            }
        } else { // if not strings then they have to be numbers of some kind
            if (!(that.value instanceof Number)) {
                return false;
            }
            double thisVal, thatVal;
            Double[] thisArrayVal, thatArrayVal;
            // for every possible this numerical operator check that.operator
            switch (this.operator) {
                case EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                        case GREATER_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal >= thatVal;
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return thisVal > thatVal;
                        case LESS_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal <= thatVal;
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisVal < thatVal;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return (thisVal >= thatArrayVal[0] && thisVal <= thatArrayVal[1]);
                        default:
                            return false;
                    }
                case GREATER_THAN:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal < thatVal;
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            return true; //both are not limited in the positive direction (i.e. + infinity)
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal < thatArrayVal[1];
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisVal < thatVal;
                        default:
                            return false;
                    }
                case GREATER_OR_EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal <= thatVal;
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            return true; //both are not limited in the positive direction (i.e. + infinity)
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal <= thatArrayVal[1];
                        case LESS_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return (thisVal == thatVal || thisVal < thatVal);
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisVal < thatVal;
                        default:
                            return false;
                    }
                case LESS_THAN:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal > thatVal;
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            return true;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal > thatArrayVal[0];
                        case GREATER_OR_EQUAL:
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return thisVal > thatVal;
                        default:
                            return false;
                    }
                case LESS_OR_EQUAL:
                    thisVal = (Double) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal >= thatVal;
                        case LESS_OR_EQUAL:
                        case LESS_THAN:
                            return true;
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return thisVal >= thatArrayVal[0];
                        case GREATER_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisVal > thatVal;
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return (thisVal == thatVal || thisVal > thatVal);
                        default:
                            return false;
                    }
                case BETWEEN:
                    thisArrayVal = (Double[]) this.value;
                    switch (that.operator) {
                        case EQUAL:
                            thatVal = (Double) that.value;
                            return (thisArrayVal[0] <= thatVal)
                                    && (thisArrayVal[1] >= thatVal);
                        case BETWEEN:
                            thatArrayVal = (Double[]) that.value;
                            return (thisArrayVal[0] <= thatArrayVal[0])
                                    || (thisArrayVal[1] >= thatArrayVal[1]);
                        case LESS_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisArrayVal[0] <= thatVal;
                        case LESS_THAN:
                            thatVal = (Double) that.value;
                            return thisArrayVal[0] < thatVal;
                        case GREATER_OR_EQUAL:
                            thatVal = (Double) that.value;
                            return thisArrayVal[1] >= thatVal;
                        case GREATER_THAN:
                            thatVal = (Double) that.value;
                            return thisArrayVal[1] > thatVal;
                        default:
                            return false;
                    }
                default:
                    System.err
                            .println("Triplet.class: this shouldn't happen! (numeric)");
                    return false;
            }
        }
    }

    /**
     * Help method that checks if this triplet covers a given string value. This
     * is the same as calling the Triplet.covers method on a triplet that has
     * the same key as this triplet, the same value as the one passed here, and
     * the operator is EQUALS.
     *
     */
    public boolean covers(String thatVal) {
        if (!(this.value instanceof String)) {
            return false;
        }
        String thisVal = ((String) this.value).toLowerCase(Locale.ENGLISH);
        thatVal = thatVal.toLowerCase(Locale.ENGLISH);

        // for every possible this string operator
        switch (this.operator) {
            case EQUAL:
                return thisVal.equals(thatVal);
            case CONTAINS_STRING:
                return thatVal.contains(thisVal);
            case STARTS_WITH_STRING:
                return thatVal.startsWith(thisVal);
            case ENDS_WITH_STRING:
                return thatVal.endsWith(thisVal);
            default:
                System.err
                        .println("Triplet.class: this shouldn't happen! (string_2)");
                return false;
        }
    }

    /**
     * Help method that checks if this triplet covers a given double value. This
     * is the same as calling the Triplet.covers method on a triplet that has
     * the same key as this triplet, the same value as the one passed here, and
     * the operator is EQUALS.
     *
     */
    public boolean covers(double thatVal) {
        Double thisVal = null;
        Double[] thisArray = null;
        if (this.value instanceof Double) {
            thisVal = (Double) this.value;
        } else if (this.value instanceof Double[]) {
            thisArray = (Double[]) this.value;
        } else {
            return false;
        }
        // for every possible this numerical operator
        switch (this.operator) {
            case EQUAL:
                return thisVal == thatVal;
            case GREATER_THAN:
                return thisVal < thatVal;
            case GREATER_OR_EQUAL:
                return thisVal <= thatVal;
            case LESS_THAN:
                return thisVal > thatVal;
            case LESS_OR_EQUAL:
                return thisVal >= thatVal;
            case BETWEEN:
                return (thisArray[0] <= thatVal) && (thatVal <= thisArray[1]);
            default:
                System.err
                        .println("Triplet.class: this shouldn't happen! (numeric_2)");
                return false;
        }
    }

    /**
     * Tests if the two Triplets are equivalent (disregarding their exact
     * parameters). Two Triplets are equivalent when they cover each other.
     */
    public boolean isEquivalent(Triplet other) {
        return (this.covers(other) && other.covers(this));
    }

    /**
     * Two Triplets are equal if the have the exact same key, value and
     * operator. Two Triplets can be EQUIVALENT without being EQUAL, because a
     * different combination of values and operators can yield the same results.
     * For example (BETWEEN [-inf, 5]) and (LESS_OR_EQUAL 5). For testing
     * equivalence use the isEquivalent method!
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Triplet) {
            Triplet otherTriplet = (Triplet) other;
            return (this.key.equals(otherTriplet.key)
                    && this.value.equals(otherTriplet.value) && this.operator
                    .equals(otherTriplet.operator));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 11 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 11 * hash
                + (this.operator != null ? this.operator.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        if (value instanceof Double[]) {
            Double[] val = (Double[]) this.value;
            return (this.key + " " + this.operator + " [" + val[0] + ","
                    + val[1] + "]");
        } else {
            return (this.key + " " + this.operator + " " + this.value);
        }
    }
}
