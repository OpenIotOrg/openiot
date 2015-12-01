package org.openiot.ui.xgsnWebInterface.model;

import org.openiot.ui.xgsnWebInterface.sensor.SensorOutput;
import org.openiot.ui.xgsnWebInterface.service.OutputTypeService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
public class SensorOutputBean {

    private List<String> outputTypes = new ArrayList<>();
    private List<SensorOutput> sensorOutputs = new ArrayList<>();

    @ManagedProperty("#{outputTypeService}")
    private OutputTypeService outputTypeService;

    @PostConstruct
    public void init() {
        outputTypes = outputTypeService.getOutputTypes();
    }

    // Setters for managed beans / properties

    public void setOutputTypeService(OutputTypeService outputTypeService) {
        this.outputTypeService = outputTypeService;
    }

    // Getters & Setters

    public List<String> getOutputTypes() {
        return outputTypes;
    }

    public void setOutputTypes(List<String> outputTypes) {
        this.outputTypes = outputTypes;
    }

    public List<SensorOutput> getSensorOutputs() {
        return sensorOutputs;
    }

    public void setSensorOutputs(List<SensorOutput> sensorOutputs) {
        this.sensorOutputs = sensorOutputs;
    }
}
