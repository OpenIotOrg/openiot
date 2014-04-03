package org.openiot.ide.core;

import org.apache.commons.collections.map.MultiValueMap;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Created by Chris Georgoulis on 2/4/2014.
 */
@RequestScoped
public class MenuFactory implements Serializable {

	private static final String IDE_CORE_GROUP = "ide.core.navigation.";

	private static final String FIELD_TITLE = "title";
	private static final String FIELD_MONITOR = "monitor";
	private static final String FIELD_URL = "url";


	public DefaultMenuModel createMenu(HashMap<String, String> navigationMap) {
		DefaultMenuModel menu = new DefaultMenuModel();
		DefaultSubMenu mainMenu = new DefaultSubMenu();
		menu.addElement(mainMenu);


		HashMap<String, HashMap<String, String>> propertyMap =
				createPropertyMap(navigationMap);

		for (HashMap<String, String> map : propertyMap.values()) {
			createMenuItem(mainMenu, map.get(FIELD_TITLE), map.get(FIELD_URL));
		}

		DefaultSubMenu monitorMenu = new DefaultSubMenu();
		menu.addElement(monitorMenu);

		return menu;
	}


	private DefaultMenuItem createMenuItem(DefaultSubMenu parent, String value,
			String url) {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setUrl(url);
		item.setValue(value);
		item.setIcon("ui-icon-minus");
		parent.addElement(item);
		return item;
	}


	private HashMap<String, HashMap<String, String>> createPropertyMap(
			HashMap<String, String> navigationMap) {
		HashMap<String, HashMap<String, String>> itemMap = null;


		try {
//		HashMap<String, List<String>> groupMap = new HashMap<>();
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemMap;
	}
}
