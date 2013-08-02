package org.openiot.gsn.utils.protocols;

import java.util.Vector;

public class BasicHCIQuery extends AbstractHCIQueryWithoutAnswer {

	public static final String DEFAULT_NAME="CUSTOM_QUERY";
	public static final String DESCRIPTION="A custom raw query: you enter bytes as a parameters and it sends bytes to the controller.";
	public static final String[] PARAMS_DESCRIPTION= {"Bytes to send to the controller."};
	/**
	 * You can change the default texts here.
	 */
	public BasicHCIQuery(String Name, String queryDescription, String[] paramsDescriptions) {
		super(Name, queryDescription, paramsDescriptions);

	}

	public BasicHCIQuery() {
		super(DEFAULT_NAME, DESCRIPTION, PARAMS_DESCRIPTION);
	}
	/* (non-Javadoc)
	 * @see org.openiot.gsn.utils.protocols.AbstractHCIQuery#buildRawQuery(java.util.Vector)
	 */
	@Override
	public byte[] buildRawQuery(Vector<Object> params) {
		byte[] rawQuery = null;
		if(params != null && params.firstElement() != null) {
			rawQuery = params.firstElement().toString().getBytes();
		} 
		return rawQuery; 
	}

}
