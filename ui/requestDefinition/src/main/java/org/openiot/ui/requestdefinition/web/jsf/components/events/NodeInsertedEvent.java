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
package org.openiot.ui.requestdefinition.web.jsf.components.events;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.extensions.event.AbstractAjaxBehaviorEvent;

/**
 * 
 * @author archie
 */
public class NodeInsertedEvent extends AbstractAjaxBehaviorEvent {
	private static final long serialVersionUID = 1L;

	private String nodeGroup;
	private String nodeType;
	private double x;
	private double y;

	public NodeInsertedEvent(UIComponent component, Behavior behavior, String nodeGroup, String nodeType, double x, double y) {
		super(component, behavior);

		this.nodeGroup = nodeGroup;
		this.nodeType = nodeType;
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getNodeGroup() {
		return nodeGroup;
	}

	public String getNodeType() {
		return nodeType;
	}
}
