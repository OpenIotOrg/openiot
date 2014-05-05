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

package org.openiot.gsn.metadata.LSM;

import java.io.FileNotFoundException;

public class utils {


    //private static LSMSchema lsmSchema=new LSMSchema();
	//static{
	//	lsmSchema.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
	//}

   

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length < 1) {
            System.out.println("Error: Metadata file is missing.\n");
            System.exit(-1);
        }

        String metadataFileName = args[0];
        System.out.println("Using metadata file: " + metadataFileName);
          
        LSMSensorMetaData metaData = new LSMSensorMetaData();
        metaData.initFromConfigFile(metadataFileName);

        //LSMSchema schema = new LSMSchema();
        //schema.initFromConfigFile(metadataFileName);
        //System.out.println(schema.toString());

        System.out.println(metaData.toString());

        //System.out.println(success);


        //String SID = addSensorToLSM(metaData);
        //System.out.println("Sensor registered to LSM with ID: " + SID);

        System.exit(0);
    }


}
