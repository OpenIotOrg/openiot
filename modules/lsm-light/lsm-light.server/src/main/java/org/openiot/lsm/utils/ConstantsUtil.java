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


public class ConstantsUtil {
	public static final String databaseName = "DERI.DBA.";
	public static final String urlHost = "jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2";
//	public static final String urlHost = "jdbc:virtuoso://localhost:1111";
	public static String realPath = "";
	
	/*---------------------------------- components' constraints constants ----------------------------------*/
	public static final String stringConstraints = "no item selected";
	
	/*---------------------------------- for the update thread ----------------------------------*/
	public static final int max_update_size = 10000;
	
	
	/*---------------------------------- regex constants ----------------------------------*/
	public static final String httpRegex = "(http|www){1}.+";
	public static final String httpjsonRegex = "(http|www){1}.+\\.json";
	public static final String double_regex = "([-\\+]?\\d+(\\.\\d*)?)";
	public static final String lat_comma_lng_Regex = "^( *)"+double_regex+"{1}( *)(,){1}( *)"+double_regex+"{1}( *)$";
	public static final String place_string_regex = "^(.*)(lat:)( *)"+double_regex+"( *)(,){1}(.*)(lng:)( *)"+double_regex+"( *)(,){1}(.*)$";
	public static final String int_regex = "[+-]?\\d+";
	
	
	/*---------------------------------- stands for useful data line in WeatherGrid. ----------------------------------*/
	public static final String useful_data_sign = "_:_";


	/*---------------------------------- bean default values ----------------------------------*/
	public static final int weather_defalut_value = -101;
	public static final int traffic_severity_default_value = 0;
	public static final String link_default_value = "";
	public static final String comment_default_value = "";
	public static final int agree_default_value = 0;
	public static final int disagree_default_value = 0;
	public static final int sealevel_default_value = -99999;
	public static final int xmlParser_length_default_value = 1000;
		
}
