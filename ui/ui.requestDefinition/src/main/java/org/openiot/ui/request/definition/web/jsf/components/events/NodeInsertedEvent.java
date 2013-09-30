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

package org.openiot.ui.request.definition.web.jsf.components.events;

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
