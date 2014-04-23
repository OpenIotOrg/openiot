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
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.utils.ConstantsUtil;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
import org.openiot.lsm.utils.SecurityUtil;
import org.openiot.security.client.PermissionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Servlet implementation class ServletAPI
 */
@WebServlet("/TriplesServlet")
public class TriplesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LoggerFactory.getLogger(TriplesServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TriplesServlet() {
        super();
        // TODO Auto-generated constructor stub        
    }

    public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    ConstantsUtil.realPath = this.getServletContext().getRealPath("WEB-INF");	   
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
		// deserialize the object, note the cast
		Object object;
		try {
			object = inputFromClient.readObject();			    
		    String graphURL = request.getHeader("graphURL");
	        String api = request.getHeader("api");	      
	        String apiType = request.getHeader("apiType");	
	        String token = request.getHeader("token");
	        String clientId = request.getHeader("clientId");
	        if(!apiType.equals("get")){
	        	String infos = (String) object;
	        	String result = processRequestImpl(api,infos,graphURL,clientId,token);      
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
	            response.getWriter().print(result);
	        }else{
	        	Sensor sensor = processObjectRequestImpl(api,(String)object,graphURL,clientId,token);
	        	response.setContentType("application/x-java-serialized-object");
	        	OutputStream outputStream = response.getOutputStream();
	        	ObjectOutputStream objOutStr = new ObjectOutputStream(outputStream);
	        	try {
		        	objOutStr.writeObject(sensor);
		        	objOutStr.flush();
		        	objOutStr.close();
//		        	response.getWriter().print("Your request processed successfully");
	        	}catch (Exception ex) {  
	            	ex.printStackTrace();
	                objOutStr.close();
	                response.getWriter().print(ex.toString());
	        	}
	        }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private Sensor processObjectRequestImpl(String api, String sensorInfo, String graphURL,String clientId,String token) {
		// TODO Auto-generated method stub		
		Sensor sensor = null;
		try {
			 SensorManager sensorManager = new SensorManager();			
			 sensorManager.setMetaGraph(graphURL);
			 switch(api){			 	
	        	case "5":
	        		String permissionString = PermissionsUtil.GET_SENSOR;
	        		if(SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
	        			sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorInfo);
	        			logger.info(sensor.getId());
	        		}else
			 			logger.info("You don't have permmison to operate this funtion");
	        		break;
	        	default:
	        		break;
			 }		    
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return sensor;
	}

	private String processRequestImpl(String api,String infos,String graphURL,String clientId,String token){  
		String result="Your request processed successfully";
		try {
			 SensorManager sensorManager = new SensorManager();				
			 switch(api){	        	
	        	case "3":
	        		String permissionString = PermissionsUtil.DEL_SENSOR;
	        		if(SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
	        			sensorManager.sensorDelete(graphURL,infos);
	        		}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
			 		}
	        		break;
			 	case "4":
			 		permissionString = PermissionsUtil.DEL_READING;
			 		if(SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
			 			sensorManager.deleteAllReadings(graphURL,infos);
			 		}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
			 		}
	        		break;
	        	default:
	        		break;
			 }		     
		} catch (Exception e) {
			e.printStackTrace();
			result = e.toString(); 
		}
		return result;
	}
}
