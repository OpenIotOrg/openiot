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
package org.openiot.ui.sensorschema.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of helper methods
 */
public class Utils {
	
	/**
	 * Load a Propoerties file from a Web Context
	 *
	 * @param <T> 
	 * @param path 
	 * @param className 
	 * @return InputStream
	 */
	public static <T> InputStream getConfigAsInputStream(String path, Class<T> className){
		ClassLoader classLoader = className.getClassLoader();
	    return classLoader.getResourceAsStream(path);
	}
	
	/**
	 * Obtain a instance of the logger class and pass it to the calling method
	 *
	 * @param <T> 
	 * @param className 
	 * @return Logger
	 */
	public static <T> Logger getLogger(Class<T> className){
		return LoggerFactory.getLogger(className);
	}
	
	/**
	 * Converts a List to an Array
	 * 
	 *
	 * @param array 
	 * @return List
	 */
	public static List toList(String[] array) {
		return Arrays.asList(array);
	}
	
	/**
	 * Clean the JSON string
	 *
	 * @param string 
	 * @return 
	 */
	public static String removeBrackets(String string) {
		if (string == null || string.trim().compareTo("") == 0){
			return null;
		}
		if (string.startsWith("[")){
			string = string.substring(1);
		}
		if (string.endsWith("]")){
			string = string.substring(0, string.length()-1);
		}
		if (string.startsWith("\"")){
			string = string.substring(1);
		}
		if (string.endsWith("\"")){
			string = string.substring(0, string.length()-1);
		}
		if (string.trim().compareTo("null")==0){
			string = null;
		}
		return string;
	}
	
	/**
	 * format the output from the server into HTML format
	 * currently not used
	 *
	 * @param html 
	 * @return 
	 */
	@Deprecated
	public static String formatHTML(String html){
		String temp_html = html.replace("<", "&lt");
		temp_html = temp_html.replace(">", "&gt");
		temp_html = "<pre><code>" + temp_html + "</pre></code>";
		return temp_html;
	}
}
