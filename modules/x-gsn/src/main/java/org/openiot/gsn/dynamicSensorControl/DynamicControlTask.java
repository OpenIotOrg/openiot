package org.openiot.gsn.dynamicSensorControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openiot.gsn.Main;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.utils.PropertiesReader;
import org.openrdf.repository.RepositoryException;

/**
 * 
 * @author Christos Georgoulis (cgeo) e-mail: cgeo@ait.edu.gr
 * 
 */
public class DynamicControlTask extends TimerTask {

	// Variable Initialization
	private static final Logger logger = Logger
			.getLogger(DynamicControlTask.class);

	private static final String REGEX_ALL_XML = "^(.*?)\\.xml$";
	private static final String METADATA_FILE_SUFFIX = ".metadata";
	// Following query used for testing purposes
	private static final String TEST_QUERY = "select ?sensorId  from <http://lsm.deri.ie/OpenIoT/demo/sensormeta#> "
			+ "WHERE {?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "
			+ "?type <http://www.w3.org/2000/01/rdf-schema#label> 'gsn'. "
			+ "FILTER EXISTS {?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. } "
			+ "?p geo:geometry ?geo.filter (<bif:st_intersects>(?geo,<bif:st_point>(6.631622,46.520131),15)).}";
	// Update the following constant with the query to be used
	private static final String QUERY = TEST_QUERY;

	private SparqlClient sparqlClient;

	// Singleton Setup
	private DynamicControlTask() {

	}

	private static class Holder {
		private static final DynamicControlTask INSTANCE = new DynamicControlTask();
	}

	public static DynamicControlTask getInstance() {
		return Holder.INSTANCE;
	}

	// Class Methods
	@Override
	public void run() {
		PropertyConfigurator.configure(Main.DEFAULT_GSN_LOG4J_PROPERTIES);

		sparqlClient = loadSparqlClient();

		if (sparqlClient != null) {

			ArrayList<String> sensorDefinitions = getSensorDefinitions();
			HashMap<String, File> activeGSNSensors = getGSNSensors(PropertiesReader
					.readProperty(LSMRepository.LSM_CONFIG_PROPERTIES_FILE,
							"virtualSensorsDir"));

			HashMap<String, File> availableGSNSensors = getGSNSensors(PropertiesReader
					.readProperty(LSMRepository.LSM_CONFIG_PROPERTIES_FILE,
							"availableSensorsDir"));

			// Compare sensors and perform update
			updateActiveSensors(sensorDefinitions, activeGSNSensors,
					availableGSNSensors);
		}
	}

	/**
	 * Activates - deactivates sensors according to SPARQL query results
	 * 
	 * @param sensorDefinitions
	 * @param activeGSNSensors
	 * @param availableGSNSensors
	 */
	private void updateActiveSensors(List<String> sensorDefinitions,
			HashMap<String, File> activeGSNSensors,
			HashMap<String, File> availableGSNSensors) {

		for (String sensorID : activeGSNSensors.keySet()) {
			if (!sensorDefinitions.contains(sensorID))
				deactivateSensor(activeGSNSensors.get(sensorID));
		}

		ArrayList<String> sensorsToActivate = new ArrayList<String>(
				sensorDefinitions);
		sensorsToActivate.removeAll(activeGSNSensors.keySet());

		for (String sensorName : sensorsToActivate) {
			File file = availableGSNSensors.get(sensorName);

			if (file != null) {
				activateSensor(file);
			} else {
				System.out.println(sensorName
						+ " not available in LSM directory");
				logger.error(sensorName + " not available in LSM directory");
			}
		}
	}

	/**
	 * Obtains sensors from directory and maps Sensor names to Sensor Files
	 * 
	 * @return
	 */
	private HashMap<String, File> getGSNSensors(String dir) {

		HashMap<String, File> sensorsMap = new HashMap<String, File>();

		File f = new File(dir);

		if (!f.getAbsoluteFile().exists()) {
			System.out.println("no such File/Dir");
			logger.error(f.getName() + "no such File/Dir");

		} else {

			Collection<File> files = FileUtils.listFiles(f,
					new RegexFileFilter(REGEX_ALL_XML), null);

			for (File file : files) {
				String path = file.getAbsolutePath() + METADATA_FILE_SUFFIX;
				String sensorID = PropertiesReader.readProperty(path,
						"sensorID");

				sensorsMap.put(sensorID, file);
			}
		}
		return sensorsMap;
	}

	/**
	 * Sends a SPARQL Query that retrieves sensors that are being defined as
	 * used on the LSM
	 * 
	 * @return
	 */
	private ArrayList<String> getSensorDefinitions() {

		Collection<String> col = sparqlClient.getQueryResults(QUERY,
				ParserFactory.SENSOR_PARSER);

		ArrayList<String> list = new ArrayList<String>(col);
		return list;
	}

	/**
	 * deletes file from virtual-sensors directory
	 * 
	 * @param file
	 */
	private void deactivateSensor(File file) {
		File metadata = new File(file.getAbsolutePath() + METADATA_FILE_SUFFIX);

		if (file.delete() && metadata.delete()) {
			logger.info(file.getName()
					+ " successfully deleted from virtual-sensors directory");
			System.out.println(file.getName() + " successfully deleted!");
		} else {
			System.out.println("Delete operation failed for " + file.getName());
			logger.info("Delete operation failed for " + file.getName());
		}
	}

	/**
	 * copies file from LSM directory to virtual-sensors directory
	 * 
	 * @param xmlSource
	 */
	private void activateSensor(File xmlSource) {

		// metadata file destination
		String virtualSensorsDir = PropertiesReader.readProperty(
				LSMRepository.LSM_CONFIG_PROPERTIES_FILE, "virtualSensorsDir");

		File xmlDest = new File(virtualSensorsDir + File.separator
				+ xmlSource.getName());

		// xmlFile destination
		File metaDataSource = new File(xmlSource.getAbsolutePath()
				+ METADATA_FILE_SUFFIX);
		File metaDataDest = new File(virtualSensorsDir + File.separator
				+ metaDataSource.getName());

		try {
			FileUtils.copyFile(xmlSource, xmlDest);
			FileUtils.copyFile(metaDataSource, metaDataDest);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** Initializes SPARQL Connection */
	private SparqlClient loadSparqlClient() {
		SparqlClient sc = null;
		try {
			sc = new SparqlClient();
			logger.info("Sparql Repository loaded");
		} catch (RepositoryException e) {
			logger.error("Sparql Repository not reached", e);
		}
		return sc;
	}

	// TODO Complete
	// private String createQuery() {
	// String query = "select distinct(?sensorid) from "
	// + Resources.LSM_FUNCTIONAL_GRAPH + "where {...}";
	//
	// return query;
	// }
}
