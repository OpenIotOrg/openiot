package org.openiot.gsn.beans.windowing;

import org.openiot.gsn.beans.StreamSource;

public abstract class QueryRewriter {
	protected StreamSource streamSource;
	
	public QueryRewriter(){
		
	}
	
	public QueryRewriter(StreamSource streamSource){
		setStreamSource(streamSource);
	}
	
	public abstract boolean initialize();
	
	public abstract StringBuilder rewrite(String query);
	
	public abstract void dispose();

	public abstract boolean dataAvailable(long timestamp);
	
	public StreamSource getStreamSource() {
		return streamSource;
	}

	public void setStreamSource(StreamSource streamSource) {
		this.streamSource = streamSource;
		streamSource.setQueryRewriter(this);
	}


}
