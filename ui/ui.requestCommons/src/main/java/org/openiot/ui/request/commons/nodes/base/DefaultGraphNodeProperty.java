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
package org.openiot.ui.request.commons.nodes.base;

import java.io.Serializable;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class DefaultGraphNodeProperty implements GraphNodeProperty,
		Serializable {
	private static final long serialVersionUID = 1L;

	private PropertyType type;
	private String name;
	private Class<?> javaType;
	private boolean isRequired;
	private String[] allowedValues;

	public DefaultGraphNodeProperty() {
	}

	public PropertyType getType() {
		return type;
	}

	public void setType(PropertyType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getJavaType() {
		return this.javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public String[] getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String[] allowedValues) {
		this.allowedValues = allowedValues;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean required) {
		this.isRequired = required;
	}

	public GraphNodeProperty getCopy() {
		DefaultGraphNodeProperty copy = new DefaultGraphNodeProperty();
		copy.setAllowedValues(allowedValues);
		copy.setJavaType(javaType);
		copy.setName(name);
		copy.setRequired(isRequired);
		copy.setType(type);

		return copy;
	}

	public JSONObject toJSON() {
		JSONObject spec = new JSONObject();
		try {
			spec.put("class", this.getClass().getCanonicalName());
			spec.put("type", getType().toString());
			spec.put("name", getName());
			spec.put("javaClass", javaType.getCanonicalName());
			spec.put("isRequired", isRequired());
			if (allowedValues != null) {
				JSONArray values = new JSONArray();
				values.put(Arrays.asList(allowedValues));
				spec.put("allowedValues", values);
			}
		} catch (JSONException ex) {
			LoggerService.log(ex);
		}
		return spec;
	}

	@Override
	public String toString() {
		return "[type: " + getType() + ", name: " + getName() + ", javaType: "
				+ getJavaType() + ", required: " + isRequired() + "]";
	}
}
