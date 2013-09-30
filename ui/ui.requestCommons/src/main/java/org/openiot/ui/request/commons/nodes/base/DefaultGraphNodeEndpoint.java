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

package org.openiot.ui.request.commons.nodes.base;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphNodeEndpoint implements GraphNodeEndpoint, Serializable {
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

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    public AnchorType getAnchor() {
        return anchor;
    }

    public void setAnchor(AnchorType type) {
        this.anchor = type;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(ConnectorType type) {
        this.connectorType = type;
    }
    
    public boolean isVisible(){
    	return isVisible;
    }
    
	public void setVisible( boolean visible ){
		this.isVisible = visible;
	}

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
    
    public String getUserData(){
        return userData;
    }

    public void setUserData(String data){
        this.userData = data;
    }

    public JSONObject toJSON(){
		JSONObject spec = new JSONObject();
		try{
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
			
		}catch(JSONException ex){
			LoggerService.log(ex);
		}
		return spec;
	}
    
    public void importJSON(JSONObject spec) throws JSONException {
		setUID(spec.getString("uid"));
		setType( EndpointType.valueOf(spec.getString("type")));
		setAnchor( AnchorType.valueOf(spec.getString("anchor")));
		setConnectorType( ConnectorType.valueOf(spec.getString("connectorType")));
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
