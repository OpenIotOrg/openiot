package org.openiot.gsn.utils;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Helpers {
	public static void initLoggerToDebug(){
		Properties properties = new Properties( );
		properties.put( "log4j.rootLogger" , "DEBUG,console" );
		properties.put( "log4j.appender.console" , "org.apache.log4j.ConsoleAppender" );
		properties.put( "log4j.appender.console.Threshold" , "DEBUG" );
		properties.put( "log4j.logger.com.mchange" , "WARN" );
		properties.put( "log4j.logger.org.mortbay" , "WARN" );
		properties.put( "log4j.logger.org.apache" , "WARN" );
		properties.put( "log4j.appender.console.layout" , "org.apache.log4j.PatternLayout" );
		properties.put( "log4j.appender.console.layout.ConversionPattern" , "%-6p[%d] [%t] (%13F:%L) %3x - %m%n" );
		PropertyConfigurator.configure( properties );
	}

	public static String formatTimePeriod (long timestamp) {
		if (timestamp < 1000) return timestamp + " ms";
		if (timestamp < 60 * 1000) return (timestamp / 1000) + " sec";
		if (timestamp < 60 * 60 * 1000) return (timestamp / (1000 * 60)) + " min";
		if (timestamp < 24 * 60 * 60 * 1000) return (timestamp / (1000 * 60 * 60)) + " h";
		return (timestamp / (24 * 1000 * 60 * 60)) + " day";
	}
	public static long convertTimeFromIsoToLong(String time) throws Exception {
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		return fmt.parseDateTime(time).getMillis();
	}

    public static long convertTimeFromIsoToLong(String time, String format) throws Exception {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
		return fmt.parseDateTime(time).getMillis();
	}

    public static String convertTimeFromLongToIso(long timestamp) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime dt = new DateTime(timestamp);
        return fmt.print(dt);
	}

    public static String convertTimeFromLongToIso(long timestamp, String format) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
        DateTime dt = new DateTime(timestamp);
        return fmt.print(dt);
	}

}


