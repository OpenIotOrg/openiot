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

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.MemorySubscription;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;

/**
 * An interface that defines a topKW processor. It should be able to hold
 * subscriptions and support their addition and removal, and also be able to
 * process a publication and to check for expired publications and remove them.
 *
 * @author Krešimir Pripužić <kpripuzic at gmail.com>, Eugen
 */
public interface TopKWProcessor extends Runnable {

    /**
     * @return if added true, else false (if already existed perhaps)
     */
    public boolean add(MemorySubscription s);

    /**
     * @return if removed true, else false (if didn't exist)
     */
    public boolean remove(MemorySubscription s);

    public void deleteSubscriber(UUID subscriberID);

    public void process(Publication publication, UUID publisher, long time);

    public void checkExpired(long time);

    public boolean unpublish(Publication publication);

    public Set<Subscription> findMatchingSubscriptions(Announcement announcement);

    public List<MemorySubscription> getSubscriptions();
}
