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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
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
import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.beans.User;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.manager.UserActiveManager;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;
import org.openiot.lsm.utils.XMLUtil;


@WebServlet("/ObjectServlet")
public class ObjectServlet extends HttpServlet {
//	private static final long serialVersionUID = 2L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ObjectServlet() {
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
				
	    PrintWriter out= response.getWriter();  
		try { 			
			ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
			// deserialize the object, note the cast
			Object object = inputFromClient.readObject();			
	        String api = request.getParameter("api");
	        System.out.println(api);        
	        String username = request.getParameter("username");
	        String pass = request.getParameter("pass");
	        
	        System.out.println(username+","+pass);
	        
	        UserActiveManager userManager = new UserActiveManager();
			User user = userManager.userAuthentication(username, pass);
	        if(user==null){
//	        	Document feedDoc = XMLUtil.createDocument();
//	        	Element root = feedDoc.addElement("lsm");
//				Element login = XMLUtil.addElementToElement(root, "login", null, "false");	
//		        sb = feedDoc.asXML();
	        	user = new User();
		        user.setId("http://lsm.deri.ie/resource/1802198512041990");
		        user.setUsername(username);
		        user.setPass(pass);
			}
//	        else{	     
		        if(NumberUtil.isInteger(api)){        
		        	sb = returnXMLFunction(api,object,user);
		        }	
//			}
        	response.setContentType("text/xml");
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
	        out.println(sb);  
	        out.close();  
	        System.out.println(sb);
        } catch (Exception ex) {  
        	System.out.println(ex);
            out.println(sb);  
            ex.printStackTrace();
            out.close();  
        } 
	}


	private String returnXMLFunction(String api, Object object,User user) {
		// TODO Auto-generated method stub
		Document feedDoc = XMLUtil.createDocument();				
		try {
			 SensorManager sensorManager = new SensorManager();
			 Sensor sensor = null;			 
			 Element root = feedDoc.addElement("lsm");
			 Element login = XMLUtil.addElementToElement(root, "login", null, "true");
			 switch(api){
	        	case "21":	        		
					if(object instanceof Sensor)
						sensor = (Sensor) object;
					else break;						
//				    System.out.println(sensor.getId()); 
			        sensor.setUser(user);
			        String sensorTypeId = sensorManager.getSensorTypeId(sensor.getSensorType().toLowerCase());
	        		String triples = TriplesDataRetriever.getSensorTripleMetadata(sensor,sensorTypeId);
	        		System.out.println(triples);
	        		if((sensor.getMetaGraph()==null)||(sensor.getMetaGraph()==""))
	        			sensor.setMetaGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI);
	        		sensorManager.setDataGraph(sensor.getDataGraph());
	        		sensorManager.setMetaGraph(sensor.getMetaGraph());
	        		sensorManager.insertTriplesToGraph(sensor.getMetaGraph(), triples);
	        		sensorManager.runSpatialIndex();
	        		
	        		 Element feed = XMLUtil.addElementToElement(root, "feed", null, "true");
	        		 Element sensorId = XMLUtil.addElementToElement(root, "sensorId", null, sensor.getId());
		   			 Element observes = root.addElement("observes");			 
		   			 Iterator it = sensor.getProperties().entrySet().iterator();
		   			 while (it.hasNext()) {
		   			     Map.Entry pairs = (Map.Entry)it.next();
		   			     Element pro = observes.addElement("property");
		   				 Element url = pro.addElement("classURL");
		   				 url.addText(pairs.getKey().toString());
		   				 Element instance = pro.addElement("instanceId");
		   				 instance.addText(pairs.getValue().toString());
		   			 }
	        		break;
	        	case "22":
	        		Observation observation = null;
					if(object instanceof Observation)
						observation = (Observation) object;
					else break;						
//				    System.out.println(observation.getId());
	        		triples = "";
     		
	        		if((observation.getMetaGraph()==null)||(observation.getMetaGraph()==""))
	        			observation.setMetaGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI);

	        		sensorManager.setDataGraph(observation.getDataGraph());
	        		sensorManager.setMetaGraph(observation.getMetaGraph());

	        		sensor = sensorManager.getSpecifiedSensorWithSensorId(observation.getSensor());
	        		String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
							Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
							Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");					
	        		triples+=TriplesDataRetriever.getObservationTripleData(observation.getId(), observation.getSensor(), foi, observation.getTimes());
	        		for(ObservedProperty obv : observation.getReadings()){
	        			if(obv.getUnit().equals(""))
	        				triples+=TriplesDataRetriever.getTripleDataHasNoUnit("http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",obv.getPropertyType(),obv.getValue(), 
	        						observation.getId(),sensor.getProperties().get(obv.getPropertyType()), observation.getTimes());
	        			else triples+=TriplesDataRetriever.getTripleDataHasUnit("http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",obv.getPropertyType(),obv.getValue(),obv.getUnit(),
	        						observation.getId(),sensor.getProperties().get(obv.getPropertyType()), observation.getTimes());
	        		}
	        		System.out.println(triples);	        		
//	        		sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
	        		sensorManager.insertTriplesToGraph(observation.getDataGraph(), triples);
	        		break;
	        	case "23":	        		
	        		RDFTuple tuple = null;
					if(object instanceof RDFTuple)
						tuple = (RDFTuple) object;
					else break;						
//				    System.out.println(tuple.getNtriple()); 
	        		sensorManager.insertTriplesToGraph(tuple.getGraphURL(), tuple.getNtriple());
	        		break;
	        	case "24":	        		
	        		tuple = null;
					if(object instanceof RDFTuple)
						tuple = (RDFTuple) object;
					else break;						
//				    System.out.println(tuple.getNtriple()); 
					if(tuple.getNtriple().equals("all"))
						sensorManager.clearGraph(tuple.getGraphURL());
					else
						sensorManager.deleteTriples(tuple.getGraphURL(), tuple.getNtriple());
	        		break;
	        	default:
	        		break;
			 }		    			 
			 			  
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
