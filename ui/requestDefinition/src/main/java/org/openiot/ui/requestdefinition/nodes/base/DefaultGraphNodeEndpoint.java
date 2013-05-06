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
package org.openiot.ui.requestdefinition.nodes.base;

import java.io.Serializable;
import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.ConnectorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;

/**
 *
 * @author aana
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
    private Object userData;

    public DefaultGraphNodeEndpoint() {
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
        
        return copy;
    }
    
    public Object getUserData(){
        return userData;
    }

    public void setUserData(Object data){
        this.userData = data;
    }

    @Override
    public String toString() {
        return "[type: " + getType() + ", anchor: " + anchor + ", label: " + getLabel() + ", javaType: " + getScope() + ", required: " + isRequired() + "]";
    }
}
