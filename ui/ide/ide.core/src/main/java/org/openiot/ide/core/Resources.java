package org.openiot.ide.core;

import org.omnifaces.cdi.ViewScoped;
import org.openiot.commons.util.PropertyManagement;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Chris Georgoulis on 3/4/2014.
 */
@ViewScoped
public class Resources implements Serializable {

	public HashMap<String, String> getNavigationMap() {
		PropertyManagement props = new PropertyManagement();
		return props.getIdeNavigationSettings();
	}
}
