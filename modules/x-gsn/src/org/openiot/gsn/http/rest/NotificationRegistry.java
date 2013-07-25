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
