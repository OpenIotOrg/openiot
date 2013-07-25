package org.openiot.gsn.wrappers;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
public class WrappersUtil {
  
  public static transient Logger logger= Logger.getLogger ( WrappersUtil.class );
  
  public static final String     DEFAULT_WRAPPER_PROPERTIES_FILE  = "conf/wrappers.properties";
  public static Properties loadWrappers(HashMap<String, Class<?>> wrappers, String location) {
    Properties config = new Properties ();
    try {// Trying to load the wrapper specified in the configuration file of the container. 
      config.load(new FileReader( location ));
    } catch ( IOException e ) {
      logger.error ( "The wrappers configuration file's syntax is not compatible." );
      logger.error ( new StringBuilder ( ).append ( "Check the :" ).append ( location ).append ( " file and make sure it's syntactically correct." ).toString ( ) );
      logger.error ( "Sample wrappers extention properties file is provided in GSN distribution." );
      logger.error ( e.getMessage ( ) , e );
      System.exit ( 1 );
    }  
   // TODO: Checking for duplicates in the wrappers file.
    return config;
  }  
  public static Properties loadWrappers(HashMap<String, Class<?>> wrappers){
    return loadWrappers(wrappers,DEFAULT_WRAPPER_PROPERTIES_FILE);
  }
}
