package org.openiot.gsn.utils.graph;

import java.io.Serializable;

public class Edge<T> implements Serializable{

	private static final long serialVersionUID = -8165242353963312649L;

	private Node<T> startNode;

	private Node<T> endNode;

	public Edge(Node<T> startNode, Node<T> endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
	}

	public Node<T> getEndNode() {
		return endNode;
	}

	public void setEndNode(Node<T> endNode) {
		this.endNode = endNode;
	}

	public Node<T> getStartNode() {
		return startNode;
	}

	public void setStartNode(Node<T> startNode) {
		this.startNode = startNode;
	}

}
