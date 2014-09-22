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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

@FacesValidator("custom.SimpleURLValidator")
public class SimpleURLValidator implements Validator, ClientValidator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		
		
		String text = String.valueOf(value);		
        boolean valid = true;                             
        
        if (!text.startsWith("http://")){
        	 valid = false;
        }
        else
        {
        	 try {
                 new URI(text);
              } catch (URISyntaxException e) {
            	  valid = false;
              }
        }
       
            
        if (!valid) {
            FacesMessage msg =
                    new FacesMessage("URL validation failed","Invalid Source URL format");
                 msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                 throw new ValidatorException(msg);
        }
		
	}

	@Override
	public Map<String, Object> getMetadata() {
		return null;
	}

	@Override
	public String getValidatorId() {
		// TODO Auto-generated method stub
		return "SimpleValidator";
	}
}
 

