package org.openiot.gsn.http.rest;

import org.openiot.gsn.beans.DataTypes;

import java.io.Serializable;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Field4RestConverter implements Converter {
	
	private static final Class<Field4Rest> clazz = Field4Rest.class;
    private static final Base64Encoder base64 = new Base64Encoder();

	public void marshal(Object in, HierarchicalStreamWriter writer, MarshallingContext context) {
		Field4Rest input = (Field4Rest) in;
		writer.addAttribute("name", input.getName());
		if(input.getValue()==null)
			writer.addAttribute("is-null", "true");
		String type = "not-detected";
		String value = null;

		switch (input.getType()) {
		case DataTypes.BIGINT:
		case DataTypes.SMALLINT:
		case DataTypes.INTEGER:
		case DataTypes.DOUBLE:
		case DataTypes.TINYINT:
			type="numeric";
			if (input.getValue() !=null)
				value = Double.toString(((Number)input.getValue()).doubleValue());
			break;
		case DataTypes.CHAR:
		case DataTypes.VARCHAR:
			type="string";
			if (input.getValue() !=null)
				value = (String)input.getValue();
			break;
		default:
			type="binary";
		if (input.getValue() !=null) 
			value =base64.encode((byte[]) input.getValue());
		}

		writer.addAttribute("type", type);
		if (value!=null)
			writer.setValue(value);
	}

	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
		Field4Rest toReturn = null;
		String name = reader.getAttribute("name");
		String type = reader.getAttribute("type");
		Byte typeId;
		if (type.equalsIgnoreCase("numeric"))
			typeId = DataTypes.DOUBLE;
		else if (type.equalsIgnoreCase("string"))
			typeId = DataTypes.VARCHAR;
		else 
			typeId = DataTypes.BINARY;
		
		Boolean isNull = false;
		if (reader.getAttribute("is-null")!=null)
			isNull = Boolean.valueOf(reader.getAttribute("is-null"));

		Serializable value = null;
		if (!isNull) {
			if (type.equalsIgnoreCase("string")) 
				value = reader.getValue();
			else if (type.equalsIgnoreCase("numeric"))
				value = Double.parseDouble(reader.getValue());
			else
				value = (byte[]) base64.decode(reader.getValue());
		}

		toReturn = new Field4Rest(name,typeId,value);
		return toReturn;
	}

	public boolean canConvert(Class arg0) {
		return clazz.equals(arg0);
	}


}
