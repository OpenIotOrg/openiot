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
