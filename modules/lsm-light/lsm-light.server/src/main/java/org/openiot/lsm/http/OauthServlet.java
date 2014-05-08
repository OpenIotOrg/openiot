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

import org.openiot.commons.util.Tuple2;
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
	private OauthServletHelper helper;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OauthServlet() {
        super();
        helper = new OauthServletHelper();
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
	        	boolean isFeed = helper.feedToServer(object,objectType,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isFeed)
	            	response.getWriter().print("FEED DONE");
	            else
	            	response.getWriter().print("FEED FAIL");
	        }else if(perform.equals("update")){
	        	boolean isFeed = helper.updateOnServer(object,objectType,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isFeed)
	            	response.getWriter().print("UPDATE DONE");
	            else
	            	response.getWriter().print("UPDATE FAIL");
	        }else if(perform.equals("delete")){
	        	boolean isFeed = helper.deleteFromServer(object.toString(),objectType,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isFeed)
	            	response.getWriter().print("DELETE DONE");
	            else
	            	response.getWriter().print("DELETE FAIL");
	        }else if(perform.equals("load")){
	        	Object returnObj = helper.getFromServer(object.toString(),objectType,graphURL);	    	    
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

	
}
