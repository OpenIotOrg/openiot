package org.openiot.gsn.vsensor;

import java.io.Serializable;
import java.util.TreeMap;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import org.apache.log4j.Logger;

public class SMACleaner extends AbstractVirtualSensor {
	
	static int index = 0;
	static double values[] ;
	static private double error_threshold;
	
	private static final transient Logger logger = Logger.getLogger(SensorscopeVS.class);
	
	public void dataAvailable(String inputStreamName,StreamElement in) {
		Double input = (Double) in.getData()[0];
		
		if (index>=values.length) {
			double sum = 0;
			for (double v:values)
				sum+=v;
			double sma = sum/values.length;
			
			StreamElement se ;
			boolean isAcceptable =  (Math.abs(input - sma)/input <= error_threshold );
			se= new StreamElement(
					new DataField[] {new DataField("raw_value","double" ), new DataField("acceptable","integer")},
					new Serializable[] {input,(isAcceptable == false ? 0 : 1)},
					in.getTimeStamp());
			dataProduced(se);
		}
		values[index++%values.length]= input;
	}

	public void dispose() {
		
	}

	public boolean initialize() {
		TreeMap <  String , String > params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );
		int size = Integer.parseInt(params.get("size"));
		error_threshold = Double.parseDouble(params.get("error-threshold"));
		values = new double[size];
		return true;
	}
}
