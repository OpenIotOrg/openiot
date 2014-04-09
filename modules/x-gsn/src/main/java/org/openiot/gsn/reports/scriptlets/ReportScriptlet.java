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
 * @author Ali Salehi
*/

package org.openiot.gsn.reports.scriptlets;

import org.openiot.gsn.reports.beans.VirtualSensor;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class ReportScriptlet extends JRDefaultScriptlet {
	
	public ReportScriptlet () {
		super () ;
	}
	
	public void afterReportInit() throws JRScriptletException {
		setListOfVirtualSensors () ;
		setServerTimeZone () ;
	}
	
	private void setServerTimeZone () throws JRScriptletException {
		this.setVariableValue("serverTimeZone", 
				TimeZone.getDefault().getDisplayName().toString() + 
				" - " +
				TimeZone.getDefault().getID().toString()
		);
	}
	
	@SuppressWarnings("unchecked")
	private void setListOfVirtualSensors () throws JRScriptletException {
		Collection<VirtualSensor> virtualSensors = (Collection<VirtualSensor>) this.getFieldValue("virtualSensors");
		StringBuilder sb = new StringBuilder () ;
		Iterator iter = (Iterator) virtualSensors.iterator();
		String nextName;
		while (iter.hasNext()) {
			nextName = ((VirtualSensor)iter.next()).getVirtualSensorName();
			sb.append(nextName);
			if (iter.hasNext()) sb.append(", ") ;
		}
		this.setVariableValue("listOfVirtualSensors", sb.toString());
	}	
}
