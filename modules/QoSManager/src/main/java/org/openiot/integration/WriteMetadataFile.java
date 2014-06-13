/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.integration;

import java.util.List;

public class WriteMetadataFile {

    String sensorName;
    String sensorID;
    double latitude;
    double longitude;
    List<String> parameters;
    List<String> lsmProperty;
    List<String> lsmUnit;

 
    public WriteMetadataFile(String sensorName, String sensorID,  double latitude, double longitude, List<String> param, List<String> lsmProp, List<String> lsmUnits) {

        this.sensorName = sensorName;
        this.sensorID = sensorID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parameters = param;
        this.lsmProperty = lsmProp;
        this.lsmUnit = lsmUnits;
    }

       public String createMetadata() {

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("sensorName=%s\n", sensorName));
        builder.append(String.format("source=%s\n", sensorID));
        builder.append(String.format("sourceType=FER mobile sensors\n"));
        builder.append(String.format("sensorType=Virtual MGRS Sensor\n"));
        builder.append(String.format("information=Virtual Sensor for designated area\n"));
        builder.append(String.format("author=FER\n"));
        builder.append(String.format("latitude=%s\n", String.valueOf(latitude)));
        builder.append(String.format("longitude=%s\n", String.valueOf(longitude)));
        builder.append(String.format("feature=\"http://lsm.deri.ie/OpenIoT/openiotfeature\"\n"));
        
        String fields = "";
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                fields += ",";
            }
            fields += parameters.get(i);
        }

        builder.append(String.format("fields=\"%s\"\n", fields));
        
        for (int i = 0; i < parameters.size(); i++) {
            builder.append(String.format("field.%s.propertyName=\"%s\"\n", parameters.get(i), lsmProperty.get(i)));
            builder.append(String.format("field.%s.unit=%s\n", parameters.get(i), lsmUnit.get(i)));
        }
        
        return builder.toString();

    }
}

