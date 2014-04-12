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

import org.openiot.gsn.metadata.rdf.SensorMetadata;
import org.openiot.lsm.server.LSMTripleStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataCreator {
    private static final transient Logger logger = LoggerFactory.getLogger(utils.class);
    private static LSMSchema lsmSchema=new LSMSchema();
	static{
		lsmSchema.initFromConfigFile(LSMRepository.LSM_CONFIG_PROPERTIES_FILE);
	}
	
	public static void addRdfMetadatatoLSM(SensorMetadata metadata){
        LSMTripleStore lsmStore = new LSMTripleStore(lsmSchema.getLsmServerUrl());
        logger.info("Connecting to LSM: "+lsmSchema.getLsmServerUrl());
        lsmSchema.getMetaGraph();
        lsmStore.pushRDF(lsmSchema.getMetaGraph(),metadata.serializeRDF());

	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		if (args.length < 1) {
			System.out.println("Error: Metadata file is missing.\n");
			System.exit(-1);
		}

	    String metadataFileName = args[0];
	    System.out.println("Using metadata file: " + metadataFileName);

	    SensorMetadata metadata=new SensorMetadata();
	    metadata.loadFromFile(metadataFileName);
	    addRdfMetadatatoLSM(metadata);
	     
	}    
}
