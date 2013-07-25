package org.openiot.gsn.utils.protocols.EPuck;

import org.openiot.gsn.utils.protocols.AbstractHCIQueryWithoutAnswer;

import java.util.Vector;

import org.apache.log4j.Logger;

public class SetSpeed extends AbstractHCIQueryWithoutAnswer {
	
	private static final transient Logger logger = Logger.getLogger( SetSpeed.class );
	public static final String queryDescription = "Set the speed of the EPuck robot's two wheels.";
	public static final String[] paramsDescriptions = {"Speed of the left wheel.","Speed of the right wheel."};
   
	public SetSpeed (String name) {
      super(name, queryDescription, paramsDescriptions);
   }
   
   /*
    * This query takes exactly two Integer objects as parameters.
    * The first one sets the speed for the left wheel and the second
    * one sets the speed for the right wheel.
    * If there is an error with the parameters, the null pointer
    * is returned.
    */
   public byte[] buildRawQuery ( Vector < Object > params ) {
      byte[] query = null;

      if(params.size() == 2 && (params.get(0) instanceof String) && (params.get(1) instanceof String)) {
         Integer leftSpeed, rightSpeed;
         String textLeftSpeed, textRightSpeed;
         //leftSpeed = (Integer)params.get(1);
         //rightSpeed = (Integer)params.get(2);
         //String textLeftSpeed = leftSpeed.toString( );
         //String textRightSpeed = rightSpeed.toString( );
         textLeftSpeed = (String) params.get(0);
         textRightSpeed = (String) params.get(1);
         
         query = new byte[3+textLeftSpeed.length()+textRightSpeed.length()];
         query[0] = 'D';
         query[1] = ',';
         byte[] bytesLeftSpeed = textLeftSpeed.getBytes();
         for(int i = 0; i < bytesLeftSpeed.length; i++ )
            query[2+i] = bytesLeftSpeed[i];
         query[2+bytesLeftSpeed.length]=',';
         byte[] bytesRightSpeed = textRightSpeed.getBytes();
         for(int i = 0; i < bytesRightSpeed.length; i++ )
            query[3+bytesLeftSpeed.length+i] = bytesRightSpeed[i];
      } else {
    	  logger.warn("Bad arguments for query ! (Number=" + params.size() + " and should be 2)" );
      }
      return query;
   }
}
