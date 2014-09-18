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

package org.openiot.cupus.message.internal;

import java.util.UUID;

import org.openiot.cupus.message.InternalMessage;

/**
 * A message for cloud-internal error reporting.
 * 
 * @author Eugen
 */
public class ErrorMessage implements InternalMessage {

	private static final long serialVersionUID = -566150330335586638L;

	private String contents;

	public ErrorMessage(String error) {
		this.contents = error;
	}

	public String getContents() {
		return contents;
	}

	@Override
	public UUID getID() {
		return null;
	}

}
