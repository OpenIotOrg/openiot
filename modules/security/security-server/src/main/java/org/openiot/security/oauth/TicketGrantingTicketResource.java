/* Copyright (c) 2011-2014, OpenIoT
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

import org.jasig.cas.CentralAuthenticationService;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of a Restlet resource for deleting a TicketGrantingTicket.
 * 
 * @author Mehdi Riahi
 */
public final class TicketGrantingTicketResource extends Resource {

	private final static Logger log = LoggerFactory.getLogger(TicketGrantingTicketResource.class);

	@Autowired
	private CentralAuthenticationService centralAuthenticationService;

	private String ticketGrantingTicketId;

	public void init(final Context context, final Request request, final Response response) {
		super.init(context, request, response);
		this.ticketGrantingTicketId = (String) request.getAttributes().get("ticketGrantingTicketId");
		this.getVariants().add(new Variant(MediaType.APPLICATION_WWW_FORM));
	}

	public boolean allowDelete() {
		return true;
	}

	public void removeRepresentations() throws ResourceException {
		log.debug("Removing TGT: {}", ticketGrantingTicketId);
		this.centralAuthenticationService.destroyTicketGrantingTicket(this.ticketGrantingTicketId);
		getResponse().setStatus(Status.SUCCESS_OK);
	}
}
