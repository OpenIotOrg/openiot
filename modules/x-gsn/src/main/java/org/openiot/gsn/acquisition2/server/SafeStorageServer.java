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
 * @author Timotee Maret
 * @author Ali Salehi
*/

package org.openiot.gsn.acquisition2.server;

import org.openiot.gsn.acquisition2.SafeStorage;
import org.openiot.gsn.acquisition2.SafeStorageDB;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeStorageServer {

	public static final byte SS_START_MODE   = 1;
	public static final byte SS_CLEAN_MODE   = 100;

	private static transient final Logger logger = LoggerFactory.getLogger(SafeStorageServer.class);

	private final NioSocketAcceptor acceptor;

	public SafeStorageServer(int portNo) throws IOException, ClassNotFoundException, SQLException {
		SafeStorage ss  = new SafeStorage(portNo);
		acceptor = new NioSocketAcceptor();
		//acceptor.getDefaultConfig().setThreadModel(ThreadModel.MANUAL);

		// Prepare the service configuration.
		acceptor.setReuseAddress(true);
		ObjectSerializationCodecFactory oscf = new ObjectSerializationCodecFactory();
	    oscf.setDecoderMaxObjectSize(oscf.getEncoderMaxObjectSize());
	    acceptor.getFilterChain().addLast("codec",   new ProtocolCodecFilter(oscf));
	    // Create an unbounded Thread pool
	    ThreadPoolExecutor tpe = new ThreadPoolExecutor (0, Integer.MAX_VALUE, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>()) ;
	    acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(tpe));

	    logger.debug("MINA Decoder MAX: {} MINA Encoder MAX: {}.",oscf.getDecoderMaxObjectSize(), oscf.getEncoderMaxObjectSize());
		acceptor.setHandler(new SafeStorageServerSessionHandler(ss));
	    acceptor.bind(new InetSocketAddress(portNo));
		logger.info("Safe Storage Server is listening on port: {}.", portNo);
	}

  public void shutdown () {
	  acceptor.unbind();
  }

  public static void main(String[] args) throws Exception {
	int safeStorageServerPort = Integer.parseInt(args[0]);
	int safeStorageControllerPort = Integer.parseInt(args[1]);
	byte safeStorageMode = Byte.parseByte(args[2]);
	switch (safeStorageMode) {
		case SS_START_MODE : {
			SafeStorageServer sss = new SafeStorageServer(safeStorageServerPort);
			new SafeStorageController(sss, safeStorageControllerPort);
			break;
		}
		case SS_CLEAN_MODE : {
			SafeStorageDB storage = new SafeStorageDB(safeStorageServerPort);
			storage.dropAllTables();
			logger.warn("SafeStorage database is now clean and empty.");
			break;
		}
		default : logger.error("Not valid SafeStorage mode >{}<", safeStorageMode);
	}
  }
}
