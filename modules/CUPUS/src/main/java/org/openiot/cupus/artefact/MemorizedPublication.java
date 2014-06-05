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
import org.openiot.cupus.entity.broker.PublisherForBroker;

/**
 * Like ActivePublication for normal boolean pub/sub...
 *
 * Represents a currently active publication in the top-k/w pub/sub (i.e. a
 * memorized publication in the top-k/w processor).
 *
 * It has references to the publication that it holds and also to the
 * subscription that has it in it's window W and to the publisher that published
 * it.
 *
 * @author Krešimir Pripužić, Eugen
 */
public class MemorizedPublication extends Publication {

    private static final long serialVersionUID = 1L;

    private UUID publisher;
    private Publication publication;
    private MemorySubscription memSub;

    private double relevance;
    private long expires;
    private boolean delivered;

    public MemorizedPublication(UUID publisher, Publication publication,
            MemorySubscription memSub, double relevance, long expires) {
        super(publication.getValidity(), publication.getStartTime());
        this.publisher = publisher;
        this.publication = publication;
        this.memSub = memSub;

        this.relevance = relevance;
        this.expires = expires;
        this.delivered = false;
    }

    public double getRelevance() {
        return relevance;
    }

    public long getExpiry() {
        return expires;
    }

    public MemorySubscription getSubscription() {
        return memSub;
    }

    public Publication getPublication() {
        return publication;
    }

    public UUID getPublisher() {
        return publisher;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean isDelivered) {
        this.delivered = isDelivered;
    }
    
    @Override
    public String toString() {
        return publication.toString() + " where relevance score is "+relevance;
    }
}
