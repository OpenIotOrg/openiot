package org.openiot.ui.xgsnWebInterface.service;

import org.openiot.ui.xgsnWebInterface.sensor.CSVPredicateData;
import org.openiot.ui.xgsnWebInterface.sensor.SensorData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Map;
import java.util.TreeMap;

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

@ManagedBean(name = "wrapperService", eager = true)
@ApplicationScoped
public class WrapperService {

    private Map<String, SensorData> wrappers;

    @PostConstruct
    public void init() {
        wrappers = new TreeMap<>();
        wrappers.put("csvWrapper", new SensorData(new CSVPredicateData()));
    }

    public Map<String, SensorData> getWrappers() {
        return wrappers;
    }
}
