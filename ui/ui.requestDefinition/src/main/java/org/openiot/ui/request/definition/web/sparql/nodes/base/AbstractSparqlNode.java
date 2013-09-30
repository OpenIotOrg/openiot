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
package org.openiot.ui.request.definition.web.sparql.nodes.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractSparqlNode implements Serializable{
	public static int DEPTH_SPACES = 2;
	public static String GRAPH_META_URI = "<http://lsm.deri.ie/OpenIoT/sensormeta#>";
	public static String GRAPH_DATA_URI = "<http://lsm.deri.ie/OpenIoT/sensordata#>";
	
	private static final long serialVersionUID = 1L;
	private List<AbstractSparqlNode> scopedItems;
	private int depth = 0;
	
	public AbstractSparqlNode() {
		this.scopedItems = new ArrayList<AbstractSparqlNode>();
	}
	
	protected boolean existsInScope( AbstractSparqlNode node ){
		for( AbstractSparqlNode child : scopedItems ){
			if( child.equals(node) ){
				return true;
			}
		}
		return false;
	}
	
	public AbstractSparqlNode appendToScope( AbstractSparqlNode node ){
		if( existsInScope(node) ){
			return node;
		}
		this.scopedItems.add(node);
		node.setDepth(depth+1);
		return node;
	}
	
	public AbstractSparqlNode prependToScope( AbstractSparqlNode node ){
		if( existsInScope(node) ){
			return node;
		}
		this.scopedItems.add(0, node);
		node.setDepth(depth+1);
		return node;
	}
	
	public void setDepth( int depth ){
		this.depth = depth;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public void removeFromScope(AbstractSparqlNode node){
		this.scopedItems.remove(node);
	}	
	
	public int getChildrenCount(){
		return this.scopedItems.size();
	}
	
	public List<String> generateChildren(){
		List<String> out = new ArrayList<String>(scopedItems.size());
		for( AbstractSparqlNode child : scopedItems ){
			out.add( child.generate() );
		}		
		return out;
	}	
	
	public String generatePad( int padDepth ){
		return StringUtils.leftPad("", DEPTH_SPACES * padDepth);
	}
	
	public abstract String generate();
}
