package org.openiot.ide.core;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Created by Chris Georgoulis on 2/4/2014.
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

		//get property map

		HashMap<String, HashMap<String, String>> propertyMap = createPropertyMap();

		//build submenus
		try {
			for (HashMap<String, String> map : propertyMap.values()) {

				//build main menu item
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//add Ide Core Monitor
		DefaultMenuItem ideCoreMonitor = createMenuItem("IDE", IDE_CORE_MONITORING_URL);
		monitorMenu.addElement(ideCoreMonitor);

		return menu;
	}


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

		itemMap = new HashMap<>();

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

	private String createMonitorURL(String baseUrl) {
		if (!StringUtils.endsWith(baseUrl, "/")) {
			baseUrl += "/";
		}
		return baseUrl + FIELD_MONITORING;
	}
}
