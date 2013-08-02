package org.openiot.gsn.utils;

public interface ChangeListener {
   
   public void changeHappended ( String changeType , Object changedKey , Object changedValue );
}
