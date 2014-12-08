package org.openiot.lsm.utils;

/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hoan Nguyen Mau Quoc
 *
 */
public class DateUtil {

	/**
	 * @param time the format should be like:"yyyyMMddHHmmss"
	 * @return the Date
	 */
	public static Date fullFormatDigits2Date(String time){
		Date date = null;
		if(time.matches("\\d{14}")){
			String format = "yyyyMMddHHmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				date = sdf.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * parse the time to string under the format.
	 * @param time
	 * @param format
	 * @return
	 */
	public static String date2FormatString(Date time, String format){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			result = sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Date RFC822Section5Format_to_Date(String time){
		String format = "EEE, dd MMM yyyy hh:mm a";
		Date date = string2Date(time,format);
		return date;
	}

	public static Date RFC822WUnderGroundFormat_to_date(String time){
		String format = "EEE, dd MMM yyyy hh:mm:ss";
		Date date = string2Date(time,format);
		return date;
	}

	public static boolean isRFC822WUnderGroundFormat(String time){
		String regex = "EEE, dd MMM yyyy hh:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(regex);
		try {
			sdf.parse(time);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static String date2StandardString(Date time){
		return date2FormatString(time,"yyyy-MM-dd'T'HH:mm:ss.SSS");
	}

	public static Date standardString2Date(String time){
		return string2Date(time,"yyyy-MM-dd'T'HH:mm:ss.SSS");
	}

	public static String getYearMonthDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String month_str = (month < 10 ? ("0" + month) : month + "");
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String day_str = (day < 10 ?("0" + day) : "" + day);
		return year + "-" + month_str + "-" + day_str;
	}

	public static Date string2Date(String time, String regex){
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(regex);
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * time1 &lt; time2 + days.
	 *
	 * @param time1
	 * @param time2
	 * @param days
	 * @return
	 */
	public static boolean isBefore_day(Date time1, Date time2, int days){
		if(time2 == null){
			return false;
		}else if(time1 == null){
			return true;
		}
		boolean result = false;
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(time1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(time2);
		calendar2.add(Calendar.DAY_OF_MONTH, days);
		if(calendar1.before(calendar2)){
			result = true;
		}
		return result;
	}

	public static boolean isBefore(Date time1, Date time2) {
		return time1.before(time2);
	}

	/**
	 * time1 &lt; time2 - 7_days.
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isBeforeOneWeek(Date time1, Date time2){
		return isBefore_day(time1,time2,-7);
	}

	public static void main(String[] args) {
		String time = "2012-03-21T11:13:36.089+07:00";
		Date date = DateUtil.string2Date(time,"yyyy-MM-dd'T'HH:mm:ss.SSS");
		System.out.println(date.toString());
		System.out.println(DateUtil.date2StandardString(new Date()));
//		DateTimeFormatter parser = ISODateTimeFormat.dateTime();
//        DateTime dt = parser.parseDateTime(time);
//
//        DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
//        System.out.println(dt.toString());
	}

}
