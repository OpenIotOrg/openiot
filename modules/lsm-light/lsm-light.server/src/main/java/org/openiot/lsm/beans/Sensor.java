package org.openiot.lsm.beans;

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
import java.util.Date;
import java.util.HashMap;


public class Sensor implements java.io.Serializable {
	private String id;
	private String name = "";
	private String source = "";
	private String sourceType = "";
	private String infor = "";
	private Date times = new Date();
	private String author="admin";
	private String code="";
	private Place place;
	private User user;
	private String metaGraph="";
	private String dataGraph="";
	private HashMap<String,String> properties;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public HashMap<String,String> getProperties() {
		return properties;
	}
	public void setProperties(HashMap<String,String> properties) {
		this.properties = properties;
	}
	public Sensor(){
		id = "http://lsm.deri.ie/resource/"+System.nanoTime();
		properties = new HashMap<String,String>();
	}
	public String getId() {
		return id;
	}
	
	@SuppressWarnings("unused")
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
	
	public String getMetaGraph() {
		return metaGraph;
	}
	public void setMetaGraph(String metaGraph) {
		this.metaGraph = metaGraph;
	}
	public String getDataGraph() {
		return dataGraph;
	}
	public void setDataGraph(String dataGraph) {
		this.dataGraph = dataGraph;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public void addProperty(String pro){
		properties.put(pro,"");
	}
	
	public void removeProperty(String pro){
		properties.remove(pro);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || (obj instanceof Sensor)){
			return false;
		}
		Sensor source = (Sensor)obj;
		if(source.getSourceType().trim().equals(this.getSourceType().trim())
				&& source.getSource().trim().equals(this.getSource().trim())){
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return 
		  (sourceType.trim().equals("") ? "" : ("sourceType:"+sourceType + ", ")) 
		+ (source.trim().equals("") ? "" : ("source:"+source + ", ")) ;
	}

}
