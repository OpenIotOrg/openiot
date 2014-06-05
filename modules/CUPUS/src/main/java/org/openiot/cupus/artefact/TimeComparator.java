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

import java.util.Comparator;

/**
 *
 * @author Krešimir Pripužić <kpripuzic at gmail.com>, Maria
 */
public class TimeComparator implements Comparator<MemorizedPublication> {

    public int compare(MemorizedPublication p1, MemorizedPublication p2) {
    	long p1Time = Math.min(p1.getExpiry(), p1.getValidity());
    	long p2Time = Math.min(p2.getExpiry(), p2.getValidity());
        
    	if (p1Time < p2Time) {
            return -1;
        } else if (p1Time > p2Time) {
            return 1;
        } else if (p1.hashCode() < p2.hashCode()) {
            return 1;
        } else if (p1.hashCode() > p2.hashCode()) {
            return -1;
        } else {
            return 0;
        }
    }
}
