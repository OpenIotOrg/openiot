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
