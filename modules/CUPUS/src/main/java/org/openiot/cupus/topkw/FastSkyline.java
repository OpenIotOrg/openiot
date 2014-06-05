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
import java.util.LinkedList;
import java.util.ListIterator;
import org.openiot.cupus.artefact.MemorizedPublication;

/**
 *
 * @author Krešimir Pripužić <kpripuzic at gmail.com>
 */
public class FastSkyline implements Serializable {

    private LinkedList<MemorizedPublication> MemorizedPublications;

    public FastSkyline() {
        this.MemorizedPublications = new LinkedList<MemorizedPublication>();
    }

    public int getNumDominators(MemorizedPublication p) {
        int n = 0;
        ListIterator<MemorizedPublication> i = MemorizedPublications.listIterator();

        while (i.hasNext()) {
            MemorizedPublication j = i.next();
            if (j.getRelevance() > p.getRelevance()) {
                if (j.getExpiry() > p.getExpiry()) {
                    n++;
                } else {
                    break;
                }
                while (i.hasNext()) {
                    j = i.next();
                    if (j.getExpiry() > p.getExpiry()) {
                        n++;
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        return n;
    }

    public void add(MemorizedPublication p) {
        ListIterator<MemorizedPublication> i = MemorizedPublications.listIterator();

        while (i.hasNext()) {
            MemorizedPublication j = i.next();
            if (j.getExpiry() < p.getExpiry()) {
                i.set(p);
                i.add(j);
                return;
            }
        }
        MemorizedPublications.addLast(p);
    }

    public void remove(MemorizedPublication p) {
        MemorizedPublications.remove(p);
    }

    public LinkedList<MemorizedPublication> pollDominated(MemorizedPublication p) {
        LinkedList<MemorizedPublication> r = new LinkedList<MemorizedPublication>();

        ListIterator<MemorizedPublication> i = MemorizedPublications.listIterator();

        while (i.hasNext()) {
            MemorizedPublication j = i.next();
            if (j.getExpiry() < p.getExpiry()) {
                if (j.getRelevance() < p.getRelevance()) {
                    i.remove();
                    r.add(j);
                } else {
                    break;
                }
                while (i.hasNext()) {
                    j = i.next();
                    if (j.getRelevance() < p.getRelevance()) {
                        i.remove();
                        r.add(j);
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        return r;
    }
}
