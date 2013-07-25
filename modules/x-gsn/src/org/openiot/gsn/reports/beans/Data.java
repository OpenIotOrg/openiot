package org.openiot.gsn.reports.beans;

public class Data {
	
	private Object p1;
	
	private Object p2;
	
	private Number value;
	
	private String label;

	public Data (Object p1, Object p2, Number value, String label) {
		this.p1 = p1;
		this.p2 = p2;
		this.value = value;
		this.label = label;
	}

	public Object getP1() {
		return p1;
	}

	public Object getP2() {
		return p2;
	}

	public Number getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

}
