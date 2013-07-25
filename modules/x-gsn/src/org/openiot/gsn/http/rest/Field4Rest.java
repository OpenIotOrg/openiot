package org.openiot.gsn.http.rest;

import java.io.Serializable;

public class Field4Rest {
	private String name;
	private Serializable value;
	private Byte type;

	public Field4Rest(String name, Byte type, Serializable value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Serializable getValue() {
		return value;
	}

	public byte getType() {
		return type;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Field(name:").append(name).append(",").append("type:").append(type).append(",value:").append(value).append(")");
		return sb.toString();
	}
	
}
