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

import java.util.List;

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
import org.openiot.ui.request.presentation.web.model.nodes.interfaces.VisualizationWidget;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

public class Map implements VisualizationWidget {

	private enum MapType {
		Markers, Circles, MarkersAndCircles
	};

	private org.primefaces.component.gmap.GMap widget;
	private Panel panel;
	private DefaultMapModel model;
	private String title;
	private Double centerLat;
	private Double centerLon;
	private Integer zoom;
	private Double valueScaler;
	private MapType mapType;

	@Override
	public Panel createWidget(List<PresentationAttr> presentationAttributes) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		parseAttributes(presentationAttributes);

		// Instanciate linechart widget
		widget = (org.primefaces.component.gmap.GMap) application.createComponent(fc, "org.primefaces.component.GMap", "org.primefaces.component.GMapRenderer");
		widget.setId("map_" + System.nanoTime());
		widget.setStyleClass("map");
		widget.setType("HYBRID");
		widget.setCenter(centerLat + "," + centerLon);
		widget.setZoom(zoom);
		widget.setRendered(true);

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

		// Setup model
		model = new DefaultMapModel();
		widget.setModel(model);

		return panel;
	}

	@Override
	public void processData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = true;

		model.getMarkers().clear();
		model.getCircles().clear();

		for (QueryResult resultBlock : resultSet.getQueryResult()) {
			for (Result result : resultBlock.getSparql().getResults().getResult()) {

				// Parse data
				Double lat = null;
				Double lon = null;
				Double value = null;

				for (Binding binding : result.getBinding()) {
					if ("VALUE".equals(binding.getName())) {
						value = Double.valueOf(binding.getLiteral().getContent());
					} else if ("LAT".equals(binding.getName())) {
						lat = Double.valueOf(binding.getLiteral().getContent());
					} else if ("LON".equals(binding.getName())) {
						lon = Double.valueOf(binding.getLiteral().getContent());
					}
				}

				// Update series
				if (value != null && lat != null && lon != null) {
					LatLng coord = new LatLng(lat, lon);
					if (MapType.Markers.equals(mapType)) {
						model.addOverlay(new Marker(coord, "Value: " + value));
					} else if (MapType.Circles.equals(mapType)) {
						Circle circle = new Circle(coord, value * valueScaler);
						circle.setStrokeColor("#d93c3c");
						circle.setFillColor("#d93c3c");
						circle.setStrokeOpacity(0.7);
						circle.setFillOpacity(0.7);
						model.addOverlay(circle);
					} else {
						Circle circle = new Circle(coord, value * valueScaler);
						circle.setStrokeColor("#d93c3c");
						circle.setFillColor("#d93c3c");
						circle.setStrokeOpacity(0.7);
						circle.setFillOpacity(0.7);

						model.addOverlay(new Marker(coord, "Value: " + value));
						model.addOverlay(circle);
					}
				}
			}
		}
		if (triggerUpdate) {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			if (requestContext != null) {
				requestContext.update(panel.getClientId());
			}
		}
	}

	private void parseAttributes(List<PresentationAttr> presentationAttributes) {
		for (PresentationAttr attr : presentationAttributes) {
			if ("TITLE".equals(attr.getName())) {
				title = attr.getValue();
			} else if ("LAT".equals(attr.getName())) {
				centerLat = Double.valueOf(attr.getValue());
			} else if ("LON".equals(attr.getName())) {
				centerLon = Double.valueOf(attr.getValue());
			} else if ("ZOOM".equals(attr.getName())) {
				zoom = Integer.valueOf(attr.getValue());
				zoom = Math.max(0, Math.min(zoom, 19));
			} else if ("SCALER".equals(attr.getName())) {
				valueScaler = Double.valueOf(attr.getValue());
				if (valueScaler == 0) {
					valueScaler = 1.0;
				}
			} else if ("TYPE".equals(attr.getName())) {
				String type = attr.getValue();
				if ("Circles only".equals(type)) {
					mapType = MapType.Circles;
				} else if ("Markers and Circles".equals(type)) {
					mapType = MapType.MarkersAndCircles;
				} else {
					mapType = MapType.Markers;
				}
			}
		}
	}

	@Override
	public void clearData() {
		model.getMarkers().clear();
		model.getCircles().clear();

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.update(panel.getClientId());
		}
	}
}
