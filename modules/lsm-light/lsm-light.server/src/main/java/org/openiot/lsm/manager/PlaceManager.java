package org.openiot.lsm.manager;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openiot.lsm.beans.Place;
import org.openiot.lsm.pooling.ConnectionPool;
import org.openiot.lsm.utils.VirtuosoConstantUtil;

import virtuoso.jena.driver.VirtGraph;


public class PlaceManager {
	private Connection conn;
	private String dataGraph = VirtuosoConstantUtil.sensormasherDataGraphURI;
	private String metaGraph = VirtuosoConstantUtil.sensormasherMetadataGraphURI;
	
	public PlaceManager(String metaGraph,String dataGraph){
		this.metaGraph = metaGraph;
		this.dataGraph = dataGraph;	
	}
	
	public PlaceManager(){
		
	}
	
	public PlaceManager(Connection conn,String metaGraph,String dataGraph){
		this.conn = conn;
		this.metaGraph = metaGraph;
		this.dataGraph = dataGraph;
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

	public Place getPlaceWithSpecifiedLatLng(double lat, double lng) {
		Place place = null;
		String sql = "sparql select distinct ?place ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+metaGraph+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					  "?place <http://linkedgeodata.org/property/is_in_province> ?proId."+
					  "?proId <http://www.w3.org/2000/01/rdf-schema#label> ?province."+
					  "?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
					  "?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					  "?place <http://linkedgeodata.org/property/is_in_continent> ?conId."+
					  "?conId <http://www.w3.org/2000/01/rdf-schema#label> ?continent."+
					  "filter(?lat="+lat +" && ?lng="+lng+")"+
					"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					place = new Place();
					place.setId(rs.getString("place"));
					place.setCity(rs.getString("city"));
					place.setProvince(rs.getString("province"));
					place.setCountry(rs.getString("country"));
					place.setContinent(rs.getString("continent"));
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));		
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return place;
	}

	
	@SuppressWarnings("unchecked")
	public List<Place> getAllPlacesWithinOneCity(String city) {
		List<Place> places = new ArrayList<Place>();
		String sql = "sparql select distinct ?place ?lat ?lng "+
					"from <"+metaGraph+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+					  
					"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					Place place = new Place();
					place.setId(rs.getString("place"));				
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));
					places.add(place);
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return places;
	}


	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id) {
		// TODO Auto-generated method stub
		List<List<String>> lst = new ArrayList();		
		String sql = "sparql select ?p ?o "+		
			"from <"+metaGraph+"> " +
			"where{ \n" +
			   " <"+id+"> ?p ?o." +			   
			  "}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){										
					ArrayList strNode = new ArrayList();					
					strNode.add(id);	
					strNode.add(rs.getString(1));					
					strNode.add(rs.getString(2));
					lst.add(strNode);
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
			return lst;
		}		
		return lst;
	}

	public Place getPlaceWithPlaceId(String id){
		Place place = null;		
		String sql = "sparql select distinct ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+metaGraph+"> " +
					"where{ "+
					"<"+id+">" + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					"<"+id+">" + " <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_province> ?proId."+
					  "?proId <http://www.w3.org/2000/01/rdf-schema#label> ?province."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_country> ?counId."+
					  "?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_continent> ?conId."+
					  "?conId <http://www.w3.org/2000/01/rdf-schema#label> ?continent."+
					"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					place = new Place();
					place.setId(id);
					place.setCity(rs.getString("city"));
					place.setProvince(rs.getString("province"));
					place.setCountry(rs.getString("country"));
					place.setContinent(rs.getString("continent"));
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));		
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return place;
	}
}
