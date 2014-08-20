package org.openiot.ide.core;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.menu.DefaultMenuModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.Serializable;

/**
 * @author Chris Georgoulis e-mail: cgeo@ait.edu.gr
 */

@Named("layout")
@ViewScoped
public class LayoutController implements Serializable {

	private String navigation;

	@Inject
	private MenuFactory menuFactory;

	private DefaultMenuModel menu;

	private final String DEFAULT_NAVIGATION = "welcome.jsf";

	@PostConstruct
	public void init() {

		menu = menuFactory.createMainMenu();
		navigation = DEFAULT_NAVIGATION;
	}

	public String getNavigation() {
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}

	public DefaultMenuModel getMenu() {
		return menu;
	}
	
	public String getLogoutScript() {
		return menuFactory.generateLogoutScript("logoutIframe", DEFAULT_NAVIGATION);
	}

}
