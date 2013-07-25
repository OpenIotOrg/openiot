package org.openiot.gsn.acquisition2.client;

import org.openiot.gsn.acquisition2.messages.DataMsg;

public interface MessageHandler {
  
  public boolean messageToBeProcessed(DataMsg dataMessage);
  
  public void restartConnection () ;
  
}
