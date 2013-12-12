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
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.utils.ConstantsUtil;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */


/**
 * Servlet implementation class ServletAPI
 */
@WebServlet("/TriplesServlet")
public class TriplesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
	        if(!apiType.equals("get")){
	        	String infos = (String) object;
	        	String result = returnXMLFunction(api,infos,graphURL);      
	        	response.setContentType("text/xml");
	            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
	            response.getWriter().print(result);
	        }else{
	        	Sensor sensor = returnObjectFunction(api,(String)object,graphURL);
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
	

	private Sensor returnObjectFunction(String api, String sensorInfo, String graphURL) {
		// TODO Auto-generated method stub		
		Sensor sensor = null;
		try {
			 SensorManager sensorManager = new SensorManager();			
			 sensorManager.setMetaGraph(graphURL);
			 switch(api){			 	
	        	case "5":
	        		sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorInfo);
	        		System.out.println(sensor.getId());
	        		break;
	        	case "6":
	        		sensor = sensorManager.getSpecifiedSensorWithSource(sensorInfo);
	        		System.out.println(sensor.getId());
	        		break;
	        	default:
	        		break;
			 }		    
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return sensor;
	}

	private String returnXMLFunction(String api,String infos,String graphURL){  
		String result="Your request processed successfully";
		try {
			 SensorManager sensorManager = new SensorManager();				
			 switch(api){
	        	case "1":
	        		sensorManager.insertTriplesToGraph(graphURL, infos);
	        		sensorManager.runSpatialIndex();
	        		break;
	        	case "2":
	        		sensorManager.insertTriplesToGraph(graphURL, infos);
	        		break;
	        	case "3":
	        		sensorManager.sensorDelete(graphURL,infos);
	        		break;
			 	case "4":
	        		sensorManager.deleteAllReadings(graphURL,infos);
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
