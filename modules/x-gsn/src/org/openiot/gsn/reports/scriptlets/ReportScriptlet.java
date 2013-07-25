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
