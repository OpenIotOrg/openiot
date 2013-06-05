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
package org.openiot.ui.requestdefinition.nodes.impl.sensors;

import java.io.Serializable;
import org.openiot.ui.requestdefinition.nodes.base.DefaultGraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;

/**
 * Generic node that models sensors. Since sensors are instanciated on the fly
 * this class contains no annotations and will not be detected by the node
 * scanner.
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class GenericSensor extends DefaultGraphNode implements Serializable {
	private static final long serialVersionUID = 1L;

    private double filterLocationLat;
    private double filterLocationLon;
    private double filterLocationRadius;

    public GenericSensor() {
        super();
    }

    public double getFilterLocationLat() {
        return filterLocationLat;
    }

    public void setFilterLocationLat(double filterLocationLat) {
        this.filterLocationLat = filterLocationLat;
    }

    public double getFilterLocationLon() {
        return filterLocationLon;
    }

    public void setFilterLocationLon(double filterLocationLon) {
        this.filterLocationLon = filterLocationLon;
    }

    public double getFilterLocationRadius() {
        return filterLocationRadius;
    }

    public void setFilterLocationRadius(double filterLocationRadius) {
        this.filterLocationRadius = filterLocationRadius;
    }

	@Override
	public GraphNode getCopy() {
		GenericSensor copy = (GenericSensor)super.getCopy();
		copy.setFilterLocationLat(filterLocationLat);
		copy.setFilterLocationLon(filterLocationLon);
		copy.setFilterLocationRadius(filterLocationRadius);
		
		return copy;
	}
}
