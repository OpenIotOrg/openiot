/**
 * Copyright © 2011-2014, OpenIoT
 * 
 * This file contains the source code of the “Jasig CAS” library by 
 * “Jasig”, licensed under the terms of the “Apache License,Version 2.0”
 * and modified for the needs of the OpenIoT project.
 * OpenIoT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License version 2.1 as published by
 * the Free Software Foundation (the "LGPL").
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library in the file COPYING-LGPL-2.1; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied. See the GNU Lesser General Public License
 * for the specific language governing rights and limitations.
 */

package org.openiot.security.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.support.oauth.OAuthConstants;
import org.jasig.cas.support.oauth.OAuthUtils;
import org.jasig.cas.support.oauth.web.BaseOAuthWrapperController;
import org.jasig.cas.support.oauth.web.OAuth20AccessTokenController;
import org.jasig.cas.support.oauth.web.OAuth20AuthorizeController;
import org.jasig.cas.support.oauth.web.OAuth20CallbackAuthorizeController;
import org.openiot.security.oauth.lsm.LSMOAuth20PermissionController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * This controller is the main entry point for OAuth version 2.0 wrapping in
 * CAS, should be mapped to something like /oauth2.0/*. Dispatch request to
 * specific controllers : authorize, accessToken...
 * 
 * @author Jerome Leleu
 * @author Mehdi Riahi
 */
public final class OAuth20WrapperController extends BaseOAuthWrapperController implements InitializingBean {

	private static final String PERMISSIONS_URL = "permissions";

	private AbstractController authorizeController;

	private AbstractController callbackAuthorizeController;

	private AbstractController accessTokenController;

	private AbstractController profileController;

	private AbstractController permissionsController;

	public void afterPropertiesSet() throws Exception {
		authorizeController = new OAuth20AuthorizeController(servicesManager, loginUrl);
		callbackAuthorizeController = new OAuth20CallbackAuthorizeController();
		accessTokenController = new OAuth20AccessTokenController(servicesManager, ticketRegistry, timeout);
		profileController = new OAuth20ProfileController(ticketRegistry);
		permissionsController = new LSMOAuth20PermissionController(servicesManager, ticketRegistry);
	}

	@Override
	protected ModelAndView internalHandleRequest(final String method, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		// authorize
		if (OAuthConstants.AUTHORIZE_URL.equals(method)) {

			return authorizeController.handleRequest(request, response);
		}

		// callback on authorize
		else if (OAuthConstants.CALLBACK_AUTHORIZE_URL.equals(method)) {

			return callbackAuthorizeController.handleRequest(request, response);
		}

		// get access token
		else if (OAuthConstants.ACCESS_TOKEN_URL.equals(method)) {

			return accessTokenController.handleRequest(request, response);
		}

		// get profile
		else if (OAuthConstants.PROFILE_URL.equals(method)) {

			return profileController.handleRequest(request, response);
		}

		// get permissions
		else if (PERMISSIONS_URL.equals(method)) {

			return permissionsController.handleRequest(request, response);
		}

		// else error
		log.error("Unknown method : {}", method);
		OAuthUtils.writeTextError(response, OAuthConstants.INVALID_REQUEST, 200);
		return null;
	}

}
