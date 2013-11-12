package org.openiot.gsn.dynamicSensorControl;

import org.apache.log4j.PropertyConfigurator;

public class DynamicControlTaskTest {

	public static void main(String[] args) {

		PropertyConfigurator.configure("conf/log4j.properties");
		DynamicControlTask c = new DynamicControlTask();
		
		c.run();

	}

}
