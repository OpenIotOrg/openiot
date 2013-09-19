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

package org.openiot.gsn.vsensor;


import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;

import org.apache.log4j.Logger;

import java.util.*;

public class LSMExporter extends AbstractVirtualSensor {
    private static final transient Logger logger = Logger.getLogger(LSMExporter.class);

    private List<String> fields = new Vector<String>();

    private LSMSensorMetaData lsmSensorMetaData;

    private String sensorName;

    private boolean allow_nulls = false;
    private boolean publish_to_lsm;
    private boolean debug_mode = false;

    public boolean initialize() {

        VSensorConfig vsensor = getVirtualSensorConfiguration();
        publish_to_lsm = vsensor.getPublishToLSM();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();
        sensorName = vsensor.getName();

        String allow_nulls_str = params.get("allow-nulls");
        if (allow_nulls_str != null)
            allow_nulls = allow_nulls_str.equalsIgnoreCase("true");

        logger.info("Allow nulls => " + allow_nulls);

        String debug_mode_str = params.get("debug-mode");
        if (debug_mode_str != null)
            debug_mode = debug_mode_str.equalsIgnoreCase("true");

        logger.info("Debug mode => " + allow_nulls);

        // for each field in output structure
        for (int i = 0; i < vsensor.getOutputStructure().length; i++) {
            fields.add(vsensor.getOutputStructure()[i].getName());
            logger.info(fields.get(i));
        }

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {

        if (!publish_to_lsm)
            return;

        //logger.info("Data available");

        Long t = data.getTimeStamp();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Double v = (Double) data.getData(field);
            Date d = new Date(t);
            String fieldName = data.getFieldNames()[i];
            logger.info(fieldName + " : t=" + d + " v=" + v);

            if (!allow_nulls && v == null)
                return; // skipping null values if allow_nulls flag is not st to true

            if (debug_mode) {
                logger.info(fieldName + " : t=" + d + " v=" + v);
            } else {
                LSMRepository.getInstance().publishSensorDataToLSM(sensorName, fieldName, v, d);
            }

        }

    }

    public void dispose() {

    }

}
