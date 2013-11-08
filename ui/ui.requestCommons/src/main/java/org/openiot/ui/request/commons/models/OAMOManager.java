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

package org.openiot.ui.request.commons.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openiot.commons.osdspec.model.OAMO;
import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.ui.request.commons.providers.SchedulerAPIWrapper;
import org.openiot.ui.request.commons.providers.exceptions.APIException;

/**
 * The OAMOManager class provides a common interface for manipulating OAMO
 * objects and its used both by the request definition and request presentation
 * modules
 * 
 * @author aana
 * 
 */
public class OAMOManager {
	private String userId;
	private List<OAMO> availableOAMOs;
	private OAMO selectedOAMO;

	public OAMOManager() {
		this.availableOAMOs = new ArrayList<OAMO>();
		this.selectedOAMO = null;
	}

	/**
	 * Load the OAMO objects for the given user id using the scheduler API
	 * 
	 * @param userId
	 * @throws APIException
	 */
	public void loadUserOAMOs(String userId) throws APIException {
		this.userId = userId;
		availableOAMOs.clear();
		selectOAMO(null);
		OSDSpec spec = SchedulerAPIWrapper.getAvailableApps(userId);
		loadOSDSPec(spec);
	}
	
	/**
	 * Load all OAMOs in the given OSDSpec
	 */
	public void loadOSDSPec( OSDSpec spec ){
		availableOAMOs.clear();
		selectOAMO(null);
		
		availableOAMOs.addAll(spec.getOAMO());
	}

	/**
	 * Create a new OAMO object and optionally activate it
	 * 
	 * @param name
	 * @param description
	 * @param makeActive
	 */
	public void createOAMO(String name, String description, boolean makeActive) {
		OAMO oamo = new OAMO();
		oamo.setName(name);
		oamo.setDescription(description);
		availableOAMOs.add(oamo);

		if (makeActive) {
			selectOAMO(oamo);
		}
	}

	/**
	 * Activate the supplied OAMO
	 * 
	 * @param oamo
	 */
	public void selectOAMO(OAMO oamo) {
		this.selectedOAMO = oamo;
	}

	/**
	 * Get the currently selected OAMO
	 * 
	 * @return
	 */
	public OAMO getSelectedOAMO() {
		return selectedOAMO;
	}
	
	public List<OAMO> getAvailableOAMOs() {
		return availableOAMOs;
	}

	
	/**
	 * Select an OAMO by its name
	 * 
	 * @param name
	 */
	public void selectOAMOByName(String name) {
		for (OAMO oamo : availableOAMOs) {
			if (oamo.getName().equals(name)) {
				selectOAMO(oamo);
				return;
			}
		}
	}

	/**
	 * Select an OAMO by its id
	 * 
	 * @param id
	 */
	public void selectOAMOById(String id) {
		for (OAMO oamo : availableOAMOs) {
			if (oamo.getId().equals(id)) {
				selectOAMO(oamo);
				return;
			}
		}
	}

	/**
	 * Delete the supplied OAMO from the availabe OAMO list
	 * 
	 * @param oamo
	 */
	protected void deleteOAMO(OAMO oamo) {
		if (oamo == null) {
			return;
		}
		availableOAMOs.remove(oamo);
		if (selectedOAMO != null && selectedOAMO.getId().equals(oamo.getId())) {
			selectedOAMO = null;
		}
	}

	/**
	 * Remove the OAMO matching the given name from the available OAMO list
	 * 
	 * @param name
	 */
	public void deleteOAMOByName(String name) {
		for (OAMO oamo : availableOAMOs) {
			if (oamo.getName().equals(name)) {
				deleteOAMO(oamo);
				return;
			}
		}
	}

	/**
	 * Remove the OAMO matching the given id from the available OAMO list
	 * 
	 * @param name
	 */
	public void deleteOAMOById(String id) {
		for (OAMO oamo : availableOAMOs) {
			if (oamo.getId().equals(id)) {
				deleteOAMO(oamo);
				return;
			}
		}
	}

	/**
	 * Clear the contents of the currently selected OAMO
	 */
	public void resetSelectedOAMO() {
		if (selectedOAMO == null) {
			return;
		}

		selectedOAMO.setGraphMeta(null);
		selectedOAMO.getOSMO().clear();
	}

	/**
	 * Persist the supplied OAMO using the scheduler API
	 * 
	 * @param oamo
	 * @throws APIException
	 */
	protected void saveOAMO(OAMO oamo) throws APIException {
		OSDSpec spec = new OSDSpec();
		spec.setUserID(this.userId);
		spec.getOAMO().add(oamo);

		SchedulerAPIWrapper.registerService(spec);
	}

	/**
	 * Persist the currently selected OAMO using the scheduler API
	 * 
	 * @throws APIException
	 */
	public void saveSelectedOAMO() throws APIException {
		if (selectedOAMO == null) {
			return;
		}

		saveOAMO(selectedOAMO);
	}

	/**
	 * Check if an OAMO with the given name exists in the available OAMO list
	 * 
	 * @param name
	 */
	public boolean exists(String name) {
		for (OAMO oamo : availableOAMOs) {
			if (oamo.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Export all OAMOs as an OSDSpec
	 * @return The OSDSpec
	 */
	public OSDSpec exportOSDSpec(){
		OSDSpec spec = new OSDSpec();
		spec.setUserID(this.userId);
		spec.getOAMO().addAll(availableOAMOs);
		
		return spec;
	}

}
