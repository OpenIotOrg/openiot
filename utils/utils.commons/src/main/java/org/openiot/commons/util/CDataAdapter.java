package org.openiot.commons.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDataAdapter extends XmlAdapter<String, String> {

	@Override
	public String marshal(String arg0) throws Exception {
		return "<![CDATA[" + arg0 + "]]>";
	}

	@Override
	public String unmarshal(String arg0) throws Exception {
		return arg0;
	}
}
