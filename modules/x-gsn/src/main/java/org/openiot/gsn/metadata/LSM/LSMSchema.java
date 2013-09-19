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

package org.openiot.gsn.metadata.LSM;

import org.openiot.gsn.utils.PropertiesReader;
import org.apache.log4j.Logger;

public class LSMSchema {

    private static final transient Logger logger = Logger.getLogger(LSMSchema.class);

    private String metaGraph;
    private String dataGraph;

    public boolean initFromConfigFile(String fileName) {
        try {
            this.setMetaGraph(PropertiesReader.readProperty(fileName, "metaGraph"));
            this.setDataGraph(PropertiesReader.readProperty(fileName, "dataGraph"));

        } catch (NullPointerException e) {
            logger.warn("Error while reading properties file: " + fileName);
            logger.warn(e);
            return false;
        }


        return true;
    }

    @Override
    public String toString() {
        return "LSMSchema{" +
                "metaGraph='" + metaGraph + '\'' +
                ", dataGraph='" + dataGraph + '\'' +
                '}';
    }

    public String getMetaGraph() {
        return metaGraph;
    }

    public void setMetaGraph(String metaGraph) {
        this.metaGraph = metaGraph;
    }

    public String getDataGraph() {
        return dataGraph;
    }

    public void setDataGraph(String dataGraph) {
        this.dataGraph = dataGraph;
    }
}
