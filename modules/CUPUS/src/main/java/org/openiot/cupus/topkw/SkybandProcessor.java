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



import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.MemorizedPublication;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.entity.broker.TopKWMatcher;

/**
 * An implementation of a topKW processor.
 *
 * @author Krešimir Pripužić <kpripuzic at gmail.com>, Eugen & Maria
 */
public class SkybandProcessor implements TopKWProcessor {

    private TopKWMatcher matcher;

    private Set<MemorySubscription> subscriptions;
    private ExpiringMemorizedPublications emp
;
    public SkybandProcessor(TopKWMatcher matcher) {
        this.matcher = matcher;
        this.subscriptions = new HashSet<MemorySubscription>();
        this.emp = new ExpiringMemorizedPublications();
    }

    @Override
    public synchronized boolean add(MemorySubscription s) {
        return this.subscriptions.add(s);
    }
    

    public synchronized List<MemorySubscription> getSubscriptions() {
        return new LinkedList<>(this.subscriptions);
    }

    @Override
    public synchronized boolean remove(MemorySubscription s) {
        return this.subscriptions.remove(s);
    }
    
    public synchronized void deleteSubscriber (UUID subscriberID) {
        Set<MemorySubscription> toDelete = new HashSet<>();
        for (MemorySubscription ms : subscriptions) {
            if (ms.getSubscriber().equals(subscriberID))
                toDelete.add(ms);
        }
        
        for (MemorySubscription ms : toDelete) {
            subscriptions.remove(ms);
        }
    }
    
    public Set<Subscription> findMatchingSubscriptions (Announcement announcement) {
        Set<Subscription> matched = new HashSet<>();
        for (MemorySubscription ms : subscriptions) {
            if (announcement.coversSubscription(ms.getSubscription())) {
                matched.add(ms.getSubscription());
            }
                
        }
        return matched;
    }

    /**
     * It has to be synchronized in full otherwise the List could be changed
     * while being iterated. So processing of a publication must be atomary (all
     * subscriptions at once) not only because of subscription removal, but
     * because of iteration.
     */
    @Override
    public synchronized void process(Publication publication, UUID publisher, long time) {
        if (matcher.isTesting()) {
            //System.out.println("Broker: topkw recieved publication " + publication.toString());
        }
        if (matcher.isLogWriting()) {
            matcher.informBroker("Broker: topkw Processor recieved publication " + publication.toString(), false);
        }
        Iterator<MemorySubscription> iter = subscriptions.iterator();
        while (iter.hasNext()) {
            MemorySubscription s = iter.next();
            if (s.getValidity() <= System.currentTimeMillis()) {
                if (s.getValidity() != -1) {
                    iter.remove();
                    continue;
                }
            }
            s.process(publication, publisher, emp, time);
        }
    }

    @Override
    public boolean unpublish(Publication publication) {

        List<MemorizedPublication> pubsToUnpublish= emp.findMatchingPubs(publication);

        boolean successful = false;
        for (MemorizedPublication memPub : pubsToUnpublish) {
            successful = emp.remove(memPub); //it is synchronized on the emp itself...
            //TODO: dohvati i obavijesti o promjenama
            memPub.getSubscription().remove(memPub); //it is synchronized on the SASubscription...
        }
        return successful;
    }

    @Override
    public void checkExpired(long time) {
                    //TODO: dohvati i obavijesti o promjenama
        emp.checkExpired(time);
    }

    @Override
    public void run() {
        while (true) {
                        //TODO: dohvati i obavijesti o promjenama
            emp.checkExpired(System.currentTimeMillis());
        }
    }
}
