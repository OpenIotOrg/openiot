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

/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.openiot.security.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceImpl;
import org.jasig.cas.services.ReloadableServicesManager;
import org.jasig.cas.services.ServiceRegistryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.github.inspektr.audit.annotation.Audit;

public class OpenIoTServicesManagerImpl implements ReloadableServicesManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/** Instance of ServiceRegistryDao. */
	@NotNull
	private ServiceRegistryDao serviceRegistryDao;

	/** Map to store all services. */
	private ConcurrentHashMap<Long, RegisteredService> services = new ConcurrentHashMap<Long, RegisteredService>();

	/** Default service to return if none have been registered. */
	private RegisteredService disabledRegisteredService;

	public OpenIoTServicesManagerImpl(final ServiceRegistryDao serviceRegistryDao) {
		this(serviceRegistryDao, new ArrayList<String>());
	}

	/**
	 * Constructs an instance of the {@link DefaultServicesManagerImpl} where the default
	 * RegisteredService can include a set of default attributes to use if no services are defined
	 * in the registry.
	 * 
	 * @param serviceRegistryDao
	 *            the Service Registry Dao.
	 * @param defaultAttributes
	 *            the list of default attributes to use.
	 */
	public OpenIoTServicesManagerImpl(final ServiceRegistryDao serviceRegistryDao, final List<String> defaultAttributes) {
		this.serviceRegistryDao = serviceRegistryDao;
		this.disabledRegisteredService = constructDefaultRegisteredService(defaultAttributes);

		load();
	}

	@Transactional(readOnly = false)
	@Audit(action = "DELETE_SERVICE", actionResolverName = "DELETE_SERVICE_ACTION_RESOLVER", resourceResolverName = "DELETE_SERVICE_RESOURCE_RESOLVER")
	public synchronized RegisteredService delete(final long id) {
		final RegisteredService r = findServiceBy(id);
		if (r == null) {
			return null;
		}

		this.serviceRegistryDao.delete(r);
		this.services.remove(id);

		return r;
	}

	/**
	 * Note, if the repository is empty, this implementation will return a default service to grant
	 * all access.
	 * <p>
	 * This preserves default CAS behavior.
	 */
	public RegisteredService findServiceBy(final Service service) {
		final Collection<RegisteredService> c = convertToTreeSet();

		if (c.isEmpty()) {
			return this.disabledRegisteredService;
		}

		for (final RegisteredService r : c) {
			if (r.matches(service)) {
				return r;
			}
		}

		return null;
	}

	public RegisteredService findServiceBy(final long id) {
		final RegisteredService r = this.services.get(id);

		try {
			return r == null ? null : (RegisteredService) r.clone();
		} catch (final CloneNotSupportedException e) {
			return r;
		}
	}

	protected TreeSet<RegisteredService> convertToTreeSet() {
		return new TreeSet<RegisteredService>(this.services.values());
	}

	public Collection<RegisteredService> getAllServices() {
		return Collections.unmodifiableCollection(convertToTreeSet());
	}

	public boolean matchesExistingService(final Service service) {
		return findServiceBy(service) != null;
	}

	@Transactional(readOnly = false)
	@Audit(action = "SAVE_SERVICE", actionResolverName = "SAVE_SERVICE_ACTION_RESOLVER", resourceResolverName = "SAVE_SERVICE_RESOURCE_RESOLVER")
	public synchronized RegisteredService save(final RegisteredService registeredService) {
		final RegisteredService r = this.serviceRegistryDao.save(registeredService);
		this.services.put(r.getId(), r);
		return r;
	}

	public void reload() {
		log.info("Reloading registered services.");
		load();
	}

	private void load() {
		final ConcurrentHashMap<Long, RegisteredService> localServices = new ConcurrentHashMap<Long, RegisteredService>();

		for (final RegisteredService r : this.serviceRegistryDao.load()) {
			log.debug("Adding registered service " + r.getServiceId());
			localServices.put(r.getId(), r);
		}

		this.services = localServices;
		log.info(String.format("Loaded %s services.", this.services.size()));
	}

	private RegisteredService constructDefaultRegisteredService(final List<String> attributes) {
		final RegisteredServiceImpl r = new RegisteredServiceImpl();
		r.setAllowedToProxy(true);
		r.setAnonymousAccess(false);
		r.setEnabled(true);
		r.setSsoEnabled(true);
		r.setAllowedAttributes(attributes);

		if (attributes == null || attributes.isEmpty()) {
			r.setIgnoreAttributes(true);
		}

		return r;
	}

}