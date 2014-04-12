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
*    Contact: OpenIoT mailto: info@openiot.eu
*    @author Sofiane Sarni
*    @author Jean-Paul Calbimonte 
*/

package org.openiot.gsn.vsensor;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.*;

public class LSMExporter extends AbstractVirtualSensor {
    private static final transient Logger logger = LoggerFactory.getLogger(LSMExporter.class);

    private List<String> fields = new Vector<String>();

    private String sensorName;
    private boolean allow_nulls = false;
    private boolean publish_to_lsm = false;

    private LSMSensorMetaData loadMetadata(VSensorConfig vsConfig) throws Exception{
    	LSMRepository lsm=LSMRepository.getInstance();
    	return lsm.loadMetadata(vsConfig);
    }
    
    public boolean initialize() {

        VSensorConfig vsensor = getVirtualSensorConfiguration();
        try {
        	loadMetadata(vsensor);
        } catch (Exception e){
        	e.printStackTrace();
        	logger.error("Could not load vsensor LSM metadata for "+vsensor.getName());
        	return false;
        }
        //publish_to_lsm = vsensor.getPublishToLSM();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();
        sensorName = vsensor.getName();        
        
        String allow_nulls_str = params.get("allow-nulls");
        if (allow_nulls_str != null)
            allow_nulls = allow_nulls_str.equalsIgnoreCase("true");

        logger.info("Allow nulls => " + allow_nulls);

        String publishLsmStr= params.get("publish-to-lsm");
        if (publishLsmStr != null)
            publish_to_lsm = publishLsmStr.equalsIgnoreCase("true");
        
        // for each field in output structure
        for (int i = 0; i < vsensor.getOutputStructure().length; i++) {
            fields.add(vsensor.getOutputStructure()[i].getName());
            logger.info(fields.get(i));
        }

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {

        Long t = data.getTimeStamp();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Double v = (Double) data.getData(field);
            Date d = new Date(t);
            String fieldName = data.getFieldNames()[i];
            logger.debug(fieldName + " : t=" + d + " v=" + v);

            if (!allow_nulls && v == null)
                return; // skipping null values if allow_nulls flag is not st to true

            if (publish_to_lsm) {
                LSMRepository.getInstance().publishSensorDataToLSM(sensorName, fieldName, v, d);
            }

            //dataProduced(data);

        }

    }

    public void dispose() {
    }

}
