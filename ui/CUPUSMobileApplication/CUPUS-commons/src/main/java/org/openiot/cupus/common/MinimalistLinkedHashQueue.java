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

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A minimalist Queue implementation that supports only the basic Queue methods.<br>
 * It doesn't allow null nor duplicate elements and guarantees no Exceptions,
 * only true/false returns.<br>
 * It is NOT serializable nor is it thread-safe nor does it have any of the
 * standard Object methods overriden (toString, hashCode and equals).
 * 
 * It is basically a one-way-linked list with a head and tail pointer which is
 * enough for all basic queue methods. The contents of the queue are also backed
 * in a HashSet which, although increases memory footprint, makes it possible to
 * check for uniqness of an element in O(1) making it possible to unable
 * duplicate elements in the queue.
 * 
 * The result is that all the methods have a time complexity of O(1).
 * 
 * @author Eugen Rozic
 */
public class MinimalistLinkedHashQueue<E> implements Iterable<E> {

	private int capacity = Integer.MAX_VALUE; // default value

	private HashMap<E, Node<E>> backupMap;

	private Node<E> head = null;
	private Node<E> tail = null;

	private boolean dirty = false;

	/**
	 * Sets the maximum capacity (after which offer returns false) to the given
	 * value.
	 */
	public MinimalistLinkedHashQueue(int capacity) {
		this.capacity = capacity;
		this.backupMap = new HashMap<E, Node<E>>(capacity + 1);
	}

	public int size() {
		return backupMap.size();
	}

	public boolean isFull() {
		return (size() >= capacity);
	}

	public boolean isEmpty() {
		return backupMap.isEmpty();
	}

	/**
	 * O(1) check - compliments of HashSet
	 */
	public boolean contains(E e) {
		return backupMap.containsKey(e);
	}

	/**
	 * Adds the element at the end of this queue if it doesn't violate capacity,
	 * if e!=null and queue !contains(e).
	 * 
	 * @return True if the element was added, false if not.
	 */
	public boolean offer(E e) {
		if (size() >= capacity || e == null || contains(e))
			return false;
		else
			this.dirty = true;

		Node<E> newNode = new Node<E>(tail, e, null);
		if (tail != null) // if newNode isn't the first element
			tail.next = newNode;
		if (head == null) // if newNode IS the first element
			head = newNode;
		tail = newNode;
		backupMap.put(e, newNode);
		return true;
	}

	/**
	 * Removes the head of this queue, if there is one.
	 * 
	 * @return The head if there was one, null if there wasn't.
	 */
	public E poll() {
		if (head == null)
			return null;
		else
			this.dirty = true;

		Node<E> toRemove = head;
		if (head == tail) {
			head = null;
			tail = null;
		} else {
			head = head.next;
			head.prev = null;
		}
		toRemove.next = toRemove.prev = null;
		backupMap.remove(toRemove.item);
		return toRemove.item;
	}

	/**
	 * Returns the head of this queue, if there is one, but doesn't remove it
	 * from the queue.
	 * 
	 * @return The first element, null if there isn't one.
	 */
	public E peek() {
		if (head == null)
			return null;
		return head.item;
	}

	/**
	 * Removes the given element from the queue, from any position.
	 * 
	 * @return true if removed, false if not
	 */
	public boolean remove(E e) {
		Node<E> removedNode = null;
		if (e == null || (removedNode = backupMap.remove(e)) == null)
			return false;
		else
			this.dirty = true;

		// link prev to next
		if (removedNode.prev != null)
			removedNode.prev.next = removedNode.next;
		else
			head = removedNode.next;

		// becklink next to prev
		if (removedNode.next != null)
			removedNode.next.prev = removedNode.prev;
		else
			tail = removedNode.prev;

		// unlink the removed element's node (for GC)
		removedNode.next = removedNode.prev = null;
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iter();
	}

	/**
	 * Represents a single element of the queue.
	 */
	private static class Node<E> {
		Node<E> prev;
		E item;
		Node<E> next;

		Node(Node<E> prev, E element, Node<E> next) {
			this.prev = prev;
			this.item = element;
			this.next = next;
		}
	}

	/**
	 * For iterating over this queue from first element to last. <br>
	 * Throws a ConcurrentmodificationException if modified while iterating...
	 */
	private class Iter implements Iterator<E> {

		Node<E> current;

		public Iter() {
			MinimalistLinkedHashQueue.this.dirty = false;
			current = head;
		}

		@Override
		public boolean hasNext() {
			if (dirty)
				throw new ConcurrentModificationException();

			return (current != null);
		}

		@Override
		public E next() {
			if (dirty)
				throw new ConcurrentModificationException();

			if (current == null)
				throw new NoSuchElementException();
			E e = current.item;
			current = current.next;
			return e;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
