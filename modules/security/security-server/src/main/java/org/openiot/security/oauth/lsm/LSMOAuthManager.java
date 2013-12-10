package org.openiot.security.oauth.lsm;

import java.util.ArrayList;
import java.util.List;

import org.jasig.cas.services.RegisteredService;
import org.openiot.lsm.security.oauth.LSMOAuthHttpManager;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class LSMOAuthManager {
	static String OAuthGraphURL = "http://lsm.deri.ie/OpenIoT/OAuth#";
	private static LSMOAuthManager instance;
	private String lSMOauthGraphURL = OAuthGraphURL;

	LSMOAuthHttpManager lsmOAuthHttpManager = new LSMOAuthHttpManager(OAuthGraphURL);

	public static LSMOAuthManager getInstance() {
		if (instance == null)
			instance = new LSMOAuthManager();
		return instance;
	}

	private LSMOAuthManager() {
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
		String prefix = "http://lsm.deri.ie/resource/";
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
			String service = "http://lsm.deri.ie/sparql";
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
		String prefix = "http://lsm.deri.ie/resource/";
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
			String service = "http://lsm.deri.ie/sparql";
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
			String service = "http://lsm.deri.ie/sparql";
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
			String service = "http://lsm.deri.ie/sparql";
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
			String service = "http://lsm.deri.ie/sparql";
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
			String service = "http://lsm.deri.ie/sparql";
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
		String userURL = "http://lsm.deri.ie/resource/user/" + username;
		if (username.contains("http://lsm.deri.ie/resource/user/")) {
			userURL = username;
			username = username.substring(username.lastIndexOf("/") + 1);
		}
		String sparql = " select ?nick ?mbox ?pass ?role" + " from <" + lSMOauthGraphURL + "> \n" + "where{ " + "<" + userURL
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>." + "OPTIONAL{<" + userURL
				+ "> <http://xmlns.com/foaf/0.1/nick> ?nick.}" + "OPTIONAL{<" + userURL + "> <http://xmlns.com/foaf/0.1/mbox> ?mbox.}" + "<" + userURL
				+ "> <http://openiot.eu/ontology/ns/password> ?pass." + "<" + userURL + "> <http://openiot.eu/ontology/ns/role> ?role." + "}";
		try {
			String service = "http://lsm.deri.ie/sparql";
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			user = new org.openiot.lsm.security.oauth.mgmt.User();
			user.setUsername(username);
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				user.setEmail(soln.get("?mbox").toString());
				user.setPassword(soln.get("?pass").toString());
				user.setName(soln.get("?nick").toString());
				List<Role> roles = user.getRoles();
				if (roles == null) {
					roles = new ArrayList<Role>();
					user.setRoles(roles);
				}
				Role role = getRole(soln.get("?role").toString());
				if (!roles.contains(role)) {
					roles.add(role);
				}
			}
			vqe.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;
	}

	/**
	 * Retrieves all LSMRegisteredServiceImpls
	 * 
	 * @return
	 */
	public List<RegisteredService> getAllRegisteredServices() {
		List<RegisteredService> serviceList = null;
		String sparql = " select ?service" + " from <" + lSMOauthGraphURL + "> \n" + "where{ "
				+ "?service <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>." + "}";
		try {
			String service = "http://lsm.deri.ie/sparql";
			QueryExecution vqe = new QueryEngineHTTP(service, sparql);
			ResultSet results = vqe.execSelect();
			serviceList = new ArrayList<RegisteredService>();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String serviceURL = soln.get("?service").toString();
				String serviceId = serviceURL.substring(serviceURL.lastIndexOf("/") + 1);
				LSMRegisteredServiceImpl t = getRegisteredService(Long.parseLong(serviceId));
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
