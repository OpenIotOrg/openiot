package org.openiot.lsm.sdum.model.beans;

import java.util.Date;

import org.openiot.lsm.sdum.model.beans.QueryScheduleBean;

public class QueryControlsBean 
{
	private String id;
	private String trigger;
	private Boolean reportIfEmpty;
	private Date initialRecordTime;
	private QueryScheduleBean querySchedBean;
	
	public QueryControlsBean(){
	}	
	public QueryControlsBean(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public Date getInitialRecordTime() {
		return initialRecordTime;
	}
	public void setInitialRecordTime(Date initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}

	public Boolean getReportIfEmpty() {
		return reportIfEmpty;
	}

	public void setReportIfEmpty(Boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}
	public QueryScheduleBean getQuerySchedBean() {
		return querySchedBean;
	}
	public void setQuerySchedBean(QueryScheduleBean querySchedBean) {
		this.querySchedBean = querySchedBean;
	}
	
	
	public String toStringIfEmpty() {
		
		StringBuffer qControl = new StringBuffer();
		qControl.append("\"qControl\":");
		qControl.append("{");

		qControl.append("\"reportIfEmpty\":"+getReportIfEmpty());
			
		qControl.append("}");
		
		return qControl.toString();
	}
	
}
