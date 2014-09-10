package org.openiot.cupus.mobile.sensors.common;

/**
 * Created by kpripuzic on 1/14/14.
 */

abstract public class AbstractSensor {
	
	private boolean initialized = false;

	public abstract boolean initialize();
	
	public final boolean isInitialized() {
		return initialized;
	}

	protected final void setInitialized(boolean initialized) {
		this.initialized = this.initialized;
	}

	public abstract boolean terminate();
	
	public abstract int getId();
}
