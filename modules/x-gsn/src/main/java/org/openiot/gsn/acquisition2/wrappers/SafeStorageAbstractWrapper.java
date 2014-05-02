/**
* Copyright (c) 2011-2014, OpenIoT
*
* This file is part of OpenIoT.
*
* OpenIoT is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, version 3 of the License.
*
* OpenIoT is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
*
* Contact: OpenIoT mailto: info@openiot.eu
* @author Timotee Maret
* @author Ali Salehi
* @author Mehdi Riahi
*/

package org.openiot.gsn.acquisition2.wrappers;

import org.openiot.gsn.acquisition2.client.MessageHandler;
import org.openiot.gsn.acquisition2.client.SafeStorageClientSessionHandler;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
/**
* Required parameters:
* ss-port
* ss-host
* wrapper-name
*
*/
public abstract class SafeStorageAbstractWrapper extends AbstractWrapper implements MessageHandler{

private static final long CONNECTION_RETRY_TIME = 10000;

  private final transient Logger logger = Logger.getLogger ( SafeStorageAbstractWrapper.class );

  public void dispose() {
    // TODO
  }

  public String getWrapperName() {
    return "Safe Storage Proxy - "+key;
  }

  String key,ss_host;
  AddressBean wrapperDetails;
  int ss_port;

  public boolean initialize() {
    String wrapper = getActiveAddressBean().getPredicateValue("wrapper-name");
    String vs = getActiveAddressBean().getVirtualSensorName();
    String inputStreamName = getActiveAddressBean().getInputStreamName();
    wrapperDetails = getActiveAddressBean();
    key = new StringBuilder(vs).append("/").append(inputStreamName).append("/").append(wrapper).toString();
    ss_host = getActiveAddressBean().getPredicateValue("ss-host");
    ss_port = getActiveAddressBean().getPredicateValueAsInt("ss-port",-1);
    return true;
  }
  public void run() {
boolean connected = false;
while (! connected) {
connected = connect(ss_host,ss_port,wrapperDetails,this,key);
if (! connected) {
try {
Thread.sleep(CONNECTION_RETRY_TIME);
} catch (InterruptedException e) {
logger.error(e.getMessage());
}
}
}
  }

 /**
* HELPER METHOD FOR CONNECTING TO STORAGE SERVER
*/
  public boolean connect(String host,int port,AddressBean wrapperDetails,MessageHandler handler,String requester) {
    int CONNECT_TIMEOUT = 30; // seconds
    NioSocketConnector connector = new NioSocketConnector();
    // Change the worker timeout to 1 second to make the I/O thread quit soon
    // when there's no connection to manage.
    //connector.setWorkerTimeout(1);
    // Configure the service.
connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
    ObjectSerializationCodecFactory oscf = new ObjectSerializationCodecFactory();
    oscf.setDecoderMaxObjectSize(oscf.getEncoderMaxObjectSize());
    //logger.debug("MINA Decoder MAX: " + oscf.getDecoderMaxObjectSize() + " MINA Encoder MAX: " + oscf.getEncoderMaxObjectSize());
    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(oscf));
connector.setHandler(new SafeStorageClientSessionHandler(wrapperDetails,handler,key ));
    IoSession session = null;
    try {
      ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
      future.awaitUninterruptibly();
      session = future.getSession();
      return true;
    } catch (RuntimeException e) {
      logger.error("Failed to connect to SafeStorage on "+host+":"+port);
      return false;
    }finally {
      if (session!=null) {
        session.getCloseFuture().awaitUninterruptibly();
      }
    }
  }

  public void restartConnection () {
run();
  }
}