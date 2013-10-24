package org.openiot.ld4s.lod_cloud;

import org.restlet.security.User;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class WeatherApi extends SearchRouter{

	private String[] timerange = null;

	private String[] coords = null;

	private String location_name = null;

	public WeatherApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
		if (context != null && context.getTime_range() != null){
			setTimerange(timerange);
		}
		if (context != null && context.getLocation() != null){
			setLocation_name(location_name);
		}
		if (context != null && context.getLocation_coords() != null){
			setCoords(coords);
		}
	}

	
	@Override
	public Model start() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTimerange(String[] timerange) {
		this.timerange = timerange;
	}

	public String[] getTimerange() {
		return timerange;
	}

	public void setCoords(String[] coords) {
		this.coords = coords;
	}

	public String[] getCoords() {
		return coords;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getLocation_name() {
		return location_name;
	}



}
