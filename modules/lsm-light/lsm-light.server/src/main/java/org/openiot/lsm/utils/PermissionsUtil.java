package org.openiot.lsm.utils;

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
}
