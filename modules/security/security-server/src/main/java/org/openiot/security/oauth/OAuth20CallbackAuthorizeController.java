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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.OAuthUtils;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class OAuth20CallbackAuthorizeController extends AbstractController {
    
    private final Logger log = LoggerFactory.getLogger(OAuth20CallbackAuthorizeController.class);
	private TicketRegistry ticketRegistry;
    
    public OAuth20CallbackAuthorizeController(TicketRegistry ticketRegistry) {
    	this.ticketRegistry = ticketRegistry;
	}

	@Override
    protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
        throws Exception {
        // get CAS ticket
        final String ticket = request.getParameter(OAuthConstants.TICKET);
        log.debug("ticket : {}", ticket);
        
        ServiceTicket serviceTicket = (ServiceTicket) ticketRegistry.getTicket(ticket);
        TicketGrantingTicket grantingTicket = serviceTicket.getGrantingTicket();
        log.debug("granting ticket : {}", grantingTicket);
        if(grantingTicket == null)
        	return null;
        
        
        // retrieve callback url from session
        final HttpSession session = request.getSession();
        String callbackUrl = (String) session.getAttribute(OAuthConstants.OAUTH20_CALLBACKURL);
        log.debug("callbackUrl : {}", callbackUrl);
        session.removeAttribute(OAuthConstants.OAUTH20_CALLBACKURL);
        // and state
        final String state = (String) session.getAttribute(OAuthConstants.OAUTH20_STATE);
        log.debug("state : {}", state);
        session.removeAttribute(OAuthConstants.OAUTH20_STATE);
        
        if(callbackUrl == null)
        	return null;
        
        // return callback url with code & state
        callbackUrl = OAuthUtils.addParameter(callbackUrl, OAuthConstants.CODE, ticket);
        if (state != null) {
            callbackUrl = OAuthUtils.addParameter(callbackUrl, OAuthConstants.STATE, state);
        }
        log.debug("callbackUrl : {}", callbackUrl);
        
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("callbackUrl", callbackUrl);
        // retrieve service name from session
        final String serviceName = (String) session.getAttribute(OAuthConstants.OAUTH20_SERVICE_NAME);
        log.debug("serviceName : {}", serviceName);
        model.put("serviceName", serviceName);
        
        model.put("userId", grantingTicket.getAuthentication().getPrincipal().getId());
        
        return new ModelAndView(OAuthConstants.CONFIRM_VIEW, model);
    }
}
