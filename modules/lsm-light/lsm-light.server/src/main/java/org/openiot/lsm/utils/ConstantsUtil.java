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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstantsUtil {
	public static final String databaseName = "DERI.DBA.";
	public static final String urlHost = "jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2";
//	public static final String urlHost = "jdbc:virtuoso://localhost:1111";
	/*---------------------------------- components' constraints constants ----------------------------------*/
	public static final String stringConstraints = "no item selected";
	
	/*---------------------------------- components' constraints constants ----------------------------------*/
	public static final String rabbitMQExchangeName = "sensorMiddExchange";
	public static final String rabbitMQExchangeType = "direct";
	
	/*---------------------------------- for the update thread ----------------------------------*/
	public static final int max_update_size = 10000;
	
	/*---------------------------------- for the data feed ----------------------------------*/
//	public static final String image_feed_prefix = "http://lsm.deri.ie/image";
	public static final String data_feed_prefix = "http://lsm.deri.ie/lsmfeed";
	public static final String image_feed_prefix = "http://localhost:8080/LSMv_1.1/image";	
//	public static final String data_feed_prefix = "http://localhost:8080/SensorMiddleware/lsmfeed";
	
//	public static final String image_feed_prefix = "http://140.203.155.176/sensormiddleware/image";
//	public static final String data_feed_prefix = "http://140.203.155.176/sensormiddleware/lsmfeed";
	
	public static final String data_feed_id = "feedId";
	public static final String imageTraffic_path = "/home/alpha/SensorDatabase/Traffic_images/";
	public static final String imageWebcam_path = "/home/alpha/SensorDatabase/Link_images/";
	
	/*---------------------------------- regex constants ----------------------------------*/
	public static final String httpRegex = "(http|www){1}.+";
	public static final String httpjsonRegex = "(http|www){1}.+\\.json";
	public static final String double_regex = "([-\\+]?\\d+(\\.\\d*)?)";
	public static final String lat_comma_lng_Regex = "^( *)"+double_regex+"{1}( *)(,){1}( *)"+double_regex+"{1}( *)$";
	public static final String place_string_regex = "^(.*)(lat:)( *)"+double_regex+"( *)(,){1}(.*)(lng:)( *)"+double_regex+"( *)(,){1}(.*)$";
	public static final String int_regex = "[+-]?\\d+";
	
	
	/*---------------------------------- stands for useful data line in WeatherGrid. ----------------------------------*/
	public static final String useful_data_sign = "_:_";

	
	/*---------------------------------- map marker constants ----------------------------------*/
	public static final String gmapKEY = "ABQIAAAAf-UfDjdvRozbsEzPeZzWThQWTvlDhdGWeFkSB-LbgH2XdFs4tBTYBqAXdEUV4XB8PnN3J5HmndzTFQ";
	public static final int marker_show_level_0 = 0; 
	public static final int marker_show_level_1 = 1; 
	public static final int marker_show_level_2 = 2; 
	public static final int marker_show_level_exactly_one = 6; 
	
	public static final double map_default_lat = 39.876019;
	public static final double map_default_lng = 116.411133;
	public static final int map_default_zoom = 1;
	
	public static final String gmarker_icon_webcam = "/imgs/sensortype/icon_webcam.png";
	public static final String gmarker_icon_all = "/imgs/sensortype/icon_all.png";
	public static final String gmarker_icon_weather = "/imgs/sensortype/icon_weather.png";	
	public static final String gmarker_icon_satellite = "/imgs/sensortype/icon_satellite.png";
	public static final String gmarker_icon_radar = "/imgs/sensortype/icon_radar.png";
	public static final String gmarker_icon_snowfall = "/imgs/sensortype/icon_snowfall.png";
	public static final String gmarker_icon_snowdepth = "/imgs/sensortype/icon_snowdepth.png";
	public static final String gmarker_icon_flood = "/imgs/sensortype/icon_flood.png";
	public static final String gmarker_icon_sealevel = "/imgs/sensortype/icon_sealevel.png";
	public static final String gmarker_icon_trafficcam = "/imgs/sensortype/icon_traffic.png";
	public static final String gmarker_icon_roadactivity = "/imgs/sensortype/icon_roadactivity.png";
	public static final String gmarker_icon_usersensor = "/imgs/sensortype/icon_usersensor.png";
	public static final String gmarker_icon_bikehire = "/imgs/sensortype/icon_bikehire.png";
	public static final String gmarker_icon_railwaystation = "/imgs/sensortype/icon_railwaystation.png";
	public static final String gmarker_icon_plane = "/imgs/sensortype/icon_plane.png";
	public static final String gmarker_icon_airport = "/imgs/sensortype/icon_airport.png";

	public static final int anchor = 10;
	
	public static final int max_markers_nearby = 50;
	
	
	
	/*---------------------------------- traffic different values ----------------------------------*/
	public static final int severity_least_severe = 1;
	public static final int severity_less_severe = 2;
	public static final int severity_severe = 3;
	public static final int severity_severer = 4;
	public static final int severity_severest = 5;
	
	
	/*---------------------------------- bean default values ----------------------------------*/
	public static final int weather_defalut_value = -101;
	public static final int traffic_severity_default_value = 0;
	public static final String link_default_value = "";
	public static final String comment_default_value = "";
	public static final int agree_default_value = 0;
	public static final int disagree_default_value = 0;
	public static final int sealevel_default_value = -99999;
	public static final int xmlParser_length_default_value = 1000;
	
	
	/*------------------------- datasource constants  for data to use -------------------------*/
	public static final Map<String,String> dataSourceType = new LinkedHashMap<String,String>();
	public static final Map<String,String> dataSourceLogo = new HashMap<String,String>();
	
	private static final String dataSourct_type_yahoo_description = 			"unit is c. Like '&u=c'";
	private static final String dataSourct_type_australia_description = 		"data source should be a json link";
	private static final String dataSourct_type_weatherbug_description = 	"data source is constrained with wunderground limit";
	private static final String dataSourct_type_wunderground_description = 	"data format from the data source should be the same as the format of weatherbug";
	private static final String dataSourct_type_other_description = 			"data source with the restriction by yourself";
	
	
	private static final String dataSource_type_yahoo_logo = 		"/imgs/logo_weather_yahoo.jpg";
	private static final String dataSource_type_australia_logo = 	"/imgs/logo_weather_australia.jpg";
	private static final String dataSource_type_weatherbug_logo = 	"/imgs/logo_weather_weatherbug.jpg";
	private static final String dataSource_type_wunderground_logo = "/imgs/logo_weather_wunderground.jpg";
	private static final String dataSource_type_other_logo = 		"/imgs/logo_others.jpg";
	
	
}
