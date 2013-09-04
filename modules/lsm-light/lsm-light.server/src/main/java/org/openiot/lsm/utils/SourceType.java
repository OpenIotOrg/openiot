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
import java.util.List;
import java.util.Map;

public enum SourceType {
	yahoo, australia, wunderground, weatherbug,google,
	webcam, // for camera is just a link. so just let the webcam be the sourceType, the same as the sensorType
	noaa,
	psmsl,
	others, traffic, bikehire, railwaystation;
	
	public static Map<String,SourceType> source_type_map = new HashMap<String,SourceType>();
	public static List<SourceType> weather_source_type = new ArrayList<SourceType>();
	static{
		initialize_source_type_map();
		initialize_weather_source_type_map();
	}
	
	private static void initialize_source_type_map(){
		for(SourceType type : SourceType.values()){
			source_type_map.put(type.toString(), type);
		}
	}
	
	private static void initialize_weather_source_type_map(){
		weather_source_type.add(SourceType.yahoo);
		weather_source_type.add(SourceType.australia);
		weather_source_type.add(SourceType.wunderground);
		weather_source_type.add(SourceType.weatherbug);
		weather_source_type.add(SourceType.google);
	}
	
	public static SourceType getSourceType(String sourceType){
		return source_type_map.get(sourceType);
	}
	public static void main(String[] args) {
		
	}
}