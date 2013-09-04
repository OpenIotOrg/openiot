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
import java.text.DecimalFormat;

public class NumberUtil {
	
	public static boolean isDouble(String input){
		boolean result = false;
		if(input == null || input.trim().equals("") || input.trim().equals("NaN")){
			return false;
		}
		try {
			Double.parseDouble(input);
			result = true;
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}
	
	public static boolean isInteger(String input){
		boolean result = false;
		if(input == null || input.trim().equals("")){
			return false;
		}
		try {
			Integer.parseInt(input);
			result = true;
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}
	
	public static boolean isLong(String input){
		boolean result = false;
		if(input == null || input.trim().equals("")){
			return false;
		}
		try {
			Long.parseLong(input);
			result = true;
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}
	
	/**
	 *
	 * @param input
	 * @return
	 */
	public static double formatWith6Digits(String input){
		DecimalFormat df  = new DecimalFormat("###.000000");
		double input_double = Double.parseDouble(input);
		String result = df.format(input_double);
		double result_double = Double.parseDouble(result);
		return result_double;
	}
	
	/**
	 *
	 * @param input
	 * @return
	 */
	public static double formatWithNDigits(String input,int n){
		String format="###.";
		for(int i=0;i<n;i++)
			format+="0";
		DecimalFormat df  = new DecimalFormat(format);
		double input_double = Double.parseDouble(input);
		String result = df.format(input_double);
		double result_double = Double.parseDouble(result);
		return result_double;
	}
	
	/**
	 * parse the string into double array with the regex
	 * @param input
	 * @param regex
	 * @return
	 */
	public static double[] string2DoubleArray(String input, String regex){
		
		String[] temps = input.split(regex);
		double[] result = new double[temps.length];
		for(int i=0;i<temps.length;i++){
			if(isDouble(temps[i])){
				result[i] = Double.parseDouble(temps[i]);
			}else{
				return null;
			}
		}
		return result;
	}
	
	public static String to2DigitsFormat(String input){
		if(input.length() == 1){
			return "0" + input;
		}
		return input;
	}
	
	public static String to2DigitsFormat(int input){
		if(input >= 0 && input <= 9){
			return "0" + input;
		}else if(input < 0 || input >= 100){
			return null;
		}
		return input + "";
	}
	
	public static int getRandomIntegerNotMoreThan(int max){
		return (int)(Math.random() * max + 1);
	}
	
	public static String secondsToHHMMSS(int secs){
		String result="";
		int hours = secs / 3600,
		remainder = secs % 3600,
		minutes = remainder / 60,
		seconds = remainder % 60;
		String disHour = (hours < 10 ? "0" : "") + hours,
		disMinu = (minutes < 10 ? "0" : "") + minutes ,
		disSec = (seconds < 10 ? "0" : "") + seconds ;
		result = disHour +":"+ disMinu+":"+disSec;
		return result;
	}

	public static String minutesToHHMM(int mins) {
		// TODO Auto-generated method stub
		String result="";
		int hours = mins / 60,
		minutes = mins % 60;
		String disHour = (hours < 10 ? "0" : "") + hours,
		disMinu = (minutes < 10 ? "0" : "") + minutes;
		result = disHour +":"+ disMinu;
		return result;
	}
	
}
