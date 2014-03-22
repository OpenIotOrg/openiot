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

import org.openiot.gsn.networking.ActionPort;
import org.openiot.gsn.networking.NetworkAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SafeStorageController {

	public static final String SAFE_STORAGE_SHUTDOWN = "SS SHUTDOWN";

	public static transient Logger logger = Logger.getLogger(SafeStorageController.class);

	public SafeStorageController(final SafeStorageServer safeStorageServer, int safeStorageControllerPort) {
		super();
		logger.info("Started Safe Storage Controller on port " + safeStorageControllerPort);
		ActionPort.listen(safeStorageControllerPort, new NetworkAction(){
			public boolean actionPerformed(Socket socket) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String incomingMsg = reader.readLine();
					
					if (incomingMsg != null && incomingMsg.equalsIgnoreCase(SAFE_STORAGE_SHUTDOWN)) {
						safeStorageServer.shutdown();
						return false;
					}
					else return true;
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					return false;
				}
			}});
	}
}
