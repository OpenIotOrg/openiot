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

import java.util.UUID;
import org.openiot.cupus.entity.broker.Notifier;
import org.openiot.cupus.entity.broker.PublisherForBroker;
import org.openiot.cupus.entity.broker.TopKWMatcher;
import org.openiot.cupus.topkw.ExpiringMemorizedPublications;

/**
 * Like ActiveSubscription for normal boolean pub/sub...
 *
 * Represents a currently active subscription in the top-k/w pub/sub (i.e. a
 * subscription in the top-k/w processor's list of subscriptions).
 *
 * It has references to the subscription that it holds and also to the
 * subscriber whos subscription it is.
 *
 * It defines 2 methods that have to be implemented - "process" that processes a
 * new publication, and "remove" that removes a current publication from the
 * "memory" of this subscription.
 *
 * @author Krešimir Pripužić, Eugen
 *
 */
public abstract class MemorySubscription extends TopKWSubscription {

    private static final long serialVersionUID = 1L;

    protected TopKWSubscription subscription;
    protected UUID subscriber;
    protected Notifier notifier;
    
    
     
    public TopKWMatcher m;

    
    

    public MemorySubscription(UUID subscriber, TopKWSubscription subscription) {
        super(subscription.K, subscription.W, subscription.getValidity(), subscription.getStartTime());
        this.subscription = subscription;
        this.subscriber = subscriber;
    }

    public UUID getSubscriber() {
        return subscriber;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public TopKWSubscription getSubscription() {
        return subscription;
    }
    
    public void setNotifier(Notifier n) {
        notifier=n;
    }

    @Override
    public double calculateRelevance(Publication p) {
        return subscription.calculateRelevance(p);
    }

    public abstract boolean process(Publication publication, UUID publisher,
            ExpiringMemorizedPublications epubs, long time);

    public abstract void remove(MemorizedPublication memPub);

    public abstract double currentThreshold();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemorySubscription) {
            MemorySubscription memSub = (MemorySubscription) obj;
            if (memSub.subscription.equals(this.subscription) && memSub.subscriber.equals(this.subscriber)) {
                return true;
            }
        }
        return false;
    }

    
    
    
    public void setPrint(TopKWMatcher mat) {
        this.m=mat;
    }
    
}
