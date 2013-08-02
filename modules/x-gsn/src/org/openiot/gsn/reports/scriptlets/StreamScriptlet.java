package org.openiot.gsn.reports.scriptlets;

import org.openiot.gsn.Main;
import org.openiot.gsn.charts.GsnChartIF;
import org.openiot.gsn.charts.GsnChartJfreechart;
import org.openiot.gsn.reports.beans.Data;
import org.openiot.gsn.utils.Helpers;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;

import org.jfree.chart.JFreeChart;

public class StreamScriptlet  extends JRDefaultScriptlet {

	private SimpleDateFormat sdf = new SimpleDateFormat (Main.getContainerConfig().getTimeFormat());

	private static GsnChartIF gsnChart = new GsnChartJfreechart();

	public StreamScriptlet () {
		super () ;
	}

	public void afterDetailEval() throws JRScriptletException {
		setStatistics () ;
		setGraphic () ;
	}

	@SuppressWarnings("unchecked")
	public void setGraphic () throws JRScriptletException {
		JFreeChart chart = gsnChart.createChart((Collection<Data>) this.getFieldValue("datas"));
		this.setVariableValue("Chart", new JCommonDrawableRenderer(chart));
	}

	@SuppressWarnings("unchecked")
	public void setStatistics () throws JRScriptletException {

		String max 						= "NA";
		String min 						= "NA";
		String average 					= "NA";
		String stdDeviation 			= "NA";
		String median					= "NA";
		String nb						= "0";
		String startTime				= "NA";
		String endTime					= "NA";
		String samplingAverage			= "NA";
//		String samplingAverageUnit		= "NA";
		String nbOfNull					= "0";
		String samplingStdDeviation		= "NA";
//		String samplingStdDeviationUnit	= "NA";
		
		Collection<Data> datas = (Collection<Data>) this.getFieldValue("datas");
		if (datas.size() > 0) {
			Double max_value = Double.MIN_VALUE;
			Double min_value = Double.MAX_VALUE;
			Double average_value = 0.0;
			Double sum_value = 0.0;
			Long start_time_value = 0L;
			Long end_time_value = 0L;
			Long sampling_average_value = 0L;
			Integer nb_value = 0;
			Integer nb_of_null = 0;
			Iterator<Data> iter = datas.iterator();
			Data nextData;
			Double nextDataValue;
			while (iter.hasNext()) {
				nextData = iter.next();
				if (nextData.getValue() != null) {
					nextDataValue = (Double) nextData.getValue();
					//
					sum_value += nextDataValue;
					//
					if (nextDataValue < min_value) min_value = nextDataValue;
					if (nextDataValue > max_value) max_value = nextDataValue; 
					//					
					if (datas.size() == 1 || nb_value == datas.size() / 2) 	median = nextDataValue.toString(); 
					//
					if ( ! iter.hasNext()) {
						startTime =  sdf.format(new Date((Long)nextData.getP2())).toString();
						start_time_value = (Long)nextData.getP2();
					}
					if (nb_value == 0) 	{
						endTime = sdf.format(new Date((Long)nextData.getP2())).toString();	
						end_time_value = (Long)nextData.getP2();
					}
				}
				else {
					nb_of_null++;
				}
				nb_value++;
			}
			//
			max 			= max_value == Double.MIN_VALUE ? "NA" : max_value.toString();
			min 			= min_value == Double.MAX_VALUE ? "NA" : min_value.toString();
			nb  			= nb_value.toString();
			average_value 	= (Double)(sum_value / nb_value);
			average 		= average_value.toString();
			nbOfNull 		= nb_of_null.toString();
			//
			if (datas.size() > 1) {
				sampling_average_value = (end_time_value - start_time_value) / (nb_value - 1);
				samplingAverage = Helpers.formatTimePeriod(sampling_average_value);
			}
			//
			iter = datas.iterator();
			Double variance_value = 0.0;
			Double sampling_variance_value = 0.0;
			Long lastDataTime = end_time_value;
			int i = 0;
			while (iter.hasNext()) {
				nextData = iter.next();
				if (nextData.getValue() != null) {
					nextDataValue = (Double) nextData.getValue();
					variance_value += Math.pow((average_value - nextDataValue), 2);
					if (i > 0) {
						sampling_variance_value += Math.pow((sampling_average_value - ((lastDataTime - (Long) nextData.getP2()))), 2);
						lastDataTime = (Long) nextData.getP2();
					}
					i++;
				}
			}
			stdDeviation = ((Double)Math.sqrt(variance_value)).toString();
			if (datas.size() > 1) {
				Double sampling_std_deviation = (Double)Math.sqrt(sampling_variance_value);
				samplingStdDeviation = Helpers.formatTimePeriod(sampling_std_deviation.longValue());
			}
		}
		
		this.setVariableValue("max", max);												// ok
		this.setVariableValue("min", min);												// ok
		this.setVariableValue("average", average);										// ok
		this.setVariableValue("stdDeviation", stdDeviation);							// ok
		this.setVariableValue("median", median);										// ok
		this.setVariableValue("nb", nb);												// ok
		this.setVariableValue("startTime", startTime);									// ok
		this.setVariableValue("endTime", endTime);										// ok
		this.setVariableValue("samplingAverage", samplingAverage);						// ok
		this.setVariableValue("nbOfNull", nbOfNull);									// ok
		this.setVariableValue("samplingStdDeviation", samplingStdDeviation);			// ok
	}
}
