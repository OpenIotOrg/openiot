package org.openiot.gsn.acquisition2.wrappers;

import org.openiot.gsn.acquisition2.messages.DataMsg;
import org.openiot.gsn.beans.DataField;

import java.io.Serializable;

public class MemoryWrapperProcessor extends SafeStorageAbstractWrapper{

//  heapMemoryUsage (long) ,  nonHeapMemoryUsage(long) , pendingFinalizationCount(int), timed(long)
  
  DataField[] output = new DataField[] {new DataField("heap_memory_usage","bigint"),new DataField("non_heap_memory_usage","bigint"),new DataField("pending_finalization_count","int")};
  
  public DataField[] getOutputFormat() {
    return output;
  }

  public boolean messageToBeProcessed(DataMsg dataMessage) {
    Long heapMemoryUsage = (Long) dataMessage.getData()[0];
    Long nonHeapMemoryUsage = (Long) dataMessage.getData()[1];
    Integer pendingFinalizationCount = (Integer) dataMessage.getData()[2];
    Long ts = (Long) dataMessage.getData()[3];
    postStreamElement(ts.longValue(),new Serializable[] {heapMemoryUsage,nonHeapMemoryUsage,pendingFinalizationCount});
    return true;
  }
  
}
