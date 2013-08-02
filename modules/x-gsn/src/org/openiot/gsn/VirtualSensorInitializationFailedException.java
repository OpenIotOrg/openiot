package org.openiot.gsn;

public class VirtualSensorInitializationFailedException extends Exception {
   
   public VirtualSensorInitializationFailedException ( ) {
      super( );
   }
   
   public VirtualSensorInitializationFailedException ( String message ) {
      super( message );
   }
   
   public VirtualSensorInitializationFailedException ( String message , Throwable cause ) {
      super( message , cause );
   }
   
   public VirtualSensorInitializationFailedException ( Throwable cause ) {
      super( cause );
   }
   
}
