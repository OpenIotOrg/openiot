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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.bean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMetaDataBean{
	public final static String KEY_SENSOR_ID = "sensorID";
    private static final transient Logger logger = LoggerFactory.getLogger(SensorMetaDataBean.class);
        
    
    private String sensorName;
    private String author;

    private String sensorType;
    private String information;
    private String sourceType;
    private String source;
    private String featureOfInterest;
    private double latitude;
    private double longitude;
    private Map<String, FieldMetaDataBean> fields = new HashMap<String, FieldMetaDataBean>();
    String sensorID;

    
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
    
    public String getSensorType() {
        return sensorType;
    }

    public String getInformation() {
        return information;
    }

    public String getSourceType() {
        return sourceType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, FieldMetaDataBean> getFields() {
        return fields;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LSMSensorMetaData{")
                .append("\nsensorName  =").append(sensorName)
                .append("\nauthor      =").append(author)
                .append("\nsensorType  =").append(sensorType)
                .append("\ninformation =").append(information)
                .append("\nsourceType  =").append(sourceType)
                .append("\nsource      =").append(source)
                .append("\nsensorID    =").append(sensorID)
                .append("\nfields =>\n");
        for (String fieldName:fields.keySet()) {
            sb.append("\t").append(fieldName).append(" : ").append(fields.get(fieldName)).append("\n");
        }
        return sb.toString();
    }

    
    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

	public String getFeatureOfInterest() {
		return featureOfInterest;
	}

	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}

	public String[] getProperties() {
		String[] props = new String[fields.size()];
		int i = 0;
		for (FieldMetaDataBean field : fields.values()) {
			props[i] = field.getLsmPropertyName();
			i++;
		}
		return props;
	}
	
}