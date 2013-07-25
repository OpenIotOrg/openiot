package org.openiot.gsn.wrappers.general;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.wrappers.AbstractWrapper;

import org.apache.log4j.Logger;

public class EmptyWrapper extends AbstractWrapper {
   
   private final transient Logger               logger        = Logger.getLogger( EmptyWrapper.class );
   
   private int                                  threadCounter = 0;
   
   private static   DataField [] dataField  ;
   
   public boolean initialize (  ) {
      setName( "EmptyWrapper-Thread" + ( ++threadCounter ) );
      AddressBean addressBean = getActiveAddressBean( );
      dataField = new DataField[] { new DataField( "DATA" , "int" , "incremental int" ) };
      return true;
   }
   
   public void run ( ) {
      while ( isActive( ) ) {
    	  // do something
      }
   }
   
   public  DataField[] getOutputFormat ( ) {
      return dataField;
   }
   public String getWrapperName() {
    return "empty template";
}
   public void dispose ( ) {
      threadCounter--;
   }
   
}
