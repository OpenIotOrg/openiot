package org.openiot.ui.xgsnWebInterface.model;

import org.openiot.ui.xgsnWebInterface.sensor.PredicateData;
import org.openiot.ui.xgsnWebInterface.sensor.SensorData;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

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

@ManagedBean
@ViewScoped
public class SensorInputBean {

    private SensorData sensorData;
    private List<String> dataSourceColumns;

    @PostConstruct
    public void init() {

    }

    // Getters & Setters

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setDataSourceColumns(List<String> dataSourceColumns) {
        this.dataSourceColumns = dataSourceColumns;
    }

    public List<String> getDataSourceColumns() {
        return dataSourceColumns;
    }

    public void setSensorDataFile(UploadedFile file) {
        sensorData.setInputFile(file);
    }

    public UploadedFile getSensorDataFile() {
        return sensorData.getInputFile();
    }

    public void setPredicateData(PredicateData predicateData) {
        sensorData.setPredicateData(predicateData);
    }

    public PredicateData getPredicateData() {
        return sensorData.getPredicateData();
    }

    public String[] getMimicOneRow() {
        return new String[1];
    }
}
