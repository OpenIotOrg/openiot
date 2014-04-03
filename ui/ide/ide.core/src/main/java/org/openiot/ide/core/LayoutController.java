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
import org.openiot.commons.util.PropertyManagement;
import org.primefaces.model.menu.DefaultMenuModel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.HashMap;

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

	@PostConstruct
	public void init() {

		PropertyManagement props = new PropertyManagement();
		HashMap<String, String> navigationMap = props.getIdeNavigationSettings();

		menu = menuFactory.createMenu(navigationMap);

		navigation = "welcome.jsf";
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

	/**
	 * Checks if the component exists / is Loaded in order to display the menu
	 * link
	 *
	 * @return boolean
	 */
//	public boolean isLoaded(String url) {
//
//		Boolean b = true;
//
//		if (map.containsKey(url)) {
//			b = map.get(url);
//		} else {
//			try {
//				URL u = new URL(url);
//				HttpURLConnection huc = (HttpURLConnection) u.openConnection();
//				huc.setRequestMethod("HEAD");
//				huc.connect();
//				b = huc.getResponseCode() == HttpURLConnection.HTTP_OK;
//				map.put(url, b);
//			} catch (IOException e) {
//				// e.printStackTrace();
//			}
//		}
//
//		return b;
//	}
}
