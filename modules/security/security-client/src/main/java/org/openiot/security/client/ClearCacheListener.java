package org.openiot.security.client;

import org.apache.shiro.subject.PrincipalCollection;

public interface ClearCacheListener {
	public void clearCache(PrincipalCollection principals);
}
