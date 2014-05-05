package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openiot.lsm.functionalont.model.entities.DynamicAttrMaxValueEnt;
import org.openiot.lsm.functionalont.model.entities.OAMOEnt;
import org.openiot.lsm.functionalont.model.entities.QueryControlsEnt;
import org.openiot.lsm.functionalont.model.entities.QueryRequestEnt;
import org.openiot.lsm.functionalont.model.entities.ReqPresentationEnt;
import org.openiot.lsm.functionalont.model.beans.DynamicAttrMaxValueBean;
import org.openiot.lsm.functionalont.model.beans.OAMOBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.QueryControlsBean;
import org.openiot.lsm.functionalont.model.beans.QueryRequestBean;
import org.openiot.lsm.functionalont.model.beans.ReqPresentationBean;
import org.openiot.lsm.schema.LSMSchema;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OSMOEnt 
{
	public static class Queries
	{
		public static ArrayList<OSMOBean> parseOSMO(TupleQueryResult qres)
		{
			ArrayList<OSMOBean> osmoBeanList = new ArrayList<OSMOBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					boolean found =false;
					OSMOBean osmoBean = new OSMOBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("osmoID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoBean.setId(str);
							System.out.println("osmoID: "+osmoBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("osmoName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoBean.setName(str);
							System.out.println("oamoName : "+osmoBean.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("osmoDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							osmoBean.setDescription(str);
							System.out.println("oamoDesc : "+osmoBean.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryControls"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoBean.setQueryControlsBean(new QueryControlsBean(str));
							System.out.println("queryControls : "+osmoBean.getQueryControlsBean().getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("reqPresentation"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osmoBean.setReqPresentationBean(new ReqPresentationBean(str));
							System.out.println("reqPresentation : "+osmoBean.getReqPresentationBean().getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("queryRequest")) //TODO: check if it will work
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(OSMOBean object : osmoBeanList)
							{
								if(object.getId().equals(osmoBean.getId()))	{
									 //grab already added osmoBean and add  queryrequest
									object.getQueryRequestBean().add(new QueryRequestBean(str));
								    found = true;
							    }
							    else {
							    	 //this osmoBean exist in the list 
							    	osmoBean.getQueryRequestBean().add(new QueryRequestBean(str));
							        found = false;
							    }
							}
						}
						else if(((String) n).equalsIgnoreCase("dynamicAttrMaxValue")) //TODO: check if it will work
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(OSMOBean object : osmoBeanList)
							{
								if(object.getId().equals(osmoBean.getId()))	{
									 //grab already added osmoBean and add  dynamicAttrMaxValue
									object.getDynamicAttrMaxValueBeanList().add(new DynamicAttrMaxValueBean(str));
								    found = true;
							    }
							    else {
							    	 //this osmoBean exist in the list 
							    	osmoBean.getDynamicAttrMaxValueBeanList().add(new DynamicAttrMaxValueBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						osmoBeanList.add(osmoBean);					
				}//while
				return osmoBeanList;
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
		
		private static String getNamespaceDeclarations()
		{
	        StringBuilder declarations = new StringBuilder();
	        declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("PREFIX spt: <" + "http://spitfire-project.eu/ontology/ns/" + "> \n");
	        declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");//http://www.w3.org/1999/02/22-rdf-syntax-ns#
	        declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");//http://www.w3.org/2000/01/rdf-schema#
	        declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
	        //declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
	        //declarations.append("PREFIX ssn: <" + "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
	        //declarations.append("PREFIX dul: <" + "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" + "> \n");
	        //declarations.append("PREFIX oiot: <" + "http://openiot.eu/ontology/ns/" + "> \n");
	        //declarations.append("base oiot: <" + "http://openiot.eu/ontology/ns/clouddb" + "> \n");	       
	        declarations.append("\n");
	        
	        return declarations.toString();
	    }
		
		public static String selectOSMOByOAMO(String graph,OAMOBean oamoBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
	        queryStr.append("SELECT ?osmoID ?osmoName ?osmoDesc ?queryControls ?reqPresentation ?queryRequest ?dynamicAttrMaxValue ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?osmoID rdf:type <http://openiot.eu/ontology/ns/OSMO> . ");
			queryStr.append("?osmoID <http://openiot.eu/ontology/ns/osmoOfOAMO> <"+oamoBean.getId()+"> . "); 
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoHasQueryControls> ?queryControls . }"); 
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoHasRequestpresentation> ?reqPresentation . }"); 
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoHasQueryRequest> ?queryRequest . }"); //may return many query requests
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoHasDynamicAttrMaxValue> ?dynamicAttrMaxValue . }"); // may return many dynamic
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoName> ?osmoName . }");
			queryStr.append("optional {?osmoID <http://openiot.eu/ontology/ns/osmoDescription> ?osmoDesc . }");
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}//queries
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual osmoClassIdv;
	private OntClass ontClsOSMO;	
	private OntProperty ontPosmoHasQueryControls;
	private OntProperty ontPosmoHasRequestPresentation;
	private OntProperty ontPosmoHasQueryRequest;
	private OntProperty ontPosmoHasDynamicAttrMaxValue;
	private OntProperty ontPosmoDescription;
	private OntProperty ontPosmoName;		
	private OntProperty ontPosmoOfOAMO;//not in osdspec

	private OSMOBean osmoBean;
	private QueryControlsEnt queryControlsEnt;
	private ReqPresentationEnt reqPresentationEnt;	
	private ArrayList<QueryRequestEnt> queryRequestEntList = new ArrayList<QueryRequestEnt>();
	private ArrayList<DynamicAttrMaxValueEnt> dynamicAttrMaxValueEntList;
	private OAMOEnt oamoEnt;//not in osdspec

	
	//constructor
	public OSMOEnt(OSMOBean osmoBean, OAMOEnt oamoEnt )
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
	
		initOnt_Osmo();
		

		setOsmoBean(osmoBean);
		setOamoEnt(oamoEnt);
	}
	public OSMOEnt(String filePath,OSMOBean osmoBean, OAMOEnt oamoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Osmo();
		
		setOsmoBean(osmoBean);
		setOamoEnt(oamoEnt);
	}
	
		
	private void initOnt_Osmo()
	{
		ontClsOSMO = ontTemplate.createClass("http://openiot.eu/ontology/ns/OSMO");
		ontPosmoHasQueryControls = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasQueryControls");
		ontPosmoHasRequestPresentation= ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasRequestpresentation");
		ontPosmoHasQueryRequest = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasQueryRequest");
		ontPosmoHasDynamicAttrMaxValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoHasDynamicAttrMaxValue");
		ontPosmoDescription = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoDescription");
		ontPosmoName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoName");
		ontPosmoOfOAMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osmoOfOAMO");
	}
	
	////
	public Individual getClassIndividual()
	{
		return osmoClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(osmoBean.getId()==null)
			osmoClassIdv = ontInstance.createIndividual(ontClsOSMO);
		else
			osmoClassIdv = ontInstance.createIndividual(osmoBean.getId(),ontClsOSMO);
	}
	public void createPosmoHasQueryControls()
	{
		if(queryControlsEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasQueryControls, queryControlsEnt.getClassIndividual());
	}
	public void createPosmoHasQueryControlsAsString()
	{
		if(queryControlsEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasQueryControls, ontInstance.getBase().createTypedLiteral(queryControlsEnt.getQueryControlsBean().toStringIfEmpty()));
	}
	public void createPosmoHasRequestpresentation()
	{
		if(reqPresentationEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasRequestPresentation, reqPresentationEnt.getClassIndividual());
	}
	public void createPosmoHasRequestpresentationAsString()
	{
		if(reqPresentationEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoHasRequestPresentation, ontInstance.getBase().createTypedLiteral(reqPresentationEnt.getReqPresentationBean().toStringWidget()));
	}
	public void createPosmoHasQueryRequest()
	{
//		for(int i=0; i<queryRequestEntList.size(); i++)
//		{
			osmoClassIdv.addProperty(ontPosmoHasQueryRequest, queryRequestEntList.get(0).getClassIndividual());
//		}
	}
	public void createPosmoHasQueryRequestAsString()
	{
//		for(int i=0; i<queryRequestEntList.size(); i++)
//		{
			osmoClassIdv.addProperty(ontPosmoHasQueryRequest, ontInstance.getBase().createTypedLiteral(queryRequestEntList.get(0).getQReqBean().toStringQueryStr()));
//		}
	}
	public void createPosmoHasDynamicAttrMaxValue()
	{
		for(int i=0; i<dynamicAttrMaxValueEntList.size(); i++)
		{
			osmoClassIdv.addProperty(ontPosmoHasDynamicAttrMaxValue, dynamicAttrMaxValueEntList.get(i).getClassIndividual());
		}
	}
	public void createPosmoDescription()
	{
		if(osmoBean.getDescription()!=null)	
			osmoClassIdv.setPropertyValue(ontPosmoDescription, ontInstance.getBase().createTypedLiteral(osmoBean.getDescription()));
	}
	public void createPosmoName()
	{
		if(osmoBean.getName()!=null)	
			osmoClassIdv.setPropertyValue(ontPosmoName, ontInstance.getBase().createTypedLiteral(osmoBean.getName()));
	}
	public void createPosmoOfOAMO()
	{
		if(oamoEnt!=null)	
			osmoClassIdv.addProperty(ontPosmoOfOAMO, oamoEnt.getClassIndividual());
	}

	
	
	
	public OSMOBean getOsmoBean() {
		return osmoBean;
	}
	public void setOsmoBean(OSMOBean osmoBean) {
		this.osmoBean = osmoBean;
	}
	
	
	
	
	public ReqPresentationEnt getReqPresentationEnt() {
		return reqPresentationEnt;
	}
	public void setReqPresentationEnt(ReqPresentationEnt reqPresentationEnt) {
		this.reqPresentationEnt = reqPresentationEnt;
	}
	public ArrayList<QueryRequestEnt> getQueryRequestEntList() {
		return queryRequestEntList;
	}
	public void setQueryRequestEntList(	ArrayList<QueryRequestEnt> queryRequestEntList) {
		this.queryRequestEntList = queryRequestEntList;
	}
	public ArrayList<DynamicAttrMaxValueEnt> getDynamicAttrMaxValueEntList() {
		return dynamicAttrMaxValueEntList;
	}
	public void setDynamicAttrMaxValueEntList(ArrayList<DynamicAttrMaxValueEnt> dynamicAttrMaxValueEntList) {
		this.dynamicAttrMaxValueEntList = dynamicAttrMaxValueEntList;
	}
	public OAMOEnt getOamoEnt() {
		return oamoEnt;
	}
	public void setOamoEnt(OAMOEnt oamoEnt) {
		this.oamoEnt = oamoEnt;
	}
	public QueryControlsEnt getQueryControlsEnt() {
		return queryControlsEnt;
	}
	public void setQueryControlsEnt(QueryControlsEnt queryControlsEnt) {
		this.queryControlsEnt = queryControlsEnt;
	}
}
