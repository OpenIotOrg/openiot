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

import org.openiot.cupus.common.UniqueObject;

/**
 * Abstract Publication class
 *
 */
public abstract class Publication extends UniqueObject implements Serializable {

    private static final long serialVersionUID = 1L;

    protected long validity;
    protected long startTime;

    /**
     * Should not be used, created only for WS methods
     */
    public Publication() {
        super();
    }

    public Publication(long validity, long startTime) {
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
        return (validity >= java.lang.System.currentTimeMillis())
                || (validity == -1);
    }

    /**
     * Method returns TRUE if this notification's type matches subscription
     * sEvent (actually, by calling a method on SubscriptionEvent, returns TRUE
     * if subscription sEvent matches this notification by type).
     *
     * @param sub Subscription to compare to.
     *
     * @return True if can publish to, else false
     */
    public boolean matchesSubscription(Subscription sub) {
        return sub.coversPublication(this);
    }

    public abstract String toString();
}
