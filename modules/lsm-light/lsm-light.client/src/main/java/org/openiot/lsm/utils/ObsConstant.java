package org.openiot.lsm.utils;
/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
import java.util.ArrayList;
import java.util.HashMap;

public class ObsConstant {
	public final static String sensormasherMetadataGraphURI = "http://lsm.deri.ie/OpenIoT/sensormeta#";
	public final static String sensormasherDataGraphURI = "http://lsm.deri.ie/OpenIoT/sensordata#";
	
	public final static String sensormasherOntologyURI = "http://lsm.deri.ie/ont/lsm.owl#";
	public final static String SSNOntolotyURI ="http://purl.oclc.org/NET/ssnx/ssn#";
	

	
//	public static String TEMPERATURE = "http://lsm.deri.ie/resource/5395423154665";
//	public static String HUMIDITY = "http://lsm.deri.ie/resource/5395341713068";
	
	public static String TEMPERATURE = "http://lsm.deri.ie/ont/lsm.owl#AirTemperature";
	public static String HUMIDITY = "http://lsm.deri.ie/ont/lsm.owl#AtmosphereHumidity";
	public static String WINDCHILL = "http://lsm.deri.ie/ont/lsm.owl#WindChill";
	public static String WINDSPEED = "http://lsm.deri.ie/ont/lsm.owl#WindSpeed";
	public static String PRESSURE = "http://lsm.deri.ie/ont/lsm.owl#AtmospherePressure";
	public static String VISIBILITY = "http://lsm.deri.ie/ont/lsm.owl#AtmosphereVisibility";
	public static String STATUS = "http://lsm.deri.ie/ont/lsm.owl#Status";
	public static String CAMERA_IMAGE = "http://lsm.deri.ie/ont/lsm.owl#WebcamSnapShot";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
