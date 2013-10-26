package org.openiot.scheduler.core.api.impl.GetAvailableAppIDs;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.commons.descriptiveids.model.DescreptiveIDs;
import org.openiot.commons.descriptiveids.model.DescriptiveID;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */

public class GetAvailableAppIDsImpl {

	
	
    private static final String PROPERTIES_FILE = "openiot.properties";
	private static final String LSM_FUNCTIONAL_GRAPH = "scheduler.core.lsm.openiotFunctionalGraph";
	private Properties props = null;
	
	private static String lsmFunctionalGraph = "";	
	
	 
	
	private static class Queries {
		public static class OAMOData {
			private String oamoName;
			private String oamoID;

			public String getOamoName() {
				return oamoName;
			}

			public void setOamoName(String oamoName) {
				this.oamoName = oamoName;
			}

			public String getOamoID() {
				return oamoID;
			}

			public void setOamoID(String oamoID) {
				this.oamoID = oamoID;
			}
		}




		public static ArrayList<OAMOData> parseAvailableAppIDsOfUser(TupleQueryResult qres) {
			ArrayList<OAMOData> oamoDataList = new ArrayList<OAMOData>();

			try {
				while (qres.hasNext()) {
					BindingSet b = qres.next();
					Set names = b.getBindingNames();

					OAMOData oamoData = new OAMOData();

					for (Object n : names) {
						if (((String) n).equalsIgnoreCase("oamoID")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							oamoData.setOamoID(str);
							System.out.print("oamoID: " + oamoData.getOamoID());
						} else if (((String) n).equalsIgnoreCase("oamoName")) {
							String str = (b.getValue((String) n) == null) ? null : b.getValue((String) n)
									.stringValue();
							oamoData.setOamoName(str);
							System.out.print("oamoName: " + oamoData.getOamoName());
						}
					}
					oamoDataList.add(oamoData);
				}// while
				return oamoDataList;
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static String getAvailableAppIDsOfUser(String userID) {
			StringBuilder update = new StringBuilder();

			String str = ("SELECT ?oamoID ?oamoName  " + "from <" + lsmFunctionalGraph + "> " + "WHERE "
					+ "{" + "?oamoID <http://openiot.eu/ontology/ns/oamoName> ?oamoName . "
					+ "?oamoID <http://openiot.eu/ontology/ns/oamoUserOf> <" + userID + "> . " + "}");

			update.append(str);
			return update.toString();
		}
	}

	// ///

	final static Logger logger = LoggerFactory.getLogger(GetAvailableAppIDsImpl.class);

	private String userID;
	private DescreptiveIDs descriptiveIDs;

	// constructor
	public GetAvailableAppIDsImpl(String userID) {
		
		initializeProperties();
		lsmFunctionalGraph = props.getProperty(LSM_FUNCTIONAL_GRAPH);
		
		
		this.userID = userID;

		logger.debug("Recieved Parameters: " + "userID=" + userID);

		findAvailableAppIDs();
		
		
		
		
		
		
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

	public DescreptiveIDs getAvailableAppIDs() {
		return descriptiveIDs;
	}

	private void findAvailableAppIDs() {
		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {
			logger.error("Init sparql repository error. ", e);
			return;
		}

		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getAvailableAppIDsOfUser(userID));
		ArrayList<Queries.OAMOData> appDataOfUserList = Queries.parseAvailableAppIDsOfUser(qres);

		descriptiveIDs = new DescreptiveIDs();

		for (Queries.OAMOData oamoData : appDataOfUserList) {
			DescriptiveID dID = new DescriptiveID();
			dID.setId(oamoData.getOamoID());
			dID.setName(oamoData.getOamoName());

			descriptiveIDs.getDescriptiveID().add(dID);
		}
	}
}
