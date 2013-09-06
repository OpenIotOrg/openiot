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

public class VirtuosoConstantUtil {
	public final static String sensormasherMetadataGraphURI = "http://lsm.deri.ie/OpenIoT/sensormeta#";
	public final static String sensormasherDataGraphURI = "http://lsm.deri.ie/OpenIoT/sensordata#";

	public final static String sensorObjectDataPrefix ="http://lsm.deri.ie/resource/";	
	public final static String sensorHasPlacePrefix = "http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation";
	public final static String observationIsObservedBySensorPrefix = "http://purl.oclc.org/NET/ssnx/ssn#observedBy";
	
	public static ArrayList<String> authorizedGraphs = new ArrayList<String>();
	
	public final static String linkedgeodataGraphURI = "http://lsm.deri.ie/linkedgeodata.com";
	public final static String lnkedgeodataSameAsPrefix ="http://www.w3.org/2002/07/owl#sameAs";
	public final static String sensorHasNearestLocation = "http://lsm.deri.ie/ont/lsm.owl#nearest";		
	public final static String FeatureOfInterest = "http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest";
	
	static{
		initAuthorizedGraph();
	}
	
	private static void initAuthorizedGraph() {
		// TODO Auto-generated method stub
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/sensordata#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/sensormeta#");
		authorizedGraphs.add("http://lsm.deri.ie/cisco/eventdata#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/testSchema#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/demo/sensordata#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/demo/sensormeta#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/test/sensordata#");
		authorizedGraphs.add("http://lsm.deri.ie/OpenIoT/test/sensormeta#");
	}
	
}
