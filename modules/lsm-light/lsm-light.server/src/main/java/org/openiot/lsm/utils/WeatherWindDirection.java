package org.openiot.lsm.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WeatherWindDirection {
	public static Set<String> map_set = null;
	public static Map<String,Double> map_12 = null;
	public static Map<Double,String> map_360_reverse = null;
	public static Map<String,Double> map_360 = null;
	
	static{
		initializeMap_set();
		initializeMap_12();
		initializeMap_360();
		initializeMap_12_reverse();
	}
	
	private static void initializeMap_set(){
		map_set = new LinkedHashSet<String>();
		map_set.add("N");
		map_set.add("NNE");
		map_set.add("NE");
		map_set.add("ENE");
		map_set.add("E");
		map_set.add("ESE");
		map_set.add("SE");
		map_set.add("SSE");
		map_set.add("S");
		map_set.add("SSW");
		map_set.add("SW");
		map_set.add("WSW");
		map_set.add("W");
		map_set.add("WNW");
		map_set.add("NW");
		map_set.add("NNW");
	}
	
	private static void initializeMap_12(){
		map_12 = new HashMap<String,Double>();
		map_12.put("N", 0d);
		map_12.put("NNE", 1d);
		map_12.put("NE", 1d);
		map_12.put("ENE", 2d);
		map_12.put("E", 3d);
		map_12.put("ESE", 4d);
		map_12.put("SE", 4d);
		map_12.put("SSE", 5d);
		map_12.put("S", 6d);
		map_12.put("SSW", 7d);
		map_12.put("SW", 7d);
		map_12.put("WSW", 8d);
		map_12.put("W", 9d);
		map_12.put("WNW", 10d);
		map_12.put("NW", 10d);
		map_12.put("NNW", 11d);
	}
	
	private static void initializeMap_360(){
		map_360 = new HashMap<String,Double>();
		map_360.put("N", 0d);
		map_360.put("NNE", 22.5d);
		map_360.put("NE", 45d);
		map_360.put("ENE", 67.5d);
		map_360.put("E", 90d);
		map_360.put("ESE", 112.5d);
		map_360.put("SE", 135d);
		map_360.put("SSE", 157.5d);
		map_360.put("S", 180d);
		map_360.put("SSW", 202.5d);
		map_360.put("SW", 225d);
		map_360.put("WSW", 247.5d);
		map_360.put("W", 270d);
		map_360.put("WNW", 292.5d);
		map_360.put("NW", 315d);
		map_360.put("NNW", 337.5d);
	}
	
	private static void initializeMap_12_reverse(){
		map_360_reverse = new HashMap<Double,String>();
		map_360_reverse.put(0.0, "N");
		map_360_reverse.put(22.5, "NNE");
		map_360_reverse.put(45.0, "NE");
		map_360_reverse.put(67.5, "ENE");
		map_360_reverse.put(90.0, "E");
		map_360_reverse.put(112.5, "ESE");
		map_360_reverse.put(135.0, "SE");
		map_360_reverse.put(157.5, "SSE");
		map_360_reverse.put(180.0, "S");
		map_360_reverse.put(202.5, "SSW");
		map_360_reverse.put(225.0, "SW");
		map_360_reverse.put(247.5, "WSW");
		map_360_reverse.put(270.0, "W");
		map_360_reverse.put(292.5, "WNW");
		map_360_reverse.put(315.0, "NW");
		map_360_reverse.put(337.5, "NNW");

	}
	
	public static String transfer360toString(double wind_direction){
		if(wind_direction <0 || wind_direction > 360){
			return "Not Available";
		}else{
			double int_part = wind_direction / 22.5;
			double left_part = wind_direction % 25;
			
			if(left_part >= 12.5){
				int_part++;
			}
			
			double code = int_part * 22.5;
			String state = map_360_reverse.get(code);
			return state;
		}
	}
}
