package org.openiot.gsn.http.datarequest;

import java.util.Hashtable;

public class AbstractCriterion {
	
	protected static final String GENERAL_ERROR_MSG 	= "Failed to create the Criteria";
	protected static final String CRITERION_ERROR_MSG 	= "Invalid Criterion";
	
	public String getCriterion (String criterion, Hashtable<String, String> allowedValues) throws DataRequestException {
		if (allowedValues.containsKey(criterion.toLowerCase())) {
			return allowedValues.get(criterion.toLowerCase());
		}
		else throw new DataRequestException (CRITERION_ERROR_MSG + " >" + criterion + "<. Valid values are >" + allowedValues.keySet().toString() + "<") ;
	}
}
