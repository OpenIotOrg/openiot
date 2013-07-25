package org.openiot.gsn.reports.beans;

import java.util.Collection;

public class Report {
	
	private String reportName;
	
	private String creationTime;
	
	private String aggregationCriterion;
	
	private String standardCriteria;
	
	private String maxNumber;
	
	private Collection<VirtualSensor> virtualSensors;
	
	public Report (String reportName, String creationTime, String aggregationCriterion, String standardCriteria, String maxNumber, Collection<VirtualSensor> virtualSensors) {
		this.reportName = reportName;
		this.creationTime = creationTime;
		this.aggregationCriterion = aggregationCriterion;
		this.standardCriteria = standardCriteria;
		this.maxNumber = maxNumber;
		this.virtualSensors = virtualSensors;
	}

	public String getReportName() {
		return reportName;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public Collection<VirtualSensor> getVirtualSensors() {
		return virtualSensors;
	}

	public String getAggregationCriterion() {
		return aggregationCriterion;
	}

	public String getStandardCriteria() {
		return standardCriteria;
	}

	public String getMaxNumber() {
		return maxNumber;
	}
}
