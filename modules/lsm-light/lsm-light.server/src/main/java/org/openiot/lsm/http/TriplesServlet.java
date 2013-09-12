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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.beans.User;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.manager.UserActiveManager;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;
import org.openiot.lsm.utils.XMLUtil;



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
        String sb = "";  
        StringBuilder stringBuilder = new StringBuilder();
	     Scanner scanner;		
		 scanner = new Scanner(request.getInputStream());		
		// TODO Auto-generated catch block
	     while (scanner.hasNextLine()) {
	         stringBuilder.append(scanner.nextLine());
	     }
	     String triples = stringBuilder.toString();
	     String graphURL = request.getHeader("graphURL");
//	     System.out.println(triples);  
	     
        String api = request.getParameter("api");
        String username = request.getParameter("username");
        String pass = request.getParameter("pass");
        String responseType = request.getParameter("responsetype");
        System.out.println(username+","+pass+","+responseType);
        
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
        
        User user = new User();
        user.setUsername(username);
        user.setPass(pass);
        
//        UserActiveManager userManager = new UserActiveManager();
//		User user = userManager.userAuthentication(username, pass);			
//		if(user==null){
//        	Document feedDoc = XMLUtil.createDocument();
//        	Element root = feedDoc.addElement("lsm");
//			Element login = XMLUtil.addElementToElement(root, "login", null, "false");				
//			sb = feedDoc.asXML();
//		}else{ 					
	        System.out.println(api);
	        if(responseType.equals("xml")){
	        	PrintWriter out= response.getWriter();  
	        	sb = returnXMLFunction(api,triples);
	        	response.setContentType("text/xml");
	        	try { 	        		
		        	out.println(sb);
		        	System.out.println(sb);  
		 	        out.close();  
	        	}catch (Exception ex) {  
	            	ex.printStackTrace();
	                System.out.println(sb);
	                out.close();
	        	}
	        }else if(responseType.equals("object")){
	        	Sensor sensor = returnObjectFunction(api,triples,graphURL);
	        	response.setContentType("application/x-java-serialized-object");
	        	OutputStream outputStream = response.getOutputStream();
	        	ObjectOutputStream objOutStr = new ObjectOutputStream(outputStream);
	        	try {
		        	objOutStr.writeObject(sensor);
		        	objOutStr.flush();
		        	objOutStr.close();
	        	}catch (Exception ex) {  
	            	ex.printStackTrace();
	                objOutStr.close();
	        	}
	        }
//	    }
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

	private String returnXMLFunction(String api,String triples) {  
		Document feedDoc = XMLUtil.createDocument();
		try {
			 SensorManager sensorManager = new SensorManager();				 
			 switch(api){
	        	case "1":
	        		sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, triples);
	        		sensorManager.runSpatialIndex();
	        		break;
	        	case "2":
	        		sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
	        		break;
	        	case "3":
	        		sensorManager.sensorDelete(triples);
	        		break;
	        	case "4":
	        		sensorManager.deleteAllReadings(triples);
	        		break;   	
	        	default:
	        		break;
			 }		     
			 Element root = feedDoc.addElement("lsm");
			 Element login = XMLUtil.addElementToElement(root, "login", null, "true");
			 Element feed = XMLUtil.addElementToElement(root, "feed", null, "true");		     
		} catch (Exception e) {
			e.printStackTrace();
			Element root = feedDoc.addElement("lsm");
			Element login = XMLUtil.addElementToElement(root, "login", null, "true");
			Element feed = XMLUtil.addElementToElement(root, "feed", null, "false");
			Element error = XMLUtil.addElementToElement(root, "error", null, e.toString()); 
		}
		return feedDoc.asXML();
	}
}
