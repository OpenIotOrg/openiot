package org.openiot.lsm.manager;

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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.SerializationUtils;
import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.beans.Place;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.security.oauth.LSMRegisteredServiceImpl;
import org.openiot.lsm.security.oauth.LSMServiceTicketImpl;
import org.openiot.lsm.security.oauth.LSMTicketGrantingTicketImpl;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.DateUtil;
import org.openiot.lsm.utils.VirtuosoConstantUtil;
import org.openiot.lsm.utils.XSLTMapFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

public class TriplesDataRetriever {

	static PropertyManagement propertyManagement = new PropertyManagement();
	final static Logger logger = LoggerFactory.getLogger(SensorManager.class);
	
	public static String getTripleDataHasUnit(String dataType,String name,String value,String unit,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		triples+="<"+prefix + id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<"+prefix+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+prefix + observationId+">.\n"+
				"<"+prefix+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\"^^<http://www.w3.org/2001/XMLSchema#double>.\n"+
				"<"+prefix+id+"> <http://lsm.deri.ie/ont/lsm.owl#unit> \""+unit+"\".\n"+
				"<"+prefix+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<"+prefix+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<"+prefix+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	
	public static String getTripleDataHasNoUnit(String dataType,String name,String value,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		triples+=
				"<"+prefix+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<"+prefix+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+ prefix + observationId+">.\n"+
				"<"+prefix+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\".\n"+
				"<"+prefix+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<"+prefix+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
//				"<"+observedURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Property>.\n"+
				"<"+prefix+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getObservationTripleData(String obsId,String sensorId,String featureOfInterest,Date time){
		String triples = "";		
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		triples+="<"+prefix+obsId+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Observation>.\n"+ 
				"<"+prefix+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">.\n"+				
				"<"+prefix+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> <"+featureOfInterest+">.\n"+
				"<"+prefix+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getSensorTripleMetadata(Sensor s,String sensorTypeId){
		String triples = "";
		String xsltPath = XSLTMapFile.sensormeta2xslt;
		xsltPath = ConstantsUtil.realPath + xsltPath;
//		xsltPath = "src/main/webapp/WEB-INF" + xsltPath;
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
//		TransformerFactory.newInstance();
		String prefix = propertyManagement.getOpeniotResourceNamespace();
        String xml = "";
        try {
        	Place place = s.getPlace();
//        	String foi = propertyManagement.getOpeniotResourceNamespace()+ 
//					Double.toString(place.getLat()).replace(".", "").replace("-", "")+
//					Double.toString(place.getLng()).replace(".", "").replace("-", "");
        	
            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xsltPath)));
            transformer.setParameter("sensorId", s.getId());
            transformer.setParameter("utc-timestamp", DateUtil.date2StandardString(new Date()));
//            transformer.setParameter("sourceType", s.getSourceType());
            transformer.setParameter("prefix", prefix);
//            transformer.setParameter("sourceURL", s.getSource());
            transformer.setParameter("placeId", place.getId());
            transformer.setParameter("geonameId", place.getGeonameid());
            transformer.setParameter("city", place.getCity());
            transformer.setParameter("province", place.getProvince());
            transformer.setParameter("country", place.getCountry());
            transformer.setParameter("continent", place.getContinent());
            transformer.setParameter("lat", place.getLat());
            transformer.setParameter("lng", place.getLng());
//            transformer.setParameter("foi", foi);
            transformer.setParameter("name", s.getName());
            transformer.setParameter("author", s.getAuthor());
            
            xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><root></root>";          
            xml = xml.trim().replaceFirst("^([\\W]+)<","<");
            
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Writer outWriter = new StringWriter();  
            StreamResult result = new StreamResult( outWriter );            
            transformer.transform(new StreamSource(inputStream),result);
            triples = outWriter.toString().trim();       
            
            String sensorTypeTriples = "";
            if(sensorTypeId=="")
            	sensorTypeId = prefix+System.nanoTime();
        	sensorTypeTriples="\n<" + s.getId() + "> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> <" + sensorTypeId +">.\n"
        		+"<" + sensorTypeId + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#SensorType>.\n"
        		+"<" + sensorTypeId + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + s.getSensorType()+"\".\n";
        	triples+=sensorTypeTriples;
            
            String observesTriples = "";           
            for(String classURL:s.getProperties().keySet()){
            	String instanceId = prefix + System.nanoTime();
            	s.getProperties().put(classURL, instanceId);
            	observesTriples+= "\n<" + s.getId() + "> <http://purl.oclc.org/NET/ssnx/ssn#observes> <" + instanceId +">.\n";
            	observesTriples+= "<" + instanceId + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + classURL + ">.\n";
//            	observesTriples+= "<" + instanceId + "> <http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf> <" + foi + ">.\n";
            }            
            triples+=observesTriples;
            triples+= "<" + place.getId() +"> <http://www.w3.org/2003/01/geo/wgs84_pos#geometry> \"POINT("+place.getLng()+" "+place.getLat()+
            			")\"^^<http://www.openlinksw.com/schemas/virtrdf#Geometry>.\n";
        } catch (Exception e) {
            logger.info(e.toString());
        }
		return triples;
	}

	public static String permissionToRDF(Permission permission) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = Permission.toPermissionIdStr(permission);
		triples += "<" + VirtuosoConstantUtil.PermissionPrefix + id
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientPermission>.\n" + "<"
				+ VirtuosoConstantUtil.PermissionPrefix + id + "> <http://www.w3.org/2000/01/rdf-schema#comment> \"" + permission.getDescription() + "\".\n";
		return triples;
	}

	public static String roleToRDF(Role role) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = Role.toRoleIdStr(role);
		triples += "<" + VirtuosoConstantUtil.RolePrefix + id
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientRole>.\n" + "<" + VirtuosoConstantUtil.RolePrefix
				+ id + "> <http://www.w3.org/2000/01/rdf-schema#comment> \"" + role.getDescription() + "\".\n";

		for (Permission permission : role.getPermissions()) {
			String perId = VirtuosoConstantUtil.PermissionPrefix + Permission.toPermissionIdStr(permission);
			triples += "<" + VirtuosoConstantUtil.RolePrefix + id + "> <http://openiot.eu/ontology/ns/forPermission> <" + perId + ">.\n";
			triples += permissionToRDF(permission);
		}
		return triples;
	}

	public static String sec_UserToRDF(org.openiot.lsm.security.oauth.mgmt.User sec_user) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = sec_user.getUsername();
		triples += "<" + VirtuosoConstantUtil.OAuthUserPrefix + id
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>.\n" + "<" + VirtuosoConstantUtil.OAuthUserPrefix
				+ id + "> <http://xmlns.com/foaf/0.1/nick> \"" + sec_user.getName() + "\".\n" + "<" + VirtuosoConstantUtil.OAuthUserPrefix + id
				+ ">  <http://xmlns.com/foaf/0.1/mbox> \"" + sec_user.getEmail() + "\".\n" + "<" + VirtuosoConstantUtil.OAuthUserPrefix + id
				+ ">  <http://openiot.eu/ontology/ns/password> \"" + sec_user.getPassword() + "\".\n";
		for (Role role : sec_user.getRoles()) {
			triples += "<" + VirtuosoConstantUtil.OAuthUserPrefix + id + "> <http://openiot.eu/ontology/ns/role> <" + VirtuosoConstantUtil.RolePrefix
					+ Role.toRoleIdStr(role) + ">.\n";
			triples += roleToRDF(role);
		}
		return triples;
	}

	public static String ticketToRDF(LSMServiceTicketImpl ticket) {
		// TODO Auto-generated method stub
		String triples = "";
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		String id = ticket.getId();
		triples += "<"+ prefix + id + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>.\n"
				+ "<"+ prefix + id + "> <http://openiot.eu/ontology/ns/timesUsed> \"" + ticket.getCountOfUses()
				+ "\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n" + "<"+ prefix + id
				+ ">  <http://openiot.eu/ontology/ns/creationTime> \"" + DateUtil.date2StandardString(new Date(ticket.getCreationTime()))
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n" + "<"+ prefix + id
				+ ">  <http://openiot.eu/ontology/ns/lastTimeUsed> \"" + DateUtil.date2StandardString(new Date(ticket.getLastTimeUsed()))
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n" + "<"+ prefix + id
				+ ">  <http://openiot.eu/ontology/ns/prevLastTimeUsed> \"" + DateUtil.date2StandardString(new Date(ticket.getPreviousTimeUsed()))
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n" + "<"+ prefix + id
				+ ">  <http://openiot.eu/ontology/ns/grantedBy> <" + prefix + ticket.getGrantingTicket().getId() + ">.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/serviceBinary> \""
				+ Hex.encodeHexString(SerializationUtils.serialize(ticket.getService())) + "\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/expirationPolicy> \""
				+ Hex.encodeHexString(SerializationUtils.serialize(ticket.getExpirationPolicy())) + "\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n";
		if (ticket.isFromNewLogin())
			triples += "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/ticketFrom> <http://openiot.eu/ontology/ns/NewLogin>.\n";
		else
			triples += "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/ticketFrom> <http://openiot.eu/ontology/ns/OldLogin>.\n";
		return triples;
	}

	public static String ticketSchedulerToRDF(LSMTicketGrantingTicketImpl ticketGrant) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = ticketGrant.getId();
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		triples += "<"+ prefix + id
				+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>.\n" + "<"+ prefix
				+ id + "> <http://openiot.eu/ontology/ns/timesUsed> \"" + ticketGrant.getCountOfUses() + "\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/creationTime> \""
				+ DateUtil.date2StandardString(new Date(ticketGrant.getCreationTime())) + "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/lastTimeUsed> \""
				+ DateUtil.date2StandardString(new Date(ticketGrant.getLastTimeUsed())) + "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/prevLastTimeUsed> \""
				+ DateUtil.date2StandardString(new Date(ticketGrant.getPreviousTimeUsed())) + "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/authenticatedBy> \""
				+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getAuthentication())) + "\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/expirationPolicy> \""
				+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getExpirationPolicy())) + "\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"
				+ "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/servicesGranted> \""
				+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getServices())) + "\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n";
//		System.out.println(Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getAuthentication())));
		if (ticketGrant.getGrantingTicket() != null) {
			triples += "<"+ prefix + id + ">  <http://openiot.eu/ontology/ns/grants> <"+prefix
					+ ticketGrant.getGrantingTicket().getId() + ">.\n";
			triples += ticketSchedulerToRDF((LSMTicketGrantingTicketImpl) ticketGrant.getGrantingTicket());
		}
		if (ticketGrant.isExpired())
			triples += "<"+ prefix + id
					+ ">  <http://openiot.eu/ontology/ns/validity> <http://openiot.eu/ontology/ns/TicketGrantingExpired>.\n";
		else
			triples += "<"+ prefix + id
					+ ">  <http://openiot.eu/ontology/ns/validity> <http://openiot.eu/ontology/ns/TicketGrantingValid>.\n";
		return triples;
	}

	public static String registeredServiceToRDF(LSMRegisteredServiceImpl service) {
		// TODO Auto-generated method stub
		String triples = "";
		String prefix = propertyManagement.getOpeniotResourceNamespace();
		String servicePrefix = VirtuosoConstantUtil.CloudServicePrefix;
		long id = service.getId();
		triples += "<" + servicePrefix + id + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>.\n" + "<"
				+ servicePrefix + id + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ProxyCloudService>.\n";
		if (service.isAnonymousAccess())
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/access> <http://openiot.eu/ontology/ns/AnonymousAccess>.\n";
		else
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/access> <http://openiot.eu/ontology/ns/CrentialAccess>.\n";
		triples += "<" + servicePrefix + id + "> <http://www.w3.org/2000/01/rdf-schema#comment> \"" + service.getDescription() + "\".\n";
		if (service.isEnabled())
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/status> <http://openiot.eu/ontology/ns/StatusEnabled>.\n";
		else
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/status> <http://openiot.eu/ontology/ns/StatusDisabled>.\n";
		triples += "<" + servicePrefix + id + "> <http://openiot.eu/ontology/ns/evaluationOrder> \"" + service.getEvaluationOrder()
				+ "\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n";
		if (!service.isIgnoreAttributes())
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/attributeStatus> <http://openiot.eu/ontology/ns/AttributeEnabled>.\n";
		else
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/attributeStatus> <http://openiot.eu/ontology/ns/AttributeDisabled>.\n";
		if (service.isSsoEnabled())
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusEnabled>.\n";
		else
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusDisabled>.\n";

		if (service.getTheme() != null)
			triples += "<" + servicePrefix + id + "> <http://openiot.eu/ontology/ns/theme> \"" + service.getTheme() + "\".\n";
		if (service.getUsernameAttribute() != null)
			triples += "<" + servicePrefix + id + "> <http://openiot.eu/ontology/ns/usernameAttr> \"" + service.getUsernameAttribute() + "\".\n";
		triples += "<" + servicePrefix + id + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + service.getName() + "\".\n" + "<" + servicePrefix + id
				+ "> <http://openiot.eu/ontology/ns/addressId> \"" + service.getServiceId() + "\".\n";
		for (String att_name : service.getAllowedAttributes()) {
			String att_id = id + att_name;
			triples += "<" + servicePrefix + id + ">  <http://openiot.eu/ontology/ns/attribute> <"+prefix + att_id + ">.\n"
					+ "<"+ prefix + att_id
					+ "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ServiceAttribute>.\n"
					+ "<"+ prefix + att_id + ">  <http://www.w3.org/2000/01/rdf-schema#label> \"" + att_name + "\".\n"
					+ "<"+ prefix + att_id + ">  <http://openiot.eu/ontology/ns/attributeFor> <" + servicePrefix + id + ">.\n";
		}
		return triples;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		Sensor sensor = new Sensor();
		sensor.setId("http://services.openiot.eu/resource/8a82919d3264f4ac013264f4e14501c0");
		sensor.setName("hello");
		sensor.setAuthor("admin");
		sensor.setSensorType("bikehire");
//		sensor.setSourceType("sdfg");
		sensor.setInfor("asfdfs");
//		sensor.setSource("affag");
		sensor.setTimes(new Date());
		Place place = new Place();
		place.setLat(32325);
		place.setLng(324);
		sensor.setPlace(place);
		System.out.println(TriplesDataRetriever.getSensorTripleMetadata(sensor, ""));
	}

	public static String addPermissionToRoleRDF(String roleId, String permId) {
		String triples = "<" + VirtuosoConstantUtil.RolePrefix + roleId + "> <http://openiot.eu/ontology/ns/forPermission> <" + VirtuosoConstantUtil.PermissionPrefix+permId + ">.\n";
		return triples;
	}
}
