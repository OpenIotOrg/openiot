package org.openiot.scheduler.core.api.impl;

/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.scheduler.core.test.SensorTypesPopulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */
public class DiscoverSensorsImpl {

	private SensorTypes sensorTypes = null;

	private Logger logger;

	public DiscoverSensorsImpl(String userID, double longitude, double latitude, float radius) {

		logger = LoggerFactory.getLogger(DiscoverSensorsImpl.class);

		logger.debug("Recieved Parameters: userID=" + userID + ", longitude=" + longitude + ", latitude="
				+ latitude + ", radius=" + radius);

		discoversensors();

	}

	/**
	 * 
	 */
	private void discoversensors() {

		// populate with Test Data
		SensorTypesPopulation sensorTypesPopulation = new SensorTypesPopulation();
		sensorTypes = sensorTypesPopulation.getSensorTypes();

	}

	/**
	 * @return SensorTypes
	 */
	public SensorTypes getSensorTypes() {

		return sensorTypes;

	}

}
