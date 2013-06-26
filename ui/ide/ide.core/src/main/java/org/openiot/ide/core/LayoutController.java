package org.openiot.ide.core;

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


import java.io.Serializable;

/**
 * @author Chris Georgoulis e-mail: cgeo@ait.edu.gr
 * 
 */

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value="layout")
@SessionScoped
public class LayoutController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6629499890720592580L;
	

	public LayoutController() {
	}
	
	@PostConstruct
	private void init() {
		navigation = "welcome.jsf";
	}
	
	private String navigation;

	
	public String getNavigation() {
		
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}
	
	
	

}
