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
