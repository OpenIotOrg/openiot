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
package org.openiot.ui.request.definition.web.model;

import java.io.Serializable;

import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class EditablePropertyField implements Serializable{
	private static final long serialVersionUID = 1L;
	private String valueKey;
    private boolean isRequired;
    private boolean isEditable;
    private boolean isVariablizable;
    private String[] allowedValues;
    private GraphNodeProperty property;
    private String controlType;

    public EditablePropertyField(String nodeType, GraphNodeProperty property) {
    	
    	this.controlType = property.getAllowedValues() != null ? "StringList" : property.getJavaType().getSimpleName();
    	this.property = property;
        this.valueKey = property.getName();
        this.isRequired = property.isRequired();
        this.isEditable = property.getType().equals(PropertyType.Writable);
        this.isVariablizable = this.isEditable && ! "SINK".equals(nodeType);
        this.allowedValues = property.getAllowedValues();
    }
    
    
    public String getControlType() {
		return controlType;
	}

	public GraphNodeProperty getProperty() {
		return property;
	}

	public String getValueKey() {
        return valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(String[] allowedValues) {
        this.allowedValues = allowedValues;
    }

	public boolean isVariablizable() {
		return isVariablizable;
	}

	public void setVariablizable(boolean isVariablizable) {
		this.isVariablizable = isVariablizable;
	}
}
