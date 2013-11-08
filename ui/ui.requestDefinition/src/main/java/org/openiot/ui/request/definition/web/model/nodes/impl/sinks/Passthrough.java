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
import java.util.Observable;
import java.util.Observer;

import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Pasthrough", type = "SINK", scanProperties = true)
@NodeProperties({ @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TITLE", required = false), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Integer.class, name = "ATTRIBUTES", required = true), })
public class Passthrough extends DefaultGraphNode implements Serializable, Observer {
	private static final long serialVersionUID = 1L;

	public Passthrough() {
		super();

		// Setup some defaults
		setProperty("ATTRIBUTES", 1);

		addPropertyChangeObserver(this);
		update(null, "ATTRIBUTES");
	}

	public void update(Observable o, Object key) {
		if (key != null && "ATTRIBUTES".equals(key)) {
			int attributeCount = (Integer) getPropertyValueMap().get("ATTRIBUTES");
			int i = 0;
			for (; i < attributeCount; i++) {
				// If we are missing the required endpoints and properties
				// create them now
				String epLabel = "attr" + (i + 1);
				GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
				if (ep == null) {
					ep = new DefaultGraphNodeEndpoint();
					ep.setType(EndpointType.Input);
					ep.setAnchor(AnchorType.Left);
					ep.setConnectorType(ConnectorType.Rectangle);
					ep.setScope("*");
					ep.setLabel(epLabel);
					ep.setRequired(true);
					getEndpointDefinitions().add(ep);
				}
			}

			// If we reduced the number of series, get rid of the old series
			for (;; i++) {
				String epLabel = "attr" + (i + 1);
				GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
				if (ep == null) {
					break;
				}
				if (ep != null) {
					disconnectEndpoint(ep);
					getEndpointDefinitions().remove(ep);
				}
			}
		}
	}
}
