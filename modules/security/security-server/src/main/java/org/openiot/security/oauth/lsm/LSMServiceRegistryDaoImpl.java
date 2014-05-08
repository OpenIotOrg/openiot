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

package org.openiot.security.oauth.lsm;

import java.util.Collections;
import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LSMServiceRegistryDaoImpl implements ServiceRegistryDao {

	private static Logger log = LoggerFactory.getLogger(LSMServiceRegistryDaoImpl.class);
	private LSMOAuthManager manager = LSMOAuthManager.getInstance();
	private boolean initializeOnStartup = false;

	public void setInitializeOnStartup(boolean initializeOnStartup) {
		this.initializeOnStartup = initializeOnStartup;
	}

	public RegisteredService save(RegisteredService registeredService) {
		final boolean isNew = registeredService.getId() == -1;
		LSMRegisteredServiceImpl lsmRegisteredServiceImpl;
		if (registeredService instanceof LSMRegisteredServiceImpl)
			lsmRegisteredServiceImpl = (LSMRegisteredServiceImpl) registeredService;
		else {
			lsmRegisteredServiceImpl = new LSMRegisteredServiceImpl();
			lsmRegisteredServiceImpl.copyFrom(registeredService);
		}

		if (isNew) {
			final List<RegisteredService> allRegisteredServices = manager.getAllRegisteredServices();
			long id = 1;
			if (allRegisteredServices != null)
				for (RegisteredService service : allRegisteredServices)
					if (service.getId() >= id)
						id = service.getId() + 1;
			lsmRegisteredServiceImpl.setId(id);

			manager.addRegisteredService(lsmRegisteredServiceImpl);
		} else {
			manager.deleteRegisteredService(lsmRegisteredServiceImpl.getId());
			manager.addRegisteredService(lsmRegisteredServiceImpl);
		}
		return manager.getRegisteredService(lsmRegisteredServiceImpl.getId());

	}

	public boolean delete(RegisteredService registeredService) {
		log.info("Deleting registered service with id {} ", registeredService.getId());
		manager.deleteRegisteredService(registeredService.getId());
		return manager.getRegisteredService(registeredService.getId()) == null;
	}

	public List<RegisteredService> load() {
		log.info("Reloading registered services ...");
		List<RegisteredService> services = manager.getAllRegisteredServices();
		// if (services.isEmpty() && initializeOnStartup) {
		// log.info("Initializing the graph ...");
		// SecurityModuleInitializer.initialize();
		// services = manager.getAllRegisteredServices();
		// }
		if (services == null) {
			services = Collections.<RegisteredService> emptyList();
			log.warn("There was a problem loading services. Returning an empty list.");
		}
		return services;
	}

	public RegisteredService findServiceById(long id) {
		return manager.getRegisteredService(id);
	}

}
