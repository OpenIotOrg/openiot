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
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MappingMap {
	public static HashMap<String,String> rdfViewMapSnowFall = new HashMap<String, String>();
		
	public static HashMap<String,String> rdfViewMapPlace = new HashMap<String, String>();
	public static HashMap<String,String> rdfViewMapSensor = new HashMap<String, String>();
	
	public static HashMap<String,String> rdfViewMapSeaLevelValue = new HashMap<String, String>();
	public static HashMap<String,String> rdfViewMapSeaLevel = new HashMap<String, String>();
		
	public static HashMap<String,String> rdfViewMapHasUnit = new HashMap<String, String>();
	public static HashMap<String,String> rdfViewMapNoUnit = new HashMap<String, String>();
	public static HashMap<String,String> rdfViewMapObservation = new HashMap<String, String>();
	
	public static LinkedHashMap<String,String> class2Table = new LinkedHashMap<String, String>();
	public static HashMap<String, HashMap<String,String>> class2MappingMap = new HashMap<String, HashMap<String,String>>();
	public static HashMap<String,String> table2ClassURI = new HashMap<String, String>();
	private String basic="";
	static{
		initialized();
		initializedClass2Table();
		initializedClass2MapingMap();
		initializedTable2ClassURI();
	}
	
	private static void initialized(){				
		
		rdfViewMapPlace.put("woeid", VirtuosoConstantUtil.sensormasherOntologyURI+"hasWoeid");
		rdfViewMapPlace.put("geonameid", VirtuosoConstantUtil.sensormasherOntologyURI+"hasGeonameid");
		rdfViewMapPlace.put("zipcode", VirtuosoConstantUtil.sensormasherOntologyURI+"hasZipcode");
		rdfViewMapPlace.put("street", VirtuosoConstantUtil.sensormasherOntologyURI+"hasStreet");
		rdfViewMapPlace.put("city", VirtuosoConstantUtil.sensormasherOntologyURI+"hasCity");
		rdfViewMapPlace.put("province", VirtuosoConstantUtil.sensormasherOntologyURI+"hasProvince");
		rdfViewMapPlace.put("country", VirtuosoConstantUtil.sensormasherOntologyURI+"hasCountry");
		rdfViewMapPlace.put("continent", VirtuosoConstantUtil.sensormasherOntologyURI+"hasContinent");
		//rdfViewMapPlace.put("infor", VirtuosoConstantUtil.sensormasherOntologyURI+"hasInfor");
		//rdfViewMapPlace.put("times", VirtuosoConstantUtil.sensormasherOntologyURI+"hasTime");
		//rdfViewMapPlace.put("author", VirtuosoConstantUtil.sensormasherOntologyURI+"hasAuthor");
		rdfViewMapPlace.put("lat", "http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		rdfViewMapPlace.put("lng", "http://www.w3.org/2003/01/geo/wgs84_pos#long");	
		
		rdfViewMapSensor.put("name", VirtuosoConstantUtil.sensormasherOntologyURI+"hasName");
		rdfViewMapSensor.put("sensorType", VirtuosoConstantUtil.sensormasherOntologyURI+"hasSensorType");
		rdfViewMapSensor.put("source", VirtuosoConstantUtil.sensormasherOntologyURI+"hasSource");
		rdfViewMapSensor.put("sourceType", VirtuosoConstantUtil.sensormasherOntologyURI+"hasSourceType");
		rdfViewMapSensor.put("placeID", "http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation");		
		rdfViewMapSensor.put("infor", VirtuosoConstantUtil.sensormasherOntologyURI+"hasInfor");
		rdfViewMapSensor.put("times", VirtuosoConstantUtil.sensormasherOntologyURI+"hasTime");
		rdfViewMapSensor.put("userID", VirtuosoConstantUtil.sensormasherOntologyURI+"isAddedBy");
		
			
		rdfViewMapHasUnit.put("value", VirtuosoConstantUtil.sensormasherOntologyURI + "value");
		rdfViewMapHasUnit.put("unit", VirtuosoConstantUtil.sensormasherOntologyURI+"unit");
		rdfViewMapHasUnit.put("times", "http://purl.oclc.org/NET/ssnx/ssn#observationResultTime");
		rdfViewMapHasUnit.put("observedURL", VirtuosoConstantUtil.SSNOntolotyURI+"observedProperty");
		rdfViewMapHasUnit.put("observationID",VirtuosoConstantUtil.sensormasherOntologyURI+"isObservedPropertyOf");
		
		rdfViewMapNoUnit.put("value",  VirtuosoConstantUtil.sensormasherOntologyURI + "value");
		//rdfViewMapNoUnit.put("times", VirtuosoConstantUtil.sensormasherOntologyURI+"hasTime");
		rdfViewMapNoUnit.put("times", "http://purl.oclc.org/NET/ssnx/ssn#observationResultTime");
		rdfViewMapNoUnit.put("observedURL", VirtuosoConstantUtil.SSNOntolotyURI+"observedProperty");
		rdfViewMapNoUnit.put("observationID", VirtuosoConstantUtil.sensormasherOntologyURI+"isObservedPropertyOf");
		
		rdfViewMapObservation.put("times", "http://purl.oclc.org/NET/ssnx/ssn#observationResultTime");
		rdfViewMapObservation.put("sensorID", "http://purl.oclc.org/NET/ssnx/ssn#observedBy");
	}
	

	private static void initializedClass2MapingMap() {
		// TODO Auto-generated method stub
		class2MappingMap.put("Amount", rdfViewMapHasUnit);
		class2MappingMap.put("AtmosphereVisibility", rdfViewMapHasUnit);
		class2MappingMap.put("AtmospherePressure", rdfViewMapHasUnit);
		class2MappingMap.put("AtmosphereVisibility", rdfViewMapHasUnit);
		class2MappingMap.put("Category", rdfViewMapNoUnit);		
		class2MappingMap.put("Description", rdfViewMapNoUnit);
		class2MappingMap.put("Direction", rdfViewMapNoUnit);
		class2MappingMap.put("District", rdfViewMapNoUnit);
		class2MappingMap.put("Duration", rdfViewMapHasUnit);
		class2MappingMap.put("Elevation", rdfViewMapHasUnit);
		class2MappingMap.put("InformationEntity", rdfViewMapNoUnit);
		class2MappingMap.put("InformationObject", rdfViewMapNoUnit);
		class2MappingMap.put("WebcamSnapShot", rdfViewMapNoUnit);
		class2MappingMap.put("Station", rdfViewMapNoUnit);
		class2MappingMap.put("Status", rdfViewMapNoUnit);
		class2MappingMap.put("UserContact", rdfViewMapNoUnit);
		class2MappingMap.put("WindSpeed", rdfViewMapHasUnit);
		class2MappingMap.put("WindChill", rdfViewMapHasUnit);
		class2MappingMap.put("Observation", rdfViewMapObservation);
		class2MappingMap.put("Temperature", rdfViewMapHasUnit);
		class2MappingMap.put("Comment", rdfViewMapNoUnit);
		class2MappingMap.put("DoubleValue", rdfViewMapHasUnit);
		class2MappingMap.put("StringValue", rdfViewMapNoUnit);
		class2MappingMap.put("IntegerValue", rdfViewMapHasUnit);
		class2MappingMap.put("TimeInterval", rdfViewMapNoUnit);
		
		class2MappingMap.put("BikeAvailability", rdfViewMapNoUnit);
		class2MappingMap.put("BikeDockAvailability", rdfViewMapNoUnit);
		class2MappingMap.put("StationPlatform", rdfViewMapNoUnit);
		class2MappingMap.put("TrainNumber", rdfViewMapNoUnit);
		class2MappingMap.put("TimeToStation", rdfViewMapHasUnit);
		class2MappingMap.put("SecondToStation", rdfViewMapHasUnit);
		class2MappingMap.put("Destination", rdfViewMapNoUnit);
		
		class2MappingMap.put("FlightCIAO", rdfViewMapNoUnit);
		class2MappingMap.put("CallSign", rdfViewMapNoUnit);
		class2MappingMap.put("Longitude", rdfViewMapNoUnit);
		class2MappingMap.put("Latitude", rdfViewMapNoUnit);
		class2MappingMap.put("Speed", rdfViewMapHasUnit);
	}

	private static void initializedClass2Table() {
		// TODO Auto-generated method stub
		class2Table.put("Amount", "amount");
		class2Table.put("AtmosphereVisibility", "atmospherevisibility");
		class2Table.put("AtmospherePressure", "atmospherepressure");
		class2Table.put("AtmosphereHumidity", "atmospherehumidity");
		class2Table.put("Category", "category");
		class2Table.put("TimeInterval", "datetimes");
		class2Table.put("Description", "description");
		class2Table.put("Direction", "direction");
		class2Table.put("District", "districtnumber");
		class2Table.put("Duration", "duration");
		class2Table.put("Elevation", "elevation");
		class2Table.put("InformationEntity", "information");
		class2Table.put("InformationObject", "name");
		class2Table.put("WebcamSnapShot", "picture");
		class2Table.put("Station", "station");
		class2Table.put("Status", "status");
		class2Table.put("UserContact", "usercontact");
		class2Table.put("WindSpeed", "windspeed");
		class2Table.put("WindChill", "windchill");
		class2Table.put("Temperature", "temperature");
		class2Table.put("Comment", "comment");
		class2Table.put("IntegerValue", "integervalue");
		class2Table.put("DoubleValue", "doublevalue");
		class2Table.put("StringValue", "stringvalue");		
		class2Table.put("Observation", "observation");
		class2Table.put("BikeAvailability", "bikeavailable");
		class2Table.put("BikeDockAvailability", "bikedockavailable");
		class2Table.put("StationPlatform", "stationplatform");
		class2Table.put("TrainNumber", "trainnumber");
		class2Table.put("TimeToStation", "timetostation");
		class2Table.put("SecondToStation", "secondtostation");
		class2Table.put("Destination", "destination");
		
	}
	private static void initializedTable2ClassURI(){
		table2ClassURI.put("amount","http://www.loa-cnr.it/ontologies/DUL.owl#Amount");
		table2ClassURI.put("atmospherevisibility","http://lsm.deri.ie/ont/lsm.owl#AtmosphereVisibility");
		table2ClassURI.put("atmospherepressure","http://lsm.deri.ie/ont/lsm.owl#AtmospherePressure");
		table2ClassURI.put("atmospherehumidity","http://lsm.deri.ie/ont/lsm.owl#AtmosphereHumidity");
		table2ClassURI.put("category", "http://lsm.deri.ie/ont/lsm.owl#Category");
		table2ClassURI.put("datetimes","http://www.loa-cnr.it/ontologies/DUL.owl#TimeInterval");
		table2ClassURI.put("description", "http://www.loa-cnr.it/ontologies/DUL.owl#Description");
		table2ClassURI.put("direction", "http://lsm.deri.ie/ont/lsm.owl#Direction");		
		table2ClassURI.put("elevation", "http://lsm.deri.ie/ont/lsm.owl#Elevation");
		table2ClassURI.put("information","http://www.loa-cnr.it/ontologies/DUL.owl#InformationEntity");
		table2ClassURI.put("name","http://www.loa-cnr.it/ontologies/DUL.owl#InformationObject");
		table2ClassURI.put("picture","http://lsm.deri.ie/ont/lsm.owl#WebcamSnapShot");		
		table2ClassURI.put("status", "http://lsm.deri.ie/ont/lsm.owl#Status");
		table2ClassURI.put("usercontact", "http://lsm.deri.ie/ont/lsm.owl#UserContact");
		table2ClassURI.put("windspeed", "http://lsm.deri.ie/ont/lsm.owl#WindSpeed");
		table2ClassURI.put("windchill", "http://lsm.deri.ie/ont/lsm.owl#WindChill");
		table2ClassURI.put("temperature", "http://lsm.deri.ie/ont/lsm.owl#AirTemperature");
		table2ClassURI.put("observation", "http://purl.oclc.org/NET/ssnx/ssn#Observation");
		table2ClassURI.put("comment", "http://lsm.deri.ie/ont/lsm.owl#Comment");
		table2ClassURI.put("station", "http://lsm.deri.ie/ont/lsm.owl#Station");
		table2ClassURI.put("districtnumber","http://lsm.deri.ie/ont/lsm.owl#District");
		table2ClassURI.put("duration", "http://lsm.deri.ie/ont/lsm.owl#Duration");
		table2ClassURI.put("integervalue", "http://lsm.deri.ie/ont/lsm.owl#IntegerValue");
		table2ClassURI.put("doublevalue", "http://lsm.deri.ie/ont/lsm.owl#DoubleValue");
		table2ClassURI.put("stringvalue", "http://lsm.deri.ie/ont/lsm.owl#StringValue");
		table2ClassURI.put("missingdays", "http://lsm.deri.ie/ont/lsm.owl#IntegerValue");
		table2ClassURI.put("sealeveltime", "http://lsm.deri.ie/ont/lsm.owl#IntegerValue");		
		table2ClassURI.put("meanlevelvalue", "http://lsm.deri.ie/ont/lsm.owl#DoubleValue");
		table2ClassURI.put("bikeavailability", "http://lsm.deri.ie/ont/lsm.owl#BikeAvailability");
		table2ClassURI.put("bikedockavailability", "http://lsm.deri.ie/ont/lsm.owl#BikeDockAvailability");
		table2ClassURI.put("stationplatform", "http://lsm.deri.ie/ont/lsm.owl#StationPlatform");
		table2ClassURI.put("timetostation", "http://lsm.deri.ie/ont/lsm.owl#TimeToStation");
		table2ClassURI.put("secondtostation", "http://lsm.deri.ie/ont/lsm.owl#SecondToStation");
		table2ClassURI.put("trainnumber", "http://lsm.deri.ie/ont/lsm.owl#TrainNumber");
		table2ClassURI.put("destination", "http://lsm.deri.ie/ont/lsm.owl#Destination");
		table2ClassURI.put("flightciao", "http://lsm.deri.ie/ont/lsm.owl#FlightCIAO");
		table2ClassURI.put("longitude", "http://lsm.deri.ie/ont/lsm.owl#Longitude");
		table2ClassURI.put("latitude", "http://lsm.deri.ie/ont/lsm.owl#Latitude");
		table2ClassURI.put("callsign", "http://lsm.deri.ie/ont/lsm.owl#CallSign");
		table2ClassURI.put("speed", "http://lsm.deri.ie/ont/lsm.owl#Speed");
		
	}
}
