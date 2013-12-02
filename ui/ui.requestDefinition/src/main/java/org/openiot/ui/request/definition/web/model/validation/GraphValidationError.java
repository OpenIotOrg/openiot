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

package org.openiot.ui.request.definition.web.model.validation;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class GraphValidationError {
	public enum ErrorType {
		/**
		 * No SINKS defined
		 */
		EmptyGraph,

		/**
		 * Connections form a closed loop
		 */
		ConnectionsFormClosedLoop,

		/**
		 * A required property is missing
		 */
		RequiredPropertyMissing,

		/**
		 * An endpoint with required connection is not connected
		 */
		RequiredEndpointNotConnected,

		/**
		 * No groups specified
		 */
		NoGroupsSpecified

	}

	private ErrorType type;
	private String nodeClassName;
	private String details;
	private String elementId;

	public GraphValidationError(ErrorType type, String nodeClassName, String details, String elementId) {
		this.type = type;
		this.nodeClassName = nodeClassName;
		this.details = details;
		this.elementId = elementId;
	}

	public String getNodeClassName() {
		return nodeClassName;
	}

	public String getDetails() {
		return details;
	}

	public ErrorType getType() {
		return type;
	}

	public String getElementId() {
		return elementId;
	}
}
