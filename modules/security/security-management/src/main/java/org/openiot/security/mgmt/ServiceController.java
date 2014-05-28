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

package org.openiot.security.mgmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.openiot.security.client.AccessControlUtil;
import org.openiot.security.client.OAuthorizationCredentials;
import org.openiot.security.client.PermissionsUtil;
import org.primefaces.context.RequestContext;

/**
 * @author Mehdi Riahi
 * 
 */
@ManagedBean
@ViewScoped
public class ServiceController extends AbstractController {
	private static final long serialVersionUID = 6553286876974799583L;

	private List<LSMRegisteredServiceImpl> services;

	private LSMRegisteredServiceImpl newService;

	private Map<Long, RegisteredService> allServices;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	private boolean restfulService;

	private boolean editing;

	private User user;

	private long myServiceId;

	public ServiceController() {

	}

	public List<LSMRegisteredServiceImpl> getServices() {
		if (services == null) {
			services = new ArrayList<>();

			final List<RegisteredService> servicesAll = securityManagerService.getAllServices();
			allServices = new HashMap<Long, RegisteredService>(servicesAll.size());
			AccessControlUtil acUtil = AccessControlUtil.getInstance();
			OAuthorizationCredentials credentials = acUtil.getOAuthorizationCredentials();

			for (RegisteredService registeredService : servicesAll) {
				allServices.put(registeredService.getId(), registeredService);
				if (acUtil.hasPermission(PermissionsUtil.SEC_MGMT_SERVICE_MGMT + registeredService.getName()))
					services.add((LSMRegisteredServiceImpl) registeredService);
				if (registeredService.getName().equals(credentials.getClientId()))
					myServiceId = registeredService.getId();
			}
			user = securityManagerService.getUser(credentials.getUserId());
		}
		return services;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public boolean isRestfulService() {
		return restfulService;
	}

	public boolean isEditing() {
		return editing;
	}

	public void removeService(LSMRegisteredServiceImpl service) {
		securityManagerService.deleteRegisteredService(service.getId());
		services.remove(service);
		allServices.remove(service.getId());

		addInfoMessage("Service deleted", service.getName());
	}

	public LSMRegisteredServiceImpl getNewService() {
		if (newService == null)
			newService = new LSMRegisteredServiceImpl();
		return newService;
	}

	public void cancelAddService() {
		newService = null;
		editing = false;
	}

	public void addService() {
		addServiceInternal(false);
	}

	public void addRestService() {
		addServiceInternal(true);
	}

	public void editing(LSMRegisteredServiceImpl service) {
		newService = service;
		editing = true;
		restfulService = isRestful(service);
	}

	public void addServiceInternal(boolean isRest) {
		boolean serviceAdded = false;
		if (newService != null && newService.getName().trim().length() > 0) {
			if (editing || isServiceNameUnique(newService) && isServiceNameValid(newService) && isURLValid(newService, isRest)) {

				if (isRest) {
					newService.setServiceId("REST://" + newService.getName());
					newService.setTheme(newService.getName() + "_");
				}

				if (!editing) {
					newService.setEnabled(true);
					newService.setSsoEnabled(true);
					newService.setAllowedToProxy(true);
					newService.setAnonymousAccess(false);
					newService.setIgnoreAttributes(true);
					newService.setEvaluationOrder(0);

					newService = securityManagerService.addRegisteredService(newService);
					services.add(newService);
					allServices.put(newService.getId(), newService);

					createAuthorization(newService);

					addInfoMessage("New service added", newService.getName());
				} else {
					securityManagerService.addRegisteredService(newService);
					addInfoMessage("Service updated", newService.getName());
				}

				newService = null;
				serviceAdded = true;
				editing = false;
			} else {
				if (!editing)
					addErrorMessage("Adding new service failed", "Service key is not unique or service key or URL is not valid");
				else
					addErrorMessage("Updating service failed", "Service URL is not valid");
			}
		} else {
			addWarnMessage("There is no new service to add", "");
		}
		RequestContext.getCurrentInstance().addCallbackParam("serviceAdded", serviceAdded);
	}

	private void createAuthorization(LSMRegisteredServiceImpl service) {
		Permission serviceMgmtPerm = new Permission("admin:service_mgmt:" + service.getName(), "Permission for managing service " + service.getName(),
				myServiceId);

		Permission perm1 = new Permission(PermissionsUtil.SEC_MGMT_CREATE_PERMISSION + service.getName(), "", myServiceId);
		Permission perm2 = new Permission(PermissionsUtil.SEC_MGMT_CREATE_ROLE + service.getName(), "", myServiceId);
		Permission perm3 = new Permission(PermissionsUtil.SEC_MGMT_DEL_PERMISSION + service.getName(), "", myServiceId);
		Permission perm4 = new Permission(PermissionsUtil.SEC_MGMT_DEL_ROLE + service.getName(), "", myServiceId);
		Permission perm5 = new Permission(PermissionsUtil.SEC_MGMT_GRANT_ROLE + service.getName(), "", myServiceId);
		Permission perm6 = new Permission(PermissionsUtil.SEC_MGMT + service.getName(), "", myServiceId);
		Permission mgmtAllPerm = new Permission(PermissionsUtil.SEC_MGMT_ALL + service.getName(), "", myServiceId);

		Permission[] perms = new Permission[] { perm1, perm2, perm3, perm4, perm5, perm6 };

		Role serviceAdminRole = null;
		String serviceAdminRoleName = PermissionsUtil.SEC_MGMT_SERVICE_MGMT + service.getName();

		boolean isAdmin = AccessControlUtil.getInstance().hasPermission("*");
		if (!isAdmin) {
			for (Role r : user.getRoles()) {
				if (r.getServiceId() == service.getId() && r.getName().equals(serviceAdminRoleName)) {
					serviceAdminRole = r;
					break;
				}
			}
		}
		if (serviceAdminRole == null) {
			serviceAdminRole = new Role(serviceAdminRoleName, "Role for managing service " + service.getName(), myServiceId);
			if (!isAdmin)
				user.addRole(serviceAdminRole);
		}
		serviceAdminRole.addPermission(serviceMgmtPerm);

		Role userMgmtRole = new Role("user_mgmt_all:" + service.getName(), "", myServiceId);
		userMgmtRole.addPermission(mgmtAllPerm);
		if (!isAdmin)
			user.addRole(userMgmtRole);

		securityManagerService.addRole(serviceAdminRole);
		securityManagerService.addRole(userMgmtRole);

		if(!isAdmin){
			//TODO: replace with an addRoleToUser() or updateUser() method
			securityManagerService.deleteUser(user.getUsername());
			securityManagerService.addUser(user);
			
			// clearing the cache in order to see the new permission modifications
			AccessControlUtil.getInstance().reset();
		}
		
		for (Permission perm : perms)
			securityManagerService.addPermission(perm);
	}

	public boolean isRestful(LSMRegisteredServiceImpl service) {
		restfulService = service.getServiceId().toLowerCase().startsWith("rest://");
		return restfulService;
	}

	private boolean isURLValid(LSMRegisteredServiceImpl service, boolean isRest) {
		if (isRest)
			return true;
		String serviceURLLowCase = service.getServiceId().toLowerCase();
		return serviceURLLowCase.startsWith("http://") || serviceURLLowCase.startsWith("https://");
	}

	public boolean isServiceNameUnique(LSMRegisteredServiceImpl service) {
		for (RegisteredService r : allServices.values())
			if (r.getName().equals(service.getName()))
				return false;
		return true;
	}

	public boolean isServiceNameValid(LSMRegisteredServiceImpl service) {
		return !service.getName().matches(".*(\\s|__|/).*");
	}
}
