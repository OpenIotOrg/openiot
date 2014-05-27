package org.openiot.security.client;

import java.util.ArrayList;

public class PermissionsUtil {

	public final static String PER_PREFIX_MAIN = "lsm-light.server.main:";
	public final static String ADD_SENSOR_MAIN = PER_PREFIX_MAIN +"addSensor";
	public final static String ADD_TRIPLES_MAIN = PER_PREFIX_MAIN +"addTriples";
	public final static String UPDATE_SENSOR_DATA_MAIN = PER_PREFIX_MAIN +"updateSensorData";
	public final static String DEL_TRIPLES_MAIN = PER_PREFIX_MAIN +"delTriples";
	public final static String UPDATE_TRIPLES_MAIN = PER_PREFIX_MAIN +"updateTriples";
	public final static String DEL_SENSOR_MAIN = PER_PREFIX_MAIN +"delSensor";
	public final static String GET_SENSOR_MAIN = PER_PREFIX_MAIN +"getSensor";
	public final static String DEL_READING_MAIN = PER_PREFIX_MAIN +"delReading";
	public final static String LSM_MAIN_ALL = PER_PREFIX_MAIN +"all";
			
	public final static String PER_PREFIX_GUESS = "lsm-light.server.guess:";
	public final static String ADD_SENSOR_GUESS = PER_PREFIX_GUESS +"addSensor";
	public final static String ADD_TRIPLES_GUESS = PER_PREFIX_GUESS +"addTriples";
	public final static String UPDATE_SENSOR_DATA_GUESS = PER_PREFIX_GUESS +"updateSensorData";
	public final static String DEL_TRIPLES_GUESS = PER_PREFIX_GUESS +"delTriples";
	public final static String UPDATE_TRIPLES_GUESS = PER_PREFIX_GUESS +"updateTriples";
	public final static String DEL_SENSOR_GUESS = PER_PREFIX_GUESS +"delSensor";
	public final static String GET_SENSOR_GUESS = PER_PREFIX_GUESS +"getSensor";
	public final static String DEL_READING_GUESS = PER_PREFIX_GUESS +"delReading";
	public final static String LSM_GUESS_ALL = PER_PREFIX_GUESS +"all";
	
	public final static String PER_PREFIX_DEMO = "lsm-light.server.demo:";
	public final static String ADD_SENSOR_DEMO = PER_PREFIX_DEMO +"addSensor";
	public final static String ADD_TRIPLES_DEMO = PER_PREFIX_DEMO +"addTriples";
	public final static String UPDATE_SENSOR_DATA_DEMO = PER_PREFIX_DEMO +"updateSensorData";
	public final static String DEL_TRIPLES_DEMO = PER_PREFIX_DEMO +"delTriples";
	public final static String UPDATE_TRIPLES_DEMO = PER_PREFIX_DEMO +"updateTriples";
	public final static String DEL_SENSOR_DEMO = PER_PREFIX_DEMO +"delSensor";
	public final static String GET_SENSOR_DEMO = PER_PREFIX_DEMO +"getSensor";
	public final static String DEL_READING_DEMO = PER_PREFIX_DEMO +"delReading";
	public final static String LSM_DEMO_ALL = PER_PREFIX_DEMO +"all";
	
	public final static String LSM_ALL = "lsm-light.server:" +"all";
	
	public final static String EXT_RETRIEVE_PERMISSIONS = "ext:retrieve_permissions";
	public final static String SEC_MGMT_GENERAL = "admin:user_mgmt_general";
	public final static String SEC_MGMT_DEL_USER = "admin:delete_user";
	public final static String SEC_MGMT = "admin:user_mgmt:";
	public final static String SEC_MGMT_CREATE_ROLE = "admin:create_role:";
	public final static String SEC_MGMT_CREATE_PERMISSION = "admin:create_permission:";
	public final static String SEC_MGMT_DEL_ROLE = "admin:delete_role:";
	public final static String SEC_MGMT_DEL_PERMISSION = "admin:delete_permission:";
	public final static String SEC_MGMT_GRANT_ROLE = "admin:grant_role:";
	public final static String SEC_MGMT_ALL = "admin:*:";
	
	public final static String SEC_MGMT_SERVICE_MGMT = "admin:service_mgmt:";
	
	public final static ArrayList<String> GUESS_GRAPHS = new ArrayList<String>();
	public final static ArrayList<String> DEMO_GRAPHS = new ArrayList<String>();
	public final static ArrayList<String> MAIN_GRAPHS = new ArrayList<String>();
	
	public final static String GUESS_USER = "guess";
	public final static String DEMO_USER = "demo";
	public final static String MAIN_USER = "main";
	
	static{
		init();
	}
	
	public static void init() {
		GUESS_GRAPHS.add("http://services.openiot.eu/graphs/guess/sensormeta");
		GUESS_GRAPHS.add("http://services.openiot.eu/graphs/guess/sensordata");
		GUESS_GRAPHS.add("http://services.openiot.eu/graphs/guess/functionaldata");
		
		DEMO_GRAPHS.add("http://services.openiot.eu/graphs/demo/sensormeta");
		DEMO_GRAPHS.add("http://services.openiot.eu/graphs/demo/sensordata");
		DEMO_GRAPHS.add("http://services.openiot.eu/graphs/demo/functionaldata");
		
		MAIN_GRAPHS.add("http://services.openiot.eu/graphs/main/sensormeta");
		MAIN_GRAPHS.add("http://services.openiot.eu/graphs/main/sensordata");
		MAIN_GRAPHS.add("http://services.openiot.eu/graphs/main/functionaldata");
	}
	
	public static String getUserType(String graphURL){
		if(GUESS_GRAPHS.contains(graphURL))
			return GUESS_USER;
		if(DEMO_GRAPHS.contains(graphURL))
			return DEMO_USER;
		return MAIN_USER;		
	}
}
