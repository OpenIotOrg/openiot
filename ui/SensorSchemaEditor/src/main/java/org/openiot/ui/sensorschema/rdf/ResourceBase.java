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
package org.openiot.ui.sensorschema.rdf;

/**
 * 
 */
public class ResourceBase {

	/**
	 * Base Time.
	 */
	protected String base_datetime = null;
	
	/** Eventual remote resource hosting server URI. */
	protected String remote_uri = null;
	
	/** Base URI - Base Name in JSON. */
	protected String base_uri = null;

	/**
	 * Resource identification.
	 */
	protected String resourceId;
	
	/**
	 * location name
	 */
	protected String locationName;
	
	/**
	 * location coordiantes
	 */
	protected String location_coordinates;
	
	/**
	 * 
	 */
	protected String type;
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocation_coordinates() {
		return location_coordinates;
	}

	public void setLocation_coordinates(String location_coordinates) {
		this.location_coordinates = location_coordinates;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setBase_uri(String base_uri) {
		this.base_uri = base_uri;
	}

	public String getBase_uri() {
		return base_uri;
	}
	
	public String getRemote_uri() {
		return remote_uri;
	}

	public void setRemote_uri(String remote_uri) {
		this.remote_uri = remote_uri;
	}

	public String getBase_datetime() {
		return base_datetime;
	}

	public void setBase_datetime(String base_datetime) {
		this.base_datetime = base_datetime;
	}
	
	
	
	
}
