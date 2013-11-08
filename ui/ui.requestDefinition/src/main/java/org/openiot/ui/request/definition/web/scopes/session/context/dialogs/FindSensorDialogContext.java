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

package org.openiot.ui.request.definition.web.scopes.session.context.dialogs;

import java.io.Serializable;

import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;
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
        mapCenter = new LatLng(46.52119378179781,6.635227203369141);
        searchCenter = new Marker(mapCenter, FaceletLocalization.getLocalizedResourceBundle().getString("UI_FIND_SOURCE_DIALOG_LOCATION_CENTER"));
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
