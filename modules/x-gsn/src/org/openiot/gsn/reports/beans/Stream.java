package org.openiot.gsn.reports.beans;

import java.util.Collection;

public class Stream {

	private String streamName;
	
	private String lastUpdate;
		
	private Collection<Data> datas;

	public Stream (String streamName, String lastUpdate, Collection<Data> datas) {
		this.streamName = streamName;
		this.lastUpdate = lastUpdate;
		this.datas = datas;
	}
	
	public String getLastUpdate() {
		return lastUpdate;
	}

	public String getStreamName() {
		return streamName;
	}

	public Collection<Data> getDatas() {
		return datas;
	}
}
