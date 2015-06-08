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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.commons.util.Tuple2;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;

import static org.openiot.lsm.utils.OAuthUtil.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class LSMOAuthHttpManager {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LSMOAuthHttpManager.class);
	String LSMOauthURL;
	private String lsmOauthGraphURL;

	public LSMOAuthHttpManager(String OAuthGraphURL, String serverAddress) {
		init(OAuthGraphURL, serverAddress);
	}

	public LSMOAuthHttpManager(String oauthGraphURL) {
		PropertyManagement props = new PropertyManagement();
		String server = props.getLSMClientConnectionServerHost();
		init(oauthGraphURL, server);
	}

	private void init(String oauthGraphURL, String serverAddress) {
		this.lsmOauthGraphURL = oauthGraphURL;
		LSMOauthURL = serverAddress + "oauth";
	}

	public String getLSMOauthGraphURL() {
		return lsmOauthGraphURL;
	}

	public void setLSMOauthGraphURL(String lSMOauthGraphURL) {
		lsmOauthGraphURL = lSMOauthGraphURL;
	}

	public Permission getPermission(String perId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		Permission permission = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_PER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(perId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				permission = (Permission) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return permission;
	}

	public void deletePermission(String perId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_PER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(perId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addPermission(Permission permission) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_PER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(permission);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public Role getRole(String roleId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		Role role = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_ROLE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(roleId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				role = (Role) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return role;
	}

	public void deleteRole(String roleId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_ROLE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(roleId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void deletePermissionFromRole(String roleId, String permId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_ROLE_PERMISSION_DEL;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "update");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(new Tuple2<>(roleId, permId));
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addRole(Role role) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_ROLE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(role);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public User getUser(String userId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		User user = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_USER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(userId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				user = (User) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return user;
	}

	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_USER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(userId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addUser(User user) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_USER;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(user);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public LSMServiceTicketImpl getServiceTicketImpl(String ticketId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		LSMServiceTicketImpl ticket = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = "ServiceTicket";
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(ticketId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				ticket = (LSMServiceTicketImpl) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return ticket;
	}

	public void deleteServiceTicketImpl(String ticketId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = "ServiceTicket";
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(ticketId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addServiceTicketImpl(LSMServiceTicketImpl serviceTicketImpl) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = "ServiceTicket";
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(serviceTicketImpl);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public LSMTicketGrantingTicketImpl getTicketGranting(String grantId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		LSMTicketGrantingTicketImpl ticketGranting = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_TICKET_GRANTING;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(grantId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				ticketGranting = (LSMTicketGrantingTicketImpl) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return ticketGranting;
	}

	public void deleteTicketGranting(String grantId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_TICKET_GRANTING;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(grantId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addTicketGrangtingTicket(LSMTicketGrantingTicketImpl ticketGranting) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_TICKET_GRANTING;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(ticketGranting);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public LSMRegisteredServiceImpl getRegisteredService(long serviceId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		LSMRegisteredServiceImpl service = null;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_SERVICE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "load");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(serviceId + "");
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				ObjectInputStream objStream = new ObjectInputStream(conn.getInputStream());
				service = (LSMRegisteredServiceImpl) objStream.readObject();
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
		return service;
	}

	public void deleteRegisteredService(long serviceId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_SERVICE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(serviceId + "");
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				LOGGER.debug("Server response: {}.", responseCode);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void addRegisteredService(LSMRegisteredServiceImpl reg_service) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_SERVICE;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(reg_service);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

	public void createGuestServices(long userId, String serviceURL) {
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMOauthURL);
			String name = OAUTH_CREATE_USER_SERVICES;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("OAuthGraphURL", lsmOauthGraphURL);
			conn.setRequestProperty("operator", "update");
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			Tuple2<Long, String> params = new Tuple2<>(userId, serviceURL);
			dos.writeObject(params);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				LOGGER.debug("Server response: {}.", response);
			} else {
				LOGGER.warn("Server returned non-OK code: {}.", responseCode);
			}
		} catch (Exception ex) {
			LOGGER.error("cannot send data to server", ex);
		}
	}

}
