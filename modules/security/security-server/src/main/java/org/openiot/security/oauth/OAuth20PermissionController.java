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

package org.openiot.security.oauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.profile.CasWrapperProfile;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * This controller returns the roles and permissions associated with the roles
 * for the requesting client
 * 
 * @author Mehdi Riahi
 */
public final class OAuth20PermissionController extends AbstractController {

	private static Logger log = LoggerFactory.getLogger(OAuth20PermissionController.class);

	private static final String MISSING_CLIENT_ID = "missing_clientId";
	private static final String NONEXISTENT_CLIENT_ID = "nonexisting_clientId";
	private static final String TICKET_NOT_GRANTED = "ticket_not_granted";

	private final ServicesManager servicesManager;

	private final TicketRegistry ticketRegistry;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private String sql = "SELECT ur.role_name AS role_name, rp.permission_name AS permission_name FROM "
			+ "USERS_ROLES ur NATURAL LEFT JOIN ROLES_PERMISSIONS rp WHERE ur.username = :username AND (rp.service_id is null OR rp.service_id=:serviceId)";

	public OAuth20PermissionController(final ServicesManager servicesManager, final TicketRegistry ticketRegistry, DataSource dataSource) {
		this.servicesManager = servicesManager;
		this.ticketRegistry = ticketRegistry;

		Set<String> userAttributeNames = new HashSet<String>();
		userAttributeNames.add("role_name");

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final String clientId = request.getParameter(OAuthConstants.CLIENT_ID);
		log.debug("clientId : {}", clientId);
		final String accessToken = request.getParameter(OAuthConstants.ACCESS_TOKEN);
		log.debug("accessToken : {}", accessToken);

		final String callerClientId = request.getParameter("caller_client_id");
		log.debug("callerClientId : {}", callerClientId);
		final String callerAccessToken = request.getParameter("caller_access_token");
		log.debug("callerAccessToken : {}", callerAccessToken);

		final JsonFactory jsonFactory = new JsonFactory();
		final JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(response.getWriter());

		response.setContentType("application/json");

		// accessToken is required
		if (StringUtils.isBlank(accessToken)) {
			log.error("missing accessToken");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", OAuthConstants.MISSING_ACCESS_TOKEN);
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// caller accessToken is required
		if (StringUtils.isBlank(callerAccessToken)) {
			log.error("missing caller accessToken");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", "missing_callerAccessToken");
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// clientId is required
		if (StringUtils.isBlank(clientId)) {
			log.error("missing clientId");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", MISSING_CLIENT_ID);
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// caller clientId is required
		if (StringUtils.isBlank(callerClientId)) {
			log.error("missing clientId");
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", "missing_callerClientId");
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// get ticket granting ticket
		final TicketGrantingTicket ticketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(accessToken);
		if (ticketGrantingTicket == null || ticketGrantingTicket.isExpired()) {
			log.error("expired accessToken : {}", accessToken);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", OAuthConstants.EXPIRED_ACCESS_TOKEN);
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// get ticket granting ticket for the caller
		final TicketGrantingTicket callerTicketGrantingTicket = (TicketGrantingTicket) this.ticketRegistry.getTicket(callerAccessToken);
		if (callerTicketGrantingTicket == null || callerTicketGrantingTicket.isExpired()) {
			log.error("expired accessToken : {}", callerAccessToken);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", OAuthConstants.EXPIRED_ACCESS_TOKEN + "_for_caller");
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// name of the CAS service
		final Collection<RegisteredService> services = servicesManager.getAllServices();
		RegisteredService service = null;
		for (final RegisteredService aService : services) {
			if (StringUtils.equals(aService.getName(), clientId)) {
				service = aService;
				break;
			}
		}

		if (service == null) {
			log.error("nonexistent clientId : {}", clientId);
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", NONEXISTENT_CLIENT_ID);
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		// TODO: check if the TGT is granted to the client?!
//		final TicketGrantingTicket rawTicket = ((AbstractDistributedTicketRegistry.TicketGrantingTicketDelegator)ticketGrantingTicket).getTicket();
//		final Field servicesField = rawTicket.getClass().getDeclaredField("services");
//		servicesField.setAccessible(true);
//		HashMap<String, Service> servicesMap = new HashMap<String, Service>();
//		servicesMap = (HashMap<String, Service>) servicesField.get(rawTicket);
//		log.error("ServiceMaps is empty ? {}", servicesMap.isEmpty());
//		for(Map.Entry<String, Service> entry : servicesMap.entrySet()){
//			AbstractWebApplicationService webAppService = (AbstractWebApplicationService) entry.getValue();
//			log.error("Service for ticket {} is {}", rawTicket.getId(), webAppService.getId());
//		}
//		if (!servicesMap.containsKey(service.getId()) || !servicesMap.get(service.getId()).equals(service)) {
//			log.error("Ticket is not granted to client : {}", clientId);
//			jsonGenerator.writeStartObject();
//			jsonGenerator.writeStringField("error", TICKET_NOT_GRANTED);
//			jsonGenerator.writeEndObject();
//			jsonGenerator.close();
//			response.flushBuffer();
//			return null;
//		}

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
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("error", NONEXISTENT_CLIENT_ID + "for_caller");
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			response.flushBuffer();
			return null;
		}

		final Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
		final Map<String, Set<String>> permissions = extractPermissions(callerService.getId(), principal.getId());

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

	private Map<String, Set<String>> extractPermissions(Long serviceId, String username) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		paramMap.put("serviceId", serviceId);

		RowCallbackHandlerImpl rowCallbackHandler = new RowCallbackHandlerImpl();
		namedParameterJdbcTemplate.query(sql, paramMap, rowCallbackHandler);
		return rowCallbackHandler.getResults();
	}

	private class RowCallbackHandlerImpl implements RowCallbackHandler {
		Map<String, Set<String>> rolePermissions = new HashMap<String, Set<String>>();

		public void processRow(ResultSet rs) throws SQLException {
			String roleName = rs.getString("role_name");
			String permissionName = rs.getString("permission_name");
			if (rolePermissions.containsKey(roleName) && permissionName != null)
				rolePermissions.get(roleName).add(permissionName);
			else {
				Set<String> set = new HashSet<String>();
				if (permissionName != null)
					set.add(permissionName);
				rolePermissions.put(roleName, set);
			}
		}

		public Map<String, Set<String>> getResults() {
			return rolePermissions;
		}

	}

	static void setLogger(final Logger aLogger) {
		log = aLogger;
	}
}
