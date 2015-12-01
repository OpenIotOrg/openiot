package org.openiot.ui.xgsnWebInterface.model;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
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

@ManagedBean
@ViewScoped
public class SensorDetailsBean {

    private String sensorName;
    private String description;
    private String metaDataFile;

    private String wrapperChoice;
    private Map<String, String> wrapperChoices = new HashMap<>();

    @PostConstruct
    public void init() {
        wrapperChoices.put("CSV Wrapper", "csvWrapper");
    }

    // Getters & Setters

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
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

    public String getWrapperChoice() {
        return wrapperChoice;
    }

    public void setWrapperChoice(String wrapperChoice) {
        this.wrapperChoice = wrapperChoice;
    }

    public Map<String, String> getWrapperChoices() {
        return wrapperChoices;
    }
}
