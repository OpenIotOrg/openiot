package org.openiot.gsn.dynamicSensorControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author Christos Georgoulis
 * 
 */
public class DynamicControlTask extends TimerTask {

	public static final String VIRTUAL_SENSORS_DIR = "/virtual-sensors";
	public static final String AVAILABLE_SENSORS_DIR = "/virtual-sensors/LSM";
	public static final String VIRTUAL_SENSORS_TAG = "virtual-sensor";
	public static final String VIRTUAL_SENSORS_TAG_NAME_ATTRIBUTE = "name";
	public static final String REGEX_ALL_XML = "^(.*?)\\.xml$";
	public static final String PROJECT_DIR = System.getProperty("user.dir");

	@Override
	public void run() {

		// TODO Query active sensors
		ArrayList<String> sensorDefinitions = getSensorDefinitions();
		HashMap<String, File> activeGSNSensors = getGSNSensors(VIRTUAL_SENSORS_DIR);

		HashMap<String, File> availableGSNSensors = getGSNSensors(AVAILABLE_SENSORS_DIR);

		updateActiveSensors(sensorDefinitions, activeGSNSensors,
				availableGSNSensors);
		// TODO Compare and load/delete

	}

	/**
	 * Activates - deactivates sensors according to SPARQL query results
	 * 
	 * @param sensorDefinitions
	 * @param activeGSNSensors
	 * @param availableGSNSensors
	 */
	private void updateActiveSensors(ArrayList<String> sensorDefinitions,
			HashMap<String, File> activeGSNSensors,
			HashMap<String, File> availableGSNSensors) {

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
				System.out
						.println("Sensor file not available in LSM directory");
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
			System.out.println("No such File/Dir");
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
					.getNamedItem(VIRTUAL_SENSORS_TAG_NAME_ATTRIBUTE)
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
		return null;

	}

	/**
	 * deletes file from virtual-sensors directory
	 * 
	 * @param file
	 */
	private void deactivateSensor(File file) {
		if (file.delete()) {
			System.out.println(file.getName() + " successfully deleted!");
		} else {
			System.out.println("Delete operation failed for " + file.getName());
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

	public static void main(String[] args) {
		DynamicControlTask c = new DynamicControlTask();

		HashMap<String, File> availableGSNSensors = c
				.getGSNSensors(DynamicControlTask.AVAILABLE_SENSORS_DIR);

		HashMap<String, File> activeGSNSensors = c
				.getGSNSensors(DynamicControlTask.VIRTUAL_SENSORS_DIR);

		ArrayList<String> sensorDefinitions = new ArrayList<String>();
		sensorDefinitions.add("opensense_1");
		c.updateActiveSensors(sensorDefinitions, activeGSNSensors,
				availableGSNSensors);
	}
}
