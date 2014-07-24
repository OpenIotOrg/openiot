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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.openiot.cupus.artefact.MemorizedPublication;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.TimeComparator;
import org.openiot.cupus.entity.broker.PublisherForBroker;

/**
 *
 * @author kpripuzic, Eugen
 */
public class ExpiringMemorizedPublications {

    private TreeSet<MemorizedPublication> publications;
    private long next;

    public ExpiringMemorizedPublications() {
        this.publications = new TreeSet<MemorizedPublication>(new TimeComparator());
        this.next = Long.MAX_VALUE;
    }

    public synchronized void add(MemorizedPublication p) {
        if (this.next == Long.MAX_VALUE || this.next > p.getExpiry()) {
            this.next = p.getExpiry();
        }
        this.publications.add(p);
    }

    public boolean remove(MemorizedPublication p) {
        synchronized (this) {
            boolean successful = this.publications.remove(p);
            //update oldest
            if (successful && p.getExpiry() == this.next) {
                if (this.publications.isEmpty()) {
                    this.next = Long.MAX_VALUE;
                } else {
                    this.next = this.publications.first().getExpiry();
                }
            }
            return successful;
        }

		//FIXME što nebi i ovo trebalo biti tu...?!
        //remove from its subscription
        //MemorySubscription ms = p.getSubscription();
        //ms.remove(p, notifier);
        //FIXME ... jer ovo se poziva u removeDominated (SASubscription) pa bi bilo kružno...
    }

    private synchronized MemorizedPublication pollFirst() {
        MemorizedPublication p = this.publications.pollFirst();
        //change next
        if (this.publications.isEmpty()) {
            this.next = Long.MAX_VALUE;
        } else {
            this.next = this.publications.first().getExpiry();
        }

        return p;
    }

    public MemorizedPublication first() {
        return this.publications.first();
    }

    public long getNext() {
        return this.next;
    }

    public void checkExpired(long time) {
        //while (this.next() == time) {
        while (this.next <= time) { //FIXME kaj ne ovak??

            MemorizedPublication p = this.pollFirst();

            //remove from its subscription
            MemorySubscription ms = p.getSubscription();
            ms.remove(p);
        }
    }

    /**
     * This method finds MemorizedPublications that contain the given
     * publication (equal by UUID) and are from the same publisher... The method
     * is used for unpublishing.
     *
     * It has to be synchronized because otherwise someone might change the
     * iterator while it is being iterated...
     */
    public synchronized List<MemorizedPublication> findMatchingPubs(Publication publication) {
        List<MemorizedPublication> matchingPubs = new ArrayList<MemorizedPublication>();
        for (MemorizedPublication memPub : publications) {
            if (memPub.getPublication().equals(publication)) {
                matchingPubs.add(memPub);
            }
        }
        return matchingPubs;
    }

    public int getSize() {
        return this.publications.size();
    }
}
