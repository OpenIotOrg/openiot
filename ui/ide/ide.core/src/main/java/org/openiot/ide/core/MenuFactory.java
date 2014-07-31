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

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author Chris Georgoulis e-mail: cgeo@ait.edu.gr
 */
@RequestScoped
public class MenuFactory implements Serializable {

	private static final String IDE_CORE_GROUP = "ide.core.navigation.";
	private static final String IDE_CORE_RELATIVE_URL = "ide.core";

	private static final String FIELD_TITLE = "title";
	private static final String FIELD_MONITORING = "monitoring";
	private static final String FIELD_URL = "url";

	private static final String IDE_CORE_MONITORING_URL = Faces.getRequestDomainURL()
			+ "/" + IDE_CORE_RELATIVE_URL + "/" + FIELD_MONITORING;

	private static final String SUBMENU_MAIN = "Main Menu";
	private static final String SUBMENU_MONITORS = "Monitors";

	private static final String MENU_ICON = "ui-icon-triangle-1-e";
	private static final String CENTER_PANEL = ":centerPanel";

	@Inject
	private Resources resources;

	private HashMap<String, HashMap<String, String>> propertyMap;

	@PostConstruct
	private void init() {
		propertyMap = createPropertyMap();
	}


	/**
	 * Creates the main menu according to the Injected Navigation map from Resources
	 */
	public DefaultMenuModel createMainMenu() {

		DefaultMenuModel menu = new DefaultMenuModel();

		//Main SubMenu
		DefaultSubMenu mainMenu = new DefaultSubMenu();
		mainMenu.setLabel(SUBMENU_MAIN);
		menu.addElement(mainMenu);

		//Monitor Submenu
		DefaultSubMenu monitorMenu = new DefaultSubMenu();
		monitorMenu.setLabel(SUBMENU_MONITORS);
		menu.addElement(monitorMenu);

		validateUrls();

		//build submenus
		for (HashMap<String, String> map : propertyMap.values()) {
			//build main menu item
			try {

				DefaultMenuItem item = createMenuItem(
						map.get(FIELD_TITLE), map.get(FIELD_URL));
				mainMenu.addElement(item);

				//build monitor item
				if (map.get(FIELD_MONITORING).equals("true")) {
					String monitoringUrl = createMonitorURL(map.get(FIELD_URL));
					DefaultMenuItem monitorItem =
							createMenuItem(map.get(FIELD_TITLE), monitoringUrl);
					monitorMenu.addElement(monitorItem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		//add Ide Core Monitor
		DefaultMenuItem ideCoreMonitor = createMenuItem("IDE", IDE_CORE_MONITORING_URL);
		monitorMenu.addElement(ideCoreMonitor);

		return menu;
	}


	/**
	 * Creates a single menu item and sets it's ActionListener to update the current
	 * navigation Url
	 *
	 * @param value
	 * @param url
	 * @return
	 */
	private DefaultMenuItem createMenuItem(String value, String url) {

		DefaultMenuItem item = new DefaultMenuItem();
		item.setValue(value);
		item.setIcon(MENU_ICON);
		item.setAjax(true);
		item.setParam("navigation", url);
		String command = String.format("#{layout.setNavigation('%s')}", url);
		item.setCommand(command);
		item.setUpdate(CENTER_PANEL);

		return item;
	}


	/**
	 * Groups the navigation properties in a Linked Hashmap where
	 * Key = the navigation group name e.g. Request Presentation
	 * Values = a Hashmap with the group fields e.g. Url, Title, Active Monitoring
	 *
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> createPropertyMap() {
		HashMap<String, HashMap<String, String>> itemMap = null;


		HashMap<String, String> navigationMap = resources.getNavigationMap();
		MultiValueMap groupMap = new MultiValueMap();

		for (String key : navigationMap.keySet()) {

			String newKey = key.split(IDE_CORE_GROUP)[1];

			Scanner sc = new Scanner(newKey).useDelimiter("\\.");
			String group = sc.next();
			groupMap.put(group, sc.next());

			sc.close();
		}

		itemMap = new LinkedHashMap<>();

		for (Object parentKey : groupMap.keySet()) {

			HashMap<String, String> childMap = new HashMap<>();

			for (Object childKey : groupMap.getCollection(parentKey)) {
				String fullKey = IDE_CORE_GROUP + parentKey + "." + childKey;
				childMap.put((String) childKey, navigationMap.get(fullKey));
			}

			itemMap.put((String) parentKey, childMap);
		}

		return itemMap;
	}

	/**
	 * checks if the base Url ends with a '/' character and appends accordingly
	 *
	 * @param baseUrl
	 * @return
	 */
	private String createMonitorURL(String baseUrl) {
//		if (!StringUtils.endsWith(baseUrl, "/")) {
//			baseUrl += "/";
//		}

//		int indexDomainUrl = StringUtils.indexOf(baseUrl, );
		String suffix = StringUtils.substring(baseUrl, Faces.getRequestDomainURL().length(), baseUrl.length() - 1);
		String moduleName = StringUtils.split(suffix, "/")[0];

		return Faces.getRequestDomainURL() + "/" + moduleName + "/" + FIELD_MONITORING;
	}

	/**
	 * Creates
	 */
	private void validateUrls() {

		//TODO Remove comments for old way
		//Spawn a new Thread for each url Validation
//		List<Thread> threads = new ArrayList<>(propertyMap.size());

		ExecutorService es = Executors.newFixedThreadPool(propertyMap.entrySet().size());


		for (Map.Entry<String, HashMap<String, String>> entry : propertyMap.entrySet()) {
			es.execute(new ValidatorRunnable(entry));
//			threads.add(new Thread(new ValidatorRunnable(entry)));
		}

		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//alternative way to do the above with threads
//		for (Thread t : threads) {
//			t.start();
//		}
//
//		for (Thread t : threads) {
//			try {
//				t.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

	}

	/**
	 * This private class validates the URLs in the property map and accordingly
	 * removes the invalid ones
	 */
	private class ValidatorRunnable implements Runnable {

		Map.Entry<String, HashMap<String, String>> entry;

		private ValidatorRunnable(Map.Entry<String, HashMap<String, String>> entry) {
			ValidatorRunnable.this.entry = entry;
		}

		@Override
		public void run() {
			String url = entry.getValue().get(FIELD_URL);
			boolean isUrlValid = isValid(url);

			if (!isUrlValid) {
				propertyMap.remove(entry.getKey());
			}
		}

		public boolean isValid(String url) {

			boolean isValid = true;

			try {
				URL u = new URL(url);
				HttpURLConnection huc = (HttpURLConnection) u.openConnection();
				huc.setRequestMethod("HEAD");
				huc.connect();
				isValid = huc.getResponseCode() == HttpURLConnection.HTTP_OK;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return isValid;
		}
	}


}
