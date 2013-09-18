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
package org.openiot.ui.request.definition.web.model.nodes.impl.sinks;

import java.io.Serializable;

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
@GraphNodeClass(label = "Map", type = "SINK", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "agr_Number agr_Integer agr_Long, agr_Float agr_Double", label = "VALUE", required = true),
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "geo_lat", label = "LAT", required = true),
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "geo_lon", label = "LON", required = true),
})
@NodeProperties({
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TITLE", required = true),
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.Double.class, name = "LAT", required = true),
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.Double.class, name = "LON", required = true),
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.Integer.class, name = "ZOOM", required = true),
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TYPE", required = true, allowedValues = {"Markers only", "Circles only", "Markers and Circles"}),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "SCALER", required = true)
})
public class Map extends DefaultGraphNode implements Serializable {
	private static final long serialVersionUID = 1L;
    
    public Map() {
        super();

        // Setup some defaults
        setProperty("TITLE", this.getClass().getSimpleName());
        setProperty("TYPE", "Markers only");
        setProperty("SCALER", 1.0);
    }
}
