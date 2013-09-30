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

package org.openiot.gsn.wrappers.sbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.wrappers.AbstractWrapper;

public class SboxWrapper extends AbstractWrapper {

	private final transient Logger logger = Logger.getLogger(SboxWrapper.class);
	// Instance params
	private static int instanceCounter = 0;
	// Sampling rate
	private static final int DEFAULT_SAMPLING_RATE = 30000; // 30sec
	private long samplingRate = DEFAULT_SAMPLING_RATE;
	// Connection params
	private String sensorServerHost;
	private int sensorServerPort;
	private String sensorUID;
	// Field descriptions
	private DataField[] fields;

	public boolean initialize() {
		AddressBean params = getActiveAddressBean();
		if (params.getPredicateValue("rate") != null) {
			samplingRate = (long) Integer.parseInt(params.getPredicateValue("rate"));
			logger.info("Sampling rate set to " + samplingRate + " msec.");
		}

		if (params.getPredicateValue("host") != null) {
			sensorServerHost = params.getPredicateValue("host");
		} else {
			logger.warn("Missing predicate >host<. Initialization failed");
			return false;
		}

		if (params.getPredicateValue("port") != null) {
			sensorServerPort = Integer.parseInt(params.getPredicateValue("port"));
		} else {
			logger.warn("Missing predicate >port<. Initialization failed");
			return false;
		}

		if (params.getPredicateValue("sensorUID") != null) {
			sensorUID = params.getPredicateValue("sensorUID");
		} else {
			logger.warn("Missing predicate >sensorUID<. Initialization failed");
			return false;
		}

		// Connect to server to lookup field definitions
		JSONArray fieldDefinitions = sendCommand("fields");
		if (fieldDefinitions == null) {
			logger.warn("Could not retrieve field definitions from server. Initialization failed");
			return false;
		}
		initializeFields(fieldDefinitions);

		// Setup wrapper name
		setName(SboxWrapper.class.getSimpleName() + "[" + sensorServerHost + ":" + sensorServerPort + "][" + sensorUID + "]-TID_" + instanceCounter++);
		return true;
	}

	public void run() {
		while (isActive()) {
			try {
				// delay
				Thread.sleep(samplingRate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}

			// Retrieve data from server
			JSONArray state = sendCommand("state");
			if (state == null) {
				logger.error("Bad response from server");
			} else {
				Serializable[] dataPacket = new Serializable[fields.length];
				for (int fieldIndex = 0; fieldIndex < fields.length; fieldIndex++) {
					DataField fieldDefinition = this.fields[fieldIndex];
					JSONObject dataMatch = null;
					for (int dataIndex = 0; dataIndex < fields.length; dataIndex++) {
						JSONObject fieldData = (JSONObject) state.get(dataIndex);
						if (fieldDefinition.getName().equalsIgnoreCase((String) fieldData.get("name"))) {
							dataMatch = fieldData;
							break;
						}
					}

					Object value = null;
					if (dataMatch != null) {
						value = dataMatch.get("value");
						if (fieldDefinition.getDataTypeID() == DataTypes.BIGINT) {
							value = Long.valueOf(value.toString());
						} else if (fieldDefinition.getDataTypeID() == DataTypes.DOUBLE) {
							value = Double.valueOf(value.toString());
						} else if (fieldDefinition.getDataTypeID() == DataTypes.INTEGER) {
							value = Integer.valueOf(value.toString());
						} else if (fieldDefinition.getDataTypeID() == DataTypes.SMALLINT) {
							value = Short.valueOf(value.toString());
						} else if (fieldDefinition.getDataTypeID() == DataTypes.TINYINT) {
							value = Byte.valueOf(value.toString());
						} else if (fieldDefinition.getDataTypeID() == DataTypes.VARCHAR) {
							value = value.toString();
						}
					}

					dataPacket[fieldIndex] = (Serializable) value;
				}

				logger.debug("Sending new data packet for sensor " + sensorUID + ": " + ArrayUtils.toString(dataPacket));

				// post the data to GSN
				postStreamElement(dataPacket);
			}
		}
	}

	public DataField[] getOutputFormat() {
		return fields;
	}

	public String getWrapperName() {
		return "SBOX wrapper";
	}

	public void dispose() {
		instanceCounter--;
	}

	protected void initializeFields(JSONArray fieldDefinitions) {
		this.fields = new DataField[fieldDefinitions.size()];
		for (int fieldIndex = 0; fieldIndex < fieldDefinitions.size(); fieldIndex++) {
			JSONObject fieldDefinition = (JSONObject) fieldDefinitions.get(fieldIndex);
			String fieldName = (String) fieldDefinition.get("name");
			String fieldType = (String) fieldDefinition.get("type");
			String fieldDescription = (String) fieldDefinition.get("description");

			this.fields[fieldIndex] = new DataField(fieldName, fieldType, fieldDescription);
		}
	}

	protected JSONArray sendCommand(String commandName) {
		JSONObject request = new JSONObject();
		request.put("command", commandName);
		request.put("sensorUID", sensorUID);
		Socket serverConnection = null;
		try {
			serverConnection = new Socket(sensorServerHost, sensorServerPort);
			PrintWriter serverOutput = new PrintWriter(serverConnection.getOutputStream(), true);
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));

			// Send command
			serverOutput.write(request.toJSONString() + "\n");
			serverOutput.flush();

			// Read output
			String output = serverInput.readLine();
			if (output == null || output.isEmpty()) {
				return null;
			}
			Object response = JSONValue.parse(output);
			if (response instanceof JSONArray) {
				return (JSONArray) response;
			}

			logger.warn("Invalid response from server");
			return null;
		} catch (UnknownHostException ex) {
			logger.warn("Unkown host >" + sensorServerHost + "<; connection attempt aborted");
			return null;
		} catch (IOException ex) {
			logger.warn("IO exception connecting to host >" + sensorServerHost + "<; connection attempt aborted");
			return null;
		} finally {
			if (serverConnection != null) {
				try {
					serverConnection.close();
				} catch (Exception ex) {
					logger.warn("Caught exception while disconnecting from host >" + sensorServerHost + "<");
					return null;
				}
			}
		}
	}
}
