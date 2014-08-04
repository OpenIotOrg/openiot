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

public class VirtualSensor {
	
	private String virtualSensorName;
	
	private String latitude;
	
	private String longitude;
	
	private Collection<Stream> reportFields;
	
	public VirtualSensor (String virtualSensorName, String latitude, String longitude, Collection<Stream> reportFields) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.virtualSensorName = virtualSensorName;
		this.reportFields = reportFields;
	}

	public Collection<Stream> getReportFields() {
		return reportFields;
	}

	public String getVirtualSensorName() {
		return virtualSensorName;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
}
