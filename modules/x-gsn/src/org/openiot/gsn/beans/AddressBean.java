package org.openiot.gsn.beans;

import java.io.Serializable;

import org.apache.commons.collections.KeyValue;

public final class AddressBean implements Serializable{

	private static final long serialVersionUID = -8975180532136014200L;

	private static final   KeyValue[] EMPTY_PREDICATES = new  KeyValue[0];

	private String                 wrapper;

	private  KeyValue[] predicates  = EMPTY_PREDICATES;
	
	private DataField [] wrapperOutputStructure = new DataField[0];

	private double random = Math.random();

	public AddressBean ( final String wrapper , KeyValue... newPredicates ) {
		this.wrapper = wrapper;
		if (newPredicates==null)
			this.predicates=EMPTY_PREDICATES;
		else
			this.predicates = newPredicates;
	}

	public AddressBean ( final String wrapper  ) {
		this.wrapper = wrapper;
		this.predicates=EMPTY_PREDICATES;
	}

	public String getWrapper ( ) {
		return this.wrapper;
	}

	public  KeyValue[] getPredicates ( ) {
		return this.predicates;
	}

	public String getPredicateValueWithException ( String key ) {
		key = key.trim( );
		for (  KeyValue predicate : this.predicates ) {
			if ( predicate.getKey( ).toString( ).trim( ).equalsIgnoreCase( key ) ) {
				final String value = ( String ) predicate.getValue( );
				if (value.trim().length()>0)
					return ( value);
			}
		}
		throw new RuntimeException("The required parameter: >"+key+"<+ is missing.from the virtual sensor configuration file.");
	}


	/**
	 * Note that the key for the value is case insensitive.
	 * 
	 * @param key
	 * @return
	 */

	public String getPredicateValue ( String key ) {
		key = key.trim( );
		for (  KeyValue predicate : this.predicates ) {
			//    logger.fatal(predicate.getKey()+" --- " +predicate.getValue());
			if ( predicate.getKey( ).toString( ).trim( ).equalsIgnoreCase( key ) ) return ( ( String ) predicate.getValue( ));
		}
		return null;
	}

	/**
	 * Gets a parameter name. If the parameter value exists and is not an empty string, returns the value otherwise returns the
	 * default value
	 * @param key The key to look for in the map.
	 * @param defaultValue Will be return if the key is not present or its an empty string.
	 * @return
	 */
	public String getPredicateValueWithDefault(String key, String defaultValue) {
		String value = getPredicateValue(key);
		if (value==null|| value.trim().length()==0)
			return defaultValue;
		else
			return value;
	}

	/**
	 * Gets a parameter name. If the parameter value exists and is a valid integer, returns the value otherwise returns the
	 * default value
	 * @param key The key to look for in the map.
	 * @param defaultValue Will be return if the key is not present or its value is not a valid integer.
	 * @return
	 */
	public int getPredicateValueAsInt(String key, int defaultValue) {
		String value = getPredicateValue(key);
		if (value==null|| value.trim().length()==0)
			return defaultValue;
		try { 
			return Integer.parseInt(value);
		}catch (Exception e) {
			return defaultValue;
		}
	}

	public int getPredicateValueAsIntWithException ( String key ) {
		String value = getPredicateValue(key);
		if (value==null|| value.trim().length()==0)
			throw new RuntimeException("The required parameter: >"+key+"<+ is missing.from the virtual sensor configuration file.");
		try { 
			return Integer.parseInt(value);
		}catch (Exception e) {
			throw new RuntimeException("The required parameter: >"+key+"<+ is bad formatted.from the virtual sensor configuration file.",e);
		}
	}



	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(random);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressBean other = (AddressBean) obj;
		if (Double.doubleToLongBits(random) != Double
				.doubleToLongBits(other.random))
			return false;
		return true;
	}

	public String toString ( ) {
		final StringBuffer result = new StringBuffer( "[" ).append( this.getWrapper( ) );
		for ( final KeyValue predicate : this.predicates ) {
			result.append( predicate.getKey( ) + " = " + predicate.getValue( ) + "," );
		}
		result.append( "]" );
		return result.toString( );
	}

	private String inputStreamName;
	private String virtualSensorName;

	public DataField[] getOutputStructure() {
		return wrapperOutputStructure;
	}

	public void setVsconfig(DataField[] outputStructure) {
		this.wrapperOutputStructure = outputStructure;
	}

	public String getInputStreamName() {
		return inputStreamName;
	}

	public void setInputStreamName(String inputStreamName) {
		this.inputStreamName = inputStreamName;
	}

	public String getVirtualSensorName() {
		return virtualSensorName;
	}

	public void setVirtualSensorName(String virtualSensorName) {
		this.virtualSensorName = virtualSensorName;
	}


}
