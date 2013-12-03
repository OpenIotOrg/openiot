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

import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServiceRegistryDao;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;

public class LSMServiceRegistryDaoImpl implements ServiceRegistryDao {

	private LSMOAuthManager manager = LSMOAuthManager.getInstance();

	public RegisteredService save(RegisteredService registeredService) {
		final boolean isNew = registeredService.getId() == -1;
		if (registeredService instanceof LSMRegisteredServiceImpl) {
			LSMRegisteredServiceImpl lsmRegisteredServiceImpl = (LSMRegisteredServiceImpl) registeredService;
			if (isNew) {
				/********************************
				 * To be retrieved from LSM *
				 ********************************/
				// "Insert into RegisteredServiceImpl (id, ...) values (...) "
				final List<RegisteredService> allRegisteredServices = manager.getAllRegisteredServices();
				long id = 1;
				if (allRegisteredServices != null)
					for (RegisteredService service : allRegisteredServices)
						if (service.getId() >= id)
							id = service.getId() + 1;
				lsmRegisteredServiceImpl.setId(1);

				manager.addRegisteredService(lsmRegisteredServiceImpl);
			} else {
				/********************************
				 * To be retrieved from LSM *
				 ********************************/
				// "update RegisteredServiceImpl where id=:registeredService.getId() set ...=..., ... "
				manager.deleteRegisteredService(lsmRegisteredServiceImpl.getId());
				manager.addRegisteredService(lsmRegisteredServiceImpl);
			}
			return manager.getRegisteredService(lsmRegisteredServiceImpl.getId());
		}
		return null;
	}

	public boolean delete(RegisteredService registeredService) {
		manager.deleteRegisteredService(registeredService.getId());
		return manager.getRegisteredService(registeredService.getId()) == null;
	}

	public List<RegisteredService> load() {
		return manager.getAllRegisteredServices();
	}

	public RegisteredService findServiceById(long id) {
		return manager.getRegisteredService(id);
	}

}
