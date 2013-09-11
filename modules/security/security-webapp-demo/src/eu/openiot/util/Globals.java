package eu.openiot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Globals {

	/**
	 * Use this logger to log development events since the business events are
	 * logged into the database (i.e. all the loggers created for classes that
	 * start with eu.openiot log into the database)
	 */
	public static Logger gLogger = LoggerFactory.getLogger(Globals.class);
	
	
}
