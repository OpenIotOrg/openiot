package org.openiot.gsn.wrappers.wsn.simulator;

public class RequestFromWebToWSN {
   
   private int             nodeId;
   
   private int             action;
   
   public static final int ASK_FOR_TEMPREATURE = 1;
   
   public static final int ASK_FOR_HIGHER_RATE = 2;
   
   public static final int ASK_FOR_LOWER_RATE  = 3;
   
   public static final int ASK_TO_STOP         = 4;
   
   public static final int ASK_TO_START        = 5;
   
   public RequestFromWebToWSN ( int nodeId , int action ) {
      this.nodeId = nodeId;
      this.action = action;
   }
   
   public int getNodeId ( ) {
      return nodeId;
   }
   
   public int getAction ( ) {
      return action;
   }
   
   public String toString ( ) {
      return "RequestFromWebToWSN{" + "nodeId=" + nodeId + ", action=" + action + '}';
   }
   
   public boolean equals ( Object o ) {
      if ( this == o ) return true;
      if ( o == null || getClass( ) != o.getClass( ) ) return false;
      
      final RequestFromWebToWSN that = ( RequestFromWebToWSN ) o;
      
      if ( action != that.action ) return false;
      if ( nodeId != that.nodeId ) return false;
      
      return true;
   }
   
   public int hashCode ( ) {
      int result;
      result = nodeId;
      result = 29 * result + action;
      return result;
   }
}
