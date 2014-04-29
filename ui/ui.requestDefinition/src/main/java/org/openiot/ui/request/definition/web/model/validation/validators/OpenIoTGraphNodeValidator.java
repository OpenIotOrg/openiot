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

package org.openiot.ui.request.definition.web.model.validation.validators;

import java.util.List;

import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.definition.web.model.nodes.impl.filters.Group;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationError;
import org.openiot.ui.request.definition.web.model.validation.GraphValidationError.ErrorType;

/**
 *
 * @author archie
 */
public class OpenIoTGraphNodeValidator extends DefaultGraphNodeValidator {

    public OpenIoTGraphNodeValidator(GraphModel model) {
        super(model);
    }

    //-------------------------------------------------------------------------
    // Visitors
    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public void visit(Group node) {
        if (visitedNodes.contains(node)) {
        	defaultVisit(node);
        	return;
        }    	        
        defaultVisit(node);
        
        // Addditional validation logic
        List<String> groupList = (List<String>) node.getPropertyValueMap().get("GROUPS");
        if( groupList == null || groupList.isEmpty() ){
        	validationErrors.add( new GraphValidationError(ErrorType.NoGroupsSpecified, node.getClass().getSimpleName(), "", node.getUID()));
        }
    }
}
