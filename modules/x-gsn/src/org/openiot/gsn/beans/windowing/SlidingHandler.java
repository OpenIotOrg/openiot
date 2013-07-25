package org.openiot.gsn.beans.windowing;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.StreamSource;

public interface SlidingHandler {
	
	public void addStreamSource(StreamSource streamSource);
	
	public void removeStreamSource(StreamSource streamSource);

	public boolean dataAvailable(StreamElement streamElement);

	public boolean isInterestedIn(StreamSource streamSource);

	public long getOldestTimestamp();
	
	public void dispose();

}
