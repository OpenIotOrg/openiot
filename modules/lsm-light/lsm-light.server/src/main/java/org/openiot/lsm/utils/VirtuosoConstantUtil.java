package org.openiot.lsm.utils;
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
