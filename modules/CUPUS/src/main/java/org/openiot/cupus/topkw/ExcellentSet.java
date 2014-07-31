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
public class ExcellentSet implements Serializable {

    protected TreeSet<MemorizedPublication> publications;
    private int maxSize;
    protected double threshold;

    public ExcellentSet(int maxSize, float initialThreshold) {
        this.publications = new TreeSet<MemorizedPublication>(new ScoreComparator());
        this.maxSize = maxSize;
        this.threshold = initialThreshold;
    }

    public MemorizedPublication pushAndUpdate(MemorizedPublication p) {
        this.publications.add(p);

        if (this.publications.size() > this.maxSize) {
            MemorizedPublication last = this.publications.pollLast();

            //update threshold
            this.threshold = this.publications.last().getRelevance();

            //return to remove from expiring publications
            return last;
        } else if (this.publications.size() == this.maxSize) {
            //update threshold
            this.threshold = this.publications.last().getRelevance();

            return null;
        } else {
            return null;
        }
    }

    public int getSize() {
        return publications.size();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void addLastAndUpdate(MemorizedPublication p) {
        this.publications.add(p);
        this.threshold = p.getRelevance();
    }

    public void remove(MemorizedPublication p) {
        this.publications.remove(p);
    }

    public MemorizedPublication last() {
        return this.publications.last();
    }

    public MemorizedPublication lower(MemorizedPublication p) {
        return this.publications.lower(p);
    }

    public MemorizedPublication pollFirst() {
        return this.publications.pollFirst();
    }

    public boolean contains(MemorizedPublication p) {
        return this.publications.contains(p);
    }

    public TreeSet<MemorizedPublication> getPublications() {
        return publications;
    }
}
