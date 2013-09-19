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
public class ThreadUtil {
	
	public static void sleepForSomeTime(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleepForDays(int day){
		sleepForSomeTime(day * 24 * 60 * 60 * 1000);
	}
	
	public static void sleepForHours(int hour){
		sleepForSomeTime(hour * 60 * 60 * 1000);
	}
	
	public static void sleepForMinutes(int minute){
		sleepForSomeTime(minute * 60 * 1000);
	}
	
	public static void sleepForSeconds(int second){
		sleepForSomeTime(second * 1000);
	}
}
