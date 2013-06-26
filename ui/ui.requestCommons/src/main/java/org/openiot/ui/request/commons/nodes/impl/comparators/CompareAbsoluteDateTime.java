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
package org.openiot.ui.request.commons.nodes.impl.comparators;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "CompareAbsoluteDateTime", type = "COMPARATOR", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "Compare.Date", label = "IN", required = true),
})
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "OPERATOR", allowedValues = {"<", "<=", ">", ">="}, required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.util.Date.class, name = "CMP_VALUE", required = true)
})
public class CompareAbsoluteDateTime extends DefaultGraphNode implements Serializable, Observer {
	private static final long serialVersionUID = 1L;

    public CompareAbsoluteDateTime() {
        super();

        // Listen for property change events
        addPropertyChangeObserver(this);
    }

    public void update(Observable o, Object arg) {
        // Mutate our label
        Map<String, Object> propertyMap = getPropertyValueMap();
        if (propertyMap.get("OPERATOR") != null && propertyMap.get("CMP_VALUE") != null) {
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            setLabel(propertyMap.get("OPERATOR") + "<br/>" + sdf.format(propertyMap.get("CMP_VALUE")));
        } else {
            setLabel("CompareAbsoluteDateTime");
        }
    }
}
