package org.openiot.scheduler.core.api.impl.DiscoverSensors.method2;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sensortypes.model.Unit;
import org.openiot.scheduler.core.api.impl.DiscoverSensors.SensorTypeMetaData;
import org.openiot.scheduler.core.test.SensorTypesPopulation;
import org.openiot.scheduler.core.utils.lsmpa.entities.Service;
import org.openiot.scheduler.core.utils.lsmpa.entities.User;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 */

public class DiscoverSensorsImpl {
	
	
    private static final String PROPERTIES_FILE = "openiot.properties";
	private static final String LSM_META_GRAPH = "scheduler.core.lsm.openiotMetaGraph";
//	private static final String LSM_DATA_GRAPH = "scheduler.core.lsm.openiotDataGraph";
	private Properties props = null;
	
	private static String lsmMetaGraph = "";
//	private static String lsmDataGraph = "";
	
	
	private static class Queries {

		public static ArrayList<SensorTypeData> parseSensorTypeInArea(TupleQueryResult qres) {
			ArrayList<SensorTypeData> sensorTypeDataList = new ArrayList<SensorTypeData>();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					SensorTypeData sensorTypeData = new SensorTypeData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("sensLabelType")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							sensorTypeData.setLabel(str);
							System.out.print("sensor label: " + sensorTypeData.getLabel() + " ");
						} else if (((String) n).equalsIgnoreCase("type")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							sensorTypeData.setID(str);
							System.out.print("unit : " + sensorTypeData.getID() + " ");
						}
					}
					sensorTypeDataList.add(sensorTypeData);
				}// while
				return sensorTypeDataList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static ArrayList<SensorTypeMetaData> parseSensorMetaData(TupleQueryResult qres) {
			ArrayList<SensorTypeMetaData> metaDataList = new ArrayList<SensorTypeMetaData>();
			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					SensorTypeMetaData metaData = new SensorTypeMetaData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("measurement")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							metaData.setMeasuredVal(str);
							System.out.print("measurement : " + str + " ");
						} else if (((String) n).equalsIgnoreCase("unit")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							metaData.setUnit(str);
							System.out.print("unit : " + str + " ");
						}
					}
					metaDataList.add(metaData);
				}// while
				return metaDataList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String parseSensorTypeMeasurementDataType(TupleQueryResult qres) {

			try {
				String sensorTypeMeasurementDataType = null;
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("value")) {
							String st = b.getValue((String) n).toString();
							String[] split = st.split("#");

							sensorTypeMeasurementDataType = (split[1].substring(0, split[1].length() - 1));
							System.out.print("datadtype : " + sensorTypeMeasurementDataType + " ");
						}
					}

				}// while
				return sensorTypeMeasurementDataType;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String getSensTypeInArea(double longitude, double latitude, float radius) {
			StringBuilder update = new StringBuilder();

			String str = ("select distinct(?sensLabelType) ?type " + "from <" + lsmMetaGraph + "> "
					+ "WHERE " + "{"

					+ "?type <http://www.w3.org/2000/01/rdf-schema#label> ?sensLabelType."
					+ "?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."
					+ "?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p."

					+ "?p geo:geometry ?geo." + "filter (<bif:st_intersects>(?geo,<bif:st_point>("
					+ longitude + "," + latitude + ")," + radius + "))."

			+ "}");

			update.append(str);
			return update.toString();
		}

		public static String getMeasumerementAndUnitOfSensorTypeInArea(double longitude, double latitude,
				float radius, String sensorType) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?measurement ?unit  " + "WHERE " + "{"

			+ "?prob <http://www.w3.org/2000/01/rdf-schema#label> ?measurement. "
					+ "?prob <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit."
					+ "?prob <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."
					+ "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId."

					+ "{" + "select ?sensorId " + "from <" + lsmMetaGraph + "> " + "WHERE " + "{"

					+ "?type <http://www.w3.org/2000/01/rdf-schema#label> '" + sensorType + "' ."
					+ "?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "

					+ "FILTER EXISTS {?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. }"
					+ "?p geo:geometry ?geo. " + "filter (<bif:st_intersects>(?geo,<bif:st_point>("
					+ longitude + "," + latitude + ")," + radius + ")). " + "}" + "}"

			+ "}group by (?measurement) ");

			update.append(str);
			return update.toString();
		}

		public static String getMeasumerementDataTypeOfSensorTypeInArea(double longitude, double latitude,
				float radius, String sensorType, String measurement, String unit) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?value  " + "WHERE " + "{"

			+ "?prob <http://lsm.deri.ie/ont/lsm.owl#value> ?value . "
					+ "?prob <http://www.w3.org/2000/01/rdf-schema#label> '"
					+ measurement
					+ "' . "
					+ "?prob <http://lsm.deri.ie/ont/lsm.owl#unit> '"
					+ unit
					+ "' ."

					+ "?prob <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."
					+ "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId."

					+ "{"
					+ "select ?sensorId "
					+ "from <"
					+ lsmMetaGraph
					+ "> "
					+ "WHERE "
					+ "{"

					+ "?type <http://www.w3.org/2000/01/rdf-schema#label> '"
					+ sensorType
					+ "' ."
					+ "?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "

					+ "FILTER EXISTS {?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. }"
					+ "?p geo:geometry ?geo. "
					+ "filter (<bif:st_intersects>(?geo,<bif:st_point>("
					+ longitude + "," + latitude + ")," + radius + ")). " + "}" + "}"

			+ "}limit 1 ");

			update.append(str);
			return update.toString();
		}
	}

	final static Logger logger = LoggerFactory.getLogger(DiscoverSensorsImpl.class);

	private String userID;
	private double longitude;
	private double latitude;
	private float radius;

	private SensorTypes sensorTypes = null;

	public DiscoverSensorsImpl(String userID, double longitude, double latitude, float radius) {
		
		initializeProperties();
		lsmMetaGraph = props.getProperty(LSM_META_GRAPH);
//		lsmDataGraph = props.getProperty(LSM_DATA_GRAPH);

		this.userID = userID;
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;

		logger.debug("Recieved Parameters: " + "userID=" + userID + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", radius=" + radius);

		discoversensors();
	}
	
	/**
	 * Initialize the Properties
	 */
	private void initializeProperties() {

		String jbosServerConfigDir = System.getProperty("jboss.server.config.dir");
		String openIotConfigFile = jbosServerConfigDir + File.separator + PROPERTIES_FILE;
		props = new Properties();

		logger.debug("jbosServerConfigDir:" + openIotConfigFile);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(openIotConfigFile);

		} catch (FileNotFoundException e) {
			// TODO Handle exception

			logger.error("Unable to find file: " + openIotConfigFile);

		}

		// loading properites from properties file
		try {
			props.load(fis);
		} catch (IOException e) {
			// TODO Handle exception
			logger.error("Unable to load properties from file " + openIotConfigFile);
		}

	}

	/**
	 * @return Returns the SensorTypes, within the given area defined by the
	 *         lon,lat and rad parameters
	 */
	public SensorTypes getSensorTypes() {
		return sensorTypes;
	}

	// helper methods
	private void discoversensors() {
		sensorTypes = new SensorTypes();

		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. Returning an empty SensorTypes object. ", e);
			return;
		}

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getSensTypeInArea(longitude, latitude,
				radius));
		List<SensorTypeData> sensorTypesList = Queries.parseSensorTypeInArea(qres);

		for (int i = 0; i < sensorTypesList.size(); i++) {
			SensorType sensorType = new SensorType();
			sensorType.setId(sensorTypesList.get(i).getID());
			sensorType.setName(sensorTypesList.get(i).getLabel());

			qres = sparqlCl.sparqlToQResult(Queries.getMeasumerementAndUnitOfSensorTypeInArea(longitude,
					latitude, radius, sensorTypesList.get(i).getLabel()));
			List<SensorTypeMetaData> sensorTypeMetaDataList = Queries.parseSensorMetaData(qres);

			for (int j = 0; j < sensorTypeMetaDataList.size(); j++) {
				MeasurementCapability mc = new MeasurementCapability();
				mc.setType(sensorTypeMetaDataList.get(j).getMeasuredVal());

				Unit unit = new Unit();
				unit.setName(sensorTypeMetaDataList.get(j).getUnit());

				qres = sparqlCl.sparqlToQResult(Queries.getMeasumerementDataTypeOfSensorTypeInArea(longitude,
						latitude, radius, sensorTypesList.get(i).getLabel(), mc.getType(), unit.getName()));
				String dataType = Queries.parseSensorTypeMeasurementDataType(qres);

				unit.setType(dataType);

				mc.getUnit().add(unit);

				sensorType.getMeasurementCapability().add(mc);
			}

			sensorTypes.getSensorType().add(sensorType);
		}// for
	}
}// class
