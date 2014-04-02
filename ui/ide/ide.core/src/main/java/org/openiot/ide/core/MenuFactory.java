package org.openiot.ide.core;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultSubMenu;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;


/**
 * Created by Chris Georgoulis on 2/4/2014.
 */
@RequestScoped
public class MenuFactory implements Serializable {

	public DefaultMenuItem createMenuItem(DefaultSubMenu parent, String value, String url) {
		DefaultMenuItem item = new DefaultMenuItem();
		item.setUrl(url);
		item.setValue(value);
		item.setIcon("ui-icon-minus");
		parent.addElement(item);
		return item;
	}
}
