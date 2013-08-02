package org.openiot.gsn.wrappers.wsn.simulator;

public class DataPacket {
   
   private int             parent;
   
   private int             identifier;
   
   private int             value;
   
   private int             typeOfPacket;
   
   public static final int ROUTING_AND_DATA_PACKET    = 1;
   
   public static final int ROUTING_WITHOUT_DATA       = 2;
   
   public static final int TEMPREATURE_REQUEST_PACKET = 3;
   
   public int getTypeOfPacket ( ) {
      return typeOfPacket;
   }
   
   public DataPacket ( int identifier , int parent , int tempreature , int typeOfThePacket ) {
      this.identifier = identifier;
      this.parent = parent;
      this.value = tempreature;
      this.typeOfPacket = typeOfThePacket;
   }
   
   public int getParent ( ) {
      return parent;
   }
   
   public int getIdentifier ( ) {
      return identifier;
   }
   
   public int getValue ( ) {
      return value;
   }
   
   public String toString ( ) {
      return "DataPacket{" + "parent=" + parent + ", identifier=" + identifier + ", value=" + value + '}';
   }
}
