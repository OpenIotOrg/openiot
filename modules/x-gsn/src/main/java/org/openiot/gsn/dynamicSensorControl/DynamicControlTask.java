package org.openiot.gsn.dynamicSensorControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Christos Georgoulis
 * 
 */
public class DynamicControlTask extends TimerTask {

	private static final Logger logger = Logger
			.getLogger(DynamicControlTask.class);

	private static final String VIRTUAL_SENSORS_DIR = "/virtual-sensors";
	private static final String AVAILABLE_SENSORS_DIR = "/virtual-sensors/LSM";
	private static final String VIRTUAL_SENSORS_TAG = "virtual-sensor";
	private static final String VIRTUAL_SENSORS_TAG_NAME_ATTR = "name";
	private static final String REGEX_ALL_XML = "^(.*?)\\.xml$";
	private static final String PROJECT_DIR = System.getProperty("user.dir");
	// Update the following query with the one used to associate Sensors with
	// Services
	@SuppressWarnings("unused")
	private static final String SENSOR_QUERY = "select distinct(?sensorid) from <url> where {...}";
	// Following query used for testing purposes
	private static final String TEST_QUERY = "select ?sensorId  from <http://lsm.deri.ie/OpenIoT/demo/sensormeta#> "
			+ "WHERE {?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "
			+ "?type <http://www.w3.org/2000/01/rdf-schema#label> 'gsn'. "
			+ "FILTER EXISTS {?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. } "
			+ "?p geo:geometry ?geo.filter (<bif:st_intersects>(?geo,<bif:st_point>(6.631622,46.520131),15)).}";
	// Update the following constant with the query to be used
	private static final String QUERY = TEST_QUERY;

	private SparqlClient sparqlClient;

	@Override
	public void run() {

		sparqlClient = loadSparqlClient();

		if (sparqlClient != null) {

			ArrayList<String> sensorDefinitions = getSensorDefinitions();
			HashMap<String, File> activeGSNSensors = getGSNSensors(VIRTUAL_SENSORS_DIR);

			HashMap<String, File> availableGSNSensors = getGSNSensors(AVAILABLE_SENSORS_DIR);

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
			Map<String, File> activeGSNSensors,
			Map<String, File> availableGSNSensors) {

		for (String sensorName : activeGSNSensors.keySet()) {
			if (!sensorDefinitions.contains(sensorName))
				deactivateSensor(activeGSNSensors.get(sensorName));
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

		String path = PROJECT_DIR + dir;
		File f = new File(path);

		if (!f.exists()) {
			System.out.println("no such File/Dir");
			logger.error(f.getName() + "no such File/Dir");

		} else {

			Collection<File> files = FileUtils.listFiles(f,
					new RegexFileFilter(REGEX_ALL_XML), null);

			for (File file : files) {
				String sensorName = getSensorNameFromFile(file);
				sensorsMap.put(sensorName, file);
			}
		}
		return sensorsMap;
	}

	/**
	 * Parses an .xml file and retrieves sensor name
	 * 
	 * @param file
	 * @return
	 */
	private String getSensorNameFromFile(File file) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		String sensorName = null;
		try {
			builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList n = doc.getElementsByTagName(VIRTUAL_SENSORS_TAG);
			sensorName = n.item(0).getAttributes()
					.getNamedItem(VIRTUAL_SENSORS_TAG_NAME_ATTR)
					.getTextContent();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sensorName;
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
		if (file.delete()) {
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
	 * @param file
	 */
	private void activateSensor(File file) {
		File dest = new File(PROJECT_DIR + VIRTUAL_SENSORS_DIR + File.separator
				+ file.getName());
		try {
			FileUtils.copyFile(file, dest);
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
}
