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
