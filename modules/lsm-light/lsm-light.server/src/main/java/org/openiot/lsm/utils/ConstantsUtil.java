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
