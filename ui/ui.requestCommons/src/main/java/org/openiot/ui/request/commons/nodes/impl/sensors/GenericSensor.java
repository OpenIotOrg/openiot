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
package org.openiot.ui.request.commons.nodes.impl.sensors;

import java.io.Serializable;

import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;

/**
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "GenericSensor", type = "SENSOR", scanProperties = true, hideFromScanner = true)
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "LAT", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "LON", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "RADIUS", required = true)})
public class GenericSensor extends DefaultGraphNode implements Serializable {
	private static final long serialVersionUID = 1L;
}
