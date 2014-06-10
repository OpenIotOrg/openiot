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
 * A message for cloud-internal reporting.
 * 
 * @author Eugen
 */
public class InfoMessage implements InternalMessage {

	private static final long serialVersionUID = 8855371241434694315L;

	private String contents;

	public InfoMessage(String msg) {
		this.contents = msg;
	}

	public String getContents() {
		return contents;
	}

	@Override
	public UUID getID() {
		return null;
	}

}