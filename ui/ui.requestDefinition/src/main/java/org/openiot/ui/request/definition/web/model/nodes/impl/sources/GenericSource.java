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

package org.openiot.ui.request.definition.web.model.nodes.impl.sources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.definition.web.util.EndpointListLabelOrderComparator;

/**
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "GenericSensor", type = "SOURCE", scanProperties = true, hideFromScanner = true)
@NodeProperties({ @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "LAT", required = true), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "LON", required = true), @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "RADIUS", required = true) })
public class GenericSource extends DefaultGraphNode implements Serializable {
	private static final long serialVersionUID = 1L;

	public void removeAllGroups() {
		Iterator<GraphNodeEndpoint> endpointIt = this.getEndpointDefinitions().iterator();
		while (endpointIt.hasNext()) {
			GraphNodeEndpoint endpoint = endpointIt.next();

			if (endpoint.getLabel().startsWith("grp_")) {
				disconnectEndpoint(endpoint);
				endpointIt.remove();
			}else if(endpoint.getScope().startsWith("sensor_")) {
				disconnectEndpoint(endpoint);
				endpoint.setScope(endpoint.getScope().replace("sensor_", ""));
			}
		}
	}

	public void addGroups(List<String> srcEndpointLabels) {
		// If an empty list is passed, remove all groups
		if( srcEndpointLabels == null || srcEndpointLabels.isEmpty() ){
			removeAllGroups();
			return;
		}
		
		// Operate on a copy of the input list
		List<String> newEndpointLabels = new ArrayList<String>(srcEndpointLabels);
		List<String> goneEndpointLabels = new ArrayList<String>();

		// Hide any visible node that is not an aggregate
		Iterator<GraphNodeEndpoint> endpointIt = this.getEndpointDefinitions().iterator();
		while (endpointIt.hasNext()) {
			GraphNodeEndpoint endpoint = endpointIt.next();
			if( endpoint.getScope().equals("Sensor")){
				continue;
			}

			// Existing group endpoint point
			if( endpoint.getLabel().startsWith("grp_")){
				String label = endpoint.getLabel().replace("grp_", "");
				if( newEndpointLabels.contains(label) ){
					newEndpointLabels.remove(label);
				}else{
					goneEndpointLabels.add(endpoint.getLabel());
				}
			} else if( !endpoint.getScope().startsWith("sensor_") ){
				disconnectEndpoint(endpoint);
				endpoint.setScope("sensor_" + endpoint.getScope());
			}
		}
		
		// Remove gone endpoints
		for( String goneLabel : goneEndpointLabels ){
			GraphNodeEndpoint ep = getEndpointByLabel(goneLabel);
			disconnectEndpoint(ep);
			getEndpointDefinitions().remove(ep);
		}

		// And new end point for each new group
		for (String srcEndpointLabel : newEndpointLabels) {
			GraphNodeEndpoint src = getEndpointByLabel(srcEndpointLabel.contains("recordTime") ? "recordTime" : srcEndpointLabel);
			
			GraphNodeEndpoint dst = new DefaultGraphNodeEndpoint();
			dst.setType(EndpointType.Output);
			dst.setAnchor(AnchorType.Right);
			dst.setConnectorType(ConnectorType.Dot);
			dst.setScope("grp_" + (srcEndpointLabel.contains("recordTime") ? "Number" : src.getScope()));
			dst.setLabel("grp_" + srcEndpointLabel);
			dst.setRequired(false);
			getEndpointDefinitions().add(dst);
		}

		// Finally sort all endpoints using the input list label indices
		Collections.sort(getEndpointDefinitions(), new EndpointListLabelOrderComparator(srcEndpointLabels));
	}
}
