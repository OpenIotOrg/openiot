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

	public LayoutController() {
	}

	@PostConstruct
	public void init() {
		navigation = "welcome.jsf";
		map = new HashMap<String, Boolean>();
	}

	private String navigation;

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
				b = Boolean.valueOf(huc.getResponseCode() == HttpURLConnection.HTTP_OK);
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
