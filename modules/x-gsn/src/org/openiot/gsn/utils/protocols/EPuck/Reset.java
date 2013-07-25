package org.openiot.gsn.utils.protocols.EPuck;

import org.openiot.gsn.utils.protocols.AbstractHCIQueryWithoutAnswer;

import java.util.Vector;

public class Reset extends AbstractHCIQueryWithoutAnswer {

	public static final String queryDescription = "Resets the state of the EPuck robot.";
	public static final String[] paramsDescriptions = null;
	public Reset (String name) {
		super(name, queryDescription, paramsDescriptions);
	}


	/*
	 * This query does not take any parameters.
	 * If you provide any, these will be ignored.
	 */
	public byte [ ] buildRawQuery ( Vector < Object > params ) {
		byte[] query = new byte[1];
		query[0] = 'r';
		return query;
	}
}
