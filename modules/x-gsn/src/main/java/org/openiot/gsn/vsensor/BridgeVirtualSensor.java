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
import org.apache.log4j.Logger;
import java.util.TreeMap;

public class BridgeVirtualSensor extends AbstractVirtualSensor {

    private static final transient Logger logger = Logger.getLogger(BridgeVirtualSensor.class);
    private boolean allow_nulls = true; // by default allow nulls

    public boolean initialize() {
        VSensorConfig vsensor = getVirtualSensorConfiguration();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();

        String allow_nulls_str = params.get("allow-nulls");
        if (allow_nulls_str != null)
            allow_nulls = allow_nulls_str.equalsIgnoreCase("true");

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {
        if (allow_nulls)
            dataProduced(data);
        else {
            if (!areAllFieldsNull(data))
                dataProduced(data);
            else {
                if (logger.isDebugEnabled())
                    logger.debug("Nulls received for timestamp (" + data.getTimeStamp() + "), discarded");
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Data received under the name: " + inputStreamName);
    }

    public boolean areAllFieldsNull(StreamElement data) {
        boolean allFieldsNull = false;
        for (int i = 0; i < data.getData().length; i++)
            if (data.getData()[i] == null) {
                allFieldsNull = true;
                break;
            }

        return allFieldsNull;
    }

    public void dispose() {

    }

}
