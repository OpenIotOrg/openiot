package org.openiot.ide.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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


	private DefaultMenuModel createMenu(HashMap<String, String> navigationMap) {
		DefaultMenuModel menu = new DefaultMenuModel();
		DefaultSubMenu mainMenu = new DefaultSubMenu();
		menu.addElement(mainMenu);


		//TODO
		Multimap<String, HashMap<String, String>> propertyMap = createPropertyMap(
				navigationMap);

		for (HashMap<String, String> map : propertyMap.values()) {
			createMenuItem(mainMenu, map.get(FIELD_TITLE), map.get(FIELD_URL));
		}

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


	private Multimap<String, HashMap<String, String>> createPropertyMap(
			HashMap<String, String> navigationMap) {

		Multimap<String, String> groupMap = ArrayListMultimap.create();

		for (String key : navigationMap.keySet()) {
			String newKey = key.split(IDE_CORE_GROUP)[1];

			Scanner sc = new Scanner(newKey);
			String group = sc.next();
			groupMap.put(group, sc.nextLine());
			sc.close();
		}

		Multimap<String, HashMap<String, String>> itemMap = ArrayListMultimap.create();

		for (String parentKey : groupMap.keySet()) {
			HashMap<String, String> childMap = new HashMap<>();
			for (String childKey : groupMap.get(parentKey)) {
				String fullKey = IDE_CORE_GROUP + "." + parentKey + "." + childKey;
				childMap.put(childKey, navigationMap.get(fullKey));
			}
			itemMap.put(parentKey, childMap);
		}

		return itemMap;
	}
}
