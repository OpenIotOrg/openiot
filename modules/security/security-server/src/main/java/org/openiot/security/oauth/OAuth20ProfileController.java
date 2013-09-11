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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.profile.CasWrapperProfile;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * This controller returns a profile for the authenticated user (identifier +
 * attributes), found with the access token (CAS granting ticket).
 * 
 * @author Jerome Leleu
 * @author Mehdi Riahi
 */
public final class OAuth20ProfileController extends AbstractController {

	private static Logger log = LoggerFactory.getLogger(OAuth20ProfileController.class);

	private final TicketRegistry ticketRegistry;

	public OAuth20ProfileController(final TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}

	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String accessToken = request.getParameter(OAuthConstants.ACCESS_TOKEN);
		log.debug("accessToken : {}", accessToken);

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

		// generate profile : identifier + attributes
		final Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField(CasWrapperProfile.ID, principal.getId());
		jsonGenerator.writeArrayFieldStart(CasWrapperProfile.ATTRIBUTES);
		final Map<String, Object> attributes = principal.getAttributes();
		for (final String key : attributes.keySet()) {
			jsonGenerator.writeStartObject();
			Object vals = attributes.get(key);
			if (vals instanceof Iterable<?>) {
				jsonGenerator.writeArrayFieldStart(key);
				for (final Object value : (Iterable<?>) vals)
					jsonGenerator.writeString(value.toString());
				jsonGenerator.writeEndArray();
			} else if ("role_name".equals(key)) {
				jsonGenerator.writeArrayFieldStart(key);
				if (!StringUtils.isBlank((String) vals))
					jsonGenerator.writeString(vals.toString());
				jsonGenerator.writeEndArray();
			} else
				jsonGenerator.writeObjectField(key, attributes.get(key));

			jsonGenerator.writeEndObject();
		}
		jsonGenerator.writeEndArray();
		jsonGenerator.writeEndObject();
		jsonGenerator.close();
		response.flushBuffer();
		return null;
	}

	static void setLogger(final Logger aLogger) {
		log = aLogger;
	}
}
