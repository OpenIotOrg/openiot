package org.openiot.gsn.utils.protocols.EPuck;

import org.openiot.gsn.utils.protocols.AbstractHCIProtocol;
import org.openiot.gsn.utils.protocols.BasicHCIQuery;

public class SerComProtocol extends AbstractHCIProtocol {
   
   public static final String EPUCK_PROTOCOL="EPUCK_PROTOCOL";
   // wait time in ms for an answer to a query
   public static final int EPUCK_DEFAULT_WAIT_TIME = 250;
      
   // add here a short name for the created query. This name
   // will be shown to the user so that he can choose between queries.
   public static final String SET_SPEED="Set speed", RESET="Reset";
      
   public SerComProtocol() {
	   super(EPUCK_PROTOCOL);
      // Create and add here a query of each type
      //0. Add always useful user-customisable query
	   addQuery(new BasicHCIQuery());
	   // 1. Add RESET command
      addQuery(new Reset(RESET));
      //2. Add SET_SPEED command
      addQuery(new SetSpeed(SET_SPEED));
      
   }

}
