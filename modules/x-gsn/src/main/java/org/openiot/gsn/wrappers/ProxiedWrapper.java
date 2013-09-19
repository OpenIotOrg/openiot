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

package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.AddressBean;

import org.apache.log4j.Logger;

public class ProxiedWrapper {
  
  private final static transient Logger      logger         = Logger.getLogger( ProxiedWrapper.class );
  
  String remoteHost ;
  String remotePort;
  String wrapperName;
  AddressBean wrapperParams;
  
  public ProxiedWrapper(String remoteHost, String remotePort, String wrapperName, AddressBean wrapperParams) {
    this.remoteHost = remoteHost;
    this.remotePort = remotePort;
    this.wrapperName = wrapperName;
    this.wrapperParams = wrapperParams;
  }
  
  
  
}
