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
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.ObservedProperty;
import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.manager.SensorManager;
import org.openiot.lsm.manager.TriplesDataRetriever;
import org.openiot.lsm.pooling.ConnectionManager;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.NumberUtil;
import org.openiot.lsm.utils.SecurityUtil;
import org.openiot.security.client.PermissionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
/**
 *
 * @author Hoan Nguyen Mau Quoc
 *
 */

@WebServlet("/ObjectServlet")
public class ObjectServlet extends HttpServlet {
//	private static final long serialVersionUID = 2L;

	final static Logger logger = LoggerFactory.getLogger(ObjectServlet.class);
    static private PropertyManagement propertyManagement = null;
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
	    propertyManagement = new PropertyManagement();
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
			String graphURL = request.getHeader("graphURL");
	        String api = request.getHeader("api");
	        String apiType = request.getHeader("apiType");
//	        System.out.println(api);
	        String token = request.getHeader("token");
	        String clientId = request.getHeader("clientId");
	        logger.info("API function:"+api);

	        if(NumberUtil.isInteger(api)){
		      	sb = processRequestImpl(api,object,clientId,token);
		    }
        	response.setContentType("text/xml");
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
	        out.println(sb);
	        out.close();
	        logger.info(sb);
        } catch (Exception ex) {
            out.println(sb);
//            ex.printStackTrace();
            out.close();
            logger.error("Server returns error",ex);
        }
	}


	private String processRequestImpl(String api, Object object,String clientId,String token) {
		// TODO Auto-generated method stub
		String result="Your request processed successfully";
		try {
			 SensorManager sensorManager = new SensorManager();
			 Sensor sensor = null;
			 String triples = "";
			 switch(api){
	        	case "21":
					if(object instanceof Sensor)
						sensor = (Sensor) object;
					else break;
					logger.info("add new sensor with id = "+sensor.getId());

					if((sensor.getMetaGraph()==null)||(sensor.getMetaGraph()==""))
	        			sensor.setMetaGraph(propertyManagement.getSchedulerLsmMetaGraph());

					String permissionString = "";
					if(PermissionsUtil.getUserType(sensor.getMetaGraph())==PermissionsUtil.GUESS_USER)
						permissionString = PermissionsUtil.ADD_SENSOR_GUESS;
					else if(PermissionsUtil.getUserType(sensor.getMetaGraph())==PermissionsUtil.DEMO_USER)
						permissionString = PermissionsUtil.ADD_SENSOR_DEMO;
					else permissionString = PermissionsUtil.ADD_SENSOR_MAIN;

					if(SecurityUtil.hasPermission(PermissionsUtil.LSM_ALL, getServletContext(), token, clientId)
						||SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
		        		sensorManager.setDataGraph(sensor.getDataGraph());
		        		sensorManager.setMetaGraph(sensor.getMetaGraph());
//				        String sensorTypeId = sensorManager.getSensorTypeId(sensor.getSensorType().toLowerCase());
		        		triples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
//		        		logger.info(triples);
		        		sensorManager.insertTriplesToGraph(sensor.getMetaGraph(), triples);
//		        		sensorManager.runSpatialIndex();
		        		logger.info("Add new sensor");
//		        		logger.debug("Add new sensor");
					}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
					}
	        		break;
	        	case "22":
	        		Observation observation = null;
					if(object instanceof Observation)
						observation = (Observation) object;
					else break;
				    logger.info("add Observation object with id = "+observation.getId());
	        		triples = "";
	        		if((observation.getMetaGraph()==null)||(observation.getMetaGraph()==""))
	        			observation.setMetaGraph(propertyManagement.getLSMLocalMetaGraph());

	        		if(PermissionsUtil.getUserType(observation.getMetaGraph())==PermissionsUtil.GUESS_USER)
	        			permissionString = PermissionsUtil.UPDATE_SENSOR_DATA_GUESS;
	        		else if(PermissionsUtil.getUserType(observation.getMetaGraph())==PermissionsUtil.DEMO_USER)
	        			permissionString = PermissionsUtil.UPDATE_SENSOR_DATA_DEMO;
	        		else permissionString = PermissionsUtil.UPDATE_SENSOR_DATA_MAIN;

	        		if(SecurityUtil.hasPermission(PermissionsUtil.LSM_ALL, getServletContext(), token, clientId)
							||SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
		        		sensorManager.setDataGraph(observation.getDataGraph());
		        		sensorManager.setMetaGraph(observation.getMetaGraph());

		        		sensor = sensorManager.getSpecificSensorWithSensorId(observation.getSensor());
		        		if(sensor==null){
		        			result="Sensor "+observation.getSensor()+" has not been registered yet. Please register your sensor!";
		        			return result;
		        		}
		        		String foi = "";
		        		if(observation.getFeatureOfInterest().equals("")||observation.getFeatureOfInterest()==null)
		        			foi = ConnectionManager.propertyManagement.getOpeniotResourceNamespace() +
								Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
								Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");
		        		else foi = observation.getFeatureOfInterest();
		        		triples+=TriplesDataRetriever.getObservationTripleData(observation.getId(), observation.getSensor(), foi, observation.getTimes());

		        		OntModel model = ModelFactory.createOntologyModel();
		        		for(ObservedProperty obv : observation.getReadings()){
		        			OntClass cl = model.createClass(obv.getPropertyType());
		        			if(obv.getUnit().equals(""))
		        				triples+=TriplesDataRetriever.getTripleDataHasNoUnit("http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",cl.getLocalName(),obv.getValue().toString(),
		        						observation.getId(),sensor.getProperties().get(obv.getPropertyType()), observation.getTimes());
		        			else triples+=TriplesDataRetriever.getTripleDataHasUnit("http://purl.oclc.org/NET/ssnx/ssn#ObservationValue",cl.getLocalName(),obv.getValue().toString(),obv.getUnit(),
		        						observation.getId(),sensor.getProperties().get(obv.getPropertyType()), observation.getTimes());
		        		}
	//	        		System.out.println(triples);
		        		sensorManager.insertTriplesToGraph(observation.getDataGraph(), triples);
		        		logger.info("Add new sensor data successfully");
	        		}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
					}
	        		break;
	        	case "23":
	        		RDFTuple tuple = null;
					if(object instanceof RDFTuple)
						tuple = (RDFTuple) object;
					else break;
//				    System.out.println(tuple.getNtriple());

					if(PermissionsUtil.getUserType(tuple.getGraphURL())==PermissionsUtil.GUESS_USER)
						permissionString = PermissionsUtil.ADD_TRIPLES_GUESS;
					else if(PermissionsUtil.getUserType(tuple.getGraphURL())==PermissionsUtil.DEMO_USER)
						permissionString = PermissionsUtil.ADD_TRIPLES_DEMO;
					else permissionString = PermissionsUtil.ADD_TRIPLES_MAIN;

					if(SecurityUtil.hasPermission(PermissionsUtil.LSM_ALL, getServletContext(), token, clientId)
							||SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
		        		sensorManager.insertTriplesToGraph(tuple.getGraphURL(), tuple.getNtriple());
		        		logger.info("Add triples to graph "+tuple.getGraphURL());
					}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
					}
	        		break;
	        	case "24":
	        		tuple = null;
					if(object instanceof RDFTuple)
						tuple = (RDFTuple) object;
					else break;
//				    System.out.println(tuple.getNtriple());
					if(PermissionsUtil.getUserType(tuple.getGraphURL())==PermissionsUtil.GUESS_USER)
						permissionString = PermissionsUtil.DEL_TRIPLES_GUESS;
					else if(PermissionsUtil.getUserType(tuple.getGraphURL())==PermissionsUtil.DEMO_USER)
						permissionString = PermissionsUtil.DEL_TRIPLES_DEMO;
					else permissionString = PermissionsUtil.DEL_TRIPLES_MAIN;

					if(SecurityUtil.hasPermission(PermissionsUtil.LSM_ALL, getServletContext(), token, clientId)
							||SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
						if(tuple.getNtriple().equals("all")){
							sensorManager.clearGraph(tuple.getGraphURL());
							logger.info("Delete all triples of graph "+tuple.getGraphURL());
						}else{
							sensorManager.deleteTriples(tuple.getGraphURL(), tuple.getNtriple());
							logger.info("Delete triples patterns of graph "+tuple.getGraphURL());
						}
					}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
					}
	        		break;
	        	case "25":
	        		HashMap<String, String> patterns = null;

	        		if(PermissionsUtil.getUserType(patterns.get("graph"))==PermissionsUtil.GUESS_USER)
	        			permissionString = PermissionsUtil.UPDATE_TRIPLES_GUESS;
					else if(PermissionsUtil.getUserType(patterns.get("graph"))==PermissionsUtil.DEMO_USER)
						permissionString = PermissionsUtil.UPDATE_TRIPLES_DEMO;
					else permissionString = PermissionsUtil.UPDATE_TRIPLES_MAIN;

	        		if(SecurityUtil.hasPermission(PermissionsUtil.LSM_ALL, getServletContext(), token, clientId)
							||SecurityUtil.hasPermission(permissionString, getServletContext(), token, clientId)){
		        		if(object instanceof HashMap<?,?>)
		        			patterns = (HashMap<String, String>) object;
		        		sensorManager.updateGraph(patterns.get("graph"),patterns.get("update"),patterns.get("delete"));
					}else{
			 			result ="You don't have permmison to operate this funtion";
			 			logger.info(result);
					}
	        	default:
	        		break;
			 }

		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Server returns error",e);
			result = e.toString();
		}
		return result;
	}
}
