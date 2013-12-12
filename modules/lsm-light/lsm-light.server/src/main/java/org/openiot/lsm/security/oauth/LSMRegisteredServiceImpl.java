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

package org.openiot.lsm.security.oauth;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.services.RegisteredService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class LSMRegisteredServiceImpl implements RegisteredService, Comparable<RegisteredService>,java.io.Serializable {
	private static final long serialVersionUID = -5172882792800294004L;

	private long id = -1;

	private List<String> allowedAttributes = new ArrayList<String>();

	private String description;

	protected String serviceId;

	private String name;

	private String theme;

	private boolean allowedToProxy = true;

	private boolean enabled = true;

	private boolean ssoEnabled = true;

	private boolean anonymousAccess = false;

	private boolean ignoreAttributes = false;

	private int evaluationOrder;

	private String usernameAttribute = null;
	

	public boolean isAnonymousAccess() {
		return this.anonymousAccess;
	}

	public void setAnonymousAccess(final boolean anonymousAccess) {
		this.anonymousAccess = anonymousAccess;
	}

	public List<String> getAllowedAttributes() {
		return this.allowedAttributes;
	}

	public long getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

	public String getServiceId() {
		return this.serviceId;
	}

	public String getName() {
		return this.name;
	}

	public String getTheme() {
		return this.theme;
	}

	public boolean isAllowedToProxy() {
		return this.allowedToProxy;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isSsoEnabled() {
		return this.ssoEnabled;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (!(o instanceof LSMRegisteredServiceImpl)) {
			return false;
		}

		final LSMRegisteredServiceImpl that = (LSMRegisteredServiceImpl) o;

		return new EqualsBuilder().append(this.allowedToProxy, that.allowedToProxy).append(this.anonymousAccess, that.anonymousAccess)
				.append(this.enabled, that.enabled).append(this.evaluationOrder, that.evaluationOrder).append(this.ignoreAttributes, that.ignoreAttributes)
				.append(this.ssoEnabled, that.ssoEnabled).append(this.allowedAttributes, that.allowedAttributes).append(this.description, that.description)
				.append(this.name, that.name).append(this.serviceId, that.serviceId).append(this.theme, that.theme)
				.append(this.usernameAttribute, that.usernameAttribute).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(7, 31).append(this.allowedAttributes).append(this.description).append(this.serviceId).append(this.name).append(this.theme)
				.append(this.enabled).append(this.ssoEnabled).append(this.anonymousAccess).append(this.ignoreAttributes).append(this.evaluationOrder)
				.append(this.usernameAttribute).toHashCode();
	}

	public void setAllowedAttributes(final List<String> allowedAttributes) {
		if (allowedAttributes == null) {
			this.allowedAttributes = new ArrayList<String>();
		} else {
			this.allowedAttributes = allowedAttributes;
		}
	}

	public void setAllowedToProxy(final boolean allowedToProxy) {
		this.allowedToProxy = allowedToProxy;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSsoEnabled(final boolean ssoEnabled) {
		this.ssoEnabled = ssoEnabled;
	}

	public void setTheme(final String theme) {
		this.theme = theme;
	}

	public boolean isIgnoreAttributes() {
		return this.ignoreAttributes;
	}

	public void setIgnoreAttributes(final boolean ignoreAttributes) {
		this.ignoreAttributes = ignoreAttributes;
	}

	public void setEvaluationOrder(final int evaluationOrder) {
		this.evaluationOrder = evaluationOrder;
	}

	public int getEvaluationOrder() {
		return this.evaluationOrder;
	}

	public String getUsernameAttribute() {
		return this.usernameAttribute;
	}

	/**
	 * Sets the name of the user attribute to use as the username when providing
	 * usernames to this registered service.
	 * 
	 * <p>
	 * Note: The username attribute will have no affect on services that are
	 * marked for anonymous access.
	 * 
	 * @param username
	 *            attribute to release for this service that may be one of the
	 *            following values:
	 *            <ul>
	 *            <li>name of the attribute this service prefers to consume as
	 *            username</li>. <li><code>null</code> to enforce default CAS
	 *            behavior</li>
	 *            </ul>
	 * @see #isAnonymousAccess()
	 */
	public void setUsernameAttribute(final String username) {
		if (StringUtils.isBlank(username)) {
			this.usernameAttribute = null;
		} else {
			this.usernameAttribute = username;
		}
	}

	public Object clone() throws CloneNotSupportedException {
		final LSMRegisteredServiceImpl clone = newInstance();
		clone.copyFrom(this);
		return clone;
	}

	/**
	 * Copies the properties of the source service into this instance.
	 * 
	 * @param source
	 *            Source service from which to copy properties.
	 */
	public void copyFrom(final RegisteredService source) {
		this.setId(source.getId());
		this.setAllowedAttributes(new ArrayList<String>(source.getAllowedAttributes()));
		this.setAllowedToProxy(source.isAllowedToProxy());
		this.setDescription(source.getDescription());
		this.setEnabled(source.isEnabled());
		this.setName(source.getName());
		this.setServiceId(source.getServiceId());
		this.setSsoEnabled(source.isSsoEnabled());
		this.setTheme(source.getTheme());
		this.setAnonymousAccess(source.isAnonymousAccess());
		this.setIgnoreAttributes(source.isIgnoreAttributes());
		this.setEvaluationOrder(source.getEvaluationOrder());
		this.setUsernameAttribute(source.getUsernameAttribute());
	}

	/**
	 * Compares this instance with the <code>other</code> registered service
	 * based on evaluation order, name. The name comparison is case insensitive.
	 * 
	 * @see #getEvaluationOrder()
	 */
	public int compareTo(final RegisteredService other) {
		return new CompareToBuilder().append(this.getEvaluationOrder(), other.getEvaluationOrder())
				.append(this.getName().toLowerCase(), other.getName().toLowerCase()).toComparison();
	}

	public String toString() {
		final ToStringBuilder toStringBuilder = new ToStringBuilder(null, ToStringStyle.SHORT_PREFIX_STYLE);
		toStringBuilder.append("id", this.id);
		toStringBuilder.append("name", this.name);
		toStringBuilder.append("description", this.description);
		toStringBuilder.append("serviceId", this.serviceId);
		toStringBuilder.append("usernameAttribute", this.usernameAttribute);
		toStringBuilder.append("attributes", this.allowedAttributes.toArray());

		return toStringBuilder.toString();
	}

	private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

	public void setServiceId(final String id) {
		this.serviceId = id;
	}

	public boolean matches(final Service service) {
		return service != null && PATH_MATCHER.match(serviceId.toLowerCase(), service.getId().toLowerCase());
	}

	protected LSMRegisteredServiceImpl newInstance() {
		return new LSMRegisteredServiceImpl();
	}

}
