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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.mina.common.IoSession;

public abstract class AbstractWrapper2  extends Thread{
  
  private AddressBean                        activeAddressBean;
  
  private final transient Logger    logger                                = Logger.getLogger( AbstractWrapper2.class );
  
  private LinkedBlockingQueue<Long> queue = new LinkedBlockingQueue<Long>(); 
  
  private String tableName;
  
  private IoSession network;
 
  private PreparedStatement insertPS;
  
  private boolean keepProcessedSafeStorageEntries = true;
 
  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public final AddressBean getActiveAddressBean ( ) {
    if (this.activeAddressBean==null) {
      throw new RuntimeException("There is no active address bean associated with the wrapper.");
    }
    return activeAddressBean;
  }
  
  /**
   * Only sets if there is no other activeAddressBean configured.
   * @param newVal the activeAddressBean to set
   */
  public void setActiveAddressBean ( AddressBean newVal ) {
    if (this.activeAddressBean!=null) {
      throw new RuntimeException("There is already an active address bean associated with the wrapper.");
    }
    this.activeAddressBean = newVal;
  }
  
  public IoSession getNetwork() {
    return network;
  }
  
  public void setNetwork(IoSession network) {
    this.network = network;
  }
  /**
   * Data stored as an array into the storage.
   * The meaning of each item should be specified through the documentation in the header.
   * @param values
   */
  protected void postStreamElement(Serializable... values)  {
    try {
      insertPS.clearParameters();
      insertPS.setObject(1,values);
      insertPS.executeUpdate();

      if (queue.isEmpty()) {
        final ResultSet generatedKeys = insertPS.getGeneratedKeys();
        generatedKeys.next();
        Long sqlNo = generatedKeys.getLong(1);// Getting the SEQ_NO
        generatedKeys.close();
        queue.put(sqlNo);
      }
    }catch (Exception e) {
      logger.fatal(e.getMessage(),e);
      // TODO, Logging the data into some other storage.
    }
  }
  
  public void setPreparedStatement(PreparedStatement preparedStatement) {
    this.insertPS = preparedStatement;
  }
  /**
   * Only called by the handler once handler has consumed every non-processed entries from the db.
   * @throws InterruptedException
   */
  public void canReaderDB() throws InterruptedException {
    queue.take();
  }
  /**
   * The addressing is provided in the ("ADDRESS",Collection<KeyValue>). If
   * the DataSource can't initialize itself because of either internal error or
   * inaccessibility of the host specified in the address the method returns
   * false. The dbAliasName of the DataSource is also specified with the
   * "DBALIAS" in the context. The "STORAGEMAN" points to the StorageManager
   * which should be used for querying.
   * 
   * @return True if the initialization do successfully otherwise false;
   */
  
  public abstract boolean initialize ( );
  
  public abstract void dispose ( ); // TODO, the safe storage should stop the acquisition part.
  
  public abstract String getWrapperName ( ); 
  
  public abstract void run();

public boolean isKeepProcessedSafeStorageEntries() {
	return keepProcessedSafeStorageEntries;
}

public void setKeepProcessedSafeStorageEntries(boolean keepProcessedInSafeStorage) {
	this.keepProcessedSafeStorageEntries = keepProcessedInSafeStorage;
}
}
