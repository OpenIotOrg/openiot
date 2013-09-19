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

package org.openiot.gsn.tests;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

public class MockWrapper extends AbstractWrapper{

  private boolean disposedCalled = false;
  private boolean initializedCalled = false;
  private ArrayList<StreamElement> streamElements= new ArrayList<StreamElement>();
  private boolean releaseResourcesCalled=false;

  private DataField[] outputStructure;
  private int dbAlias;


  public boolean isdisposedCalled() {
    return disposedCalled;
  }

  public void setdisposedCalled(boolean disposedCalled) {
    this.disposedCalled = disposedCalled;
  }

  public boolean isInitializedCalled() {
    return initializedCalled;
  }

  public void setInitializedCalled(boolean initializedCalled) {
    this.initializedCalled = initializedCalled;
  }

  public DataField[] getOutputStructure() {
    return outputStructure;
  }

  public void setOutputStructure(DataField[] outputStructure) {
    this.outputStructure = outputStructure;
  }

  public void dispose() {
    disposedCalled = true;
  }

  public DataField[] getOutputFormat() {
    return outputStructure;
  }

  public String getWrapperName() {
    return null;
  }

  public boolean initialize() {
    return (initializedCalled  =true);
  }


  protected void postStreamElement(long timestamp, Serializable[] values) {
    StreamElement se = new StreamElement(getOutputFormat(),values,timestamp);
    streamElements.add(se); 
  }

  protected void postStreamElement(Serializable... values) {
    StreamElement se = new StreamElement(getOutputFormat(),values,System.currentTimeMillis());
    streamElements.add(se); 
  }

  protected Boolean postStreamElement(StreamElement se) {
    streamElements.add(se);
    return true;
  }

  public ArrayList<StreamElement> getStreamElements() {
    return streamElements;
  }

  public void releaseResources() throws SQLException {
    releaseResourcesCalled=true;
  }

  public boolean isReleaseResourcesCalled() {
    return releaseResourcesCalled;
  }

  public void setReleaseResourcesCalled(boolean releaseResourcesCalled) {
    this.releaseResourcesCalled = releaseResourcesCalled;
  }

  public int getDBAlias() {
    return dbAlias;
  }

  public int getDbAlias() {
    return dbAlias;
  }

  public void setDbAlias(int dbAlias) {
    this.dbAlias = dbAlias;
  }

  public boolean manualDataInsertion(StreamElement se) {
    return postStreamElement(se);
  }

}
