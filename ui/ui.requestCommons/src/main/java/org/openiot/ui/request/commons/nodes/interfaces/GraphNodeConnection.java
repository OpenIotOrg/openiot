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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public interface GraphNodeConnection {

	/**
	 * A unique identifier for the node
	 */
	public String getUID();

	public void setUID(String UID);

	/**
	 * Source node
	 */
	public GraphNode getSourceNode();
	public void setSourceNode(GraphNode node);

	/**
	 * Source endpoint
	 */
	public GraphNodeEndpoint getSourceEndpoint();
	public void setSourceEndpoint(GraphNodeEndpoint endpoint);

	/**
	 * Destination node
	 */
	public GraphNode getDestinationNode();
	public void setDestinationNode(GraphNode node);

	/**
	 * Destination endpoint
	 */
	public GraphNodeEndpoint getDestinationEndpoint();
	public void setDestinationEndpoint(GraphNodeEndpoint endpoint);

	/**
	 * Convert to JSON
	 */
	public JSONObject toJSON();

	/**
	 * Import data from JSON object
	 */
	public void importJSON(JSONObject spec) throws JSONException;
}
