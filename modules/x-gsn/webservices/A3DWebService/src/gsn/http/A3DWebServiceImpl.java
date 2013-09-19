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

package gsn.http;

import javax.jws.WebService;
import java.util.Iterator;
import java.util.Vector;
import java.sql.SQLException;

import gsn.Main;
import gsn.Mappings;
import gsn.storage.DataEnumerator;
import gsn.storage.StorageManager;
import gsn.beans.VSensorConfig;
import gsn.beans.DataField;
import gsn.beans.StreamElement;
import gsn.beans.DataTypes;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.collections.KeyValue;
import org.apache.log4j.Logger;

@WebService(endpointInterface = "gsn.http.A3DWebService")
public class A3DWebServiceImpl implements A3DWebService {

    private static transient Logger logger = Logger.getLogger(A3DWebServiceImpl.class);

    public String[] getSensors() {
        Iterator<VSensorConfig> vsIterator = Mappings.getAllVSensorConfigs();

        Vector<String> sensors = new Vector<String>();

        while (vsIterator.hasNext()) {
            VSensorConfig sensorConfig = vsIterator.next();
            sensors.add(sensorConfig.getName());
        }
        String v_sensors[] = new String[sensors.size()];
        for (int i=0;i<sensors.size();i++)
            v_sensors[i] = sensors.get(i);

        return v_sensors;
    }

    public String[] getSensorInfo(String sensor) {

        VSensorConfig sensorConfig = Mappings.getVSensorConfig(sensor);

        Vector<String> sensorInfo = new Vector<String>();

        for (DataField df : sensorConfig.getOutputStructure())
            sensorInfo.add(df.getName() + ":" + df.getType());

        String v_sensor_info[] = new String[sensorInfo.size()];
        for (int i=0;i<sensorInfo.size();i++)
            v_sensor_info[i] = sensorInfo.get(i);

        return v_sensor_info;
    }

    public String[] getLatestMeteoData(String sensor) {

        String vsFields = "*";

        StringBuilder query = new StringBuilder("select " + vsFields + " from " + sensor + " order by timed DESC limit 1 offset 0");
        DataEnumerator result;
        try {
            result = Main.getStorage(sensor).executeQuery(query, true);
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            return new String[]{"ERROR IN EXECUTING, query: " + query};
        }

        Vector<String> latestMeteoData = new Vector<String>();

        while (result.hasMoreElements()) {
            StreamElement se = result.nextElement();

            latestMeteoData.add(new StringBuilder("TIMED="+se.getTimeStamp()).toString());

            for (int i = 0; i < se.getFieldNames().length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(se.getFieldNames()[i]).append("=");
                if (se.getData()[i] != null)
                    if (se.getFieldTypes()[i] == DataTypes.BINARY)
                        sb.append(se.getData()[i].toString());
                    else
                        sb.append(StringEscapeUtils.escapeXml(se.getData()[i].toString()));
                latestMeteoData.add(sb.toString());
            }

        }
        //result.close();

        String v_latestMeteoData[] = new String[latestMeteoData.size()];
        for (int i=0;i<latestMeteoData.size();i++)
            v_latestMeteoData[i] = latestMeteoData.get(i);

        return v_latestMeteoData;
    }

    public String[] getLatestMeteoDataMeasurement(String sensor, String measurement) {

        StringBuilder query = new StringBuilder("select " + measurement + " from " + sensor + " order by timed DESC limit 1 offset 0");
        DataEnumerator result;
        try {
            result = Main.getStorage(sensor).executeQuery(query, false);
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            return new String[]{"ERROR IN EXECUTING, query: " + query};
        }



        Vector<String> latestMeteoData = new Vector<String>();

        while (result.hasMoreElements()) {
            StreamElement se = result.nextElement();


            latestMeteoData.add(new StringBuilder("TIMED="+se.getTimeStamp()).toString());

            for (int i = 0; i < se.getFieldNames().length; i++) {
                //StringBuilder sb = new StringBuilder();
                //sb.append(se.getFieldNames()[i]).append("=");
                String value;
                if (se.getData()[i] != null)
                    if (se.getFieldTypes()[i] == DataTypes.BINARY)
                        value = se.getData()[i].toString();
                    else
                        value = StringEscapeUtils.escapeXml(se.getData()[i].toString());
                else value = "";
                latestMeteoData.add(measurement+"="+value);
            }
        }
        //result.close();

        String v_latestMeteoData[] = new String[latestMeteoData.size()];
        for (int i=0;i<latestMeteoData.size();i++)
            v_latestMeteoData[i] = latestMeteoData.get(i);

        return v_latestMeteoData;
    }

    public String[] getMeteoData(String sensor, long from, long to) {

        String str_from = Long.toString(from);
        String str_to = Long.toString(to);

        StringBuilder query = new StringBuilder("select * from " + sensor + " where timed >= "+ str_from + " and timed <= " + str_to + " order by timed ASC");
        DataEnumerator result;
        try {
            result = Main.getStorage(sensor).executeQuery(query, true);
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            return new String[]{"ERROR IN EXECUTING, query: " + query};
        }

        Vector<String> meteoData = new Vector<String>();

        while (result.hasMoreElements()) {
            StreamElement se = result.nextElement();
            StringBuilder sb = new StringBuilder();

            sb.append(new StringBuilder ("TIMED="+se.getTimeStamp()).toString()+";");

            for (int i = 0; i < se.getFieldNames().length; i++) {

                sb.append(se.getFieldNames()[i]).append("=");
                if (se.getData()[i] != null)
                    if (se.getFieldTypes()[i] == DataTypes.BINARY)
                        sb.append(se.getData()[i].toString());
                    else
                        sb.append(StringEscapeUtils.escapeXml(se.getData()[i].toString()));
                sb.append(";");
            }
            meteoData.add(sb.toString());
        }
        //result.close();

        String v_meteoData[] = new String[meteoData.size()];
        for (int i=0;i<meteoData.size();i++)
            v_meteoData[i] = meteoData.get(i);

        return v_meteoData;
    }

    public String[] getMeteoDataMeasurement(String sensor, String measurement, long from, long to) {

        String str_from = Long.toString(from);
        String str_to = Long.toString(to);

        StringBuilder query = new StringBuilder("select timed, " + measurement + " from " + sensor + " where timed >= "+ str_from + " and timed <= " + str_to + " order by timed ASC");
        logger.warn("query => "+query);
        DataEnumerator result;
        try {
            result = Main.getStorage(sensor).executeQuery(query, false);
        } catch (SQLException e) {
            logger.error("ERROR IN EXECUTING, query: " + query);
            logger.error(e.getMessage(), e);
            return new String[]{"ERROR IN EXECUTING, query: " + query};
        }

        Vector<String> meteoData = new Vector<String>();

        while (result.hasMoreElements()) {
            StreamElement se = result.nextElement();
            StringBuilder sb = new StringBuilder();

            sb.append(new StringBuilder ("TIMED="+se.getTimeStamp()).toString()+";");

            for (int i = 0; i < se.getFieldNames().length; i++) {
                //sb.append(se.getFieldNames()[i]).append(":");
                if (se.getData()[i] != null)
                    if (se.getFieldTypes()[i] == DataTypes.BINARY)
                        sb.append(measurement+"="+se.getData()[i].toString());
                    else
                        sb.append(measurement+"="+StringEscapeUtils.escapeXml(se.getData()[i].toString()));
            }
            meteoData.add(sb.toString());
        }
        //result.close();

        String v_meteoData[] = new String[meteoData.size()];
        for (int i=0;i<meteoData.size();i++)
            v_meteoData[i] = meteoData.get(i);

        return v_meteoData;
    }

    public String[] getSensorLocation(String sensor) {

            VSensorConfig sensorConfig = Mappings.getVSensorConfig(sensor);
            Vector<String> sensorLocation = new Vector<String>();
            for (KeyValue df : sensorConfig.getAddressing())
                sensorLocation.add(StringEscapeUtils.escapeXml(df.getKey().toString()) + "=" + df.getValue().toString());

            String v_sensorLocation[] = new String[sensorLocation.size()];
            for (int i=0;i<sensorLocation.size();i++)
                v_sensorLocation[i] = sensorLocation.get(i);

            return v_sensorLocation;

        }

}
