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
import java.util.Iterator;

/**
 * Publication implementation using hashtable. Supports equivalence operator
 * only.
 *
 */
public class HashtablePublication extends Publication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     * user-defined parameters describing the event
     */
    private HashMap<String, Object> userProperties;

    /**
     * Should not be used, created only for WS methods
     */
    public HashtablePublication() {
        super();
    }

    public HashtablePublication(long validity, long startTime) {
        super(validity, startTime);
        userProperties = new HashMap<String, Object>();
    }

    public HashtablePublication(HashMap<String, Object> userProperties,
            long validity, long startTime) {
        super(validity, startTime);
        this.userProperties = userProperties;
    }

    /**
     * Returns reference to user-defined parameters.
     *
     * @return Returns reference to user-defined parameters.
     */
    public HashMap<String, Object> getProperties() {
        return userProperties;
    }

    /**
     * Sets userProperties.
     *
     * @param userProperties Sets userProperties.
     */
    public void setProperties(HashMap<String, Object> userProperties) {
        this.userProperties = userProperties;
    }

    /**
     * Method for adding a user-defined property. Name and Value must be
     * not-null objects.
     *
     * @param propertyName Name of property.
     * @param propertyValue Value of property.
	 *
     */
    public void setProperty(String propertyName, Object propertyValue) {
        if ((propertyName != null) && (propertyValue != null)) {
            userProperties.put(propertyName, propertyValue);
        }
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof HashtablePublication) {
            HashtablePublication publication = (HashtablePublication) anObject;
            return userProperties.equals(publication.getProperties());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (29 * result)
                + ((userProperties != null) ? userProperties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        Iterator<String> iterator = userProperties.keySet().iterator();
        String key = iterator.next();
        StringBuilder retVal = new StringBuilder("(" + key + "="
                + userProperties.get(key));
        while (iterator.hasNext()) {
            key = iterator.next();
            retVal.append(", " + key + "=" + userProperties.get(key));
        }
        return retVal.append(')').toString();
    }
}
