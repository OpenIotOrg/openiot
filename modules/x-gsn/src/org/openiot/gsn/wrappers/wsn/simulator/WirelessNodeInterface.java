package org.openiot.gsn.wrappers.wsn.simulator;

public interface WirelessNodeInterface {
   
   public WirelessNode getParent ( );
   
   public void send ( DataPacket e );
}
