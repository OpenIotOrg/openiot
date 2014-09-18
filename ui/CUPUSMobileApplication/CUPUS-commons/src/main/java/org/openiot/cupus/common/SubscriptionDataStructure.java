/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

package org.openiot.cupus.common;

import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.ActiveSubscription;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletAnnouncement;

/**
 * Interface of a data structure that is holding the active subscriptions and
 * facilitating the matching operation.
 * 
 * @author Eugen
 * 
 */
public interface SubscriptionDataStructure {

	public static final int ERROR_ADDING_SUB = -1;

	public static final int SUB_NOT_ADDED = 0;
	public static final int NEW_TREE_ROOT = 1;
	public static final int SUB_ADDED = 2;
	public static final int NEW_TREE_CREATED = 3;
	public static final int SUB_ALREADY_IN_FOREST = 4;

	public static final int SUB_NOT_REMOVED = 0;
	public static final int SUB_REMOVED = 2;

	int addSubscription(ActiveSubscription subscription);

	Set<UUID> findMatchingSubscribers(Publication publication);

	Set<Subscription> findMatchingSubscriptions(Announcement announcement);

	int removeSubscription(ActiveSubscription subscription);

	int deleteSubscriber(UUID subscriberID);

	void removeExpiredSubscriptions();

}
