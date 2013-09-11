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
