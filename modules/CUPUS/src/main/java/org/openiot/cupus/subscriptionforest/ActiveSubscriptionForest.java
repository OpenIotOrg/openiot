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

package org.openiot.cupus.subscriptionforest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openiot.cupus.artefact.ActiveSubscription;
import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.common.SubscriptionDataStructure;

/**
 * 
 * @author Aleksandar, Eugen
 */
public class ActiveSubscriptionForest implements SubscriptionDataStructure {
    

	private List<ActiveSubscriptionTree> trees = new ArrayList<ActiveSubscriptionTree>();

	public List<ActiveSubscriptionTree> getTrees() {
		return trees;
	}
	
	public HashSet<UUID> findMatchingSubscribers(Publication publication){
		HashSet<UUID> subscribers = null;
		for (ActiveSubscriptionTree tree : trees){
			if (subscribers==null)
				subscribers = tree.findMatchingSubscribers(publication);
			else
				subscribers.addAll(tree.findMatchingSubscribers(publication));
		}
		return subscribers;
	}
        
        public HashSet<Subscription> findMatchingSubscriptions(Announcement announcement){
		HashSet<Subscription> subscriptions = null;
		for (ActiveSubscriptionTree tree : trees){
			if (subscriptions==null)
				subscriptions = tree.findMatchingSubscriptions(announcement);
			else
				subscriptions.addAll(tree.findMatchingSubscriptions(announcement));
		}
		return subscriptions;
	}

	public int getMaxTreeDepth() {
		int temp, retVal = 0;
		for (ActiveSubscriptionTree e : this.trees) {
			temp = e.getTreeDepth(e.getRootElement());
			if (temp > retVal) {
				retVal = temp;
			}

		}
		return retVal;
	}

	public int addSubscription(ActiveSubscription subscription) {
		
		ActiveSubscriptionNode newNode = new ActiveSubscriptionNode(subscription);
		
		if (trees.isEmpty()) {
			trees.add(new ActiveSubscriptionTree(newNode));
			return NEW_TREE_CREATED;
		}

		List<ActiveSubscriptionTree> toBeRemoved = new ArrayList<ActiveSubscriptionTree>();
		for (ActiveSubscriptionTree e : this.trees) {
			switch(e.addNode(newNode, e.getRootElement())){ //try to add the node to each of the trees...
			case (NEW_TREE_ROOT):
				toBeRemoved.add(e); //try others to see if common root to more trees...
				break;
			case (SUB_NOT_ADDED):
				break; //try the next tree...
			case (SUB_ALREADY_IN_FOREST):
				return SUB_ALREADY_IN_FOREST;
			case (SUB_ADDED):
				return SUB_ADDED;
			case (NEW_TREE_CREATED):
				System.err.println("ActiveSubscriptionForest: new tree created?!?!");
				return ERROR_ADDING_SUB;
			case (ERROR_ADDING_SUB):
			default:
				System.err.println("ActiveSubscriptionForest: WTF?!?");
				return ERROR_ADDING_SUB;
			}
		}

		if (toBeRemoved.size()==0){ //the subscription wasn't added anywhere
			trees.add(new ActiveSubscriptionTree(newNode));
			return NEW_TREE_CREATED;
		} else {
			for (int i=1; i<toBeRemoved.size(); i++) { //remove all but the first (because all of them have the same node as root)
				toBeRemoved.get(0).addAllSubscribers(toBeRemoved.get(i), true);
				this.trees.remove(toBeRemoved.get(i));
			}
			return NEW_TREE_ROOT;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ActiveSubscriptionTree e : this.trees) {
			sb.append(e.toString()).append("\n\n-----------------------------------------\n");
		}
		return sb.toString();
	}

	public int removeSubscription(ActiveSubscription subscription) {

		for (ActiveSubscriptionTree e : trees) {
			if (e.getRootElement().getData().equals(subscription)) {
				for (ActiveSubscriptionNode f : e.getRootElement().getChildren()) {
					trees.add(new ActiveSubscriptionTree(f));
				}
				trees.remove(e);
				return 1;//new root node
			} else {
				if (e.getRootElement().getData().coversActiveSubscription(subscription)) {
					e.removeNode(subscription, e.getRootElement());
				}
			}
		}
		return 2;
	}

	public int deleteSubscriber(UUID subscriberID) {
		int retVal = 2;
		for (ActiveSubscriptionTree tree : new ArrayList<ActiveSubscriptionTree>(this.trees)) {
			if (tree.getRootElement().getData().getSubscriberID().equals(subscriberID)) {
				retVal = 1;
			}
			for (ActiveSubscriptionNode e : new ArrayList<ActiveSubscriptionNode>(tree.toList())) {
				if (e.getData().getSubscriberID().equals(subscriberID)) {
					this.removeSubscription(e.getData());
				}
			}
			tree.activeSubscribersSet.remove(subscriberID);
			tree.activeSubscribersCounter.remove(subscriberID);
		}
		return retVal;
	}

	public List<ActiveSubscription> getRootSubs() {
		List<ActiveSubscription> retVal = new ArrayList<ActiveSubscription>();
		for (ActiveSubscriptionTree e : trees) {
			retVal.add(e.getRootElement().getData());
		}
		return retVal;
	}

	public boolean contains(Subscription sub) {
		for (ActiveSubscriptionTree e : trees) {
			if (e.contains(sub, e.getRootElement())) {
				return true;
			}
		}

		return false;
	}

	public float percentCovered() {
		float retVal = 100 * (1 - ((float) this.trees.size()) / (float) this.toList().size());
		return retVal;
	}

	public List<ActiveSubscriptionNode> toList() {
		List<ActiveSubscriptionNode> retVal = new ArrayList<ActiveSubscriptionNode>();
		for (ActiveSubscriptionTree e : this.trees) {
			retVal.addAll(e.toList());
		}
		return retVal;
	}

	public void removeExpiredSubscriptions() {
		List<ActiveSubscriptionNode> retVal = toList();
		for (ActiveSubscriptionNode e : retVal) {
			if (!((e.getData().getSubscription().getValidity() >= java.lang.System.currentTimeMillis()) || (e.getData().getSubscription().getValidity() == -1))) {
				removeSubscription(e.getData());
			}
		}
	}
}
