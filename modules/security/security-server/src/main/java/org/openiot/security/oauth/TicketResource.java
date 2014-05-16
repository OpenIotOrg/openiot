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

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.TicketException;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles the creation of Ticket Granting Tickets.
 * 
 * @author Scott Battaglia
 * @author Mehdi Riahi
 * @version $Revision$ $Date$
 * @since 3.3
 * 
 */
public class TicketResource extends Resource {

	private static final Logger log = LoggerFactory.getLogger(TicketResource.class);

	@Autowired
	private CentralAuthenticationService centralAuthenticationService;

	@Autowired
	private ServicesManager servicesManager;

	public final boolean allowGet() {
		return false;
	}

	public final boolean allowPost() {
		return true;
	}

	public final void acceptRepresentation(final Representation entity) throws ResourceException {
		if (log.isDebugEnabled()) {
			log.debug("Obtaining credentials...");
			log.debug(getRequest().getEntityAsForm().toString());
		}

		final RestTGTRequestCredentials credentials = obtainCredentials();

		final Collection<RegisteredService> services = servicesManager.getAllServices();
		RegisteredService service = null;
		for (final RegisteredService aService : services) {
			if (StringUtils.equals(aService.getName(), credentials.getClientId())) {
				service = aService;
				break;
			}
		}
		if (service == null) {
			log.warn("Request for TGT from unknown client: {}", credentials.getClientId());
			getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "Request for TGT from unknown client");
		} else if (!credentials.getSecret().equals(service.getDescription())) {
			log.warn("Client secret [{}] deosn't match for client [{}]", credentials.getSecret(), credentials.getClientId());
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Client secret doesn't match");
		} else {

			try {
				UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials();
				usernamePasswordCredentials.setUsername(credentials.getUsername());
				usernamePasswordCredentials.setPassword(credentials.getPassword());
				final String ticketGrantingTicketId = this.centralAuthenticationService.createTicketGrantingTicket(usernamePasswordCredentials);
				getResponse().setStatus(determineStatus());
				final Reference ticket_ref = getRequest().getResourceRef().addSegment(ticketGrantingTicketId);
				getResponse().setLocationRef(ticket_ref);
				getResponse()
						.setEntity(
								"<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head><title>"
										+ getResponse().getStatus().getCode()
										+ " "
										+ getResponse().getStatus().getDescription()
										+ "</title></head><body><h1>TGT Created</h1><form action=\""
										+ ticket_ref
										+ "\" method=\"POST\">Service:<input type=\"text\" name=\"service\" value=\"\"><br><input type=\"submit\" value=\"Submit\"></form></body></html>",
								MediaType.TEXT_HTML);
			} catch (final TicketException e) {
				log.error(e.getMessage(), e);
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
			}
		}
	}

	/**
	 * Template method for determining which status to return on a successful ticket creation. This
	 * method exists for compatibility reasons with bad clients (i.e. Flash) that can't process 201
	 * with a Location header.
	 * 
	 * @return the status to return.
	 */
	protected Status determineStatus() {
		return Status.SUCCESS_CREATED;
	}

	protected RestTGTRequestCredentials obtainCredentials() {
		final RestTGTRequestCredentials c = new RestTGTRequestCredentials();
		final WebRequestDataBinder binder = new WebRequestDataBinder(c);
		final RestletWebRequest webRequest = new RestletWebRequest(getRequest());

		if (log.isDebugEnabled()) {
			log.debug(getRequest().getEntityAsForm().toString());
			log.debug("Username from RestletWebRequest: " + webRequest.getParameter("username"));
		}

		binder.bind(webRequest);

		return c;
	}

	protected class RestletWebRequest implements WebRequest {

		private final Form form;

		private final Request request;

		public RestletWebRequest(final Request request) {
			this.form = getRequest().getEntityAsForm();
			this.request = request;
		}

		public boolean checkNotModified(String s) {
			return false;
		}

		public boolean checkNotModified(long lastModifiedTimestamp) {
			return false;
		}

		public String getContextPath() {
			return this.request.getResourceRef().getPath();
		}

		public String getDescription(boolean includeClientInfo) {
			return null;
		}

		public Locale getLocale() {
			return LocaleContextHolder.getLocale();
		}

		public String getParameter(String paramName) {
			return this.form.getFirstValue(paramName);
		}

		public Map<String, String[]> getParameterMap() {
			final Map<String, String[]> conversion = new HashMap<String, String[]>();

			for (final Map.Entry<String, String> entry : this.form.getValuesMap().entrySet()) {
				conversion.put(entry.getKey(), new String[] { entry.getValue() });
			}

			return conversion;
		}

		public String[] getParameterValues(String paramName) {
			return this.form.getValuesArray(paramName);
		}

		public String getRemoteUser() {
			return null;
		}

		public Principal getUserPrincipal() {
			return null;
		}

		public boolean isSecure() {
			return this.request.isConfidential();
		}

		public boolean isUserInRole(String role) {
			return false;
		}

		public Object getAttribute(String name, int scope) {
			return null;
		}

		public String[] getAttributeNames(int scope) {
			return null;
		}

		public String getSessionId() {
			return null;
		}

		public Object getSessionMutex() {
			return null;
		}

		public void registerDestructionCallback(String name, Runnable callback, int scope) {
			// nothing to do
		}

		public void removeAttribute(String name, int scope) {
			// nothing to do
		}

		public void setAttribute(String name, Object value, int scope) {
			// nothing to do
		}

		public String getHeader(final String s) {
			return null;
		}

		public String[] getHeaderValues(String s) {
			return new String[0];
		}

		public Iterator<String> getHeaderNames() {
			return null;
		}

		public Iterator<String> getParameterNames() {
			return null;
		}

		public Object resolveReference(String s) {
			return null;
		}
	}
}
