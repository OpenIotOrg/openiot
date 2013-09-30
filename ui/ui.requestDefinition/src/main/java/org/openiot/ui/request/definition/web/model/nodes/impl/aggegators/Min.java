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

package org.openiot.ui.request.definition.web.model.nodes.impl.aggegators;

import java.io.Serializable;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Min", type = "AGGREGATOR", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "Number Integer Long Double Float sensor_Number sensor_Integer sensor_Long sensor_Double sensor_Float grp_Number grp_Integer grp_Long grp_Double grp_Float", label = "IN", required = true),
    @Endpoint(type = EndpointType.Output, anchorType = AnchorType.Right, scope = "agr_Number", label = "OUT", required = true)
})
public class Min extends DefaultGraphNode implements Serializable {

	private static final long serialVersionUID = 1L;

	public Min() {
        super();
    }
}
