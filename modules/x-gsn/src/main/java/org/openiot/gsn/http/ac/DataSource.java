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
 * @author Behnaz Bostanipour
 * @author Sofiane Sarni
*/

package org.openiot.gsn.http.ac;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 14, 2010
 * Time: 8:14:43 PM
 * To change this template use File | Settings | File Templates.
 */

/* a DataSource object basically presents a tuple of (Virtual Sensor name, access right) */
public class DataSource {
    private String dataSourceName; // a virtual sensor name
    private String dataSourceType;// access right for the virtual sensor
    private String fileName = "n";//the name of well formed XML file describing a virtual sensor, it is initially assigned to "n" which means not defined
    private String fileType = "n";// the type of file (the desired type should be .XML), it is initially assigned to "n" which means not defined
    private String path = "n";// indicates the directory in which the XML file describing a virtual sensor is stored, it is initially assigned to "n" which means not defined
    private User owner;// owner of the virtual sensor ( the one who uploads the .XML file to the GSN Server)
    private String ownerDecision = "notreceived";// the decision of the owner about access granting to a user
    private String isCandidate = "no"; // a DataSource becomes candidate if its virtual sensor file is uploaded and it is waiting for the Admin confirmation of the registration of the virtual sensor to GSN server.
    private static transient Logger logger = Logger.getLogger(DataSource.class);


    /****************************************** Constructors*******************************************/
    /**
     * *********************************************************************************************
     */


    public DataSource(String dataSourceName, String dataSourceType, String fileName, String fileType, String path) {
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.fileName = fileName;
        this.fileType = fileType;
        this.path = path;
    }

    public DataSource(String dataSourceName) {
        this.dataSourceName = dataSourceName;

    }

    public DataSource(String dataSourceName, String dataSourceType, String fileName, String fileType, String path, String ownerDecision) {
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.fileName = fileName;
        this.fileType = fileType;
        this.path = path;
        this.ownerDecision = ownerDecision;


    }

    public DataSource(String dataSourceName, String dataSourceType) {
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
    }

    public DataSource(String dataSourceName, String dataSourceType, String ownerDecision) {
        this.dataSourceName = dataSourceName;
        this.dataSourceType = dataSourceType;
        this.ownerDecision = ownerDecision;
    }

    /****************************************** Set Methods *******************************************/
    /**
     * *********************************************************************************************
     */

    void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    void setFileType(String fileType) {
        this.fileType = fileType;
    }

    void setOwner(User owner) {
        this.owner = owner;
    }

    void setPath(String path) {
        this.path = path;
    }

    void setOwnerDecision(String ownerDecision) {
        this.ownerDecision = ownerDecision;
    }

    void setIsCandidate(String isCandidate) {
        this.isCandidate = isCandidate;
    }


    /****************************************** Get Methods *******************************************/
    /**
     * *********************************************************************************************
     */

    String getDataSourceName() {
        return this.dataSourceName;
    }

    String getDataSourceType() {
        return this.dataSourceType;
    }

    String getFileName() {
        return this.fileName;
    }

    String getFileType() {
        return this.fileType;
    }

    User getOwner() {
        return this.owner;
    }

    String getPath() {
        return this.path;
    }

    String getOwnerDecision() {
        return this.ownerDecision;
    }

    String getIsCandidate() {
        return this.isCandidate;
    }

    /****************************************** AC Methods********************************************/
    /**
     * *********************************************************************************************
     */
    //returns true if the DataSourceType(acccess right) is 1(read)or 3(read/write)or 4(own)
    public boolean hasReadAccessRight(String srname) {
        boolean hasACRight = false;
        if ((this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '1') || (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '3') || (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '4')) {
            hasACRight = true;
        }
        return hasACRight;
    }

    //returns true if the DataSourceType(acccess right) is 2(write)or 3(read/write)or 4(own)
    public boolean hasWriteAccessRight(String srname) {
        boolean hasACRight = false;
        if ((this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '2') || (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '3') || (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '4')) {
            hasACRight = true;
        }
        return hasACRight;
    }

    //returns true if the DataSourceType(acccess right) is 3(read/write)or 4(own)
    public boolean hasReadWriteAccessRight(String srname) {
        boolean hasACRight = false;
        if ((this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '3') || (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '4')) {
            hasACRight = true;
        }
        return hasACRight;
    }

    //returns true if the DataSourceType(acccess right) is 4(own)
    public boolean hasOwnAccessRight(String srname) {
        boolean hasACRight = false;
        if (this.dataSourceName.equals(srname) && this.dataSourceType.charAt(0) == '4') {
            hasACRight = true;
        }
        return hasACRight;
    }

    public static boolean isVSManaged(String vsname) {
        ConnectToDB ctdb = null;
        boolean isManaged = false;
        try {
            ctdb = new ConnectToDB();
            if (ctdb.valueExistsForThisColumn(new Column("DATASOURCENAME", vsname), "ACDATASOURCE") == true) {
                isManaged = true;
            } else {
                isManaged = false;
            }
        } catch (Exception e) {
            logger.error("ERROR IN isVSManaged METHOD :");
            logger.error(e.getMessage(), e);
        } finally {
            if (ctdb != null) {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return isManaged;

    }

    /*
    * Returns true if all virtual sensors in the list ARE NOT managed by the access control layer
    * */
    public static boolean allVirtualSensorsInListAreNotManaged(List<String> listOfVirtualSensors) {
        Iterator<String> iterator = listOfVirtualSensors.iterator();
        boolean result = true;
        while (iterator.hasNext()) {
            String sensor = iterator.next();
            result = result && isVSManaged(sensor);
        }

        return !result;
    }
}

