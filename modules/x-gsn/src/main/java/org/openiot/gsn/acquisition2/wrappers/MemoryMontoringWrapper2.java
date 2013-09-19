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

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.utils.ParamParser;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import org.apache.log4j.Logger;
/**
 * The output format is:
 * 
 * heapMemoryUsage (long) ,  nonHeapMemoryUsage(long) , pendingFinalizationCount(int), timed(long)
 *
 */
public class MemoryMontoringWrapper2 extends AbstractWrapper2{
  
  private static final int          DEFAULT_SAMPLING_RATE                 = 1000;
  
  private int                       samplingRate                          = DEFAULT_SAMPLING_RATE;
  
  private final transient Logger    logger                                = Logger.getLogger( MemoryMontoringWrapper2.class );
  
  private static int                threadCounter                         = 0;
  
  private static final MemoryMXBean mbean                                 = ManagementFactory.getMemoryMXBean( );
  
  public boolean initialize ( ) {
     setName( "MemoryMonitoringWrapper-Thread" + ( ++threadCounter ) );
     AddressBean addressBean = getActiveAddressBean( );
     if ( addressBean.getPredicateValue( "sampling-rate" ) != null ) {
        samplingRate = ParamParser.getInteger( addressBean.getPredicateValue( "sampling-rate" ) , DEFAULT_SAMPLING_RATE );
        if ( samplingRate <= 0 ) {
           logger.warn( "The specified >sampling-rate< parameter for the >MemoryMonitoringWrapper< should be a positive number.\nGSN uses the default rate (" + DEFAULT_SAMPLING_RATE + "ms )." );
           samplingRate = DEFAULT_SAMPLING_RATE;
        }
     }
     return true;
  }
  
  public void run ( ) {
     while ( isAlive() ) {
        try {
           Thread.sleep( samplingRate );
        } catch ( InterruptedException e ) {
           logger.error( e.getMessage( ) , e );
        }
        long heapMemoryUsage = mbean.getHeapMemoryUsage( ).getUsed( );
        long nonHeapMemoryUsage = mbean.getNonHeapMemoryUsage( ).getUsed( );
        int pendingFinalizationCount = mbean.getObjectPendingFinalizationCount( );
        
        postStreamElement(  heapMemoryUsage ,  nonHeapMemoryUsage , pendingFinalizationCount, System.currentTimeMillis( ));
     }
  }
  
  public void dispose ( ) {
     threadCounter--;
  }
  
  public String getWrapperName ( ) {
     return "System memory consumption usage";
  }
  
}
