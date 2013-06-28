package org.openiot.scheduler.core.api.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 * 
 */
public class DiscoverSensorsImpl 
{	
	private static class QueriesV2
	{
		private static String openiotMetaGraph = "http://lsm.deri.ie/OpenIoT/sensormeta#";
		private static String openiotDataGraph = "http://lsm.deri.ie/OpenIoT/sensordata#";
		
		public static ArrayList<FullMeta> parseSensorFullMeta(TupleQueryResult qres)
		{
			ArrayList<FullMeta> fullMetas = new ArrayList<FullMeta>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					FullMeta fm = new FullMeta();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("label"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							fm.setMeasuredVal(str);
							System.out.print("label : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("unit"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							fm.setUnit(str);
							System.out.print("unit : "+str+" ");	
						}
						else if(((String) n).equalsIgnoreCase("avalue"))
						{
							//String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							String st = b.getValue((String) n).toString();
							String[] split=st.split("#");
							
							fm.setValue(split[1].substring(0, split[1].length()-1));
							System.out.print("value : "+fm.getValue()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("sensLabelType"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							fm.setSensorType(str);
							System.out.print("sensor type : "+str+" ");	
						}
					}
					fullMetas.add(fm);					
				}//while
				return fullMetas;
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
		public static ArrayList<String> parseLabelTypeInArea(TupleQueryResult qres)
		{
			ArrayList<String> sensorTypes = new ArrayList<String>();
			try
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					String sensorType = null;
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("sensLabelType"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							sensorType =str;
							System.out.print("sensor id: "+sensorType+" ");	
						}						
					}
					sensorTypes.add(sensorType);					
				}//while
				return sensorTypes;
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
		
		public static String getDataFromSensorsInArea(double longitude, double latitude, float radius)
		{
			StringBuilder update = new StringBuilder();	        
			
			String str=("SELECT ?label ?unit (AVG(?value) AS ?avalue) ?sensLabelType "
							+"from <"+openiotDataGraph+"> "
							+"WHERE "
							+"{"								
							
							+"?prob <http://www.w3.org/2000/01/rdf-schema#label> ?label. "
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit."
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#value> ?value." 
							+"?prob <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."								
							+"?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensorId."
							
							+"{"
								+"select ?sensorId ?sensLabelType "
								+"from <"+openiotMetaGraph+"> "
								+"WHERE "
								+"{"
								
								+"?type <http://www.w3.org/2000/01/rdf-schema#label> ?sensLabelType."
								+"?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."
								
								+"?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p." 
								+"?p geo:geometry ?geo. "								
								+"?p geo:lat ?lat. "
								+"?p geo:long ?long. "
								+"filter (<bif:st_intersects>(?geo,<bif:st_point>("+longitude+","+latitude+"),"+radius+")). "
								+"}"
							+"}"
							
							+"}group by (?label)(?unit)(?sensLabelType)");
			
			update.append(str);
			return update.toString();
		}
		public static String getSensLabelTypeInArea(double longitude, double latitude, float radius)
		{
			StringBuilder update = new StringBuilder();			
			
			String str=("select distinct(?sensLabelType) "
								+"from <"+openiotMetaGraph+"> "
								+"WHERE "
								+"{"								
								
								+"?type <http://www.w3.org/2000/01/rdf-schema#label> ?sensLabelType."
								+"?sensorId <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."
								+"?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p."
								
								+"?p geo:geometry ?geo."
								+"?p geo:lat ?lat."
								+"?p geo:long ?long."
								+"filter (<bif:st_intersects>(?geo,<bif:st_point>("+longitude+","+latitude+"),"+radius+"))."
								
								+"}");
			
			update.append(str);
			return update.toString();
		}
	}
	
//	private static class Queries
//	{		
//		private static String openiotMetaGraph = "http://lsm.deri.ie/OpenIoT/sensormeta#";
//		private static String openiotDataGraph = "http://lsm.deri.ie/OpenIoT/sensordata#";
//		
//		public static ArrayList<String> parseSensorsInArea(TupleQueryResult qres)
//		{
//			ArrayList<String> sensors = new ArrayList<String>();
//			try 
//			{
//				while (qres.hasNext())
//				{
//					BindingSet b = qres.next();
//					Set names = b.getBindingNames();
//					String sensor = null;
//					
//					for (Object n : names)
//					{						
//						if(((String) n).equalsIgnoreCase("sensorId"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							sensor =str;
//							System.out.print("sensor id: "+sensor+" ");	
//						}						
//					}
//					sensors.add(sensor);					
//				}//while
//				return sensors;
//			} 
//			catch (QueryEvaluationException e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//			catch (Exception e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//		}
//		public static String parseSensorMeta(TupleQueryResult qres)
//		{
//			//ArrayList<FullMeta> fullMetas = new ArrayList<FullMeta>();
//			try 
//			{
////				while (qres.hasNext())
////				{
//					BindingSet b = qres.next();
//					Set names = b.getBindingNames();
//					String sensorType = null;
//					
//					for (Object n : names)
//					{						
//						if(((String) n).equalsIgnoreCase("label"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							sensorType = str;
//							System.out.print("label : "+str+" ");	
//						}						
//					}
//					//fullMetas.add(fm);					
////				}//while
//				return sensorType;
//			} 
//			catch (QueryEvaluationException e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//			catch (Exception e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//		}
//		public static FullMeta parseSensorFullMeta(TupleQueryResult qres)
//		{
//			//ArrayList<FullMeta> fullMetas = new ArrayList<FullMeta>();
//			try 
//			{
////				while (qres.hasNext())
////				{
//					BindingSet b = qres.next();
//					Set names = b.getBindingNames();
//					FullMeta fm = new FullMeta();
//					
//					for (Object n : names)
//					{						
//						if(((String) n).equalsIgnoreCase("label"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							fm.setMeasuredVal(str);
//							System.out.print("label : "+str+" ");	
//						}
//						else if(((String) n).equalsIgnoreCase("unit"))
//						{
//							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							fm.setUnit(str);
//							System.out.print("unit : "+str+" ");	
//						}
//						else if(((String) n).equalsIgnoreCase("value"))
//						{
//							//String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
//							String st = b.getValue((String) n).toString();
//							String[] split=st.split("#");
//							
//							fm.setValue(split[1].substring(0, split[1].length()-1));
//							System.out.print("value : "+fm.getValue()+" ");	
//						}
//					}
//					//fullMetas.add(fm);					
////				}//while
//				return fm;
//			} 
//			catch (QueryEvaluationException e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//			catch (Exception e)			
//			{				
//				e.printStackTrace();
//				return null;
//			}
//		}
//		
//		public static String getSensorsInArea(double longitude, double latitude, float radius)
//		{
//			StringBuilder update = new StringBuilder();	        
//			
//			String str=("select distinct(?sensorId) "
//								+"from <"+openiotMetaGraph+"> "
//								+"WHERE "
//								+"{"								
//								+"?sensorId <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?p. "
//								+"?p geo:geometry ?geo."
//								+"?p geo:lat      ?lat." 
//								+"?p geo:long     ?long."
//								+"filter (<bif:st_intersects>(?geo,<bif:st_point>("+longitude+","+latitude+"),"+radius+"))."
//								+"}");
//			
//			update.append(str);
//			return update.toString();
//		}
//		public static String getSensorMeta_Type(String sensorID)
//		{
//			StringBuilder update = new StringBuilder();			
//			
//			String str=("select ?label "
//								+"from <"+openiotMetaGraph+"> "
//								+"WHERE "
//								+"{"								
//								+"?type <http://www.w3.org/2000/01/rdf-schema#label> ?label."
//								+"<"+sensorID+"> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type. "
//								+"}");
//			
//			update.append(str);
//			return update.toString();
//		}
//		public static String getSensorFullMeta(String sensorID)
//		{
//			StringBuilder update = new StringBuilder();	        
//			
//			String str=("select ?label ?unit ?value "
//								+"from <"+openiotDataGraph+"> "
//								+"WHERE "
//								+"{"								
//								+"?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorID+">. "
//								+"?prob <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."
//								+"?prob <http://www.w3.org/2000/01/rdf-schema#label> ?label."								
//								+"?prob <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit."
//								+"?prob <http://lsm.deri.ie/ont/lsm.owl#value> ?value."
//								+"}limit 1");
//			
//			update.append(str);
//			return update.toString();
//		}
//	}
	
	private String userID;
	private double longitude;
	private double latitude;
	private float radius;
	
	private SensorTypes sensorTypes = null;

	final static Logger logger = LoggerFactory.getLogger(DiscoverSensorsImpl.class);

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

		discoversensorsV2();
	}
	
		
	/**
	 * @return Returns the SensorTypes, within the given area defined by the lon,lat and rad parameters 
	 */
	public SensorTypes getSensorTypes() 
	{
		return sensorTypes;
	}
	
	//helper methods
	private void discoversensorsV2() 
	{
		sensorTypes = new SensorTypes();

		SesameSPARQLClient sparqlCl = new SesameSPARQLClient();
		TupleQueryResult qres = sparqlCl.sparqlToQResult(QueriesV2.getDataFromSensorsInArea(longitude, latitude, radius));
		List<FullMeta> fullMetaData = QueriesV2.parseSensorFullMeta(qres);
		
		qres = sparqlCl.sparqlToQResult(QueriesV2.getSensLabelTypeInArea(longitude, latitude, radius));
		List<String> sensorTypes2 = QueriesV2.parseLabelTypeInArea(qres);

		for (int i=0; i<sensorTypes2.size(); i++) 
		{
			SensorType sensorType = new SensorType();
			sensorType.setId("http://www.w3.org/2000/01/rdf-schema#label");
			sensorType.setName(sensorTypes2.get(i));	
			
			for (int j=0; j<fullMetaData.size(); j++) 
			{
				if(sensorTypes2.get(i).equals(fullMetaData.get(j).getSensorType()))
				{
					MeasurementCapability mc = new MeasurementCapability();
					mc.setId("http://www.w3.org/2000/01/rdf-schema#label");
					mc.setType(fullMetaData.get(j).getMeasuredVal());
					
					Unit unit = new Unit();
					unit.setName(fullMetaData.get(j).getUnit());
					unit.setType(fullMetaData.get(j).getValue());
					
					mc.getUnit().add(unit);
					
					sensorType.getMeasurementCapability().add(mc);
				}//if
			}//for
			
			sensorTypes.getSensorType().add(sensorType);
		}//for
	}
}//class
