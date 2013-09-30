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
import org.openiot.ui.request.presentation.web.util.FaceletLocalization;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;

public class Passthrough implements VisualizationWidget {

	private HtmlOutputText widget;
	private HtmlOutputText emptyMessage;
	private Panel panel;
	private String title;
	private int numAttributes;
	private String[] attributeValues;

	@Override
	public Panel createWidget(List<PresentationAttr> presentationAttributes) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application application = fc.getApplication();

		parseAttributes(presentationAttributes);

		// Instanciate linechart widget
		widget = new HtmlOutputText();
		widget.setId("passthrough_" + System.nanoTime());
		widget.setStyleClass("passthrough");
		widget.setEscape(false);
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

		return panel;
	}

	@Override
	public void processData(SdumServiceResultSet resultSet) {
		boolean triggerUpdate = true;

		for (int i = 0; i < numAttributes; i++) {
			attributeValues[i] = null;
		}

		for (QueryResult resultBlock : resultSet.getQueryResult()) {
			for (Result result : resultBlock.getSparql().getResults().getResult()) {

				// Parse data
				for (Binding binding : result.getBinding()) {
					if (binding.getName().startsWith("attr")) {
						// attr values start at index 1 (attr1, attr2 e.t.c)
						Integer seriesIndex = Integer.valueOf(binding.getName().substring(5)) - 1;
						attributeValues[seriesIndex] = binding.getLiteral().getContent();
					}
				}
			}
		}

		if (triggerUpdate) {

			// Generate html table
			String html = "<table>";
			for (int i = 0; i < numAttributes; i++) {
				html += "<tr><th class=\"no-wrap\">attr" + (i + 1) + "</th><td align=\"right\">" + (attributeValues[i] == null ? "" : attributeValues[i]) + "</td></tr>";
			}
			html += "</table>";
			widget.setValue(html);

			widget.setRendered(true);
			emptyMessage.setRendered(false);
			RequestContext requestContext = RequestContext.getCurrentInstance();
			if (requestContext != null) {
				requestContext.update(panel.getClientId());
			}
		}

	}

	private void parseAttributes(List<PresentationAttr> presentationAttributes) {
		numAttributes = 0;

		// Figure out number of attributes
		for (PresentationAttr attr : presentationAttributes) {
			if ("ATTRIBUTES".equals(attr.getName())) {
				numAttributes = Integer.valueOf(attr.getValue());
				attributeValues = new String[numAttributes];
				break;
			}
		}
	}

	@Override
	public void clearData() {
		for (int i = 0; i < numAttributes; i++) {
			attributeValues[i] = null;
		}

		widget.setRendered(false);
		emptyMessage.setRendered(true);

		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (requestContext != null) {
			requestContext.update(panel.getClientId());
		}
	}

}
