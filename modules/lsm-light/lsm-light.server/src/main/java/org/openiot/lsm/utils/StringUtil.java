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
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class StringUtil {
	
  	/**
  	 * at first the method will remove the first and last blanks.
  	 * @param input the quoted string
  	 * @return
  	 */
  	public static String removeFirstAndLastQuotes(String input){
		int index_first = input.indexOf("\"");
		int index_last = input.lastIndexOf("\"");
		if(index_first != -1 && index_last != -1 && index_last > index_first){
			return input.substring(index_first+1,index_last);
		}else{
			return input;
		}
  	}
  	
  	/**
  	 * as the method name suggest.
  	 * @param input
  	 * @return
  	 */
  	public static String toFirstUpperLetter(String input){
  		String first_lower_letter = input.substring(0,1);
  		String first_upper_letter = first_lower_letter.toUpperCase();
  		return first_upper_letter + input.substring(1);
  	}
  	
  	public static String replaceAll(String input, String origin, String replacement){
  		return input.replaceAll(origin, replacement);
  	}
  	
  	/**
  	 * trim the blanks in the string
  	 * @param input
  	 * @return
  	 */
  	public static String trimBlanksInner(String input){
  		return StringUtil.replaceAll(input, " ", "");
  	}
  	
  	/**
  	 * replace all sign '_' with blank
  	 * @param input
  	 * @return
  	 */
  	public static String replaceAll_WithBlank(String input){
  		return StringUtil.replaceAll(input, "_", " ");
  	}
  	
  	/**
  	 * replace all blanks with the sign '_'
  	 * @param input
  	 * @return
  	 */
  	public static String replaceAllBlankWith_(String input){
  		return StringUtil.replaceAll(input, " ", "_");
  	}
}
