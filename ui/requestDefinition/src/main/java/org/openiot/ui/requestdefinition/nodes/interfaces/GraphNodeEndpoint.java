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
package org.openiot.ui.requestdefinition.nodes.interfaces;

import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.ConnectorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;

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
    public void setConnectorType( ConnectorType type );

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
     * Set user data
     */
    public Object getUserData();

    public void setUserData(Object data);


    /** Return a copy of this endpoint */
    public GraphNodeEndpoint getCopy();
}
