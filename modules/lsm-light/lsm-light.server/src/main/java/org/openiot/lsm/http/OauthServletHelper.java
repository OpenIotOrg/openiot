package org.openiot.lsm.http;

import org.openiot.commons.util.Tuple2;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import static org.openiot.lsm.utils.OAuthUtil.*;

public class OauthServletHelper {

	public boolean feedToServer(Object object, String objectType, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();
			sensorManager.setMetaGraph(graphURL);
			String triples = "";
			if (object instanceof Permission) {
				Permission permission = (Permission) object;
				triples = TriplesDataRetriever.permissionToRDF(permission);
			} else if (object instanceof Role) {
				Role role = (Role) object;
				triples = TriplesDataRetriever.roleToRDF(role);
			} else if (object instanceof org.openiot.lsm.security.oauth.mgmt.User) {
				org.openiot.lsm.security.oauth.mgmt.User OAuthUser = (org.openiot.lsm.security.oauth.mgmt.User) object;
				triples = TriplesDataRetriever.sec_UserToRDF(OAuthUser);
			} else if (object instanceof LSMRegisteredServiceImpl) {
				LSMRegisteredServiceImpl reg_service = (LSMRegisteredServiceImpl) object;
				triples = TriplesDataRetriever.registeredServiceToRDF(reg_service);
			} else if (object instanceof LSMTicketGrantingTicketImpl) {
				LSMTicketGrantingTicketImpl ticket_grant = (LSMTicketGrantingTicketImpl) object;
				triples = TriplesDataRetriever.ticketSchedulerToRDF(ticket_grant);
			} else if (object instanceof LSMServiceTicketImpl) {
				LSMServiceTicketImpl service_ticket = (LSMServiceTicketImpl) object;
				triples = TriplesDataRetriever.ticketToRDF(service_ticket);
			}
			// System.out.println(triples);
			sensorManager.insertTriplesToGraph(graphURL, triples);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Object getFromServer(String value, String objectType, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();
			sensorManager.setMetaGraph(graphURL);
			if (objectType.equals("OAuthUser")) {
				org.openiot.lsm.security.oauth.mgmt.User user = sensorManager.getOAuthUserById(value);
				return user;
			} else if (objectType.equals(OAUTH_SERVICE)) {
				LSMRegisteredServiceImpl ser = sensorManager.getServiceById(value);
				return ser;
			} else if (objectType.equals(OAUTH_TICKET_GRANTING)) {
				LSMTicketGrantingTicketImpl tickGrant = sensorManager.getTicketSchedulerById(value);
				return tickGrant;
			} else if (objectType.equals(OAUTH_TICKET)) {
				LSMServiceTicketImpl ticket = sensorManager.getTicketById(value);
				return ticket;
			} else if (objectType.equals(OAUTH_ROLE)) {
				Role role = sensorManager.getRoleById(value);
				return role;
			} else if (objectType.equals(OAUTH_PER)) {
				Permission permission = sensorManager.getPermissionById(value);
				return permission;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public boolean deleteFromServer(String value, String objectType, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();
			sensorManager.setMetaGraph(graphURL);
			if (objectType.equals("OAuthUser")) {
				return sensorManager.deleteOAuthUserById(value);
			} else if (objectType.equals(OAUTH_SERVICE)) {
				return sensorManager.deleteServiceById(value);
			} else if (objectType.equals(OAUTH_TICKET_GRANTING)) {
				return sensorManager.deleteTicketSchedulerById(value);
			} else if (objectType.equals(OAUTH_TICKET)) {
				return sensorManager.deleteTicketById(value);
			} else if (objectType.equals(OAUTH_ROLE)) {
				return sensorManager.deleteRoleById(value);
			} else if (objectType.equals(OAUTH_PER)) {
				return sensorManager.deletePermissionById(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean updateOnServer(Object obj, String objectType, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();
			sensorManager.setMetaGraph(graphURL);
			if (objectType.equals(OAUTH_ROLE_PERMISSION_ADD)) {
				Tuple2<String, String> roleIdAndPermissionId = (Tuple2<String, String>) obj;
				sensorManager.insertTriplesToGraph(graphURL,
						TriplesDataRetriever.addPermissionToRoleRDF(roleIdAndPermissionId.getItem1(), roleIdAndPermissionId.getItem2()));
			} else if (objectType.equals(OAUTH_ROLE_PERMISSION_DEL)) {
				Tuple2<String, String> roleIdAndPermissionId = (Tuple2<String, String>) obj;
				return sensorManager.deletePermissionFromRole(roleIdAndPermissionId.getItem1(), roleIdAndPermissionId.getItem2());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
