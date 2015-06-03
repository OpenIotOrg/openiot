/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.ui.request.commons.nodes.base;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphNodeEndpoint implements GraphNodeEndpoint, Serializable {

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGraphNodeEndpoint.class);
	private static final long serialVersionUID = 1L;

	private String UID = "graphNodeEndpoint_" + System.nanoTime();
	private EndpointType type;
	private AnchorType anchor;
	private ConnectorType connectorType;
	private int maxConnections;
	private String label;
	private String scope;
	private boolean isRequired;
	private String userData;
	private boolean isVisible;

	public DefaultGraphNodeEndpoint() {
		this.isVisible = true;
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public void setUID(String UID) {
		this.UID = UID;
	}

	@Override
	public EndpointType getType() {
		return type;
	}

	@Override
	public void setType(EndpointType type) {
		this.type = type;
	}

	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getScope() {
		return this.scope;
	}

	@Override
	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	@Override
	public void setRequired(boolean required) {
		this.isRequired = required;
	}

	@Override
	public AnchorType getAnchor() {
		return anchor;
	}

	@Override
	public void setAnchor(AnchorType type) {
		this.anchor = type;
	}

	@Override
	public ConnectorType getConnectorType() {
		return connectorType;
	}

	@Override
	public void setConnectorType(ConnectorType type) {
		this.connectorType = type;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public GraphNodeEndpoint getCopy() {
		GraphNodeEndpoint copy = new DefaultGraphNodeEndpoint();
		copy.setAnchor(anchor);
		copy.setConnectorType(connectorType);
		copy.setScope(scope);
		copy.setLabel(label);
		copy.setMaxConnections(maxConnections);
		copy.setRequired(isRequired);
		copy.setType(type);
		copy.setUserData(userData);
		copy.setVisible(isVisible);

		return copy;
	}

	@Override
	public String getUserData() {
		return userData;
	}

	@Override

	public void setUserData(String data) {
		this.userData = data;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject spec = new JSONObject();
		try {
			spec.put("class", this.getClass().getCanonicalName());
			spec.put("uid", getUID());
			spec.put("type", getType().toString());
			spec.put("anchor", getAnchor().toString());
			spec.put("connectorType", getConnectorType().toString());
			spec.put("maxConnections", maxConnections);
			spec.put("label", label);
			spec.put("scope", scope);
			spec.put("isRequired", isRequired);
			spec.put("userData", userData);
			spec.put("isVisible", isVisible);

		} catch (JSONException ex) {
			LOGGER.error("", ex);
		}
		return spec;
	}

	@Override
	public void importJSON(JSONObject spec) throws JSONException {
		setUID(spec.getString("uid"));
		setType(EndpointType.valueOf(spec.getString("type")));
		setAnchor(AnchorType.valueOf(spec.getString("anchor")));
		setConnectorType(ConnectorType.valueOf(spec.getString("connectorType")));
		setMaxConnections(spec.getInt("maxConnections"));
		setLabel(spec.getString("label"));
		setScope(spec.getString("scope"));
		setRequired(spec.getBoolean("isRequired"));
		setUserData(spec.optString("userData"));
		setVisible(spec.optBoolean("isVisible", true));
	}

	@Override
	public String toString() {
		return "[type: " + getType() + ", anchor: " + anchor + ", label: " + getLabel() + ", javaType: " + getScope() + ", required: " + isRequired() + ", visible: " + isVisible + "]";
	}
}
