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

package org.openiot.cupus.subForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class ActSubTree {
	
	public static final int ADDED_AS_PARENT = 13;

    private ActSubNode rootElement;
    protected Set<UUID> activeSubscribersSet = new HashSet<UUID>();
    protected HashMap<UUID, Long> activeSubscribersCounter = new HashMap<UUID, Long>();

    public ActSubTree(ActSubNode rootNode) {
        this.addNode(rootNode, null); //will set this as new root
    }

    public int getTreeDepth(ActSubNode node) {
        int i = 0;
        if (!node.getChildren().isEmpty()) {
            int temp;
            for (ActSubNode e : node.getChildren()) {
                temp = getTreeDepth(e) + 1;
                if (temp > i) {
                    i = temp;
                }
            }
        } else {
            i = 1;
        }
        return i;
    }
    
    /**
     * This method adds all the subscribers (counters) from the given tree's
     * activeSubscribersCounter map to "this" tree's activeSubscribersSet/
     * activeSubscribersCounter maps.
     * 
     * @param excludeRoot If "excludeRoot" is set to true then the subscriber from the
     * subscription of the root node of the "other" tree is subtracted/removed from the
     * map of subscribers/subcriberCounters of "this" tree.
     */
    protected void addAllSubscribers(ActSubTree other, boolean excludeRoot){
    	//add all from other tree
    	for (Map.Entry<UUID, Long> entry : other.activeSubscribersCounter.entrySet()){
    		Long counter = this.activeSubscribersCounter.get(entry.getKey());
    		if (counter==null){
    			this.activeSubscribersSet.add(entry.getKey());
    			this.activeSubscribersCounter.put(entry.getKey(), entry.getValue());
    		} else {
    			this.activeSubscribersCounter.put(entry.getKey(), counter+entry.getValue());
    		}
    	}
    	if (!excludeRoot)
    		return;
    	// minus the root of the other tree
    	UUID otherRootSubscriber = other.getRootElement().data.getSubscriberID();
    	Long counter = this.activeSubscribersCounter.get(otherRootSubscriber);
    	if (counter==1){
    		this.activeSubscribersSet.remove(otherRootSubscriber);
    		this.activeSubscribersCounter.remove(otherRootSubscriber);
    	} else {
    		this.activeSubscribersCounter.put(otherRootSubscriber, counter-1);
    	}
    }

    protected HashSet<UUID> findMatchingSubscribers(Publication publication) {
        HashSet<UUID> subscribers = new HashSet<UUID>();
        findMatchingSubscribers(publication, this.rootElement, subscribers);
        return subscribers;
    }
    
    private void findMatchingSubscribers(Publication publication, ActSubNode node, HashSet<UUID> subscribers){
        if (subscribers.size()==activeSubscribersSet.size()) {
            return;
        }
    	if (node.data.coversPublication(publication)) {
            subscribers.add(node.data.getSubscriberID());
            for (ActSubNode e : node.getChildren()) {
                findMatchingSubscribers(publication, e, subscribers);
            }
        }
    }

    protected HashSet<Subscription> findMatchingSubscriptions(Announcement announcement) {
        HashSet<Subscription> subscriptions = new HashSet<Subscription>();
        findMatchingSubscriptions(announcement, this.rootElement, subscriptions);
        return subscriptions;
    }
    
    private void findMatchingSubscriptions(Announcement announcement, ActSubNode node, HashSet<Subscription> subscriptions){
    	if (announcement.coversSubscription(node.data)) {
            subscriptions.add(node.data.getSubscription());
            for (ActSubNode e : node.getChildren()) {
                findMatchingSubscriptions(announcement, e, subscriptions);
            }
        }
    }
    
    /**
     * Return the root ActSubNode of the tree.
     *
     * @return the root element.
     */
    public ActSubNode getRootElement() {
        return this.rootElement;
    }

    /**
     * Set the root Element for the tree.
     *
     * @param rootElement the root element to set.
     */
    public void setRootElement(ActSubNode rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Returns the Tree<T> as a List of ActSubNode<T> objects. The elements of
     * the List are generated from a pre-order traversal of the tree.
     *
     * @return a List<ActSubNode<T>>.
     */
    public List<ActSubNode> toList() {
        List<ActSubNode> list = new ArrayList<ActSubNode>();
        walk(rootElement, list);
        return list;
    }

    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     *
     * @return the String representation of the Tree.
     */
    @Override
    public String toString() {
        return toList().toString();
    }

    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference
     * * as it recurses down the tree.
     *
     * @param element the starting element.
     * @param list the output of the walk.
     */
    private void walk(ActSubNode element, List<ActSubNode> list) {
        list.add(element);
        for (ActSubNode data : element.getChildren()) {
            walk(data, list);
        }
    }

    public int addNode(ActSubNode newNode, ActSubNode startNode) {

        if (startNode == null) {
            this.setRootElement(newNode);
            this.activeSubscribersSet.add(newNode.data.getSubscriberID()); 
            this.activeSubscribersCounter.put(newNode.data.getSubscriberID(), 1L); 
            return SubscriptionDataStructure.NEW_TREE_CREATED;
        }
        
        if (newNode.data.equals(startNode.data))
        	return SubscriptionDataStructure.SUB_ALREADY_IN_FOREST;

        if (startNode.equals(this.getRootElement())) {
        	Long subCount = this.activeSubscribersCounter.get(newNode.data.getSubscriberID());
        	if (subCount==null){
        		this.activeSubscribersSet.add(newNode.data.getSubscriberID());
                this.activeSubscribersCounter.put(newNode.data.getSubscriberID(), 1L);
        	} else {
        		this.activeSubscribersCounter.put(newNode.data.getSubscriberID(), subCount+1);
        	}
        }

        if (newNode.data.getSubscription().coversSubscription(startNode.data.getSubscription())) {
        	newNode.addChild(startNode);
        	
            if (startNode.equals(this.rootElement)){
            	setRootElement(newNode);
            	return SubscriptionDataStructure.NEW_TREE_ROOT;
            } else {
            	return ADDED_AS_PARENT;
            }
            
        } else if (startNode.data.getSubscription().coversSubscription(newNode.data.getSubscription())) {
            if (startNode.getChildren().isEmpty()) {
                startNode.addChild(newNode);
                return SubscriptionDataStructure.SUB_ADDED;
            }
            List<ActSubNode> toBeRemoved = new ArrayList<ActSubNode>();
            for (ActSubNode e : startNode.getChildren()) {
            	switch (addNode(newNode, e)){ //try to add the node to each of the children...
            	case (ADDED_AS_PARENT):
            		toBeRemoved.add(e); //try other children to see if new node is a common parent to more...
            		break;
            	case (SubscriptionDataStructure.SUB_NOT_ADDED):
            		break; //try other children...
            	case (SubscriptionDataStructure.SUB_ADDED):
            		return SubscriptionDataStructure.SUB_ADDED;
            	case (SubscriptionDataStructure.SUB_ALREADY_IN_FOREST):
            		return SubscriptionDataStructure.SUB_ALREADY_IN_FOREST;
            	case (SubscriptionDataStructure.NEW_TREE_CREATED):
            		return SubscriptionDataStructure.NEW_TREE_CREATED; //should not happen, let the forest deal with it...
            	case (SubscriptionDataStructure.NEW_TREE_ROOT):
            		System.err.println("ActSubTree: New ROOT?!?! Probably multiple pointers at root...");
            	case (SubscriptionDataStructure.ERROR_ADDING_SUB):
            	default:
            		return SubscriptionDataStructure.ERROR_ADDING_SUB;
            	}
            }
            startNode.children.removeAll(toBeRemoved); //remove all the children that put the new node as their parent...
    		startNode.addChild(newNode);
    		return SubscriptionDataStructure.SUB_ADDED;
        } else {
        	return SubscriptionDataStructure.SUB_NOT_ADDED;
        }
    }

    public void removeNode(ActiveSubscription subscription, ActSubNode rootNode) {

        for (ActSubNode e : rootNode.getChildren()) {
            if (e.data.equals(subscription)) {
                for (ActSubNode f : e.getChildren()) {
                    rootNode.children.add(f);
                }
                rootNode.children.remove(e);
                long temp;
                if (this.activeSubscribersCounter.containsKey(subscription.getSubscriberID())) {
                    temp = this.activeSubscribersCounter.remove(subscription.getSubscriberID());
                } else {
                    temp = 0;
                }
                this.activeSubscribersCounter.put(subscription.getSubscriberID(), temp - 1);

                return;
            } else {
                if (e.data.getSubscription().coversSubscription(subscription.getSubscription())) {
                    removeNode(subscription, e);
                }
            }
        }
    }

    public boolean coversPublication(Publication publication) {
        if (this.getRootElement().data.coversPublication(publication)) {
            return true;
        }

        return false;
    }

    public void deleteSubscriber(UUID subscriberID) {

        for (ActSubNode e : this.toList()) {
            if (e.data.getSubscriberID().equals(subscriberID)) {
                this.removeNode(e.data, rootElement);
            }
        }
        activeSubscribersSet.remove(subscriberID);
        activeSubscribersCounter.remove(subscriberID);
    }

    public boolean contains(Subscription sub, ActSubNode root) {
        if (this.rootElement.data.getSubscription().equals(sub)) {
            return true;
        } else {
            for (ActSubNode e : root.getChildren()) {
                contains(sub, e);
            }
        }
        return false;
    }
}
