/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
 * @author Timotee Maret
*/

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
