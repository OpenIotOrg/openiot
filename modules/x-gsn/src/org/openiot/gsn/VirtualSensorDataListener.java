package org.openiot.gsn;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;

public interface VirtualSensorDataListener {
	public void consume(StreamElement se,VSensorConfig config);
}
