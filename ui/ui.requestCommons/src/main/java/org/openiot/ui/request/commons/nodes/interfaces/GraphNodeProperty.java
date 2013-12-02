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

package org.openiot.ui.request.commons.nodes.interfaces;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public interface GraphNodeProperty {
	
    /**
     * The type of property
     */
    public PropertyType getType();

    public void setType(PropertyType type);

    /**
     * Property label
     */
    public String getName();

    public void setName(String name);

	/**
	 * Variable property
	 */
	public boolean isVariable();
	public void setVariable( boolean isVariable );

    /**
     * Variable name
     */
    public String getVariableName();

    public void setVariableName(String name);
    
    /**
     * The property value type
     */
    public Class<?> getJavaType();

    public void setJavaType(Class<?> javaType);

    /**
     * Allowed values
     */
    public String[] getAllowedValues();

    public void setAllowedValues(String[] allowedValues);

    /**
     * Flag whether this field is required
     */
    public boolean isRequired();

    public void setRequired(boolean required);
    
    /**
     * Return a copy of the original property
     */
    public GraphNodeProperty getCopy();
    
    /**
     * Convert to JSON
     */
    public JSONObject toJSON();
    
    /**
     * Import data from JSON object
     */
    public void importJSON(JSONObject spec) throws JSONException;
}
