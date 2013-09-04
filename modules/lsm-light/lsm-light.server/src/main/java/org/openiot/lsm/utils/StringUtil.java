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
