package org.openiot.ui.sensorschema.utils;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

@FacesValidator("custom.SimpleValidator")
public class SimpleValidator implements Validator, ClientValidator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		
		
		String text = String.valueOf(value);
        boolean valid = true;
        
        if (value == null) {
            valid = false;
        } else if (text.contains("CLICK TO EDIT")) {
            valid = false;
        }
       
            
        if (!valid) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Invalid Value",
                    "Please update the value.");
            throw new ValidatorException(message);
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
 

