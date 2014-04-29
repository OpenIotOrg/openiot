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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.profile.CasWrapperProfile;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * This controller returns the roles and permissions associated with the roles for the requesting
 * client
 * 
 * @author Mehdi Riahi
 */
public final class LSMOAuth20PermissionController extends AbstractController {

	private static Logger log = LoggerFactory.getLogger(LSMOAuth20PermissionController.class);

	private static final String MISSING_CLIENT_ID = "missing_clientId";
	private static final String NONEXISTENT_CLIENT_ID = "nonexisting_clientId";

	public static final String ERROR = "error";
	public static final String CALLER_ACCESS_TOKEN = "caller_access_token";
	public static final String CALLER_CLIENT_ID = "caller_client_id";
	public static final String USER_ACCESS_TOKEN = "user_access_token";
	public static final String USER_CLIENT_ID = "user_client_id";
	public static final String TARGET_CLIENT_ID = "target_client_id";

	public static final String PERMISSION_NAME = "ext:retrieve_permissions";

	private final ServicesManager servicesManager;

	private final TicketRegistry ticketRegistry;

	private LSMOAuthManager manager = LSMOAuthManager.getInstance();

	public LSMOAuth20PermissionController(final ServicesManager servicesManager, final TicketRegistry ticketRegistry) {
		this.servicesManager = servicesManager;
		this.ticketRegistry = ticketRegistry;
	}

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final String clientId = request.getParameter(OAuthConstants.CLIENT_ID);
		log.debug("clientId : {}", clientId);
		final String accessToken = request.getParameter(OAuthConstants.ACCESS_TOKEN);
		log.debug("accessToken : {}", accessToken);

		final String callerClientId = request.getParameter(CALLER_CLIENT_ID);
		log.debug("callerClientId : {}", callerClientId);
		final String callerAccessToken = request.getParameter(CALLER_ACCESS_TOKEN);
		log.debug("callerAccessToken : {}", callerAccessToken);

		final String userClientId = request.getParameter(USER_CLIENT_ID);
		log.debug("userClientId : {}", userClientId);

		final String userAccessToken = request.getParameter(USER_ACCESS_TOKEN);
		log.debug("userAccessToken : {}", userAccessToken);

		final String targetClientId = request.getParameter(TARGET_CLIENT_ID);
		log.debug("targetClientId : {}", targetClientId);

		response.setContentType("application/json");

		// accessToken is required
		if (StringUtils.isBlank(accessToken)) {
			log.error("missing accessToken");
			writeErrorMessage(response, OAuthConstants.MISSING_ACCESS_TOKEN);
			return null;
		}

		// clientId is required
		if (StringUtils.isBlank(clientId)) {
			log.error("missing clientId");
			writeErrorMessage(response, MISSING_CLIENT_ID);
			return null;
		}

		// userToken is required
		if (StringUtils.isBlank(userAccessToken)) {
			log.error("missing user accessToken");
			writeErrorMessage(response, "missing_userAccessToken");
			return null;
		}

		// target clientId is required
		if (StringUtils.isBlank(targetClientId)) {
			log.error("missing target clientId");
			writeErrorMessage(response, MISSING_CLIENT_ID + "for_target");
			return null;
		}

		// caller accessToken and clientId are required if one of them is provided
		if (!StringUtils.isBlank(callerAccessToken) || !StringUtils.isBlank(callerClientId)) {
			if (StringUtils.isBlank(callerAccessToken)) {
				log.error("missing caller accessToken");
				writeErrorMessage(response, "missing_callerAccessToken");
				return null;
			} else if (StringUtils.isBlank(callerClientId)) {
				log.error("missing caller clientId");
				writeErrorMessage(response, "missing_callerClientId");
				return null;
			}
		}

		// get ticket granting ticket
		final TicketGrantingTicket ticketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(accessToken);
		if (ticketGrantingTicket == null || ticketGrantingTicket.isExpired()) {
			log.error("expired accessToken : {}", accessToken);
			writeErrorMessage(response, OAuthConstants.EXPIRED_ACCESS_TOKEN);
			return null;
		}

		// get ticket granting ticket for the user
		final TicketGrantingTicket userTicketGrantingTicket;
		if (StringUtils.equals(accessToken, userAccessToken))
			userTicketGrantingTicket = ticketGrantingTicket;
		else {
			userTicketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(userAccessToken);
			if (userTicketGrantingTicket == null || userTicketGrantingTicket.isExpired()) {
				log.error("expired user accessToken : {}", accessToken);
				writeErrorMessage(response, OAuthConstants.EXPIRED_ACCESS_TOKEN + "_for_user");
				return null;
			}
		}

		// Retrieve all registered services
		final Collection<RegisteredService> services = servicesManager.getAllServices();

		// If called accessToken and clientId are provided, check their validity
		if (!StringUtils.isBlank(callerAccessToken)) {
			// get ticket granting ticket for the caller
			final TicketGrantingTicket callerTicketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(callerAccessToken);
			if (callerTicketGrantingTicket == null || callerTicketGrantingTicket.isExpired()) {
				log.error("expired accessToken : {}", callerAccessToken);
				writeErrorMessage(response, OAuthConstants.EXPIRED_ACCESS_TOKEN + "_for_caller");
				return null;
			}

			// name of the CAS service for caller
			RegisteredService callerService = null;
			for (final RegisteredService aService : services) {
				if (StringUtils.equals(aService.getName(), callerClientId)) {
					callerService = aService;
					break;
				}
			}

			if (callerService == null) {
				log.error("nonexistent caller clientId : {}", callerClientId);
				writeErrorMessage(response, NONEXISTENT_CLIENT_ID + "for_caller");
				return null;
			}
		}

		// if user clienId is provided, check its validity
		if (!StringUtils.isBlank(userClientId)) {
			RegisteredService userService = null;
			for (final RegisteredService aService : services) {
				if (StringUtils.equals(aService.getName(), userClientId)) {
					userService = aService;
					break;
				}
			}

			if (userService == null) {
				log.error("nonexistent clientId : {}", userClientId);
				writeErrorMessage(response, NONEXISTENT_CLIENT_ID + "_for_user");
				return null;
			}
		}

		// check validity of clientId
		RegisteredService service = null;
		for (final RegisteredService aService : services) {
			if (StringUtils.equals(aService.getName(), clientId)) {
				service = aService;
				break;
			}
		}

		if (service == null) {
			log.error("nonexistent clientId : {}", clientId);
			writeErrorMessage(response, NONEXISTENT_CLIENT_ID);
			return null;
		}

		// check validity of target clientId
		RegisteredService targetService = null;
		for (final RegisteredService aService : services) {
			if (StringUtils.equals(aService.getName(), targetClientId)) {
				targetService = aService;
				break;
			}
		}

		if (targetService == null) {
			log.error("nonexistent target clientId : {}", clientId);
			writeErrorMessage(response, NONEXISTENT_CLIENT_ID + "for_target");
			return null;
		}

		// TODO: check if the TGT is granted to the client?!
		// final TicketGrantingTicket rawTicket =
		// ((AbstractDistributedTicketRegistry.TicketGrantingTicketDelegator)ticketGrantingTicket).getTicket();
		// final Field servicesField =
		// rawTicket.getClass().getDeclaredField("services");
		// servicesField.setAccessible(true);
		// HashMap<String, Service> servicesMap = new HashMap<String,
		// Service>();
		// servicesMap = (HashMap<String, Service>)
		// servicesField.get(rawTicket);
		// log.error("ServiceMaps is empty ? {}", servicesMap.isEmpty());
		// for(Map.Entry<String, Service> entry : servicesMap.entrySet()){
		// AbstractWebApplicationService webAppService =
		// (AbstractWebApplicationService) entry.getValue();
		// log.error("Service for ticket {} is {}", rawTicket.getId(),
		// webAppService.getId());
		// }
		// if (!servicesMap.containsKey(service.getId()) ||
		// !servicesMap.get(service.getId()).equals(service)) {
		// log.error("Ticket is not granted to client : {}", clientId);
		// jsonGenerator.writeStartObject();
		// jsonGenerator.writeStringField("error", TICKET_NOT_GRANTED);
		// jsonGenerator.writeEndObject();
		// jsonGenerator.close();
		// response.flushBuffer();
		// return null;
		// }

		// Check if the caller has permission for retrieving permission information
		if (!targetClientId.equals(clientId)) {
			final Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
			if (!isPermitted(principal.getId(), targetService.getId())) {
				log.error("[{} from {}] is not permitted to retrieve permission information on [{}]", principal.getId(), clientId, targetClientId);
				writeErrorMessage(response, "permission_denied");
				return null;
			}
		}

		final JsonFactory jsonFactory = new JsonFactory();
		final JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(response.getWriter());

		final Principal principal = userTicketGrantingTicket.getAuthentication().getPrincipal();
		final Map<String, Set<String>> permissions = extractPermissions(targetService.getId(), principal.getId());

		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField(CasWrapperProfile.ID, principal.getId());

		jsonGenerator.writeArrayFieldStart("role_permissions");

		for (final String roleName : permissions.keySet()) {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeArrayFieldStart(roleName);

			for (final String permission : permissions.get(roleName))
				jsonGenerator.writeString(permission);

			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
		}

		jsonGenerator.writeEndArray();
		jsonGenerator.writeEndObject();
		jsonGenerator.close();
		response.flushBuffer();

		return null;
	}

	private void writeErrorMessage(HttpServletResponse response, String message) throws IOException {
		final JsonFactory jsonFactory = new JsonFactory();
		final JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(response.getWriter());

		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("error", message);
		jsonGenerator.writeEndObject();
		jsonGenerator.close();
		response.flushBuffer();
	}

	private Map<String, Set<String>> extractPermissions(Long serviceId, String username) {

		Map<String, Set<String>> rolePermissions = new HashMap<String, Set<String>>();
		final User user = manager.getUserByUsername(username);
		final List<Role> roles = user.getRoles();
		for (Role role : roles) {
			if (role.getServiceId().equals(serviceId)) {
				Set<String> set = new HashSet<String>();
				final List<Permission> permissionForService = role.getPermissions();
				if (permissionForService != null) {
					for (Permission perm : permissionForService)
						set.add(perm.getName());
				}
				rolePermissions.put(role.getName(), set);
			}
		}

		return rolePermissions;
	}

	private boolean isPermitted(String username, Long targetServiceId) {
		final User user = manager.getUserByUsername(username);
		final List<Role> roles = user.getRoles();
		boolean permitted = false;
		for (Role role : roles) {
			if (!permitted && role.getServiceId().equals(targetServiceId)) {
				final List<Permission> permissionForService = role.getPermissions();
				if (permissionForService != null) {
					for (Permission perm : permissionForService)
						if ("*".equals(perm.getName()) || PERMISSION_NAME.equals(perm.getName())) {
							permitted = true;
							break;
						}
				}
			}
		}

		return permitted;
	}

	static void setLogger(final Logger aLogger) {
		log = aLogger;
	}
}
