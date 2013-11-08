package org.openiot.lsm.utils;
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