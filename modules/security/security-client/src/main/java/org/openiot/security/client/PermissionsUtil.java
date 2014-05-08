package org.openiot.security.client;

public class PermissionsUtil {

	public final static String PER_PREFIX = "lsm-light.server:";
	public final static String ADD_SENSOR = PER_PREFIX +"addSensor";
	public final static String ADD_TRIPLES = PER_PREFIX +"addTriples";
	public final static String UPDATE_SENSOR_DATA = PER_PREFIX +"updateSensorData";
	public final static String DEL_TRIPLES = PER_PREFIX +"delTriples";
	public final static String UPDATE_TRIPLES = PER_PREFIX +"updateTriples";
	public final static String DEL_SENSOR = PER_PREFIX +"delSensor";
	public final static String GET_SENSOR = PER_PREFIX +"getSensor";
	public final static String DEL_READING = PER_PREFIX +"delReading";
	
	public final static String EXT_RETRIEVE_PERMISSIONS = "ext:retrieve_permissions";
	public final static String SEC_MGMT_GENERAL = "admin:user_mgmt_general";
	public final static String SEC_MGMT_DEL_USER = "admin:delete_user";
	public final static String SEC_MGMT = "admin:user_mgmt:";
	public final static String SEC_MGMT_CREATE_ROLE = "admin:create_role:";
	public final static String SEC_MGMT_CREATE_PERMISSION = "admin:create_permission:";
	public final static String SEC_MGMT_DEL_ROLE = "admin:delete_role:";
	public final static String SEC_MGMT_DEL_PERMISSION = "admin:delete_permission:";
	public final static String SEC_MGMT_GRANT_ROLE = "admin:grant_role:";
	
}
