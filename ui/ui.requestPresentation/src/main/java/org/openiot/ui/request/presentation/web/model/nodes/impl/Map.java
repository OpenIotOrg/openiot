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

import javax.el.MethodExpression;
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
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.behavior.ajax.AjaxBehavior;
import org.primefaces.component.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.gmap.GMapInfoWindow;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

public class Map implements VisualizationWidget {

	private enum MapType {
		Markers, Circles, MarkersAndCircles
	};

	private org.primefaces.component.gmap.GMap widget;
	private HtmlOutputText infoWindowContent; 
	private Panel panel;
	private DefaultMapModel model;
	private String title;
	private Double centerLat;
	private Double centerLon;
	private Integer zoom;
	private Double valueScaler;
	private MapType mapType;
	// Map state
	private Marker selectedMarker;
	private String mapViewType;
	private String mapCenter;
	private int mapZoom;

	@Override
	public Panel createWidget(String serviceId, List<PresentationAttr> presentationAttributes) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		parseAttributes(presentationAttributes);

		// Setup initial map state
		mapViewType = mapType.toString();
		mapCenter = centerLat + "," + centerLon;
		mapZoom = zoom;

		// Instanciate map widget
		model = new DefaultMapModel();
		widget = (org.primefaces.component.gmap.GMap) application.createComponent(fc, "org.primefaces.component.GMap", "org.primefaces.component.GMapRenderer");
		widget.setId("map_" + System.nanoTime());
		widget.setWidgetVar(widget.getId());
		widget.setStyleClass("map");
		widget.setValueExpression("type", application.getExpressionFactory().createValueExpression(fc.getELContext(), "#{requestPresentationPageController.context.serviceIdToWidgetMap[\"" + serviceId + "\"].mapViewType}", String.class));
		widget.setValueExpression("center", application.getExpressionFactory().createValueExpression(fc.getELContext(), "#{requestPresentationPageController.context.serviceIdToWidgetMap[\"" + serviceId + "\"].mapCenter}", String.class));
		widget.setValueExpression("zoom", application.getExpressionFactory().createValueExpression(fc.getELContext(), "#{requestPresentationPageController.context.serviceIdToWidgetMap[\"" + serviceId + "\"].mapZoom}", Integer.class));
		widget.setRendered(true);
		widget.setMapTypeControl(true);
		widget.setModel(model);

		// Add ajax behavior so we can maintain zoom and pan between map updates
		MethodExpression dummyExpression = application.getExpressionFactory().createMethodExpression(fc.getELContext(), "#{requestPresentationPageController.dummy}", void.class, new Class[] {});
		MethodExpression stateChangeMethodExpression = application.getExpressionFactory().createMethodExpression(fc.getELContext(), "#{requestPresentationPageController.context.serviceIdToWidgetMap[\"" + serviceId + "\"].onMapWidgetStateChange}", void.class, new Class[] { StateChangeEvent.class });
		AjaxBehavior ajaxBehavior = new AjaxBehavior();
		ajaxBehavior.setProcess("@this");
		ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(dummyExpression, stateChangeMethodExpression));
		widget.addClientBehavior("stateChange", ajaxBehavior);

		// Add ajax behavior so we can track selected markers
		MethodExpression markerSelectMethodExpression = application.getExpressionFactory().createMethodExpression(fc.getELContext(), "#{requestPresentationPageController.context.serviceIdToWidgetMap[\"" + serviceId + "\"].onMapWidgetMarkerSelection}", void.class, new Class[] { OverlaySelectEvent.class });
		ajaxBehavior = new AjaxBehavior();
		ajaxBehavior.setProcess("@this");
		ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(dummyExpression, markerSelectMethodExpression));
		widget.addClientBehavior("overlaySelect", ajaxBehavior);
		
		// Instanciate infowindow widget and embed it in map widget
		GMapInfoWindow infoWindow = new GMapInfoWindow();
		infoWindow.setId("map_infowindow_" + System.nanoTime());
		OutputPanel infoWindowPanel = new OutputPanel();
		infoWindowPanel.setStyleClass("text-align:center;display:block;margin:auto;");
		infoWindowContent = new HtmlOutputText();
		infoWindowContent.setEscape(false);
		infoWindowPanel.getChildren().add(infoWindowContent);
		infoWindow.getChildren().add(infoWindowPanel);
		widget.getChildren().add(infoWindow);

		// Instanciate a panel to host the widget
		panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel", "org.primefaces.component.PanelRenderer");
		panel.setId("widget_panel_" + System.nanoTime());
		panel.setHeader(title != null ? title : "");
		panel.setClosable(false);
		panel.setToggleable(false);
		panel.setStyleClass("widget service_" + serviceId);
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
					String overlayData = FaceletLocalization.getLocalisedMessage(FaceletLocalization.getLocalizedResourceBundle(), "UI_MAP_WIDGET_MARKER_TEMPLATE", "(" + lat + ", " + lon + ")", value);
					
					LatLng coord = new LatLng(lat, lon);
					if (MapType.Markers.equals(mapType)) {
						model.addOverlay(new Marker(coord, "sensor", overlayData));
					} else if (MapType.Circles.equals(mapType)) {
						Circle circle = new Circle(coord, value * valueScaler);
						circle.setStrokeColor("#ff0000");
						circle.setStrokeOpacity(0.8);
						circle.setFillColor("#ff0000");
						circle.setFillOpacity(0.35);
						model.addOverlay(circle);
					} else {
						Circle circle = new Circle(coord, value * valueScaler);
						circle.setStrokeColor("#ff0000");
						circle.setStrokeOpacity(0.8);
						circle.setFillColor("#ff0000");
						circle.setFillOpacity(0.35);
						model.addOverlay(new Marker(coord, "sensor", overlayData));
						model.addOverlay(circle);
					}
				}
			}
		}
		if (triggerUpdate) {
			// If we update the map, we will trigger a reload (causes flicker).
			// Instead, we will use the JS api to remove all overlays and then
			// add the new ones.
			RequestContext requestContext = RequestContext.getCurrentInstance();
			if (requestContext != null) {
				requestContext.execute("clearMapOverlays(" + widget.getWidgetVar() + ");");
				for (Circle circle : widget.getModel().getCircles()) {
					requestContext.execute("addMapCircle(" + widget.getWidgetVar() + ", '"  + circle.getId() + "', " + circle.getCenter().getLat() + "," + circle.getCenter().getLng() + ", " + circle.getRadius() + ",'" + circle.getStrokeColor() + "'," + circle.getStrokeOpacity() + ",'" + circle.getFillColor() + "', " + circle.getFillOpacity() + ");");
				}
				for (Marker marker : widget.getModel().getMarkers()) {
					requestContext.execute("addMapMarker(" + widget.getWidgetVar() + ", '"  + marker.getId() + "', " + marker.getLatlng().getLat() + "," + marker.getLatlng().getLng() + ");");
				}
				requestContext.execute("addMapEventListeners(" + widget.getWidgetVar() + ");");
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
		selectedMarker = null;
		infoWindowContent.setValue("");

		// Reset map state to original values
		mapViewType = mapType.toString();
		mapCenter = centerLat + "," + centerLon;
		mapZoom = zoom;

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.update(panel.getClientId());
		}
	}

	// ------------------------------------------------------
	// Ajax behavior listener for monitoring map location
	// ------------------------------------------------------
	public void onMapWidgetStateChange(StateChangeEvent event) {
		mapZoom = event.getZoomLevel();
		mapCenter = event.getCenter().getLat() + ", " + event.getCenter().getLng();
	}
	
	public void onMapWidgetMarkerSelection(OverlaySelectEvent event) {  
        selectedMarker = (Marker) event.getOverlay();
        infoWindowContent.setValue(selectedMarker.getData() != null ? selectedMarker.getData() : "");
    }  

	public String getMapViewType() {
		return mapViewType;
	}

	public void setMapViewType(String mapViewType) {
		this.mapViewType = mapViewType;
	}

	public String getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(String mapCenter) {
		this.mapCenter = mapCenter;
	}

	public int getMapZoom() {
		return mapZoom;
	}

	public void setMapZoom(int mapZoom) {
		this.mapZoom = mapZoom;
	}

	public Marker getSelectedMarker() {
		return selectedMarker;
	}

	public void setSelectedMarker(Marker selectedMarker) {
		this.selectedMarker = selectedMarker;
	}

}
