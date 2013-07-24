/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.request.commons.nodes.interfaces;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.interfaces.GraphModel;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public interface GraphNode {

	/**
	 * Set a reference to the graph model
	 */
	public void setGraphModel(GraphModel model);

	public GraphModel getGraphModel();

	/**
	 * A unique identifier for the node
	 */
	public String getUID();

	public void setUID(String UID);

	/**
	 * Node type
	 */
	public String getType();

	/**
	 * Node label
	 */
	public String getLabel();

	/**
	 * Node property definition list
	 */
	public List<GraphNodeProperty> getPropertyDefinitions();

	public void setPropertyDefinitions(List<GraphNodeProperty> propertyDefinitions);

	public GraphNodeProperty getPropertyByName(String name);

	/**
	 * Node property value map
	 */
	public Map<String, Object> getPropertyValueMap();

	/**
	 * Node endpoint definition list
	 */
	public List<GraphNodeEndpoint> getEndpointDefinitions();

	public void setEndpointDefinitions(List<GraphNodeEndpoint> endpointDefinitions);

	public GraphNodeEndpoint getEndpointByLabel(String label);

	/**
	 * Return a copy of this node
	 */
	public GraphNode getCopy();

	/**
	 * Generate a JSON object describing the node
	 */
	public JSONObject toJSON();

	/**
	 * Import data from JSON object
	 */
	public void importJSON(JSONObject spec) throws JSONException;

}
