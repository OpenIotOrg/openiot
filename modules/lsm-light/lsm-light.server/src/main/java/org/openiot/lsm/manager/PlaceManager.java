package org.openiot.lsm.manager;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openiot.lsm.beans.Place;
import org.openiot.lsm.pooling.ConnectionManager;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

public class PlaceManager {
	private String dataGraph;
	private String metaGraph;
	
	public PlaceManager(String metaGraph,String dataGraph){
		this.metaGraph = metaGraph;
		this.dataGraph = dataGraph;	
	}
	
	public PlaceManager(){
		
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
		Connection conn = null;
		String sql = "sparql select distinct ?place ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+metaGraph+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://openiot.eu/ontology/ns/is_in_city> ?cityId."+
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
			conn = ConnectionManager.getConnection();
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
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return place;
	}

	
	@SuppressWarnings("unchecked")
	public List<Place> getAllPlacesWithinOneCity(String city) {
		Connection conn = null;
		List<Place> places = new ArrayList<Place>();
		String sql = "sparql select distinct ?place ?lat ?lng "+
					"from <"+metaGraph+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://openiot.eu/ontology/ns/is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+					  
					"}";			 
		try{
			conn = ConnectionManager.getConnection();				
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
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return places;
	}


	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id) {
		// TODO Auto-generated method stub
		Connection conn = null;
		List<List<String>> lst = new ArrayList();		
		String sql = "sparql select ?p ?o "+		
			"from <"+metaGraph+"> " +
			"where{ \n" +
			   " <"+id+"> ?p ?o." +			   
			  "}";			 
		try{
			conn = ConnectionManager.getConnection();				
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
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
			return lst;
		}		
		return lst;
	}

	public Place getPlaceWithPlaceId(String id){
		Place place = null;		
		Connection conn = null;
		String sql = "sparql select distinct ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+metaGraph+"> " +
					"where{ "+
					"<"+id+">" + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					"<"+id+">" + " <http://openiot.eu/ontology/ns/is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_province> ?proId."+
					  "?proId <http://www.w3.org/2000/01/rdf-schema#label> ?province."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_country> ?counId."+
					  "?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_continent> ?conId."+
					  "?conId <http://www.w3.org/2000/01/rdf-schema#label> ?continent."+
					"}";			 
		try{
			conn = ConnectionManager.getConnection();				
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
				ConnectionManager.attemptClose(rs);				
			}
			ConnectionManager.attemptClose(st);
			ConnectionManager.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionManager.attemptClose(conn);
		}
		return place;
	}
}
