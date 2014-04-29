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

package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

public class MockWrapper extends AbstractWrapper {
	int threadCounter;
	
	private DataField[] outputFormat =new DataField[] {new DataField("data","int")};

	public boolean initialize() {
		setName("TestWrapperMockObject-Thread" + (++threadCounter));
		return true;
	}

	public void run() {

	}

	public DataField[] getOutputFormat() {
		return outputFormat;
	}

	public boolean publishStreamElement(StreamElement se) {
		return postStreamElement(se);
	}

	public void dispose() {
		threadCounter--;
	}

	public String getWrapperName() {
		return "TestWrapperMock";
	}


	
}
