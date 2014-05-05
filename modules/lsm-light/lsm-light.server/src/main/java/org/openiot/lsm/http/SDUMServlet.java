package org.openiot.lsm.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.lsm.functionalont.model.beans.OSDSpecBean;
import org.openiot.lsm.functionalont.ops.SchedulerOps;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;

/**
 * Servlet implementation class SDUMServlet
 */
public class SDUMServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SDUMServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
	        String graphURL = request.getHeader("FunctionalGraphURL");
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
	        	boolean isUpdated = updateToServer(object,graphURL);	    	    
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1        
	            if (isUpdated)
	            	response.getWriter().print("Update DONE");
	            else
	            	response.getWriter().print("Update FAIL");
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
			if(object instanceof OSDSpecBean){
				OSDSpec osdspec = (OSDSpec) object;
	    		triples = SchedulerOps.registerService(osdspec);
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
//		try {
//			SensorManager sensorManager = new SensorManager();	
//			sensorManager.setMetaGraph(graphURL);
//			if(objectType.equals("OAuthUser")){
//				org.openiot.lsm.security.oauth.mgmt.User user = sensorManager.getOAuthUserById(value);
//				return user;
//			}else if(objectType.equals(OAUTH_SERVICE)){
//				LSMRegisteredServiceImpl ser = sensorManager.getServiceById(value);
//				return ser;
//			}else if(objectType.equals(OAUTH_TICKET_GRANTING)){
//				LSMTicketGrantingTicketImpl tickGrant = sensorManager.getTicketSchedulerById(value);
//				return tickGrant;
//			}else if(objectType.equals(OAUTH_TICKET)){
//				LSMServiceTicketImpl ticket = sensorManager.getTicketById(value);
//				return ticket;
//			}else if(objectType.equals(OAUTH_ROLE)){
//				Role role = sensorManager.getRoleById(value);
//				return role;
//			}else if(objectType.equals(OAUTH_PER)){
//				Permission permission = sensorManager.getPermissionById(value);
//				return permission;
//			}			
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
		return null;
	}
	
	private boolean updateToServer(Object object, String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();	
			sensorManager.setMetaGraph(graphURL);
      		String triples = "";
			if(object instanceof OSDSpecBean){				
				OSDSpecBean osdspec = (OSDSpecBean) object;
				sensorManager.deleteOSDSpecById(osdspec.getId());
	    		triples = SchedulerOps.OSDSpecBeanToTriples(osdspec);
			}
    		sensorManager.insertTriplesToGraph(graphURL, triples);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean deleteFromServer(String value, String objectType,String graphURL) {
		// TODO Auto-generated method stub
		try {
			SensorManager sensorManager = new SensorManager();	
			sensorManager.setMetaGraph(graphURL);
			if(objectType.equals("OSDSpec")){
				return sensorManager.deleteOSDSpecById(value);
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
