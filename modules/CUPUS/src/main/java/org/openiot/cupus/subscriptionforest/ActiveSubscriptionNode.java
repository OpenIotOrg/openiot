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
import java.util.List;

import org.openiot.cupus.artefact.ActiveSubscription;

/**
 *
 * @author Aleksandar
 */
public class ActiveSubscriptionNode {

    public ActiveSubscription data;
    public List<ActiveSubscriptionNode> children;

    public ActiveSubscriptionNode(ActiveSubscription sub) {
        data = sub;
        children = new ArrayList<ActiveSubscriptionNode>();
    }

    /**
     * Return the children of ActSubNode<T>. The Tree<T> is represented by a single
     * root ActSubNode<T> whose children are represented by a List<ActSubNode<T>>. Each of
     * these ActSubNode<T> elements in the List can have children. The getChildren()
     * method will return the children of a ActSubNode<T>.
     * @return the children of ActSubNode<T>
     */
    public List<ActiveSubscriptionNode> getChildren() {
        return this.children;
    }

    public List<ActiveSubscriptionNode> getFutureParents(ActiveSubscription subscription) {

        return this.children;
    }

    /**
     * Sets the children of a ActSubNode<T> object. 
     * @param children the List<ActSubNode<T>> to set.
     */
    public void setChildren(List<ActiveSubscriptionNode> children) {
        this.children = children;
    }

    /**
     * Returns the number of immediate children of this ActSubNode<T>.
     * @return the number of immediate children.
     */
    public int getNumberOfChildren() {
        return children.size();
    }

    /**
     * Adds a child to the list of children for this ActSubNode<T>. The addition of
     * the first child will create a new List<ActSubNode<T>>.
     * @param child a ActSubNode<T> object to set.
     */
    public void addChild(ActiveSubscriptionNode child) {
        children.add(child);
    }

    /**
     * Inserts a ActSubNode<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     * @param index the position to insert at.
     * @param child the ActSubNode<T> object to insert.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void insertChildAt(int index, ActiveSubscriptionNode child) throws IndexOutOfBoundsException {
        if (index == getNumberOfChildren()) {
        	children.add(child);
        } else {
            children.add(index, child);
        }
    }

    /**
     * Remove the ActSubNode<T> element at index index of the List<ActSubNode<T>>.
     * @param index the index of the element to delete.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }

    public ActiveSubscription getData() {
        return this.data;
    }

    public void setData(ActiveSubscription data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{').append(getData().toString()).append(",[");
        int i = 0;
        for (ActiveSubscriptionNode e : getChildren()) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(e.getData().toString());
            i++;
        }
        sb.append(']').append("}\n");
        return sb.toString();
    }
}


