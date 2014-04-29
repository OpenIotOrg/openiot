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
