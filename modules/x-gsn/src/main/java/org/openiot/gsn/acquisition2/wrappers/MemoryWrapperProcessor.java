/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

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
