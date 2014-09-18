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

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.metadata.LSM.LSMFieldMetaData;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;
import org.openiot.gsn.metadata.LSM.SensorAnnotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;



import java.util.*;

public class LSMExporter extends AbstractVirtualSensor {
    private static final transient Logger logger = LoggerFactory.getLogger(LSMExporter.class);

    private List<String> fields = new Vector<String>();

    private String sensorName;
    private boolean allow_nulls = false;
    private boolean publish_to_lsm = false;
    private Map<String,String> fieldUris=new HashMap<String,String>();
    
    public boolean initialize() {

    	//Config prefixes=ConfigFactory.load().getConfig("prefixes");
    	LSMSensorMetaData metadata;
        VSensorConfig vsensor = getVirtualSensorConfiguration();
        try {        	
        	metadata = LSMRepository.getInstance().loadMetadata(vsensor);
        } catch (Exception e){
        	e.printStackTrace();
        	logger.error("No LSM metadata available for loading vsensor "+vsensor.getName());
        	return false;
        }
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();
        sensorName = vsensor.getName();        
        
        for (DataField df:vsensor.getOutputStructure()){
        	logger.info("Property:"+ df.getName()+"--"+df.getProperty());
        	if (df.getProperty()!=null)
        	  fieldUris.put(df.getName().toUpperCase(), df.getProperty());
        	else {
              for (LSMFieldMetaData md:metadata.getFields().values()){
            	  if (md.getGsnFieldName().equals(df.getName()))
              		fieldUris.put(df.getName().toUpperCase(), md.getLsmPropertyName());            		  
              }        				
        	}
        }
        
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
            Object val;
            if (data.getFieldTypes()[i].equals(DataTypes.VAR_CHAR_PATTERN_STRING) ||
                    data.getFieldTypes()[i].equals(DataTypes.VARCHAR) ||
                    data.getFieldTypes()[i].equals(DataTypes.VARCHAR_NAME) ){
            	val = (String) data.getData(field);
            }
            else {
            	val = (Double) data.getData(field);
            }
            Date d = new Date(t);
            String fieldName = data.getFieldNames()[i];
            logger.debug(fieldName + " : t=" + d + " v=" + val);
            
            if (!allow_nulls && val == null)
                return; // skipping null values if allow_nulls flag is not st to true
          
            if (publish_to_lsm) {
                SensorAnnotator.updateSensorDataOnLSM(sensorName, fieldName, fieldUris.get(fieldName), val, d);                
            }

            //dataProduced(data);

        }

    }

    public void dispose() {
    }

}
