package org.openiot.ui.request.presentation.web.model.nodes.impl;

import java.util.List;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
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
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.PieChartModel;

public class Pie implements VisualizationWidget {

	private org.primefaces.component.chart.pie.PieChart widget;
	private HtmlOutputText emptyMessage;
	private Panel panel;
	private PieChartModel model;
	private String title;
	private int numSeries;
	private String[] seriesLabels;

	@Override
	public UIComponent createWidget(List<PresentationAttr> presentationAttributes) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		parseAttributes(presentationAttributes);

		// Instanciate linechart widget
		widget = (org.primefaces.component.chart.pie.PieChart) application.createComponent(fc, "org.primefaces.component.chart.PieChart", "org.primefaces.component.chart.PieChartRenderer");
		widget.setId("pie_" + System.nanoTime());
		widget.setStyleClass("pie-chart");
		widget.setLegendPosition("e");
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
		model = new PieChartModel();
		widget.setValue(model);

		return panel;
	}

	@Override
	public void processData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = false;

		for (QueryResult resultBlock : resultSet.getQueryResult()) {
			for (Result result : resultBlock.getSparql().getResults().getResult()) {

				Number yValue = null;
				Integer seriesIndex = null;

				// Parse data
				for (Binding binding : result.getBinding()) {
					if (binding.getName().startsWith("y")) {
						// y values start at index 1 (y1, y2 e.t.c)
						seriesIndex = Integer.valueOf(binding.getName().substring(1)) - 1;
						yValue = Double.valueOf(binding.getLiteral().getContent());
					}
				}

				// Update series
				if (seriesIndex == null || yValue == null) {
					continue;
				}

				model.set(seriesLabels[seriesIndex], yValue);
				triggerUpdate = true;
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
			} else if (attr.getName().startsWith("SERIES_")) {
				int seriesIndex = Integer.valueOf(attr.getName().split("_")[1]);
				seriesLabels[seriesIndex] = attr.getValue();
			}
		}
	}

	@Override
	public void clearData() {
		model.clear();

		widget.setRendered(false);
		emptyMessage.setRendered(true);

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.update(panel.getClientId());
		}
	}

}
