package org.openiot.sdum.core.api.impl.PollForReport;

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
 */

public class ServicePresentationData 
{
	
	private String widgetPreID;
	private String widgetID;
	private String widgetAttrID;
	private String widgetAttrName;
	private String widgetAttrDesc;
	
	
	public String getWidgetPreID() {
		return widgetPreID;
	}
	public void setWidgetPreID(String widgetPreID) {
		this.widgetPreID = widgetPreID;
	}
	
	public String getWidgetID() {
		return widgetID;
	}
	public void setWidgetID(String widgetID) {
		this.widgetID = widgetID;
	}
	
	public String getWidgetAttrID() {
		return widgetAttrID;
	}
	public void setWidgetAttrID(String widgetAttrID) {
		this.widgetAttrID = widgetAttrID;
	}
	
	public String getWidgetAttrName() {
		return widgetAttrName;
	}
	public void setWidgetAttrName(String widgetAttrName) {
		this.widgetAttrName = widgetAttrName;
	}
	
	public String getWidgetAttrDesc() {
		return widgetAttrDesc;
	}
	public void setWidgetAttrDesc(String widgetAttrDesc) {
		this.widgetAttrDesc = widgetAttrDesc;
	}
}//class
