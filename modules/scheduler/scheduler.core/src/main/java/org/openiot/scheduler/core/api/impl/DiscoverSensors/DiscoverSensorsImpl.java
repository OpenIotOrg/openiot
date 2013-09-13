package org.openiot.scheduler.core.api.impl.DiscoverSensors;

/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openiot.commons.sensortypes.model.MeasurementCapability;
import org.openiot.commons.sensortypes.model.SensorType;
import org.openiot.commons.sensortypes.model.SensorTypes;
import org.openiot.commons.sensortypes.model.Unit;
import org.openiot.scheduler.core.test.SensorTypesPopulation;
import org.openiot.scheduler.core.utils.lsmpa.entities.Service;
import org.openiot.scheduler.core.utils.lsmpa.entities.User;
import org.openiot.scheduler.core.utils.sparql.SesameSPARQLClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 */

public class DiscoverSensorsImpl 
{	
	private static class Queries
	{
		private static String openiotMetaGraph = "http://lsm.deri.ie/OpenIoT/demo/sensormeta#";
		private static String openiotDataGraph = "http://lsm.deri.ie/OpenIoT/demo/sensordata#";
				
		public static ArrayList<SensorTypeMetaData> parseSensorTypeMetaData(TupleQueryResult qres)
		{
			ArrayList<SensorTypeMetaData> sensorTypeMetaDataList = new ArrayList<SensorTypeMetaData>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					SensorTypeMetaData sensorTypeMetaData = new SensorTypeMetaData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("measurement"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							sensorTypeMetaData.setMeasuredVal(str);
							System.out.print("measurement : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("unit"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							sensorTypeMetaData.setUnit(str);
							System.out.print("unit : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("avalue"))
						{
							//String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							String st = b.getValue((String) n).toString();
							String[] split=st.split("#");
							
							sensorTypeMetaData.setValue(split[1].substring(0, split[1].length()-1));
							System.out.print("value : "+sensorTypeMetaData.getValue()+" ");	
						}
						
					}
					sensorTypeMetaDataList.add(sensorTypeMetaData);					
				}//while
				return sensorTypeMetaDataList;
			} 
			catch (QueryEvaluationException e)			
			{				
				e.printStackTrace();
				return null;
			}
			catch (Exception e)			
			{				
				e.printStackTrace();
				return null;
			}
		}
		public static ArrayList<SensorTypeData> parseSensorTypeInArea(TupleQueryResult qres)
		{
			ArrayList<SensorTypeData> sensorTypeDataList = new ArrayList<SensorTypeData>();
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					SensorTypeData sensorTypeData = new SensorTypeData();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("sensLabelType"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							sensorTypeData.setLabel(str);
							System.out.print("sensor label: "+sensorTypeData.getLabel()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("type"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							sensorTypeData.setID(str);
							System.out.print("unit : "+sensorTypeData.getID()+" ");
						}
					}
					sensorTypeDataList.add(sensorTypeData);					
				}//while
				return sensorTypeDataList;
			} 
			catch (QueryEvaluationException e)			
			{				
				e.printStackTrace();
				return null;
			}
			catch (Exception e)			
			{				
				e.printStackTrace();
				return null;
			}
		}	
		
		public static String getSensTypeInArea(double longitude, double latitude, float radius)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("select distinct(?sensLabelType) ?type "
								+"from <"+openiotMetaGraph+"> "
								+"WHERE "
								+"{"								
								
								+"?type <http://www.w3.org/2000/01/rdf-schema#label> ?sensLabelType."
								+"?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."
								+"?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p."
								
								+"?p geo:geometry ?geo."								
								+"filter (<bif:st_intersects>(?geo,<bif:st_point>("+longitude+","+latitude+"),"+radius+"))."
								
								+"}");
			
			update.append(str);
			return update.toString();
		}
		public static String getMDataOfSensorTypeInArea(double longitude, double latitude, float radius,String sensorType)
		{
			StringBuilder update = new StringBuilder();	        
			
			String str=("SELECT ?measurement ?unit (AVG(?value) AS ?avalue) "							
							+"WHERE "
							+"{"								
							
							+"?prob <http://www.w3.org/2000/01/rdf-schema#label> ?measurement. "
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit."
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#value> ?value." 
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."								
							+"?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId."
							
							+"{"
								+"select ?sensorId "
								+"from <"+openiotMetaGraph+"> "
								+"WHERE "
								+"{"
								
								+"?type <http://www.w3.org/2000/01/rdf-schema#label> '"+sensorType+"' ."
								+"?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "
								
								+"FILTER EXISTS {?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. }" 
								+"?p geo:geometry ?geo. "
								+"filter (<bif:st_intersects>(?geo,<bif:st_point>("+longitude+","+latitude+"),"+radius+")). "
								+"}"
							+"}"
							
							+"}group by (?measurement)(?unit) ");
			
			update.append(str);
			return update.toString();
		}		
	}
	
	final static Logger logger = LoggerFactory.getLogger(DiscoverSensorsImpl.class);
		
	private String userID;
	private double longitude;
	private double latitude;
	private float radius;
	
	private SensorTypes sensorTypes = null;
	
	public DiscoverSensorsImpl(String userID, double longitude, double latitude, float radius) 
	{
		this.userID = userID;
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;
		
		logger.debug("Recieved Parameters: " +
				"userID=" + userID + 
				", longitude=" + longitude + 
				", latitude=" + latitude + 
				", radius=" + radius);				

		discoversensors();
	}
	
		
	/**
	 * @return Returns the SensorTypes, within the given area defined by the 
	 * lon,lat and rad parameters 
	 */
	public SensorTypes getSensorTypes() 
	{
		return sensorTypes;
	}
	
	//helper methods
	private void discoversensors() 
	{
		sensorTypes = new SensorTypes();

		SesameSPARQLClient sparqlCl = null;
		try {
			sparqlCl = new SesameSPARQLClient();
		} catch (RepositoryException e) {			
			logger.error("Init sparql repository error. Returning an empty SensorTypes object. ",e);
			return;
		}
		
		TupleQueryResult qres = sparqlCl.sparqlToQResult(Queries.getSensTypeInArea(longitude, latitude, radius));
		List<SensorTypeData> sensorTypesList = Queries.parseSensorTypeInArea(qres);
	

		for (int i=0; i<sensorTypesList.size(); i++) 
		{
			SensorType sensorType = new SensorType();			
			sensorType.setId(sensorTypesList.get(i).getID());
			sensorType.setName(sensorTypesList.get(i).getLabel());	
			
			qres = sparqlCl.sparqlToQResult(Queries.getMDataOfSensorTypeInArea(longitude, latitude, radius,sensorTypesList.get(i).getLabel()));
			List<SensorTypeMetaData> fullMetaData = Queries.parseSensorTypeMetaData(qres);
			
			for (int j=0; j<fullMetaData.size(); j++) 
			{
				MeasurementCapability mc = new MeasurementCapability();				
				mc.setType(fullMetaData.get(j).getMeasuredVal());
				
				Unit unit = new Unit();
				unit.setName(fullMetaData.get(j).getUnit());
				unit.setType(fullMetaData.get(j).getValue());
				
				mc.getUnit().add(unit);
				
				sensorType.getMeasurementCapability().add(mc);
			}
			
			sensorTypes.getSensorType().add(sensorType);
		}//for
	}
}//class
