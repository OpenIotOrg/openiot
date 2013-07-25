package org.openiot.gsn;

import org.openiot.gsn.beans.VSensorConfig;

public interface VSensorStateChangeListener {
	public boolean vsLoading(VSensorConfig config);
	
	public boolean vsUnLoading(VSensorConfig config);
	
	public void release() throws Exception;
}
