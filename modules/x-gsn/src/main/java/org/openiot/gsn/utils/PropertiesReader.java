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

package org.openiot.gsn.utils;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static final transient Logger logger = Logger.getLogger(PropertiesReader.class);

    public static String readProperty(String fileName, String propertyName) {
        Properties prop = new Properties();

        String output = null;

        try {
            prop.load(new FileInputStream(fileName));
            output = prop.getProperty(propertyName);

        } catch (IOException ex) {
            logger.error(new StringBuilder("Can't read property \"")
                    .append(propertyName)
                    .append("\" from file ")
                    .append(fileName));
        }
        return output;
    }

    public static boolean writeProperty(String fileName, String propertyName, String propertyValue) {
        Properties prop = new Properties();

        boolean success = true;

        try {
            prop.load(new FileInputStream(fileName));
            prop.setProperty(propertyName, propertyValue);
            prop.store(new FileOutputStream(fileName),"Updated");

        } catch (IOException ex) {
            logger.error(new StringBuilder("Can't read/write property \"")
                    .append(propertyName)
                    .append("\" in/to file ")
                    .append(fileName));
            success = false;
        }
        return success;
    }
}
