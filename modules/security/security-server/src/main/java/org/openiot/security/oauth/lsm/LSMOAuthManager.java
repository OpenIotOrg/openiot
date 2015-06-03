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
import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.security.oauth.LSMOAuthHttpManager;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class LSMOAuthManager {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LSMOAuthManager.class);
	private static LSMOAuthManager instance;
	private String lSMOauthGraphURL;
	private String sparqlEndPoint;
	private String instancePrefix;
	
	private LSMOAuthHttpManager lsmOAuthHttpManager;

	public static LSMOAuthManager getInstance() {
		if (instance == null)
			instance = new LSMOAuthManager();
		return instance;
	}

	private LSMOAuthManager() {
		PropertyManagement propertyManagement = new PropertyManagement();
		sparqlEndPoint = propertyManagement.getSecurityLsmSparqlEndPoint();
		lSMOauthGraphURL = propertyManagement.getSecurityLsmGraphURL();
		instancePrefix = propertyManagement.getOpeniotResourceNamespace();
		lsmOAuthHttpManager = new LSMOAuthHttpManager(lSMOauthGraphURL);
	}

	public String getLSMOauthGraphURL() {
		return lSMOauthGraphURL;
	}

	public void setLSMOauthGraphURL(String lSMOauthGraphURL) {
		this.lSMOauthGraphURL = lSMOauthGraphURL;
		lsmOAuthHttpManager.setLSMOauthGraphURL(lSMOauthGraphURL);
	}

	public Permission getPermission(String perId) {
		return lsmOAuthHttpManager.getPermission(perId);
	}

	public void deletePermission(String perId) {
		lsmOAuthHttpManager.deletePermission(perId);
	}

	public void addPermission(Permission permission) {
		lsmOAuthHttpManager.addPermission(permission);

	}

	public Role getRole(String roleId) {
		return lsmOAuthHttpManager.getRole(roleId);
	}

	public void deleteRole(String roleId) {
		lsmOAuthHttpManager.deleteRole(roleId);
	}

	public void addRole(Role role) {
		lsmOAuthHttpManager.addRole(role);
	}

	public User getUser(String userId) {
		return lsmOAuthHttpManager.getUser(userId);
	}

	public void deleteUser(String userId) {
		lsmOAuthHttpManager.deleteUser(userId);
	}

	public void addUser(User user) {
		lsmOAuthHttpManager.addUser(user);
	}

	public LSMServiceTicketImpl getServiceTicketImpl(String ticketId) {
		return lsmOAuthHttpManager.getServiceTicketImpl(ticketId);
	}

	public void deleteServiceTicketImpl(String ticketId) {
		lsmOAuthHttpManager.deleteServiceTicketImpl(ticketId);
	}

	public void addServiceTicketImpl(LSMServiceTicketImpl serviceTicketImpl) {
		lsmOAuthHttpManager.addServiceTicketImpl(serviceTicketImpl);
	}

	public LSMTicketGrantingTicketImpl getTicketGranting(String grantId) {
		return lsmOAuthHttpManager.getTicketGranting(grantId);
	}

	public void deleteTicketGranting(String grantId) {
		lsmOAuthHttpManager.deleteTicketGranting(grantId);
	}

	public void addTicketGrangtingTicket(LSMTicketGrantingTicketImpl ticketGranting) {
		lsmOAuthHttpManager.addTicketGrangtingTicket(ticketGranting);
	}

	public LSMRegisteredServiceImpl getRegisteredService(long serviceId) {
		final LSMRegisteredServiceImpl registeredService = lsmOAuthHttpManager.getRegisteredService(serviceId);
		return registeredService;
	}

	public void deleteRegisteredService(long serviceId) {
		lsmOAuthHttpManager.deleteRegisteredService(serviceId);
	}

	public void addRegisteredService(LSMRegisteredServiceImpl reg_service) {
		lsmOAuthHttpManager.addRegisteredService(reg_service);
	}

	/**
	 * Returns the list of all LSMTicketGrantingTicketImpl having grantId as ticketGrantingTicket
	 * 
	 * @param grantId
	 * @return
	 */
	public List<LSMTicketGrantingTicketImpl> getAllTicketsOfTicketGrantingTicket(String grantId) {
		String prefix = instancePrefix;
		String grantURL = prefix + grantId;
		if (grantId.contains(prefix)) {
			grantURL = grantId;
			grantId = grantId.substring(grantId.lastIndexOf("/") + 1);
		}
		List<LSMTicketGrantingTicketImpl> ticketList = null;
		String sparql = "select ?tic_grant" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?tic_grant <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>."
				+ "?tic_grant <http://openiot.eu/ontology/ns/grants> " + "<" + grantURL + ">. \n" + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			ticketList = new ArrayList<LSMTicketGrantingTicketImpl>();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				LSMTicketGrantingTicketImpl t = getTicketGranting(soln.get("?tic_grant").toString());
				ticketList.add(t);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ticketList;
	}

	/**
	 * Returns the list of all LSMServiceTicketImpl having grantId as ticketGrantingTicket
	 * 
	 * @param grantId
	 * @return
	 */
	public List<LSMServiceTicketImpl> getAllServiceTicketsOfTicketGrantingTicket(String grantId) {
		String prefix = instancePrefix;
		String grantURL = prefix + grantId;
		if (grantId.contains(prefix)) {
			grantURL = grantId;
			grantId = grantId.substring(grantId.lastIndexOf("/") + 1);
		}
		List<LSMServiceTicketImpl> ticketList = null;
		String sparql = "select ?ticket" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?ticket <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>."
				+ "?ticket <http://openiot.eu/ontology/ns/grantedBy> " + "<" + grantURL + ">. \n" + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			ticketList = new ArrayList<LSMServiceTicketImpl>();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				LSMServiceTicketImpl t = getServiceTicketImpl(soln.get("?ticket").toString());
				ticketList.add(t);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ticketList;
	}

	/**
	 * Returns the list of all LSMTicketGrantingTicketImpls
	 * 
	 * @return
	 */
	public List<LSMTicketGrantingTicketImpl> getAllTicketGrantingTickets() {
		List<LSMTicketGrantingTicketImpl> grantList = null;
		String sparql = " select ?tic_grant" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?tic_grant <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>." + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			grantList = new ArrayList<LSMTicketGrantingTicketImpl>();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				LSMTicketGrantingTicketImpl t = getTicketGranting(soln.get("?tic_grant").toString());
				grantList.add(t);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return grantList;
	}

	/**
	 * Returns the list of all LSMServiceTicketImpls
	 * 
	 * @return
	 */
	public List<LSMServiceTicketImpl> getAllServiceTickets() {
		List<LSMServiceTicketImpl> ticketList = null;
		String sparql = " select ?ticket" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?ticket <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>." + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			ticketList = new ArrayList<LSMServiceTicketImpl>();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				LSMServiceTicketImpl t = getServiceTicketImpl(soln.get("?ticket").toString());
				ticketList.add(t);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ticketList;
	}

	/**
	 * Returns the the number of available LSMTicketGrantingTicketImpls
	 * 
	 * @return
	 */
	public int getTicketGrantingTicketsCount() {
		int count = -1;
		String sparql = " select (count(?tic_grant) as ?count)" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?tic_grant <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>." + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();

			if (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				count = soln.get("?count").asLiteral().getInt();
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Returns the the number of available LSMServiceTicketImpls
	 * 
	 * @return
	 */
	public int getServiceTicketsCount() {
		int count = -1;
		String sparql = " select (count(?ticket) as ?count)" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?ticket <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>." + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();

			if (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				count = soln.get("?count").asLiteral().getInt();
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Retrievs a user by the username
	 * 
	 * @param username
	 * @return
	 */
	public User getUserByUsername(String username) {
		org.openiot.lsm.security.oauth.mgmt.User user = null;
		String userURL = instancePrefix + "user/" + username;
		if (username.contains(instancePrefix + "user/")) {
			userURL = username;
			username = username.substring(username.lastIndexOf("/") + 1);
		}
		String sparql = " select ?nick ?mbox ?pass ?role" + " from <" + lSMOauthGraphURL + "> \n" + "where{ " + "<" + userURL
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>." + "OPTIONAL{<" + userURL
				+ "> <http://xmlns.com/foaf/0.1/nick> ?nick.}" + "OPTIONAL{<" + userURL + "> <http://xmlns.com/foaf/0.1/mbox> ?mbox.}" + "<" + userURL
				+ "> <http://openiot.eu/ontology/ns/password> ?pass.}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			if (results.hasNext()) {
				user = new org.openiot.lsm.security.oauth.mgmt.User();
				user.setUsername(username);
				QuerySolution soln = results.nextSolution();
				user.setEmail(soln.get("?mbox").toString());
				user.setPassword(soln.get("?pass").toString());
				user.setName(soln.get("?nick").toString());
				List<Role> roles = getUserRoles(username);
				if (roles != null)
					user.setRoles(roles);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}

	/**
	 * Retrievs roles of a user
	 * 
	 * @param username
	 * @return
	 */
	public List<Role> getUserRoles(String username) {
		List<Role> roles = new ArrayList<Role>();
		String userURL = instancePrefix + "user/" + username;
		if (username.contains(instancePrefix + "user/")) {
			userURL = username;
			username = username.substring(username.lastIndexOf("/") + 1);
		}
		String sparql = " select ?roleId " + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?roleId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientRole>." + "<" + userURL
				+ "> <http://openiot.eu/ontology/ns/role> ?roleId." + "}";
		try {
			String service = sparqlEndPoint;
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Role role = getRole(soln.get("?roleId").toString());
				roles.add(role);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return roles;
	}

	/**
	 * Retrieves all LSMRegisteredServiceImpls
	 * 
	 * @return
	 */
	public List<RegisteredService> getAllRegisteredServices() {

		List<RegisteredService> serviceList = new ArrayList<RegisteredService>();
		String sparql = " select ?service" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?service <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>." + "}";
		try {
			String service = sparqlEndPoint;
			LOGGER.info("Using endpoint: {}", service);
			LOGGER.debug("Running query: {}", sparql);
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String serviceURL = soln.get("?service").toString();
				String serviceId = serviceURL.substring(serviceURL.lastIndexOf("/") + 1);
				LSMRegisteredServiceImpl t = getRegisteredService(Long.parseLong(serviceId));
				if(t != null)
					serviceList.add(t);
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return serviceList;
	}

}
