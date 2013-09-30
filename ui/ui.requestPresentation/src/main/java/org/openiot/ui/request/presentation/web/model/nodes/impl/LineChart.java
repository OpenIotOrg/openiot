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
package org.openiot.ui.request.presentation.web.model.nodes.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;
import org.openiot.commons.sparql.protocoltypes.model.QueryResult;
import org.openiot.commons.sparql.result.model.Binding;
import org.openiot.commons.sparql.result.model.Result;
import org.openiot.ui.request.commons.logging.LoggerService;
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

public class LineChart implements VisualizationWidget {
	
	private enum XAxisType {
		Number, DateFromResultSet, DateFromObservation
	};

	private org.primefaces.component.chart.line.LineChart widget;
	private HtmlOutputText emptyMessage;
	private Panel panel;
	private CartesianChartModel model;
	private String title;
	private String xAxisLabel;
	private XAxisType xAxisType;
	private String yAxisLabel;
	private int numSeries;
	private String[] seriesLabels;

	@Override
	public Panel createWidget(List<PresentationAttr> presentationAttributes) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		parseAttributes(presentationAttributes);

		// Instanciate linechart widget
		widget = (org.primefaces.component.chart.line.LineChart) application.createComponent(fc, "org.primefaces.component.chart.LineChart", "org.primefaces.component.chart.LineChartRenderer");
		widget.setId("lineChart_" + System.nanoTime());
		widget.setStyleClass("line-chart");
		widget.setXaxisLabel(xAxisLabel);
		widget.setYaxisLabel(yAxisLabel);
		widget.setXaxisAngle(90);
		widget.setShowMarkers(true);
		widget.setLegendPosition("s");
		if( ! xAxisType.Number.equals(xAxisType)){
			widget.setExtender("lineChartExtender");
		}		
		widget.setRendered(false);

		// Instanciate a panel to host the widget
		panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel", "org.primefaces.component.PanelRenderer");
		panel.setId("widget_panel_" + System.nanoTime());
		panel.setHeader(title);
		panel.setClosable(false);
		panel.setToggleable(false);
		panel.setStyleClass("widget");
		panel.getChildren().add(widget);

		// Generate clear data link
		CommandLink clearLink = (CommandLink) application.createComponent(fc, "org.primefaces.component.CommandLink", "org.primefaces.component.CommandLinkRenderer");
		clearLink.setAjax(true);
		clearLink.setOnclick("windowBlockUI.block();");
		clearLink.setOncomplete("windowBlockUI.unblock();");
		clearLink.setProcess("@this");
		clearLink.setStyleClass("ui-panel-titlebar-icon ui-corner-all ui-state-default");
		HtmlOutputText btn = new HtmlOutputText();
		btn.setStyleClass("ui-icon ui-icon-arrowrefresh-1-w");
		clearLink.getChildren().add(btn);
		clearLink.addActionListener(new ActionListener() {

			@Override
			public void processAction(ActionEvent arg0) throws AbortProcessingException {
				clearData();
			}
		});
		panel.getFacets().put("actions", clearLink);

		// Generate empty message
		emptyMessage = new HtmlOutputText();
		emptyMessage.setValue(FaceletLocalization.getLocalizedResourceBundle().getString("UI_WIDGET_NO_DATA"));
		emptyMessage.setStyleClass("no-data");
		emptyMessage.setRendered(true);
		panel.getChildren().add(emptyMessage);

		// Setup model
		model = new CartesianChartModel();
		for (String seriesLabel : seriesLabels) {
			model.addSeries(new ChartSeries(seriesLabel));
		}
		widget.setValue(model);

		return panel;
	}

	private boolean processGrouppedData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = true;
		Calendar cal = GregorianCalendar.getInstance();
		@SuppressWarnings("unchecked")
		Map<Object, Number>[] seriesMaps = new Map[numSeries];
		SimpleDateFormat[] seriesFormatters = new SimpleDateFormat[numSeries];
		for (int i = 0; i < numSeries; i++) {
			seriesMaps[i] = new TreeMap<Object, Number>();
		}

		for (QueryResult resultBlock : resultSet.getQueryResult()) {
			for (Result result : resultBlock.getSparql().getResults().getResult()) {

				Object xValue = null;
				Number yValue = null;
				Integer seriesIndex = null;

				boolean hasDay = false;
				boolean hasMonth = false;
				boolean hasYear = false;
				boolean hasHour = false;
				boolean hasMin = false;
				boolean hasSec = false;

				// Parse data
				for (Binding binding : result.getBinding()) {
					if (binding.getName().startsWith("x") && binding.getName().indexOf('_') != -1) {
						String dateComp = binding.getName().split("_")[1];
						int dateCompValue = Integer.valueOf(binding.getLiteral().getContent());
						if ("day".equals(dateComp)) {
							cal.set(Calendar.DAY_OF_MONTH, dateCompValue);
							hasDay = true;
						} else if ("month".equals(dateComp)) {
							// Note: Month field is 0-based
							cal.set(Calendar.MONTH, dateCompValue + 1);
							hasMonth = true;
						} else if ("year".equals(dateComp)) {
							cal.set(Calendar.YEAR, dateCompValue);
							hasYear = true;
						} else if ("hours".equals(dateComp)) {
							cal.set(Calendar.HOUR_OF_DAY, dateCompValue);
							hasHour = true;
						} else if ("minutes".equals(dateComp)) {
							cal.set(Calendar.MINUTE, dateCompValue);
							hasMin = true;
						} else if ("seconds".equals(dateComp)) {
							cal.set(Calendar.SECOND, dateCompValue);
							hasSec = true;
						}
					} else if (binding.getName().startsWith("y")) {
						// y values start at index 1 (y1, y2 e.t.c)
						seriesIndex = Integer.valueOf(binding.getName().substring(1)) - 1;
						yValue = Double.valueOf(binding.getLiteral().getContent());
					}
				}

				xValue = cal.getTime();

				// Update series
				if (seriesIndex == null || xValue == null || yValue == null) {
					continue;
				}

				// Update formatter
				if (seriesFormatters[seriesIndex] == null) {
					String dateFormat = "";
					String timeFormat = "";
					if (hasDay) {
						dateFormat += "dd";
					}
					if (hasMonth) {
						dateFormat += (dateFormat.isEmpty() ? "" : "/") + "MM";
					}
					if (hasYear) {
						dateFormat += (dateFormat.isEmpty() ? "" : "/") + "yyyy";
					}
					if (hasHour) {
						timeFormat += "HH";
					}
					if (hasMin) {
						timeFormat += (timeFormat.isEmpty() ? "" : ":") + "mm";
					}
					if (hasSec) {
						timeFormat += (timeFormat.isEmpty() ? "" : ":") + "ss";
					}
					dateFormat += " " + timeFormat;
					seriesFormatters[seriesIndex] = new SimpleDateFormat(dateFormat.trim());
				}

				seriesMaps[seriesIndex].put(xValue, yValue);
			}
		}

		// Update series
		for (int i = 0; i < numSeries; i++) {
			ChartSeries series = model.getSeries().get(i);
			series.getData().clear();
			for (Map.Entry<Object, Number> entry : seriesMaps[i].entrySet()) {
				series.getData().put(seriesFormatters[i].format(entry.getKey()), entry.getValue());
			}
		}
		
		return triggerUpdate;
	}

	public boolean processUngrouppedData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = false;

		for (QueryResult resultBlock : resultSet.getQueryResult()) {
			for (Result result : resultBlock.getSparql().getResults().getResult()) {

				Object xValue = null;
				Number yValue = null;
				Integer seriesIndex = null;

				// Parse data
				for (Binding binding : result.getBinding()) {
					if ("x".equals(binding.getName())) {
						xValue = Double.valueOf(binding.getLiteral().getContent());
					} else if (binding.getName().startsWith("y")) {
						// y values start at index 1 (y1, y2 e.t.c)
						seriesIndex = Integer.valueOf(binding.getName().substring(1)) - 1;
						yValue = Double.valueOf(binding.getLiteral().getContent());
					}
				}

				if (XAxisType.DateFromResultSet.equals(xAxisType)) {
					xValue = new Date();
				}

				// Update series
				if (seriesIndex == null || xValue == null || yValue == null) {
					continue;
				}

				ChartSeries series = model.getSeries().get(seriesIndex);
				series.getData().put(xValue, yValue);
				triggerUpdate = true;
			}
		}

		return triggerUpdate;
	}

	@Override
	public void processData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = false;

		if (XAxisType.DateFromObservation.equals(xAxisType)) {
			triggerUpdate = processGrouppedData(resultSet);
		} else {
			triggerUpdate = processUngrouppedData(resultSet);
		}

		if (triggerUpdate) {
			widget.setRendered(true);
			emptyMessage.setRendered(false);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			if (requestContext != null) {
				requestContext.update(panel.getClientId());
			}
		}

	}

	private void parseAttributes(List<PresentationAttr> presentationAttributes) {
		seriesLabels = null;
		numSeries = 0;

		// Figure out number of series
		for (PresentationAttr attr : presentationAttributes) {
			if ("SERIES".equals(attr.getName())) {
				numSeries = Integer.valueOf(attr.getValue());
				seriesLabels = new String[numSeries];
				break;
			}
		}
		//
		for (PresentationAttr attr : presentationAttributes) {
			if ("TITLE".equals(attr.getName())) {
				title = attr.getValue();
			} else if ("X_AXIS_LABEL".equals(attr.getName())) {
				xAxisLabel = attr.getValue();
			} else if ("X_AXIS_TYPE".equals(attr.getName())) {
				if ("Date (result set)".equals(attr.getValue())) {
					xAxisType = XAxisType.DateFromResultSet;
				} else if ("Date (observation)".equals(attr.getValue())) {
					xAxisType = XAxisType.DateFromObservation;
				} else {
					xAxisType = XAxisType.Number;
				}
			} else if ("Y_AXIS_LABEL".equals(attr.getName())) {
				yAxisLabel = attr.getValue();
			} else if (attr.getName().startsWith("SERIES_")) {
				int seriesIndex = Integer.valueOf(attr.getName().split("_")[1]);
				seriesLabels[seriesIndex] = attr.getValue();
			}
		}		
	}

	@Override
	public void clearData() {
		for (ChartSeries series : model.getSeries()) {
			series.getData().clear();
		}

		widget.setRendered(false);
		emptyMessage.setRendered(true);

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.update(panel.getClientId());
		}
	}

}
