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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.register;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.server.LSMTripleStore;
import org.openiot.ui.sensorschema.bean.SensorMetaDataBean;
import org.openiot.ui.sensorschema.utils.OpeniotVocab;
import org.openiot.ui.sensorschema.utils.SesameSPARQLClient;
import org.openiot.ui.sensorschema.utils.SsnVocab;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorRegistrarLSM implements SensorRegistrar {

	/*
	 * This is a Sensor Registration code for LSM server
	 * The service will register the sensor with LSM server
	 * Provide the user with a sensor ID after a successful registration
	 */
	
	private static final transient Logger logger = LoggerFactory.getLogger(SensorRegistrarLSM.class);
	
	private static SensorRegistrarLSM instance = null;
	
	public SensorRegistrarLSM(){
		//class initialisation
	}
	
	
	public static SensorRegistrarLSM getInstance() {
		if (instance == null){
			instance = new SensorRegistrarLSM();
			return instance;
		}
		else
			return instance;
	}
	
	@Override
	public String registerSensorInstance(SensorMetaDataBean metadata) {
		
		// Code to register sensor with LSM
		// Uses the LSM client
        String sensorID = null;
        logger.info("Add sensor: "+metadata.getSensorName()+","+metadata.getAuthor()+","+metadata.getSourceType()+","+metadata.getSensorType());
        logger.info("Graphs: "+ OpeniotVocab.LSM_METAGRAPH + ":" + OpeniotVocab.LSM_DATAGRAPH);
        for (String p:metadata.getProperties()){
        	logger.info("Properties: "+p);
        }
        try {
            Sensor sensor = new Sensor();
            sensor.setName(metadata.getSensorName());
            sensor.setAuthor(metadata.getAuthor());
            sensor.setSensorType(metadata.getSensorType());
            sensor.setSourceType(metadata.getSourceType());
            sensor.setSource(metadata.getSource());  
            sensor.setInfor(metadata.getInformation());
            
            
            for (String p:metadata.getProperties()){
            	sensor.addProperty(p);            	
            }
            
            sensor.setTimes(new Date());
            sensor.setMetaGraph(OpeniotVocab.LSM_METAGRAPH);
            sensor.setDataGraph(OpeniotVocab.LSM_DATAGRAPH);
         
            // set sensor location information (latitude, longitude, city, country, continent...)
            Place place = new Place();
            place.setLat(metadata.getLatitude());
            place.setLng(metadata.getLongitude());
            sensor.setPlace(place);

            
            // create LSMTripleStore instance
            logger.info("Connecting to LSM: "+OpeniotVocab.LSM_SERVER_URL);
            LSMTripleStore lsmStore = new LSMTripleStore(OpeniotVocab.LSM_SERVER_URL);
            sensorID=lsmStore.sensorAdd(sensor);
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("cannot send the data to server");
            return null;
        }
        return sensorID;
	}

	@Override
	public boolean unregisterSensorInstance(String sensorID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerSensorType(String rdf) {
		// TODO Auto-generated method stub
		  LSMTripleStore lsmStore = new LSMTripleStore(OpeniotVocab.LSM_SERVER_URL);
		  logger.info("Connecting to LSM and Storing data to graph: "+OpeniotVocab.LSM_SERVER_URL + ";" + OpeniotVocab.LSM_METAGRAPH);        
	      boolean success = lsmStore.pushRDF(OpeniotVocab.LSM_METAGRAPH,rdf);
	      return success;
	}

	@Override
	public boolean checkSensorTypeRegistration(String sensorID) {
		
		String sensorURI =null, metagraphURI = null;
		
		sensorURI = OpeniotVocab.NS + sensorID;
		metagraphURI = OpeniotVocab.LSM_METAGRAPH;
		
		
		StringBuilder query = new StringBuilder();		
		query.append("SELECT * from");
		query.append(" <" + metagraphURI + ">"); //sensor metagraph from configuration file
		query.append(" WHERE" );
		query.append(" {");
		query.append(" <" + sensorURI + ">"); //subject
		query.append(" ?o"); //object
		query.append(" ?p"); //predicate
		query.append(" }");
		
		//System.out.println("SPARQL Query" + query.toString());
		
		try {
			SesameSPARQLClient sparqlclient = new SesameSPARQLClient();
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			if (qres.hasNext())
				return true;
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		
		return false;
	}

	@Override
	public List<String> getSensorList() {
		String metagraphURI = null;
		ArrayList<String> sensorlist = new ArrayList<String>();
	
		// set the properties value	 				
		metagraphURI = OpeniotVocab.LSM_METAGRAPH;		
		
		StringBuilder query = new StringBuilder();		
		query.append("SELECT * from");
		query.append(" <" + metagraphURI + ">"); //sensor metagraph from configuration file
		query.append(" WHERE" );
		query.append(" {");
		query.append(" ?s"); //subject
		query.append(" <" + OpeniotVocab.RDF_SUBCLASS + ">"); //object
		query.append(" <" + SsnVocab.SSN_SENSOR + ">"); //predicate
		query.append(" }");
		
		//System.out.println("SPARQL Query" + query.toString());
		SesameSPARQLClient sparqlclient;
		try {
			sparqlclient = new SesameSPARQLClient();
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			while (qres.hasNext()){
				
				BindingSet b = qres.next();				
				Set names = b.getBindingNames();				
				String text = b.getValue("s").stringValue();	
//				System.out.println(text);
//				System.out.println(OpeniotVocab.NS);
//				text = text.replace(OpeniotVocab.NS, "");
				sensorlist.add(text);
					
			}						
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return sensorlist;
	}
	
	public ArrayList<String> getSensorDescription(String sensorTypeName) {
		
		String metagraphURI = null;
		ArrayList<String> sensordesc = new ArrayList<String>();
	
		// set the properties value	 				
		metagraphURI = OpeniotVocab.LSM_METAGRAPH;		
		
		StringBuilder query = new StringBuilder();		
		query.append("SELECT * from");
		query.append(" <" + metagraphURI + ">"); //sensor metagraph from configuration file
		query.append(" WHERE" );
		query.append(" {");
		query.append(" <" + sensorTypeName + ">"); //subject
		query.append(" ?o"); //object
		query.append(" ?p"); //predicate
		query.append(" }");
		
		//System.out.println("SPARQL Query" + query.toString());
		SesameSPARQLClient sparqlclient;
		try {
			sparqlclient = new SesameSPARQLClient();
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			while (qres.hasNext()){
				
				BindingSet b = qres.next();				
				Set names = b.getBindingNames();				
				String text = b.getValue("o").stringValue();
				if (text.contains((SsnVocab.OBSERVES).toString())){
//					System.out.println(text);
					sensordesc.add(b.getValue("p").stringValue());
				}					
			}						
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return sensordesc;
	}
	
	public boolean checkSensorInstanceRegistrationbyName(String sensorName){
		
		String sensorURI =null, metagraphURI = null;
		
		sensorURI = sensorName;
		metagraphURI = OpeniotVocab.LSM_METAGRAPH;
		
		
		StringBuilder query = new StringBuilder();		
		query.append("SELECT * from");
		query.append(" <" + metagraphURI + ">"); //sensor metagraph from configuration file
		query.append(" WHERE" );
		query.append(" {");
		query.append(" ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor> ."); //sensortype is SSN
		query.append(" ?s <http://www.w3.org/2000/01/rdf-schema#label> "); //sensor name stored as a label
		query.append("\"" + sensorName + "\""); //is equal to sensor Name
		query.append(" }");
		
		System.out.println("SPARQL Query" + query.toString());
		
		try {
			SesameSPARQLClient sparqlclient = new SesameSPARQLClient();
			TupleQueryResult qres = sparqlclient.sparqlToQResult(query.toString());
			if (qres.hasNext())
				return true;
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		
		return false;
	}

}
