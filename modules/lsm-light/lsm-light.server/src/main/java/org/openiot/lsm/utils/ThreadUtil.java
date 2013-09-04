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
