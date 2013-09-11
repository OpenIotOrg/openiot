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

package org.openiot.gsn.acquisition2.client;

import org.openiot.gsn.Main;
import org.openiot.gsn.acquisition2.messages.DataMsg;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.utils.KeyValueImp;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

public class SafeStorageClient {
  
  private static final int CONNECT_TIMEOUT = 30; // seconds 
  
  private static transient Logger                                logger                              = Logger.getLogger ( SafeStorageClient.class );
  
  
  public SafeStorageClient(String host,int port,AddressBean wrapperDetails) {
    SocketConnector connector = new SocketConnector();
    // Change the worker timeout to 1 second to make the I/O thread quit soon
    // when there's no connection to manage.
    connector.setWorkerTimeout(1);
    // Configure the service.
    SocketConnectorConfig cfg = new SocketConnectorConfig();
    cfg.setConnectTimeout(CONNECT_TIMEOUT);
    cfg.getFilterChain().addLast("codec",   new ProtocolCodecFilter( new ObjectSerializationCodecFactory()));
    IoSession session = null;
    try {
      ConnectFuture future = connector.connect(new InetSocketAddress(host, port), new SafeStorageClientSessionHandler(wrapperDetails ,new MessageHandler() {

        public boolean messageToBeProcessed(DataMsg dataMessage) {
          System.out.println(dataMessage);
          return true;
        }

		public void restartConnection() {
			// TODO Auto-generated method stub
			
		}},"requester-1"), cfg);
      future.join();
      session = future.getSession();
    } catch (RuntimeIOException e) {
      logger.error("Failed to connect to "+host+":"+port); 
      logger.error( e.getMessage(),e);
    }finally {
      if (session!=null)
        session.getCloseFuture().join();
    }
  }
  
  public static void main(String[] args) {
    PropertyConfigurator.configure ( Main.DEFAULT_GSN_LOG4J_PROPERTIES );
    AddressBean wrapperDetails = new AddressBean("mem2",new KeyValueImp("MyKey","MyValue"));
    new SafeStorageClient("localhost",12345,wrapperDetails);
  }
}
