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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;

/**
 * @author Mehdi Riahi
 * 
 */
@ManagedBean
@ViewScoped
public class GuestServiceController extends AbstractController {
	private static final long serialVersionUID = 2253446876974799511L;

	private List<LSMRegisteredServiceImpl> services;

	private String serviceURL;

	@ManagedProperty(value = "#{securityManagerService}")
	private SecurityManagerService securityManagerService;

	public GuestServiceController() {

	}

	public void fetchServices() {
		if (services == null)
			services = new ArrayList<>();
		else
			services.clear();

		if (isDemoEnabled()) {
			int idx = serviceURL.indexOf("//");
			String hostAddress = "";
	        if(idx > -1 && serviceURL.length() > idx + 2)
	        	hostAddress = serviceURL.substring(idx + 2);
			Pattern pattern = Pattern.compile(String.format("(http|https)://%s/(.*?)/.*", hostAddress));
			String[] demoServices = Utils.getPropertyManagement().getProperty(Utils.DEMO_SERVICES, "").split(",");
			HashSet<String> servicesSet = new HashSet<>();
			for (String serviceKey : demoServices)
				servicesSet.add(serviceKey);
			final List<RegisteredService> servicesAll = securityManagerService.getAllServices();
			for (RegisteredService registeredService : servicesAll) {
				if (registeredService.getServiceId().toLowerCase().startsWith("rest")) {
					if (servicesSet.contains(registeredService.getName()))
						services.add((LSMRegisteredServiceImpl) registeredService);
				} else {
					Matcher matcher = pattern.matcher(registeredService.getServiceId());
					if (matcher.matches() && servicesSet.contains(matcher.group(2)))
						services.add((LSMRegisteredServiceImpl) registeredService);
				}
			}
		}
	}

	public List<LSMRegisteredServiceImpl> getServices() {
		return services;
	}

	public void setSecurityManagerService(SecurityManagerService securityManagerService) {
		this.securityManagerService = securityManagerService;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

}
