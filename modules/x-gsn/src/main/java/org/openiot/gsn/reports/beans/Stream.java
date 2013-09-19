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

package org.openiot.gsn.reports.beans;

import java.util.Collection;

public class Stream {

	private String streamName;
	
	private String lastUpdate;
		
	private Collection<Data> datas;

	public Stream (String streamName, String lastUpdate, Collection<Data> datas) {
		this.streamName = streamName;
		this.lastUpdate = lastUpdate;
		this.datas = datas;
	}
	
	public String getLastUpdate() {
		return lastUpdate;
	}

	public String getStreamName() {
		return streamName;
	}

	public Collection<Data> getDatas() {
		return datas;
	}
}
