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
package org.openiot.ui.requestdefinition.nodes.impl.comparators;

import java.io.Serializable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.openiot.ui.requestdefinition.annotations.Endpoint;
import org.openiot.ui.requestdefinition.annotations.Endpoints;
import org.openiot.ui.requestdefinition.annotations.GraphNodeClass;
import org.openiot.ui.requestdefinition.annotations.NodeProperties;
import org.openiot.ui.requestdefinition.annotations.NodeProperty;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNode;
import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;
import org.openiot.ui.requestdefinition.nodes.enums.PropertyType;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Compare", type = "COMPARATOR", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "Compare.Number", label = "IN", required = true),
})
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "OPERATOR", allowedValues = {"=", "<", "<=", ">", ">="}, required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "CMP_VALUE", required = true)
})
public class Compare extends DefaultGraphNode implements Serializable, Observer {
	private static final long serialVersionUID = 1L;

    public Compare() {
        super();

        // Listen for property change events
        addPropertyChangeObserver(this);
    }

    public void update(Observable o, Object arg) {
        // Mutate our label
        Map<String, Object> propertyMap = getPropertyValueMap();
        if (propertyMap.get("OPERATOR") != null && propertyMap.get("CMP_VALUE") != null) {
            setLabel(propertyMap.get("OPERATOR") + "<br/>" + propertyMap.get("CMP_VALUE"));
        } else {
            setLabel("Compare");
        }
    }
}
