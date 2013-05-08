/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.requestdefinition.web.scopes.session.context.dialogs;

import java.io.Serializable;
import org.openiot.ui.requestdefinition.web.scopes.session.base.DisposableContext;
import org.openiot.ui.requestdefinition.web.util.FaceletLocalization;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class FindSensorDialogContext extends DisposableContext implements Serializable {
	private static final long serialVersionUID = 1L;

    private LatLng mapCenter;
    private int mapZoom;
    private MapModel mapModel;
    private Marker searchCenter;
    private double searchRadius;
    private String searchLocation;
    
    public FindSensorDialogContext() {
        super();
        this.register();
        
        // Initialize defaults
        mapCenter = new LatLng(37.983715,23.72931);
        searchCenter = new Marker(mapCenter, FaceletLocalization.getLocalizedResourceBundle().getString("UI_FIND_SENSOR_DIALOG_LOCATION_CENTER"));
        searchRadius = 15;
        mapZoom = 13;
        
        mapModel = new DefaultMapModel();
        mapModel.addOverlay(searchCenter);
    }

    @Override
    public String getContextUID() {
        return "findSensorDialogContext";
    }

    public LatLng getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    public int getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
    }

    public Marker getSearchCenter() {
        return searchCenter;
    }

    public void setSearchCenter(Marker searchCenter) {
        this.searchCenter = searchCenter;
    }

    public double getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(double searchRadius) {
        this.searchRadius = searchRadius;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public MapModel getMapModel() {
        return mapModel;
    }
}
