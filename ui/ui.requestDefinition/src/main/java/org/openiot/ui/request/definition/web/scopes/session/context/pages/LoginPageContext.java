/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
package org.openiot.ui.request.definition.web.scopes.session.context.pages;

import org.openiot.ui.request.definition.web.scopes.session.base.DisposableContext;

/**
 * 
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class LoginPageContext extends DisposableContext {

	// Login form
	private String userName;
	private String password;
	// Register form
	private String regUserName;
	private String regPassword;
	private String regEmail;

	public LoginPageContext() {
		super();
		this.register();
	}

	@Override
	public String getContextUID() {
		return "loginPageContext";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegUserName() {
		return regUserName;
	}

	public void setRegUserName(String regUserName) {
		this.regUserName = regUserName;
	}

	public String getRegPassword() {
		return regPassword;
	}

	public void setRegPassword(String regPassword) {
		this.regPassword = regPassword;
	}

	public String getRegEmail() {
		return regEmail;
	}

	public void setRegEmail(String regEmail) {
		this.regEmail = regEmail;
	}

}
