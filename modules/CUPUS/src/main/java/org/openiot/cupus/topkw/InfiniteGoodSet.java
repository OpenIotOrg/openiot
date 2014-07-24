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
package org.openiot.cupus.topkw;

import java.io.Serializable;
import java.util.TreeSet;
import org.openiot.cupus.artefact.MemorizedPublication;

/**
 *
 * @author kpripuzic
 */
public class InfiniteGoodSet implements Serializable {

    protected TreeSet<MemorizedPublication> publications;

    public InfiniteGoodSet() {
        this.publications = new TreeSet<MemorizedPublication>(new ScoreComparator());
    }

    public void add(MemorizedPublication p) {
        this.publications.add(p);
    }

    public void remove(MemorizedPublication p) {
        this.publications.remove(p);
    }

    public MemorizedPublication pollFirst() {
        return this.publications.pollFirst();
    }

    public boolean contains(MemorizedPublication p) {
        return this.publications.contains(p);
    }

    public int getSize() {
        return publications.size();
    }

    public boolean isEmpty() {
        return this.publications.isEmpty();
    }

    public TreeSet<MemorizedPublication> getPublications() {
        return publications;
    }
}
