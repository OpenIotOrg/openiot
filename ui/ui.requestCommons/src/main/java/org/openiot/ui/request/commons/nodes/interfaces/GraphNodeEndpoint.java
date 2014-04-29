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
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public interface GraphNodeEndpoint {

	/**
	 * A unique identifier for the node
	 */
	public String getUID();

	public void setUID(String UID);

	/**
	 * The type of endpoint
	 */
	public EndpointType getType();

	public void setType(EndpointType type);

	/**
	 * Endpoint anchor point
	 */
	public AnchorType getAnchor();

	public void setAnchor(AnchorType type);

	/**
	 * Endpoint connector type
	 */
	public ConnectorType getConnectorType();

	public void setConnectorType(ConnectorType type);

	/**
	 * Endpoint max number of connections
	 */
	public int getMaxConnections();

	public void setMaxConnections(int maxConnections);

	/**
	 * The endpoint label
	 */
	public String getLabel();

	public void setLabel(String label);

	/**
	 * The type of objects that can be connected to this endpoint
	 */
	public String getScope();

	public void setScope(String scope);

	/**
	 * Flag whether this field has to be connected
	 */
	public boolean isRequired();

	public void setRequired(boolean required);

	/** 
	 * Endpoint visibility
	 */
	public boolean isVisible();
	public void setVisible( boolean visible );
	
	/**
	 * Set user data
	 */
	public String getUserData();

	public void setUserData(String data);

	/** Return a copy of this endpoint */
	public GraphNodeEndpoint getCopy();

	/**
	 * Convert to JSON
	 */
	public JSONObject toJSON();

	/**
	 * Import data from JSON object
	 */
	public void importJSON(JSONObject spec) throws JSONException;

}
