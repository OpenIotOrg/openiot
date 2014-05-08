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
 * @author Ali Salehi
*/

package org.openiot.gsn.http.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NotificationRegistry {
	
	private static NotificationRegistry singleton = new NotificationRegistry();
	
	private Map<Double, PushRemoteWrapper> clients =Collections.synchronizedMap( new HashMap<Double, PushRemoteWrapper>());
	
	public static NotificationRegistry getInstance() {
		return singleton;
	}
	
	public void addNotification(Double notificationId,PushRemoteWrapper wrapper ) {
		clients.put(notificationId, wrapper);
	}
	
	public void removeNotification(Double notificationId) {
		clients.remove(notificationId);
	}
	
	public PushRemoteWrapper getNotification(Double notificationId) {
		return clients.get(notificationId);
	}
	
	
}
