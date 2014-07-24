/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 *
 * @author Jean-Paul Calbimonte
 * @author Hylke van der Schaaf
 */
package org.openiot.gsn.http.restapi;

import com.typesafe.config.Config;
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
import org.openiot.gsn.metadata.rdf.SensorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import org.openiot.gsn.metadata.LSM.LSMRepository;

@Path("/vsensor")
public class VSManagerService {

	private static final transient Logger logger = LoggerFactory.getLogger(VSManagerService.class);
	private static final String KEY_DELETE_FROM_LSM = "deleteFromLSM";
	private static final Config DEFAULT_DELETE_CONFIG = ConfigFactory.empty();

	static {
		DEFAULT_DELETE_CONFIG.withValue(KEY_DELETE_FROM_LSM, ConfigValueFactory.fromAnyRef(false));
	}

	//public VSManagerService() { super(VSManagerService.class); }
	@POST
	@Path("/{vsname}/create")
	public Response createVS(Reader vsconfig, @PathParam("vsname") String vsname) {
		VSensorLoader vsloader
				= VSensorLoader.getInstance(Main.DEFAULT_VIRTUAL_SENSOR_DIRECTORY);
		logger.info("Start loading vs config");
		String xml;

		try {
			xml = IOUtils.toString(vsconfig);
			vsconfig.close();
		} catch (IOException e) {
			logger.error("Errors detected!", e);
			throw new VSensorConfigException(e.getMessage(), e);
		}
		logger.debug("The xml vs config: {}", xml);

		try {
			logger.info("Now we start");
			vsloader.loadVirtualSensor(xml, vsname);
			logger.info("The vs is loaded");
		} catch (Exception e) {
			logger.error("Errors detected!", e);
			throw new VSensorConfigException(e.getMessage(), e);
		}
		logger.info("Finalized loading.");
		return Response.ok(vsname).build();
	}

	@POST
	@Path("/{vsname}/registerRdf")
	public Response registerRdfVS(Reader metadata, @PathParam("vsname") String vsname) {
		SensorMetadata meta = new SensorMetadata();
		String filePath = VSensorLoader.getVSConfigurationFilePath(vsname).replace(".xml", ".ttl");
		try {
			List<String> lines = IOUtils.readLines(metadata);
			String concat = "";
			for (String line : lines) {
				concat += line;
			}
			InputStream is = new ByteArrayInputStream(concat.getBytes());
			meta.load(is);
			MetadataCreator.addRdfMetadatatoLSM(meta);
			FileWriter fw = new FileWriter(filePath, true);
			IOUtils.writeLines(lines, "\n", fw);
			fw.close();
		} catch (Exception e) {
			logger.error("Unable to load RDF metadata for sensor.", e);
			throw new VSensorConfigException("Unable to load RDF metadata for sensor.", e);
		}
		return Response.ok().build();
	}

	@POST
	@Path("/{vsname}/register")
	public Response registerVS(InputStream metadata, @PathParam("vsname") String vsname) {
		String sensorId;
		String sensorIdOld = null;
		String filePath = VSensorLoader.getVSConfigurationFilePath(vsname).replace(".xml", ".metadata");
		try {
			List<String> lines = IOUtils.readLines(metadata);
			FileWriter fw = new FileWriter(filePath, false);
			IOUtils.writeLines(lines, "\n", fw);
			fw.close();
			LSMSensorMetaData lsmmd = new LSMSensorMetaData();

			Config configData = ConfigFactory.parseFile(new File(filePath));
			if (configData.hasPath(LSMSensorMetaData.KEY_SENSOR_ID)) {
				sensorIdOld = configData.getString(LSMSensorMetaData.KEY_SENSOR_ID);
			}
			lsmmd.init(configData, true);
			sensorId = MetadataCreator.addSensorToLSM(lsmmd);

			if (sensorIdOld == null || sensorId.compareTo(sensorIdOld) != 0) {
				logger.info("SensorId has changed from {} to {}.", sensorIdOld, sensorId);
				lsmmd.setSensorID(sensorId);
				Config configDataNew = configData.withValue(LSMSensorMetaData.KEY_SENSOR_ID, ConfigValueFactory.fromAnyRef(sensorId));
				configDataNew = configDataNew.withValue(LSMSensorMetaData.KEY_SENSOR_ID, ConfigValueFactory.fromAnyRef(sensorId));
				String metadataNew = configDataNew.root().render(ConfigRenderOptions.defaults().setJson(false).setOriginComments(false));
				fw = new FileWriter(filePath, false);
				IOUtils.write(metadataNew, fw);
				fw.close();
			}

		} catch (Exception e) {
			logger.error("Unable to load metadata for sensor", e);
			throw new VSensorConfigException("Unable to load metadata for sensor.", e);
		}
		return Response.ok(sensorId).build();
	}

	@POST
	@Path("/{vsname}/delete")
	public Response deleteVS(InputStream data, @PathParam("vsname") String vsname) {
		logger.info("Deleting sensor {}.", vsname);
		try {
			Config configData = ConfigFactory.parseString(IOUtils.toString(data, "UTF-8"));
			Config config = configData.withFallback(DEFAULT_DELETE_CONFIG);

			String vsFilePath = VSensorLoader.getVSConfigurationFilePath(vsname);
			File vsFile = new File(vsFilePath);

			if (config.getBoolean(KEY_DELETE_FROM_LSM)) {
				String vsMetaPath = vsFilePath.replace(".xml", ".metadata");
				File vsMetaFile = new File(vsMetaPath);

				if (vsMetaFile.exists()) {
					Config sensorConfig = ConfigFactory.parseFile(vsMetaFile);

					if (!sensorConfig.hasPath(LSMSensorMetaData.KEY_SENSOR_ID)) {
						logger.info("Can not delete sensor {} from LSM, sensorId not found in metadata file.", vsname);
					} else {

						String sensorId = sensorConfig.getString(LSMSensorMetaData.KEY_SENSOR_ID);
						logger.info("Deleting sensor {} from LSM. SensorId: {}.", vsname, sensorId);
						MetadataCreator.deleteSensorFromLSM(sensorId);

						if (!vsMetaFile.delete()) {
							logger.error("Failed to delete sensor metadata file.");
						}
					}
				}
			}

			if (!vsFile.delete()) {
				throw new VSensorConfigException("Failed to unload sensor.");
			}
			LSMRepository lsm=LSMRepository.getInstance();
			lsm.unloadMetaData(vsname);

		} catch (IOException e) {
			logger.warn("Could not parse options.", e);
			throw new VSensorConfigException("Could not parse options.", e);
		}
		return Response.ok(vsname).build();
	}
}

class VSensorConfigException extends WebApplicationException {

	private static final long serialVersionUID = -2199585164343127464L;

	public VSensorConfigException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST)
				.entity(message).type(MediaType.TEXT_PLAIN).build());
	}

	public VSensorConfigException(String message, Throwable cause) {
		super(cause, Response.status(Response.Status.BAD_REQUEST)
				.entity(message).type(MediaType.TEXT_PLAIN).build());
	}
}
