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
