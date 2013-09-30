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

package org.openiot.ui.request.commons.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.logging.LoggerService;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class GraphNodePosition implements Serializable {
	private static final long serialVersionUID = 1L;

    double x;
    double y;

    public GraphNodePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public JSONObject toJSON(){
    	JSONObject spec = new JSONObject();
    	try{
    		spec.put("x", x);
    		spec.put("y", y);
    	}catch(JSONException ex){
    		LoggerService.log(ex);
    	}
    	return spec;
    }
    
    public void importJSON(JSONObject spec) throws JSONException{
    	x = spec.getDouble("x");
    	y = spec.getDouble("y");
    }
}
