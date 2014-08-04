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
 * @author Mehdi Riahi
 * @author gsn_devs
*/

package org.openiot.gsn.utils.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Graph<T> implements Serializable{
	private ArrayList<Node<T>> nodes;

	private ArrayList<Node<T>> rootNodes;

	public Graph() {
		nodes = new ArrayList<Node<T>>();
		rootNodes = new ArrayList<Node<T>>();
	}

	public List<Node<T>> getDescendingNodes(Node<T> node) {
		resetVisitingStatus();
		ArrayList<Node<T>> list = new ArrayList<Node<T>>();
		dfs(node, list);
		return list;
	}

	public List<T> getNodesByDFSSearch() {
		ArrayList<Node<T>> list = new ArrayList<Node<T>>();
		for (Node<T> node : rootNodes) {
			dfs(node, list);
		}
		ArrayList<T> objectList = new ArrayList<T>();
		for (Node<T> node : list) {
			objectList.add(node.getObject());
		}
		return objectList;
	}

	private List<Node<T>> getAscendingNodes(Node<T> node) {
		resetVisitingStatus();
		ArrayList<Node<T>> list = new ArrayList<Node<T>>();
		rdfs(node, list);
		return list;
	}

	private void rdfs(Node<T> node, ArrayList<Node<T>> list) {
		if (node == null)
			return;
		node.setVisited(true);
		for (Edge<T> edge : node.getInputEdges()) {
			if (edge.getStartNode().isVisited() == false) {
				rdfs(edge.getStartNode(), list);
			}
		}
		list.add(node);

	}

	/**
	 * Returns list of nodes that are ascendings of the <code>node</code> including the <code>node</code> itself
	 * @param node
	 * @return
	 */
	public List<Node<T>> nodesAffectedByRemoval(Node<T> node) {
		return getAscendingNodes(node);
	}

	public boolean hasCycle() {
		resetVisitingStatus();
		for (Node<T> node : rootNodes) {
			if (isNodeInCycle(node))
				return true;
		}
		return false;
	}

	private boolean isNodeInCycle(Node<T> node) {
		if (node.isVisited())
			return true;
		node.setVisited(true);
		for (Edge<T> edge : node.getOutputEdges()) {
			if (isNodeInCycle(edge.getEndNode()))
				return true;
		}
		node.setVisited(false);
		return false;
	}

	public Node<T> addNode(T object) {
		if (findNode(object) == null) {
			Node<T> node = new Node<T>(object);
			nodes.add(node);
			rootNodes.add(node);
			return node;
		}
		return null;
	}

	public void addEdge(T startObject, T endObject)
			throws NodeNotExistsExeption {
		Node<T> startNode = findNode(startObject);
		if (startNode == null)
			throw new NodeNotExistsExeption(startObject == null ? "null" : startObject.toString());
		Node<T> endNode = findNode(endObject);
		if (endNode == null)
			throw new NodeNotExistsExeption(endObject == null ? "null" : endObject.toString());
		try {
			startNode.addEdge(endNode);
			if(!endNode.equals(findRootNode(startNode)))
				rootNodes.remove(endNode);
		} catch (EdgeExistsException e) {
			// TODO Auto-generated catch block
		}
	}

	public Node<T> findRootNode(Node<T> startNode) {
		List<Node<T>> ascendingNodes = getAscendingNodes(startNode);
		for (Node<T> node : ascendingNodes) {
			if(rootNodes.contains(node))
				return node;
		}
		return null;
	}

	/**
	 * Removes node having <code>object</code> as node's object and also
	 * removes all ascending nodes of it, except root node.
	 * 
	 * @param Object
	 * @return a boolean indicating whether the node is removed
	 * @throws NodeNotExistsExeption
	 */
	public boolean removeNode(T object) throws NodeNotExistsExeption {
		Node<T> node = findNode(object);
		if (node == null)
			throw new NodeNotExistsExeption(object == null ? "null" : object.toString());

		List<Node<T>> ascendingNodes = getAscendingNodes(node);
		for (Node<T> ascendingNode : ascendingNodes) {
			ArrayList<Edge<T>> outputEdges = ascendingNode.getOutputEdges();
			ArrayList<Node<T>> nodesToRemove = new ArrayList<Node<T>>(
					outputEdges.size());
			for (Edge<T> edge : outputEdges) {
				nodesToRemove.add(edge.getEndNode());
			}
			for (Node<T> node2 : nodesToRemove) {
				ascendingNode.removeEdge(node2);
			}

			nodes.remove(ascendingNode);
			rootNodes.remove(ascendingNode);
		}
		nodes.remove(node);
		rootNodes.remove(node);

		for (Node<T> remainedNode : nodes) {
			if (remainedNode.getInputEdges().isEmpty()
					&& rootNodes.contains(remainedNode) == false)
				rootNodes.add(remainedNode);
		}
		return true;
	}

	public Node<T> findNode(T object) {
		for (Node<T> node : nodes) {
			if (node.getObject() == null && object == null)
				return null;

			if (node.getObject() != null && node.getObject().equals(object))
				return node;
		}
		return null;
	}

	private void dfs(Node<T> node, List<Node<T>> list) {
		if (node == null)
			return;
		for (Edge<T> edge : node.getOutputEdges()) {
			if (edge.getEndNode().isVisited() == false)
				dfs(edge.getEndNode(), list);
		}
		if (node.isRoot() == false) {
			list.add(node);
			node.setVisited(true);
		}
	}

	private void resetVisitingStatus() {
		for (Node<T> node : nodes) {
			node.setVisited(false);
		}
	}

	public ArrayList<Node<T>> getNodes() {
		return nodes;
	}

	public ArrayList<Node<T>> getRootNodes() {
		return rootNodes;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[Graph]\n");
		for (Node<T> node : nodes) {
			if (node.getOutputEdges().isEmpty() && rootNodes.contains(node))
				stringBuilder.append("\t").append(node).append("\n");
			for (Edge<T> edge : node.getOutputEdges()) {
				stringBuilder.append("\t").append(node).append(" -- > ")
						.append(edge.getEndNode()).append("\n");
			}
		}
		return stringBuilder.toString();
	}


}
