package org.openiot.lsm.http;
/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
/**
 * Servlet implementation class OauthServlet
 */ 
// @WebServlet("/OauthServlet")
public class OauthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static String OAUTH_PER = "Permission";
	final static String OAUTH_ROLE = "Role";
	final static String OAUTH_USER = "OAuthUser";
	final static String OAUTH_SERVICE = "RegisteredService";
	final static String OAUTH_TICKET = "ServiceTicket";
	final static String OAUTH_TICKET_GRANTING = "TicketGranting";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OauthServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try { 			
			ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
			// deserialize the object, note the cast
			Object object = inputFromClient.readObject();			
	        String objectType = request.getHeader("objectType");
	        String pro = request.getHeader("project").toLowerCase(); 
	        String graphURL = request.getHeader("OAuthGraphURL");
	        String perform = request.getHeader("operator");
			
	        if(perform.equals("insert")){
	        	boolean isFeed = feedToServer(object,objectType,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isFeed)
	            	response.getWriter().print("FEED DONE");
	            else
	            	response.getWriter().print("FEED FAIL");
	        }else if(perform.equals("update")){
	        	
	        }else if(perform.equals("delete")){
	        	boolean isFeed = deleteFromServer(object.toString(),objectType,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isFeed)
	            	response.getWriter().print("DELETE DONE");
	            else
	            	response.getWriter().print("DELETE FAIL");
	        }else if(perform.equals("load")){
	        	Object returnObj = getFromServer(object.toString(),objectType,graphURL);	    	    
	        	response.setContentType("application/x-java-serialized-object");
	        	ObjectOutputStream outputToApplet = new ObjectOutputStream(response.getOutputStream());
	        	outputToApplet.writeObject(returnObj);
	        	outputToApplet.flush();          
	            outputToApplet.close();
	        }	       
        } catch (Exception ex) {   
            ex.printStackTrace(); 
        } 
	}

	private boolean feedToServer(Object object, String objectType, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();	
			sensorManager.setMetaGraph(graphURL);
      		String triples = "";
			if(object instanceof Permission){
				Permission permission = (Permission) object;
	    		triples = TriplesDataRetriever.permissionToRDF(permission);
			}else if(object instanceof Role){
				Role role = (Role) object;
				triples = TriplesDataRetriever.roleToRDF(role);
			}else if(object instanceof org.openiot.lsm.security.oauth.mgmt.User){
				org.openiot.lsm.security.oauth.mgmt.User OAuthUser = (org.openiot.lsm.security.oauth.mgmt.User) object;
				triples = TriplesDataRetriever.sec_UserToRDF(OAuthUser);
			}else if(object instanceof LSMRegisteredServiceImpl){
				LSMRegisteredServiceImpl reg_service = (LSMRegisteredServiceImpl) object;
				triples = TriplesDataRetriever.registeredServiceToRDF(reg_service);
			}else if(object instanceof LSMTicketGrantingTicketImpl){
				LSMTicketGrantingTicketImpl ticket_grant = (LSMTicketGrantingTicketImpl) object;
				triples = TriplesDataRetriever.ticketSchedulerToRDF(ticket_grant);
			}else if(object instanceof LSMServiceTicketImpl){
				LSMServiceTicketImpl service_ticket = (LSMServiceTicketImpl) object;
				triples = TriplesDataRetriever.ticketToRDF(service_ticket);
			}
//    		System.out.println(triples);
    		sensorManager.insertTriplesToGraph(graphURL, triples);

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Object getFromServer(String value, String objectType,
			String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();	
			sensorManager.setMetaGraph(graphURL);
			if(objectType.equals("OAuthUser")){
				org.openiot.lsm.security.oauth.mgmt.User user = sensorManager.getOAuthUserById(value);
				return user;
			}else if(objectType.equals(OAUTH_SERVICE)){
				LSMRegisteredServiceImpl ser = sensorManager.getServiceById(value);
				return ser;
			}else if(objectType.equals(OAUTH_TICKET_GRANTING)){
				LSMTicketGrantingTicketImpl tickGrant = sensorManager.getTicketSchedulerById(value);
				return tickGrant;
			}else if(objectType.equals(OAUTH_TICKET)){
				LSMServiceTicketImpl ticket = sensorManager.getTicketById(value);
				return ticket;
			}else if(objectType.equals(OAUTH_ROLE)){
				Role role = sensorManager.getRoleById(value);
				return role;
			}else if(objectType.equals(OAUTH_PER)){
				Permission permission = sensorManager.getPermissionById(value);
				return permission;
			}			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	private boolean deleteFromServer(String value, String objectType,String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();	
			sensorManager.setMetaGraph(graphURL);
			if(objectType.equals("OAuthUser")){
				return sensorManager.deleteOAuthUserById(value);
			}else if(objectType.equals(OAUTH_SERVICE)){
				return sensorManager.deleteServiceById(value);
			}else if(objectType.equals(OAUTH_TICKET_GRANTING)){
				return sensorManager.deleteTicketSchedulerById(value);
			}else if(objectType.equals(OAUTH_TICKET)){
				return sensorManager.deleteTicketById(value);
			}else if(objectType.equals(OAUTH_ROLE)){
				return sensorManager.deleteRoleById(value);
			}else if(objectType.equals(OAUTH_PER)){
				return sensorManager.deletePermissionById(value);
			}			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
