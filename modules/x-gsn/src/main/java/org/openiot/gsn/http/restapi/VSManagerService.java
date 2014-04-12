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
*     @author Jean-Paul Calbimonte
*/

package org.openiot.gsn.http.restapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.openiot.gsn.Main;
import org.openiot.gsn.VSensorLoader;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;
import org.openiot.gsn.metadata.LSM.MetadataCreator;
import org.openiot.gsn.metadata.LSM.utils;
import org.openiot.gsn.metadata.rdf.SensorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

@Path("/vsensor")
public class VSManagerService {
    private static final transient Logger logger = LoggerFactory.getLogger(VSManagerService.class);

	//public VSManagerService() { super(VSManagerService.class); }

	@POST
	@Path("/{vsname}/create")
	public Response createVS(Reader vsconfig,@PathParam("vsname") String vsname) {
		VSensorLoader vsloader =
				VSensorLoader.getInstance(Main.DEFAULT_VIRTUAL_SENSOR_DIRECTORY);
		logger.info("Start loading vs config");
		String xml;
		
		try {
			xml=IOUtils.toString(vsconfig);
			vsconfig.close();
		} catch (IOException e) {
			e.printStackTrace();	
			logger.error("Errors detected: "+e.getMessage());
			throw new VSensorConfigException(e.getMessage());					
		}
		logger.info("The xml vs config: "+xml);
		
		try {
			logger.info("Now we start");
			vsloader.loadVirtualSensor(xml, vsname);
			logger.info("The vs is loaded");
		} catch (Exception e) {
			e.printStackTrace();
			throw new VSensorConfigException(e.getMessage());					
		}
		logger.info("Finalized loading.");
		return Response.ok(vsname).build();
	}
	
	@POST
	@Path("/{vsname}/registerRdf")
	public Response registerRdfVS(Reader metadata,@PathParam("vsname") String vsname) {
	    SensorMetadata meta=new SensorMetadata();
	    String filePath=VSensorLoader.getVSConfigurationFilePath(vsname).replace(".xml",".ttl");
		try {
			List<String> lines =IOUtils.readLines(metadata);
			String concat="";
			for (String line:lines)
				concat+=line;			
			InputStream is=new ByteArrayInputStream(concat.getBytes());
			meta.load(is);
		    MetadataCreator.addRdfMetadatatoLSM(meta);
			FileWriter fw = new FileWriter(filePath, true);			
		    IOUtils.writeLines(lines, "\n", fw);
	        fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new VSensorConfigException("Unable to load RDF metadata for sensor. "+e.getMessage());
		}
		return Response.ok().build();
	}	

	@POST
	@Path("/{vsname}/register")
	public Response registerVS(InputStream metadata,@PathParam("vsname") String vsname) {
	    
	    String filePath=VSensorLoader.getVSConfigurationFilePath(vsname).replace(".xml",".metadata");
		try {
			List<String> lines =IOUtils.readLines(metadata);
			FileWriter fw = new FileWriter(filePath, false);			
		    IOUtils.writeLines(lines, "\n", fw);
	        fw.close();
			LSMSensorMetaData lsmmd=new LSMSensorMetaData();
			
			lsmmd.init(ConfigFactory.parseFile(new File(filePath)));
			utils.addSensorToLSM(lsmmd);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new VSensorConfigException("Unable to load RDF metadata for sensor. "+e.getMessage());
		}
		return Response.ok().build();
	}	
}

 class VSensorConfigException extends WebApplicationException {
	private static final long serialVersionUID = -2199585164343127464L;

	public VSensorConfigException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
            .entity(message).type(MediaType.TEXT_PLAIN).build());        
    }
}
