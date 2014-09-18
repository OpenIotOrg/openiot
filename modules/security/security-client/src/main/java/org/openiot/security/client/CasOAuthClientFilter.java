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

package org.openiot.security.client;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.buji.pac4j.ClientFilter;

/**
 * @author Mehdi Riahi
 * 
 */
public class CasOAuthClientFilter extends ClientFilter {
	private static Logger log = LoggerFactory.getLogger(CasOAuthClientFilter.class);

	/**
	 * If login has failed, redirect user to the error page except if the user is already
	 * authenticated, in which case redirect to the default success url.
	 * 
	 * @param token
	 *            the token representing the current authentication
	 * @param ae
	 *            the current authentication exception
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the outgoing response
	 */
	@Override
	protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae, final ServletRequest request,
			final ServletResponse response) {
		// is user authenticated ?
		final Subject subject = getSubject(request, response);
		if (subject.isAuthenticated()) {
			try {
				issueSuccessRedirect(request, response);
			} catch (final Exception e) {
				log.error("Cannot redirect to the default success url", e);
			}
		} else {
			try {
				ae.printStackTrace();
				WebUtils.issueRedirect(request, response, getFailureUrl());
			} catch (final IOException e) {
				log.error("Cannot redirect to failure url : {}", getFailureUrl(), e);
			}
		}
		return false;
	}
}
