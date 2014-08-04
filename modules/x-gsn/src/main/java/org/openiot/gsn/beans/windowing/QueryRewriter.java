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
 * @author gsn_devs
 * @author Mehdi Riahi
*/

package org.openiot.gsn.beans.windowing;

import org.openiot.gsn.beans.StreamSource;

public abstract class QueryRewriter {
	protected StreamSource streamSource;
	
	public QueryRewriter(){
		
	}
	
	public QueryRewriter(StreamSource streamSource){
		setStreamSource(streamSource);
	}
	
	public abstract boolean initialize();
	
	public abstract StringBuilder rewrite(String query);
	
	public abstract void dispose();

	public abstract boolean dataAvailable(long timestamp);
	
	public StreamSource getStreamSource() {
		return streamSource;
	}

	public void setStreamSource(StreamSource streamSource) {
		this.streamSource = streamSource;
		streamSource.setQueryRewriter(this);
	}


}
