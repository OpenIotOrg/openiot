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

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * @author Chris Georgoulis e-mail: cgeo@ait.edu.gr
 * 
 */

@Named("layout")
@SessionScoped
public class LayoutController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6629499890720592580L;
	private HashMap<String, Boolean> map;
	private String navigation;

	public LayoutController() {
	}

	@PostConstruct
	public void init() {
		navigation = "welcome.jsf";
		map = new HashMap<String, Boolean>();
	}

	public String getNavigation() {
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}

	/**
	 * Checks if the component exists / is Loaded in order to display the menu
	 * link
	 * 
	 * @return boolean
	 */
	public boolean isLoaded(String url) {

		Boolean b = true;

		if (map.containsKey(url)) {
			b = map.get(url);
		} else {
			try {
				URL u = new URL(url);
				HttpURLConnection huc = (HttpURLConnection) u.openConnection();
				huc.setRequestMethod("HEAD");
				huc.connect();
				b = Boolean
						.valueOf(huc.getResponseCode() == HttpURLConnection.HTTP_OK);
				map.put(url, b);
			} catch (MalformedURLException e) {
				// e.printStackTrace();
			} catch (ProtocolException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		return b.booleanValue();
	}

}
