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

/*
* DEPRECATED don't Use
* TODO: cleanup
* */

package org.openiot.gsn.metadata.LSM.depecated;

import org.openiot.gsn.utils.PropertiesReader;

public class SensorMetaData {

    public String getSensorName() {
        return sensorName;
    }

    public String getAuthor() {
        return author;
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

    private String sensorName;
    private String author;
    private String sensorType;
    private String information;
    private String sourceType;
    private String source;

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

    String sensorID;

    public String toString() {
        return "SensorMetaData{" + "\n" +
                "sensorName='" + sensorName + '\'' + "\n" +
                ", author='" + author + '\'' + "\n" +
                ", sensorType='" + sensorType + '\'' + "\n" +
                ", information='" + information + '\'' + "\n" +
                ", sourceType='" + sourceType + '\'' + "\n" +
                ", source='" + source + '\'' + "\n" +
                ", sensorID='" + sensorID + '\'' + "\n" +
                '}' + "\n";
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public void initFromFile(String fileName) {
        this.setSensorName(PropertiesReader.readProperty(fileName, "sensorName"));
        this.setAuthor(PropertiesReader.readProperty(fileName, "author"));
        this.setInformation(PropertiesReader.readProperty(fileName, "information"));
        this.setSensorType(PropertiesReader.readProperty(fileName, "sensorType"));
        this.setSourceType(PropertiesReader.readProperty(fileName, "sourceType"));
        this.setSource(PropertiesReader.readProperty(fileName, "source"));
        this.setSensorID(PropertiesReader.readProperty(fileName, "sensorID"));
    }

}

