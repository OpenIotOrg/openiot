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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.SerializationUtils;
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
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */

public class TriplesDataRetriever {

	public static String getTripleDataHasUnit(String dataType,String name,String value,String unit,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\"^^<http://www.w3.org/2001/XMLSchema#double>.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#unit> \""+unit+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	
	public static String getTripleDataHasNoUnit(String dataType,String name,String value,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+=
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
//				"<"+observedURL+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Property>.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getObservationTripleData(String obsId,String sensorId,String featureOfInterest,Date time){
		String triples = "";		
		triples+="<http://lsm.deri.ie/resource/"+obsId+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Observation>.\n"+ 
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">.\n"+				
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> <"+featureOfInterest+">.\n"+
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getTripleComplexTypeData(String dataType,String name,String value,String unit,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#unit> \""+unit+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getSensorTripleMetadata(Sensor s,String sensorTypeId){
		String triples = "";
		String xsltPath = XSLTMapFile.sensormeta2xslt;
		xsltPath = ConstantsUtil.realPath + xsltPath;
//		xsltPath = "webapp/WEB-INF" + xsltPath;
		TransformerFactory tFactory = TransformerFactory.newInstance();
        String xml = "";
        try {
        	Place place = s.getPlace();
        	String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
					Double.toString(place.getLat()).replace(".", "").replace("-", "")+
					Double.toString(place.getLng()).replace(".", "").replace("-", "");
        	
            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xsltPath)));
            transformer.setParameter("sensorId", s.getId());
            transformer.setParameter("sourceType", s.getSourceType());
//            transformer.setParameter("sensortype", s.getSensorType());
            transformer.setParameter("sourceURL", s.getSource());
            transformer.setParameter("placeId", place.getId());
            transformer.setParameter("geonameId", place.getGeonameid());
            transformer.setParameter("city", place.getCity());
            transformer.setParameter("province", place.getProvince());
            transformer.setParameter("country", place.getCountry());
            transformer.setParameter("continent", place.getContinent());
            transformer.setParameter("lat", place.getLat());
            transformer.setParameter("lng", place.getLng());
            transformer.setParameter("foi", foi);
            transformer.setParameter("name", s.getName());
          
            xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><root></root>";          
            xml = xml.trim().replaceFirst("^([\\W]+)<","<");
            
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Writer outWriter = new StringWriter();  
            StreamResult result = new StreamResult( outWriter );            
            transformer.transform(new StreamSource(inputStream),result);
            triples = outWriter.toString().trim();       
            
            String sensorTypeTriples = "";
            if(sensorTypeId=="")
            	sensorTypeId = "http://lsm.deri.ie/resource/"+System.nanoTime();
        	sensorTypeTriples="\n<" + s.getId() + "> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> <" + sensorTypeId +">.\n"
        		+"<" + sensorTypeId + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#SensorType>.\n"
        		+"<" + sensorTypeId + "> <http://www.w3.org/2000/01/rdf-schema#label> \"" + s.getSensorType()+"\".\n";
        	triples+=sensorTypeTriples;
            
            String observesTriples = "";           
            for(String classURL:s.getProperties().keySet()){
            	String instanceId = "http://lsm.deri.ie/resource/"+System.nanoTime();
            	s.getProperties().put(classURL, instanceId);
            	observesTriples+= "\n<" + s.getId() + "> <http://purl.oclc.org/NET/ssnx/ssn#observes> <" + instanceId +">.\n";
            	observesTriples+= "<" + instanceId + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + classURL + ">.\n";
            	observesTriples+= "<" + instanceId + "> <http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf> <" + foi + ">.\n";
            }
            
            triples+=observesTriples;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return triples;
	}
	
	public static String permissionToRDF(Permission permission) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = permission.getName();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientPermission>.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/2000/01/rdf-schema#comment> \""+permission.getDescription()+"\".\n";
		return triples;
	}
	
	public static String roleToRDF(Role role) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = role.getName();
		triples+="<"+VirtuosoConstantUtil.RolePrefix+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ClientRole>.\n"+ 
				"<"+VirtuosoConstantUtil.RolePrefix+id+"> <http://www.w3.org/2000/01/rdf-schema#comment> \""+role.getDescription()+"\".\n";
		Iterator<Entry<Long, Set<Permission>>> it = role.getPermissionsPerService().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        long serviceId = (long) pairs.getKey();
	        Set<Permission> set_Per = (Set<Permission>) pairs.getValue();
	        String role_per_Id = id+"_"+serviceId;
	        triples+="<http://lsm.deri.ie/resource/"+role_per_Id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/RoleRight>.\n"+
	        		"<http://lsm.deri.ie/resource/"+role_per_Id+"> <http://openiot.eu/ontology/ns/forRole> <"+VirtuosoConstantUtil.RolePrefix+id+">.\n"+ 
					"<http://lsm.deri.ie/resource/"+role_per_Id+"> <http://openiot.eu/ontology/ns/forService> <http://lsm.deri.ie/resource/"+serviceId+">.\n";
	        Iterator<Permission> per_Iter = set_Per.iterator();
	        while(per_Iter.hasNext()){
	        	Permission per = per_Iter.next();
	        	triples+="<http://lsm.deri.ie/resource/"+role_per_Id+"> <http://openiot.eu/ontology/ns/forPermission> <http://lsm.deri.ie/resource/"+per.getName()+">.\n";
	        	triples+=permissionToRDF(per);
	        }
	    }
		return triples;
	}
	
	public static String sec_UserToRDF(org.openiot.lsm.security.oauth.mgmt.User sec_user) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = sec_user.getUsername();
		triples+="<" + VirtuosoConstantUtil.OAuthUserPrefix + id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/User>.\n"+ 
				"<" + VirtuosoConstantUtil.OAuthUserPrefix+id+"> <http://xmlns.com/foaf/0.1/nick> \""+sec_user.getName()+"\".\n"+
				"<" + VirtuosoConstantUtil.OAuthUserPrefix+id+">  <http://xmlns.com/foaf/0.1/mbox> \""+sec_user.getEmail()+"\".\n"+
				"<" + VirtuosoConstantUtil.OAuthUserPrefix+id+">  <http://openiot.eu/ontology/ns/password> \""+sec_user.getPassword()+"\".\n";
		for(Role role:sec_user.getRoles()){
			triples+="<" + VirtuosoConstantUtil.OAuthUserPrefix+id+"> <http://openiot.eu/ontology/ns/role> <"+VirtuosoConstantUtil.RolePrefix+role.getName()+">.\n";
			triples+=roleToRDF(role);
	    }
		return triples;
	}
	
	public static String ticketToRDF(LSMServiceTicketImpl ticket) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = ticket.getId();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/Ticket>.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://openiot.eu/ontology/ns/timesUsed> \""+ticket.getCountOfUses()+"\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/creationTime> \""+DateUtil.date2StandardString(new Date(ticket.getCreationTime()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/lastTimeUsed> \""+DateUtil.date2StandardString(new Date(ticket.getLastTimeUsed()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/prevLastTimeUsed> \""+DateUtil.date2StandardString(new Date(ticket.getPreviousTimeUsed()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/grantedBy> <http://lsm.deri.ie/resource/"+ ticket.getGrantingTicket().getId() +">.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/serviceBinary> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticket.getService()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/expirationPolicy> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticket.getExpirationPolicy()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n";
		if(ticket.isFromNewLogin())
			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/ticketFrom> <http://openiot.eu/ontology/ns/NewLogin>.\n";
		else
			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/ticketFrom> <http://openiot.eu/ontology/ns/OldLogin>.\n";
		return triples;
	}
	
	public static String ticketSchedulerToRDF(LSMTicketGrantingTicketImpl ticketGrant) {
		// TODO Auto-generated method stub
		String triples = "";
		String id = ticketGrant.getId();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/TicketScheduler>.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://openiot.eu/ontology/ns/timesUsed> \""+ticketGrant.getCountOfUses()+"\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/creationTime> \""+DateUtil.date2StandardString(new Date(ticketGrant.getCreationTime()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/lastTimeUsed> \""+DateUtil.date2StandardString(new Date(ticketGrant.getLastTimeUsed()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/prevLastTimeUsed> \""+DateUtil.date2StandardString(new Date(ticketGrant.getPreviousTimeUsed()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n"+	
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/authenticatedBy> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getAuthentication()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/expirationPolicy> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getExpirationPolicy()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n"+
				"<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/servicesGranted> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getServices()))
						+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n";
		System.out.println(Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getAuthentication())));
		if(ticketGrant.getGrantingTicket()!=null){
			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/grants> <http://lsm.deri.ie/resource/"+ ticketGrant.getGrantingTicket().getId() +">.\n";
//			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/grants> \""+ Hex.encodeHexString(SerializationUtils.serialize(ticketGrant.getGrantingTicket()))
//					+"\"^^<http://www.w3.org/2001/XMLSchema#hexBinary>.\n";
			triples+=ticketSchedulerToRDF((LSMTicketGrantingTicketImpl)ticketGrant.getGrantingTicket());
		}
		if(ticketGrant.isExpired())
			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/validity> <http://openiot.eu/ontology/ns/TicketGrantingExpired>.\n";
		else
			triples+="<http://lsm.deri.ie/resource/"+id+">  <http://openiot.eu/ontology/ns/validity> <http://openiot.eu/ontology/ns/TicketGrantingValid>.\n";
		return triples;
	}
	
	public static String registeredServiceToRDF(LSMRegisteredServiceImpl service) {
		// TODO Auto-generated method stub
		String triples = "";
		String servicePrefix = "http://lsm.deri.ie/resource/service/";
		long id = service.getId();
		triples+="<"+servicePrefix+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/CloudService>.\n"+ 
				"<"+servicePrefix+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ProxyCloudService>.\n";
		if(service.isAnonymousAccess())
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/access> <http://openiot.eu/ontology/ns/AnonymousAccess>.\n";
		else
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/access> <http://openiot.eu/ontology/ns/CrentialAccess>.\n";
		triples += "<"+servicePrefix+id+"> <http://www.w3.org/2000/01/rdf-schema#comment> \""+service.getDescription()+"\".\n";
		if(service.isEnabled())
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/status> <http://openiot.eu/ontology/ns/StatusEnabled>.\n";
		else
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/status> <http://openiot.eu/ontology/ns/StatusDisabled>.\n";
		triples+="<"+servicePrefix+id+"> <http://openiot.eu/ontology/ns/evaluationOrder> \""+service.getEvaluationOrder()+"\"^^<http://www.w3.org/2001/XMLSchema#integer>.\n";		
		if(!service.isIgnoreAttributes())
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/attributeStatus> <http://openiot.eu/ontology/ns/AttributeEnabled>.\n";
		else
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/attributeStatus> <http://openiot.eu/ontology/ns/AttributeDisabled>.\n";
		if(service.isSsoEnabled())
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusEnabled>.\n";
		else
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusDisabled>.\n";
//		if(service.isAllowedToProxy())
//			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusEnabled>.\n";
//		else
//			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/ssoStatus> <http://openiot.eu/ontology/ns/SSOStatusDisabled>.\n";
		
		if(service.getTheme()!=null)
			triples += "<"+servicePrefix+id+"> <http://openiot.eu/ontology/ns/theme> \""+service.getTheme()+"\".\n";
		if(service.getUsernameAttribute()!=null)
			triples += "<"+servicePrefix+id+"> <http://openiot.eu/ontology/ns/usernameAttr> \""+service.getUsernameAttribute()+"\".\n";
		triples += "<"+servicePrefix+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+service.getName()+"\".\n"+
				   "<"+servicePrefix+id+"> <http://openiot.eu/ontology/ns/addressId> \""+service.getServiceId()+"\".\n";
		for(String att_name:service.getAllowedAttributes()){
			String att_id = id+att_name;
			triples+="<"+servicePrefix+id+">  <http://openiot.eu/ontology/ns/attribute> <http://lsm.deri.ie/resource/"+att_id+">.\n"+
					"<http://lsm.deri.ie/resource/"+att_id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://openiot.eu/ontology/ns/ServiceAttribute>.\n"+  
					"<http://lsm.deri.ie/resource/"+att_id+">  <http://www.w3.org/2000/01/rdf-schema#label> \""+att_name+"\".\n"+
					"<http://lsm.deri.ie/resource/"+att_id+">  <http://openiot.eu/ontology/ns/attributeFor> <"+servicePrefix+id+">.\n";
		}
		return triples; 
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
		Sensor sensor  = new Sensor();
        sensor.setId("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
        sensor.setName("hello");
        sensor.setAuthor("admin");
//		sensor.setSensorType("bikehire");
		sensor.setSourceType("sdfg");
		sensor.setInfor("asfdfs");
		sensor.setSource("affag");
		sensor.setTimes(new Date());
        Place place = new Place();
        place.setLat(32325);
        place.setLng(324);
        sensor.setPlace(place);
        System.out.println(TriplesDataRetriever.getSensorTripleMetadata(sensor,""));
	}
}
