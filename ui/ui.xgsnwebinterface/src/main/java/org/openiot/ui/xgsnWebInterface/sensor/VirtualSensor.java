package org.openiot.ui.xgsnWebInterface.sensor;

import org.openiot.ui.xgsnWebInterface.model.SensorDetailsBean;
import org.openiot.ui.xgsnWebInterface.model.SensorInputBean;
import org.openiot.ui.xgsnWebInterface.model.SensorOutputBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2011-2014, OpenIoT
 * <p/>
 * This file is part of OpenIoT.
 * <p/>
 * OpenIoT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * OpenIoT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact: OpenIoT mailto: info@openiot.eu
 * @author Luke Herron
 */

public class VirtualSensor implements Serializable {

    private String name;
    private String description;
    private String metaDataFile;
    //private String inputDataFile;
    private String query;

    private SensorData sensorData;
    private List<SensorOutput> sensorOutputs;
    private Map<String, String> outputDataValues = new HashMap<>();

    public void setDetails(SensorDetailsBean detailsBean) {
        name = detailsBean.getSensorName();
        description = detailsBean.getDescription();
        metaDataFile = detailsBean.getMetaDataFile();
    }

    public void setInputProperties(SensorInputBean inputBean) {
        sensorData = inputBean.getSensorData();
    }

    public void setOutputProperties(SensorOutputBean outputBean) {
        sensorOutputs = outputBean.getSensorOutputs();
        query = generateQuery(sensorOutputs);

        for (SensorOutput output: sensorOutputs) {
            outputDataValues.put(output.getInput(), output.getType());
        }
    }

    private String generateQuery(List<SensorOutput> sensorOutputs) {
        String query = "select ";
        for(SensorOutput sensorOutput: sensorOutputs) {
            query = query.concat(sensorOutput.getOutput()).concat(" as ").concat(sensorOutput.getInput()).concat(", ");
        }
        query = query.concat("timed from source1");

        return query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetaDataFile() {
        return metaDataFile;
    }

    public void setMetaDataFile(String metaDataFile) {
        this.metaDataFile = metaDataFile;
    }

    public String getInputDataFile() {
        return sensorData.getPredicateData().getDataFile();
    }

    public void setInputDataFile(String inputDataFile) {
        sensorData.getPredicateData().setDataFile(inputDataFile);
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public List<SensorOutput> getSensorOutputs() {
        return sensorOutputs;
    }

    public void setSensorOutputs(List<SensorOutput> sensorOutputs) {
        this.sensorOutputs = sensorOutputs;
    }

    public Map<String, String> getOutputDataValues() {
        return outputDataValues;
    }

    public void setOutputDataValues(Map<String, String> outputDataValues) {
        this.outputDataValues = outputDataValues;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
