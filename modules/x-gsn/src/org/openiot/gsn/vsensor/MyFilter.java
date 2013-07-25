package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;

import org.apache.log4j.Logger;

public class MyFilter extends AbstractVirtualSensor {

  private static final transient Logger logger = Logger.getLogger( BridgeVirtualSensor.class );

  public boolean initialize ( ) {
    return true;
  }

  public void dataAvailable ( String inputStreamName , StreamElement data ) {
    
    dataProduced( data );
    if ( logger.isDebugEnabled( ) ) logger.debug( "Data received under the name: " + inputStreamName );
  }

  public void dispose ( ) {

  }

}
