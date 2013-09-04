package org.openiot.lsm.beans;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.openiot.lsm.pooling.ConnectionPool;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.DateUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;



public class SensorManager {
	private Connection conn;
	private String dataGraph = VirtuosoConstantUtil.sensormasherDataGraphURI;
	private String metaGraph = VirtuosoConstantUtil.sensormasherMetadataGraphURI;
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public SensorManager(){
		if(ConnectionPool.getConnectionPool()==null)
			ConnectionPool.init();
	}
	
	public SensorManager(String metaGraph,String dataGraph){
		if(ConnectionPool.getConnectionPool()==null)
			ConnectionPool.init();
		this.dataGraph = dataGraph;
		this.metaGraph = metaGraph;
	}
	
	
	public String getDataGraph() {
		return dataGraph;
	}

	public void setDataGraph(String dataGraph) {
		this.dataGraph = dataGraph;
	}

	public String getMetaGraph() {
		return metaGraph;
	}

	public void setMetaGraph(String metaGraph) {
		this.metaGraph = metaGraph;
	}

	public void runSpatialIndex(){
		String sql = "DB.DBA.RDF_GEO_FILL()";
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			if(i) System.out.println("create spatial index succed");		
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);			
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}		
	}
	
	public void insertTriplesToGraph(String graphName, String triples) {
		// TODO Auto-generated method stub
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			String sql = "sparql insert into graph <" + graphName + ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Insert triples to graph "+graphName);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}
	
	public void clearGraph(String graphName) {
		// TODO Auto-generated method stub
		try{
			if(!VirtuosoConstantUtil.authorizedGraphs.contains(graphName))
				return;
			conn = ConnectionPool.getConnectionPool().getConnection();
			String sql = "sparql clear graph <" + graphName + ">";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Remove triples of graph "+graphName);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}
		
	public void deleteTriples(String graphName, String triples) {
		// TODO Auto-generated method stub
		try{
			if(!VirtuosoConstantUtil.authorizedGraphs.contains(graphName))
				return;
			conn = ConnectionPool.getConnectionPool().getConnection();
			String sql = "sparql delete from <" + graphName + "> {"+triples+"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Remove triples of graph "+graphName);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}
	
	public void deleteAllReadings(String sensorURL) {
		// TODO Auto-generated method stub
		String sql = "sparql delete from <"+dataGraph+"> {?s ?p ?o} "+
						"where{ "+
							"{ "+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+    
								"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
								"?s ?p ?o."+
							"}"+
						"union{ "+
							"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
							"?s ?p  ?o."+
						"}"+
					"}";
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();					
			System.out.println("All triples were deleted");
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}


	public void deleteAllReadings(String sensorURL, String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub
		String sql = "";				
		if(toTime!=null){
			sql = "sparql delete from <"+ dataGraph+"> {?s ?p ?o} "+
				"where{ "+
					"{ {"+
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+  						
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
						    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime && "+
						    "?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)."+
						 "}"+
						"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
						"?s ?p ?o."+
					"}"+
				"union{ "+
					"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
					"?s ?p  ?o."+
				"}"+
			"}";
		}else{
			sql = "sparql delete from <"+ dataGraph+"> {?s ?p ?o} "+
					"where{ "+
						"{ {"+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+  						
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
							    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)."+
							 "}"+
							"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
							"?s ?p ?o."+
						"}"+
					"union{ "+
						"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">."+
						"?s ?p  ?o."+
					"}"+
				"}";					
		}
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();	
			System.out.println("All triples were deleted");
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}
	

//	@SuppressWarnings("unchecked")
//	public List<String> getAllSourcesWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType) {
//		List<String> list = new ArrayList<String>();
//		String sql = "sparql select ?source "+
//				" from <"+ metaGraph +"> " +
//				"where{ "+
//				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
//				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+				   
//				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
//				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
//				"}";			 
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();
//			Statement st = conn.createStatement();
//			if(st.execute(sql)){
//				ResultSet rs = st.getResultSet();
//				while(rs.next()){
//					list.add(rs.getString(1));					
//				}
//				ConnectionPool.attemptClose(rs);				
//			}
//			ConnectionPool.attemptClose(st);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return list;
//	}
	
//	@SuppressWarnings("unchecked")
//	public List<Sensor> getAllSensorWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType) {
//		List<Sensor> sensors = new ArrayList<Sensor>();
//		try {
//			 conn = ConnectionPool.getConnectionPool().getConnection();			
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
////		PlaceManager placeManager = new PlaceManager(conn);
//		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
//				" from <"+ metaGraph +"> " +
//				"where{ "+
//				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
//				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
//				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
//				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
//				"}";			 
//		try{
//			Statement st = conn.createStatement();
//			if(st.execute(sql)){
//				ResultSet rs = st.getResultSet();
//				while(rs.next()){
//					Sensor sensor = new Sensor();
//					sensor.setId(rs.getString(1));
//					sensor.setSource(rs.getString(2));
//					sensor.setSensorType(sensorType);
//					sensor.setSourceType(rs.getString(3));
//					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
//					sensor.setPlace(place);					
//					sensors.add(sensor);
//				}
//				ConnectionPool.attemptClose(rs);				
//			}
//			ConnectionPool.attemptClose(st);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return sensors.size()>0?sensors:null;
//	}
	
//	@SuppressWarnings("unchecked")
//	public List<Sensor> getAllSensorWithSpecifiedCitySensorType(String city, String country, String sensorType) {
//		List<Sensor> sensors = new ArrayList<Sensor>();
//		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
////		PlaceManager placeManager = new PlaceManager(conn);
//		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
//				" from <"+ metaGraph +"> " +
//				"where{ "+
//				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
//				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
//				   "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
//				   "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+
//				   "?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
//				   "?counId <http://www.w3.org/2000/01/rdf-schema#label> \"" + country +"\"."+
//				"}";			 
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();			
//			Statement st = conn.createStatement();
//			if(st.execute(sql)){
//				ResultSet rs = st.getResultSet();
//				while(rs.next()){
//					Sensor sensor = new Sensor();
//					sensor.setId(rs.getString(1));
//					sensor.setSource(rs.getString(2));
//					sensor.setSensorType(sensorType);
//					sensor.setSourceType(rs.getString(3));
//					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
//					sensor.setPlace(place);							
//					sensors.add(sensor);
//				}
//				ConnectionPool.attemptClose(rs);				
//			}
//			ConnectionPool.attemptClose(st);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return sensors.size()>0?sensors:null;
//	}
	
	
	/*search*/
//	@SuppressWarnings("unchecked")
//	public List<String> getAllSensorTypesWithOneSpecifiedLatLng(double lat,double lng) {
//		List<String> sensorTypes = new ArrayList<String>();
//		String sql = "sparql select distinct ?sensorType "+
//				" from <"+ metaGraph +"> \n" +
//				"where{ "+
//				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+				   
//				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+				   
//				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
//				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
//				"}";			 
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();
//			Statement st = conn.createStatement();
//			if(st.execute(sql)){
//				ResultSet rs = st.getResultSet();
//				while(rs.next()){
//					sensorTypes.add(rs.getString(1));					
//				}
//				ConnectionPool.attemptClose(rs);				
//			}
//			ConnectionPool.attemptClose(st);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return sensorTypes;
//	}
	

//	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String sensorType,String spatialOperator,double lng,double lat,double distance){		
//		ArrayList<List> lst = new ArrayList<List>(3);
//		List<String> l1= new ArrayList<String>();
//		List<String> l2 = new ArrayList<String>();
//		List<Double> l3 = new ArrayList<Double>();
//		
//		String sql = "sparql select distinct(?sensor) ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
//			lng+","+lat+")) as ?distance "+		
//				" from <"+ metaGraph +"> " + 			
//				"where {"+			
//				"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//				"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+							
//				"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
//				"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
//				"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
//				"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
//				"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
//				"?place geo:geometry ?geo."+
//				"filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
//						lng+","+lat+"),"+distance+"))." +
//				"} order by ?distance";
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();			
//			PreparedStatement ps = conn.prepareStatement(sql);
//			ResultSet rs = ps.executeQuery();		
//			while(rs.next()){
//				l1.add(rs.getString(1));
//				l2.add(rs.getString(2)+", "+rs.getString(3));
//				l3.add(rs.getDouble(4));
//			}
//			lst.add(l1);
//			lst.add(l2);
//			lst.add(l3);
//			ConnectionPool.attemptClose(rs);
//			ConnectionPool.attemptClose(ps);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return l1.size()>0?lst:null;
//	}
	
	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String spatialOperator,
			double lng,double lat,double distance){		
		ArrayList<List> lst = new ArrayList<List>(3);
		List<String> l1= new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		List<Double> l3 = new ArrayList<Double>();
		
		String sql = "sparql select distinct(?sensor) ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
				lng+","+lat+")) as ?distance "+		
					" from <"+ metaGraph +"> " + 			
					"where {"+			
					"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+												
					"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
					"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
					"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					"?place geo:geometry ?geo."+
					"filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
							lng+","+lat+"),"+distance+"))." +
					"} order by ?distance";		
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				l1.add(rs.getString(1));
				l2.add(rs.getString(2)+", "+rs.getString(3));
				l3.add(rs.getDouble(4));
			}
			lst.add(l1);
			lst.add(l2);
			lst.add(l3);
			ConnectionPool.attemptClose(rs);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return lst;
	}
			
//	@SuppressWarnings("unchecked")
//	public ArrayList<Sensor> getAllSpecifiedSensorAroundPlace(String sensorType,String lat,String lng,String distance){		
//		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
//		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
//		
//		String sql = "sparql select distinct(?sensor) ?source ?sourceType ?place "+	
//			" from <"+ metaGraph +"> " +
//			"where {"+			  
//			"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//			"?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
//			"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//			"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+	
//			"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
//			"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
//		     "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
//		     "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long;" +
//		     "geo:geometry ?geo."+
//		     "filter (<bif:st_intersects>(?geo,<bif:st_point>("+
//				lng+","+lat+"),"+distance+"))." +
//		"} order by ?distance";		
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();
//			PreparedStatement ps = conn.prepareStatement(sql);
//			ResultSet rs = ps.executeQuery();		
//			while(rs.next()){
//				Sensor sensor = new Sensor();
//				sensor.setId(rs.getString("sensor"));
//				sensor.setSource(rs.getString("source"));
//				sensor.setSensorType(sensorType);
//				sensor.setSourceType(rs.getString("sourceType"));
//				Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
//				sensor.setPlace(place);							
//				sensors.add(sensor);
//			}
//			ConnectionPool.attemptClose(rs);
//			ConnectionPool.attemptClose(ps);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		return sensors.size()>0?sensors:null;
//	}
		
	
	public String getNearestLocationIdFromGraph(String linkedgeoId) {
		// TODO Auto-generated method stub
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ VirtuosoConstantUtil.linkedgeodataGraphURI +"> " +
			"where {"+			  
			"<"+linkedgeoId+"> <"+ VirtuosoConstantUtil.lnkedgeodataSameAsPrefix +"> ?near." +		     
			"}";		
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			ConnectionPool.attemptClose(rs);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return nearbyId;
	}
	
	public String getNearestLocationWithSensorIdURL(String id){
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ metaGraph +"> \n" +
			"where {"+			  
			"<"+id+"> <"+ VirtuosoConstantUtil.sensorHasNearestLocation +"> ?near." +		     
			"}";		
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			ConnectionPool.attemptClose(rs);
			ConnectionPool.attemptClose(ps);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return nearbyId;
	}


		
	//**********************sensor table***************************/
	public Sensor getSpecifiedSensorWithSource(String source){
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
//		PlaceManager placeManager = new PlaceManager(conn);		
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?sourceType ?place ?userId "+
//		String sql = "sparql select ?sensor ?sensorType ?sourceType ?place ?userId "+
				" from <"+ metaGraph +"> \n" +
					"where{ "+
					   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
					   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> <"+source+">."+
					   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//					   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId."+
					"}";			 
			try{
				conn = ConnectionPool.getConnectionPool().getConnection();				
				Statement st = conn.createStatement();
				if(st.execute(sql)){
					ResultSet rs = st.getResultSet();
					while(rs.next()){
						sensor = new Sensor();
						sensor.setId(rs.getString("sensor"));
//						sensor.setSensorType(rs.getString(2));
						sensor.setSource(source);
						sensor.setSourceType(rs.getString("sourceType"));
						Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
						sensor.setPlace(place);
					}
					ConnectionPool.attemptClose(rs);				
				}
				ConnectionPool.attemptClose(st);
				ConnectionPool.attemptClose(conn);
			}catch(Exception e){
				e.printStackTrace();
				ConnectionPool.attemptClose(conn);
			}		
			return sensor;
	}
	
	@SuppressWarnings("unchecked")
	public Sensor getSpecifiedSensorWithPlaceId(String placeId){
		Sensor sensor = null;
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);		
		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
//		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> <"+placeId+">."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
//					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(placeId);
					sensor.setPlace(place);								
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithSensorId(String id){		
		Sensor sensor = null;
		String sql = "sparql select ?name ?source ?sourceType ?place ?userId "+
//		String sql = "sparql select ?name ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "<"+id+"> <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "<"+id+"> <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "<"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
//				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);		
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(id);
					sensor.setSource(rs.getString("source"));
//					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					sensor.setProperties(getObservesListOfSensor(id));
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng) {		
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?source ?sourceType ?place "+
//		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place "+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+				  
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
//					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return sensor;
	}
	
	public HashMap<String,String> getObservesListOfSensor(String sensorId){
		HashMap<String, String> lstPro = new HashMap<>();
		String sql = "sparql select ?obs ?type"+
				" from <"+ metaGraph +"> \n" +
				"where{ "+
				   "<"+sensorId+"> <http://purl.oclc.org/NET/ssnx/ssn#observes> ?obs."+
				   "?obs rdf:type ?type." +				   
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					lstPro.put(rs.getString("type"), rs.getString("obs"));					
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return lstPro;		
	}
	
	
	//**********************observation table***************************/
	public Observation getNewestObservationForOneSensor(String sensorId) {
		Observation observation = null;
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ dataGraph +"> \n" +
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time) limit 1";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return  observation;
	}
	
	public List<String> getObservationsWithTimeCriteria(String sensorId,
			String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub				
		String sql;
		
		if(toTime!=null){
			sql= "sparql select ?obs"+
					" from <"+ dataGraph+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime"+" && ?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)" +
					"}";
		}else{
			sql= "sparql select ?obs"+
					" from <"+ dataGraph+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)" +
					"}";
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return observations;
	}
	
	public List<Observation> getObservationsForOneSensor(String sensorId) {
		// TODO Auto-generated method stub		
		List<Observation> observations = new ArrayList<Observation>();
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ dataGraph +"> \n" +
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time)";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Observation observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
					observations.add(observation);
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return  observations;
	}
	
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId,String timeOper, String dateTime, String readingType,
			String oper, String value) {		
		// TODO Auto-generated method stub
		Date date = DateUtil.standardString2Date(dateTime);
		String sql;
		if(timeOper.equals("latest")){
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
					   "?sign rdf:type ?type." +
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
					   " filter regex(?type,'"+readingType+"','i')"+
					   " filter (?value" + oper + value +")" +
					"}order by desc(?time) limit 1";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+						   
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+	
						"}order by desc(?time) limit 1";
			}
		}else{
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?value" + oper + value +" && ?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ dataGraph +"> \n" +
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
				
			}
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return observations;
	}
	

	

	//***********************************check*********************
//	public ArrayList<List> getAllSensorPropertiesForSpecifiedSensorType(String type) {
//		// TODO Auto-generated method stub
//		ArrayList<List> list = new ArrayList<List>(2);
//		List list1 = new ArrayList<String>();
//		List list2 = new ArrayList<String>();
//		String sql = "sparql select ?x ?type "+
//				" from <"+ metaGraph +"> \n" +
//					  "where{ "+
//						   "{"+
//						     "select ?sensor "+
//						     "where{ "+
//						         "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
//						         "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
//						   "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+type+"\"."+
//						    " }limit 1"+
//						   "}"+
//						   "?sensor <http://purl.oclc.org/NET/ssnx/ssn#observes> ?x."+
//						   "?x rdf:type ?type."+
//						"}";			 
//		try{
//			conn = ConnectionPool.getConnectionPool().getConnection();			
//			Statement st = conn.createStatement();
//			if(st.execute(sql)){
//				ResultSet rs = st.getResultSet();
//				while(rs.next()){					
//					list1.add(rs.getString(1));
//					list2.add(rs.getString(2).substring(rs.getString(2).lastIndexOf("#")+1));
////					list2.add(rs.getString(2));
//				}
//				ConnectionPool.attemptClose(rs);				
//			}
//			ConnectionPool.attemptClose(st);
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//		list.add(list1);
//		list.add(list2);
//		return list;
//	}


	public List<ArrayList> getReadingDataOfObservation(String observationId) {		
		// TODO Auto-generated method stub
		List<ArrayList> list = new ArrayList<ArrayList>();
		String sql = "sparql select ?type ?value ?uni ?name "+
				" from <"+ dataGraph+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +				  
				   "OPTIONAL{?sign <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();	
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					ArrayList<String> arr = new ArrayList<String>();
					arr.add(rs.getString("type"));
					arr.add(rs.getString("value"));
					arr.add(rs.getString("uni"));
					arr.add(rs.getString("name"));
					list.add(arr);
				}				
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return list;
	}


	public Sensor getSpecifiedSensorWithObservationId(String obsId) {
		// TODO Auto-generated method stub 
		Sensor sensor = null;
		PlaceManager placeManager = new PlaceManager(metaGraph,dataGraph);
		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
//		String sql = "sparql select ?sensor ?source ?sourceType ?sensorType ?place ?userId "+
				" from <"+ metaGraph +"> " +
				"where{ "+
				   "{select ?sensor from <"+dataGraph +"> " +
				   " where{ <"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensor.}"+
				   "}"+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
//				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
//				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
//					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return sensor;
	}
	
	private List<String> data2List(Observation observation){
		String source = observation.getSensor();//the reference of more data of the specified weather
		String sign;
		List<String> list = new ArrayList<String>();
		List<ArrayList> readings = getReadingDataOfObservation(observation.getId());
		for(ArrayList reading : readings){
			sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
			list.add(sign);						
			String unit=null;
			String content=null;
			try{
				content = reading.get(1).toString();
				unit = reading.get(2).toString();				
			}catch(Exception e){
			}
			if(unit == null){
				unit = "no";
			}
			try {				
				if(content == null){
					list.add("not_supported" + ConstantsUtil.useful_data_sign);
				}else if(NumberUtil.isDouble(content)){
					double data = Double.parseDouble(content);
					if(data != ConstantsUtil.weather_defalut_value){
						list.add(String.valueOf(data) + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}else{
					String data = content;
					if(!data.trim().equals("") ){
						list.add(data + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	public ArrayList getSensorHistoricalData(String sensorURL, Date fromTime) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> reading = new LinkedHashMap<>();
		ArrayList arr = new ArrayList<>();
		String query = "sparql select ?s ?type ?name ?value ?time "+
				" from <"+ dataGraph +"> " +
					"where{ "+
					  "{ "+
					   "select ?observation where "+
					     "{ "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">. "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					    "filter( ?time >\""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime).} "+
					  "} "+ 
					  "?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation. "+
					  "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "+
					  "?s <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					  "?s <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					  "OPTIONAL{?s <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
					"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			String sign = "";
			if(st.execute(query)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){				
					reading = new LinkedHashMap<>();					
					if(rs.getString("name")==null)
						sign = rs.getString("type");
					else sign = rs.getString("name");
					reading.put("property",sign.substring(sign.lastIndexOf("#")+1));
					reading.put("value", rs.getString("value"));
					reading.put("time", rs.getString("time"));
					arr.add(reading);
				}			
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();			
			ConnectionPool.attemptClose(conn);
		}
//		return json;
		return arr;
	}


	

	public void sensorDelete(String sensorURL) {
		// TODO Auto-generated method stub
		String sql = "sparql DELETE from <"+ metaGraph +">{" +
				"<"+sensorURL + "> ?p ?o.}" + 			
				"where{<"+sensorURL + "> ?p ?o." +							     	
				"}";				  
							 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();		
			Statement st = conn.createStatement();
			st.execute(sql);
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		
	}

	
}
