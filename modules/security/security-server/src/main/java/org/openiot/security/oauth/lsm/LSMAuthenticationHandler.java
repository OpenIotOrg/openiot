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

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.openiot.lsm.security.oauth.mgmt.User;

/**
 * 
 * @author Mehdi Riahi
 *
 */
public class LSMAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

	private LSMOAuthManager manager = LSMOAuthManager.getInstance();
	
	@Override
	protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
		final String username = getPrincipalNameTransformer().transform(credentials.getUsername());
		final String password = credentials.getPassword();
		final String encryptedPassword = this.getPasswordEncoder().encode(password);

		User user = manager.getUserByUsername(username);
		if(user == null)
			return false;
		final String dbPassword = user.getPassword();
		return dbPassword.equals(encryptedPassword);
	}

}
