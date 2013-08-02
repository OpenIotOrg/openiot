package org.openiot.gsn.utils.protocols.EPuck;

import org.openiot.gsn.utils.protocols.AbstractHCIQueryWithoutAnswer;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class BodyLED extends AbstractHCIQueryWithoutAnswer {

	public static final String queryDescription = "Switches the body LED on and off.";
	public static final String[] paramsDescriptions = null;
	
	public enum LED_STATE {OFF, ON, INVERSE};
	/**
	 * @param Name
	 * @param queryDescription
	 * @param paramsDescriptions
	 */
	public BodyLED(String Name) {
		super(Name, queryDescription, paramsDescriptions);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.openiot.gsn.utils.protocols.AbstractHCIQuery#buildRawQuery(java.util.Vector)
	 */
	@Override
	public byte[] buildRawQuery(Vector<Object> params) {
		byte[] answer = null;
		if(params.firstElement() != null && params.firstElement() instanceof Integer) {
			answer = new byte[3];
			answer[0] = 'B';
			answer[1] = ',';
			try {
				answer[2] = ((Integer) params.firstElement()).toString().getBytes("UTF-8")[0];
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return answer;
	}

}
