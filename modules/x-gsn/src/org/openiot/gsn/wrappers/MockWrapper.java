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
