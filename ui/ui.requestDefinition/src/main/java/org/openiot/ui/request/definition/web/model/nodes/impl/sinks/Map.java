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
