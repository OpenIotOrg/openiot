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

package org.openiot.security.oauth.lsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractDefaultAttributePersonAttributeDao;
import org.jasig.services.persondir.support.CaseInsensitiveNamedPersonImpl;
import org.jasig.services.persondir.support.IUsernameAttributeProvider;
import org.springframework.beans.factory.annotation.Required;

public class LSMNamedParameterPersonAttributeDao extends AbstractDefaultAttributePersonAttributeDao {

	private IUsernameAttributeProvider usernameAttributeProvider;
	private Set<String> availableQueryAttributes = null; // default
	private Set<String> userAttributeNames = null; // default

	public LSMNamedParameterPersonAttributeDao() {
		userAttributeNames = new HashSet<String>();
		// SELECT role_name FROM USERS_ROLES WHERE username=:username
		userAttributeNames.add("role_name");
	}

	public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
		String username = usernameAttributeProvider.getUsernameFromQuery(query);
		final ArrayList<Object> roleNames = new ArrayList<Object>();
		
		/********************************
		 * To be retrieved from LSM     *
		 ********************************/
		//roleNames should be populated from LSM. The SQL query was "SELECT role_name FROM USERS_ROLES WHERE username=:username"

		Map<String, List<Object>> mapOfLists = new HashMap<String, List<Object>>();
		mapOfLists.put(userAttributeNames.iterator().next(), roleNames);
		IPersonAttributes person = new CaseInsensitiveNamedPersonImpl(username, mapOfLists);
		return Collections.singleton(person);
	}

	public Set<String> getPossibleUserAttributeNames() {
		return userAttributeNames;
	}

	public Set<String> getAvailableQueryAttributes() {
		return availableQueryAttributes;
	}

	@Required
	public void setUsernameAttributeProvider(IUsernameAttributeProvider usernameAttributeProvider) {
		this.usernameAttributeProvider = usernameAttributeProvider;
	}

	public void setAvailableQueryAttributes(Set<String> availableQueryAttributes) {
		this.availableQueryAttributes = Collections.unmodifiableSet(availableQueryAttributes);
	}

	@Required
	public void setUserAttributeNames(Set<String> userAttributeNames) {
		this.userAttributeNames = Collections.unmodifiableSet(userAttributeNames);
	}

}
