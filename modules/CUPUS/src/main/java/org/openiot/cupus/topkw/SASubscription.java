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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;
import org.openiot.cupus.artefact.MemorizedPublication;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.TopKWSubscription;
import org.openiot.cupus.entity.broker.TopKWMatcher;
import org.openiot.cupus.message.external.PublishMessage;

/**
 *
 * @author Krešimir Pripužić <kpripuzic at gmail.com>, Eugen
 */
public class SASubscription extends MemorySubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExcellentSet excellent;
    private InfiniteGoodSet good;
    private FastSkyline[] collection;

    public SASubscription(UUID subscriber, TopKWSubscription subscription) {
        super(subscriber, subscription);
        this.excellent = new ExcellentSet(subscription.getK(), Float.NEGATIVE_INFINITY);
        this.good = new InfiniteGoodSet();

        this.collection = new FastSkyline[subscription.getK()];
        for (int i = 0; i < subscription.getK(); i++) {
            this.collection[i] = new FastSkyline();
        }
    }

    @Override
    public synchronized boolean process(Publication publication, UUID publisher,
            ExpiringMemorizedPublications epubs, long time) {

        long pubValidity = time;
        if (time > (System.currentTimeMillis() + this.getW()) || time == -1) {
            pubValidity = System.currentTimeMillis() + this.getW();
        }
        MemorizedPublication mp = new MemorizedPublication(publisher, publication,
                this, this.calculateRelevance(publication), pubValidity);

        LinkedList<MemorizedPublication> uDom = this.addToSkyband(mp, epubs);
        if (uDom == null) {
            //bad candidate
            return false;
        }
        if (mp.getRelevance() >= this.excellent.getThreshold()) {
            //excellent candidate
            //push to excellent and get overflow
            MemorizedPublication overflow = this.excellent.pushAndUpdate(mp);

            if (overflow != null) {
                //add overflow (worst from excellent) to good
                this.good.add(overflow);
            }
            //it is top-k
            //TODO: DELIVER 
            this.getNotifier().send(mp.getPublication(), this.getSubscriber());
            mp.setDelivered(true);

            //add excellent to expiring
            epubs.add(mp);
            this.removeDominated(uDom, epubs);
            return false;
        } else {
            //good candidate

            //add to good
            this.good.add(mp);

            //add good to expiring
            epubs.add(mp);
            this.removeDominated(uDom, epubs);
            return false;
        }
    }

    @Override
    public synchronized void remove(MemorizedPublication p) {
        this.removeFromSkyband(p);

        if (p.getRelevance() >= this.excellent.getThreshold()) {
            //excellent candidate
            this.excellent.remove(p);

            //get the best from good
            MemorizedPublication best = this.good.pollFirst();

            if (best != null) {
                //add the best from good to excellent, updates threshold
                this.excellent.addLastAndUpdate(best);

                //deliver if it is top-k
                if (!best.isDelivered()) {
                    this.getNotifier().send(best.getPublication(), this.getSubscriber());
                    best.setDelivered(true);
                }
            } else {
                //reset threshold, good is empty
                this.excellent.setThreshold(0);
            }
        } else {
            //if it is not excellent than it is good
            this.good.remove(p);
        }
    }

    @Override
    public double currentThreshold() {
        return this.excellent.getThreshold();
    }

    public boolean contains(MemorizedPublication mp) {
        if (mp.getRelevance() > this.excellent.getThreshold()) {
            //excellent candidate
            return this.excellent.contains(mp);
        } else {
            //if it is not excellent than it is good
            return this.good.contains(mp);
        }
    }

    public int getExcellentSize() {
        return this.excellent.getSize();
    }

    public int getGoodSize() {
        return this.good.getSize();
    }

    private void removeFromSkyband(MemorizedPublication p) {

        int numAllDominators = 0;
        for (int i = 0; i < super.getK(); i++) {
            int numDominators = collection[i].getNumDominators(p);
            numAllDominators += numDominators;

            if (numAllDominators == i) {
                //remove it from its skyband
                collection[i].remove(p);

                //repair dominated
                for (int l = i + 1; l < super.getK(); l++) {
                    LinkedList<MemorizedPublication> lDom = collection[l].pollDominated(p);

                    if (!lDom.isEmpty()) {
                        for (ListIterator<MemorizedPublication> j = lDom.listIterator(); j.hasNext();) {
                            collection[l - 1].add(j.next());
                        }
                    }
                }
                return;
            }
        }
    }

    private LinkedList<MemorizedPublication> addToSkyband(MemorizedPublication p, ExpiringMemorizedPublications epubs) {
        int numAllDominators = 0;
        for (int i = 0; i < super.getK(); i++) {
            int numDominators = collection[i].getNumDominators(p);
            numAllDominators += numDominators;

            if (numAllDominators == i) {
                //get and remove dominated
                LinkedList<MemorizedPublication> uDom = collection[i].pollDominated(p);

                //add it to its skyband
                collection[i].add(p);

                //remove all dominated
                for (int l = i + 1; l < super.getK(); l++) {
                    LinkedList<MemorizedPublication> lDom = collection[l].pollDominated(p);

                    if (!uDom.isEmpty()) {
                        for (ListIterator<MemorizedPublication> j = uDom.listIterator(); j.hasNext();) {
                            collection[l].add(j.next());
                        }
                    }
                    uDom = lDom;
                }
                return uDom;
            } else if (numAllDominators >= super.getK()) {
                //bad candidate
                return null;
            }
        }
        return null;
    }

    private void removeDominated(LinkedList<MemorizedPublication> uDom, ExpiringMemorizedPublications epubs) {
        for (ListIterator<MemorizedPublication> j = uDom.listIterator(); j.hasNext();) {
            MemorizedPublication d = j.next();

            //remove d from other structures
            this.good.remove(d);
            epubs.remove(d);
        }
    }

    public boolean equals(Object sub) {
        if (this == sub) {
            return true;
        }
        if (sub instanceof SASubscription) {
            SASubscription saSub = (SASubscription) sub;
            return this.subscriber.equals(saSub.subscriber)
                    && this.subscription.equals(saSub.subscription);
        } else {
            return false;
        }
    }

    @Override
    public boolean coversPublication(Publication pub) {
        System.err.println("coversPublication method of TopKWSubscription subscriptions"
                + " should not be used!");
        return false;
    }

    @Override
    public String toString() {
        return subscription.toString();
    }
}
