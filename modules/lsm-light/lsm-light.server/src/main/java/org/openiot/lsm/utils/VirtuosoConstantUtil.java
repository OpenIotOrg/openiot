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
	
	public final static String sensormasherDataQuadURI = "<http://lsm.deri.ie/data/quad_storage/default>";
	public final static String sensormasherOntologyURI = "http://lsm.deri.ie/ont/lsm.owl#";
	public final static String SSNOntolotyURI ="http://purl.oclc.org/NET/ssnx/ssn#";
	
	public final static String sensorQuadStorageURI = "<http://lsm.deri.ie/sensor/quad_storage/default>"; 
	public final static String placeQuadStorageURI = "<http://lsm.deri.ie/place/quad_storage/default>";
	public final static String placeQuadGraphURI = "http://lsm.deri.ie/resource/place";
	
	public final static String sensorObservesReadingPrefix="http://purl.oclc.org/NET/ssnx/ssn#observes";
	public final static String sensorObjectDataPrefix ="http://lsm.deri.ie/resource/";
	public final static String sensorHasObservationPrefix = "http://lsm.deri.ie/ont/lsm.owl#hasObservation";
	public final static String sensorAddbyUserPrefix = "http://lsm.deri.ie/ont/lsm.owl#isAddedBy";
	public final static String sensorHasPlacePrefix = "http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation";
	public final static String sensorHasWrapperPrefix = "http://lsm.deri.ie/ont/lsm.owl#hasWrapper";
	
	public final static String placeObjectDataPrefix ="http://lsm.deri.ie/resource/";
	
	public final static String observationObjectDataPrefix="http://lsm.deri.ie/data#";	
	public final static String observationIsObservedBySensorPrefix = "http://purl.oclc.org/NET/ssnx/ssn#observedBy";
	public final static String observationHasObservationResult = "http://purl.oclc.org/NET/ssnx/ssn#observationResult";
	
	public final static String dataIsPropertyOfObservationPrefix = "http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf";	
	public final static String dataIsPropertyOfFeatureOfInterestPrefix="http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf";
	
	public static HashMap<String,String> sensorType2Datasource = new HashMap<String, String>();
	public static HashMap<String,String> sensorType2className = new HashMap<String, String>();
	public static ArrayList<String> authorizedGraphs = new ArrayList<String>();
	
	public final static String linkedgeodataGraphURI = "http://lsm.deri.ie/linkedgeodata.com";
	public final static String lnkedgeodataSameAsPrefix ="http://www.w3.org/2002/07/owl#sameAs";
	public final static String sensorHasNearestLocation = "http://lsm.deri.ie/ont/lsm.owl#nearest";
	
	public final static String provenanceCreateby = "http://purl.org/net/provenance/ns#CreatedBy";
	public final static String provenancePerformedAt = "http://purl.org/net/provenance/ns#PerformedAt";
	public final static String provenancePerformedBy = "http://purl.org/net/provenance/ns#PerformedBy";
	
	public final static String FeatureOfInterest = "http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest";
	public final static String ArrivingTrainAtStation = "http://lsm.deri.ie/ont/lsm.owl#ArrivingTrainAtStation";
	public final static String atStation = "http://lsm.deri.ie/ont/lsm.owl#atStation";
	public final static String RailwayStation = "http://lsm.deri.ie/ont/lsm.owl#RailwayStation";
	
	static{
		initAuthorizedGraph();
	}

	
	
	private static void initilalize_sensorType2className(){
		sensorType2className.put("weather","Weather");		
		sensorType2className.put("webcam", "linkvalue");
		sensorType2className.put("satellite", "WebcamSnapShot");
		sensorType2className.put("radar", "WebcamSnapShot");
		sensorType2className.put("flood", "WebcamSnapShot");
		sensorType2className.put("snowfall", "SnowFall");
		sensorType2className.put("snowdepth", "SnowDepth");
		sensorType2className.put("traffic", "WebcamSnapShot");
		sensorType2className.put("roadactivity", "RoadActivity");
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
