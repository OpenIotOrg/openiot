package org.openiot.ui.request.presentation.web.model.nodes.interfaces;

import java.util.List;

import javax.faces.component.UIComponent;

import org.openiot.commons.osdspec.model.PresentationAttr;
import org.openiot.commons.sdum.serviceresultset.model.SdumServiceResultSet;

public interface VisualizationWidget {
	/**
	 * Generate a JSF widget to render the widget view
	 */
	UIComponent createWidget( List<PresentationAttr> presentationAttributes );
	
	/**
	 * Process incoming data
	 */
	public void processData(SdumServiceResultSet resultSet);
}
