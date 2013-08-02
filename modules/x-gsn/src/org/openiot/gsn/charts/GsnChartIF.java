package org.openiot.gsn.charts;

import org.openiot.gsn.reports.beans.Data;

import java.util.Collection;

import org.jfree.chart.JFreeChart;

public interface GsnChartIF {
	
	public JFreeChart createChart (Collection<Data> datas) ;

}
