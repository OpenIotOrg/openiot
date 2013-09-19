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

package org.openiot.gsn.utils.graph;

import java.io.Serializable;
import java.util.ArrayList;

public class Node<T> implements Serializable{

	private static final long serialVersionUID = -62301155506174334L;

	private ArrayList<Edge<T>> inputEdges;

	private ArrayList<Edge<T>> outputEdges;

	private T object;

	private boolean root;

	private boolean visited;
	
	private boolean removed;

	public Node() {
		this(null);
	}

	public Node(T object) {
		inputEdges = new ArrayList<Edge<T>>();
		outputEdges = new ArrayList<Edge<T>>();
		root = false;
		visited = false;
		removed = false;
		this.object = object;
	}

	public Edge<T> addEdge(Node<T> node) throws EdgeExistsException {
		if (edgeExists(node))
			throw new EdgeExistsException();
		Edge<T> edge = new Edge<T>(this, node);
		outputEdges.add(edge);
		node.getInputEdges().add(edge);
		return edge;
	}

	public boolean removeEdge(Node<T> node) {
		boolean removed = false;
		Edge<T> edge = getEdge(node);
		if (edge != null) {
			outputEdges.remove(edge);
			edge.getEndNode().getInputEdges().remove(edge);
			removed = true;
		}
		return removed;
	}

	private boolean edgeExists(Node<T> node) {
		for (Edge edge : outputEdges) {
			if (edge.getEndNode().equals(node))
				return true;
		}
		return false;
	}

	private Edge<T> getEdge(Node<T> node) {
		for (Edge<T> edge : outputEdges) {
			if (edge.getEndNode().equals(node))
				return edge;
		}
		return null;
	}

	public ArrayList<Edge<T>> getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(ArrayList<Edge<T>> inputEdges) {
		this.inputEdges = inputEdges;
	}

	public ArrayList<Edge<T>> getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(ArrayList<Edge<T>> outputEdges) {
		this.outputEdges = outputEdges;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean equals(Object obj) {
		if (this.object == obj)
			return true;
		if (obj instanceof Node && this.object != null){
			Node node = (Node) obj;
			return this.object.equals(node.getObject());
		}
		return false;
	}
	
	public String toString(){
		return new StringBuilder("Node[").append(object != null ? object.toString() : null).append("]").toString(); 
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	
}
