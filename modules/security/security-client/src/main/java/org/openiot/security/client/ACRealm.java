package org.openiot.security.client;

public interface ACRealm {
	public String getPermissionsURL();
	public void addClearCacheListener(ClearCacheListener listener);
}
