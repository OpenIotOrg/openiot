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
import java.util.Observer;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Gauge", type = "SINK", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "agr_Number agr_Integer agr_Long, agr_Float agr_Double", label = "VALUE", required = true),
})
@NodeProperties({
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TITLE", required = true),
	@NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "UNIT", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "MIN", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "MAX", required = true)
})
public class Gauge extends DefaultGraphNode implements Serializable {
	private static final long serialVersionUID = 1L;
    
    public Gauge() {
        super();

        // Setup some defaults
        setProperty("TITLE", this.getClass().getSimpleName());
        setProperty("UNIT", "unit");
        setProperty("MIN", 0.0);
        setProperty("MAX", 100.0);
    }
}
