package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Set;

import org.openiot.lsm.functionalont.model.beans.DynamicAttrMaxValueBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.schema.LSMSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class DynamicAttrMaxValueEnt 
{
	public static class Queries
	{
		public static ArrayList<DynamicAttrMaxValueBean> parseOSMO(TupleQueryResult qres)
		{
			ArrayList<DynamicAttrMaxValueBean> dynamicAttrMaxValueBeanList = new ArrayList<DynamicAttrMaxValueBean>();
			
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					DynamicAttrMaxValueBean dynamicAttrMaxValueBean = new DynamicAttrMaxValueBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("dmaxAttrValID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							dynamicAttrMaxValueBean.setId(str);
							System.out.println("dmaxAttrValID: "+dynamicAttrMaxValueBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("dmaxAttrValName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							dynamicAttrMaxValueBean.setName(str);
							System.out.println("dmaxAttrValName : "+dynamicAttrMaxValueBean.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("dmaxAttrValValue"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							dynamicAttrMaxValueBean.setValue(str);
							System.out.println("dmaxAttrValValue : "+dynamicAttrMaxValueBean.getValue()+" ");	
						}
					}//for
					dynamicAttrMaxValueBeanList.add(dynamicAttrMaxValueBean);					
				}//while
				return dynamicAttrMaxValueBeanList;
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
		
		public static String selectDynamicAttrMaxValByOSMO(String graph,OSMOBean osmoBean)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
			queryStr.append("SELECT ?dmaxAttrValID ?dmaxAttrValName ?dmaxAttrValValue ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?dmaxAttrValID rdf:type <http://openiot.eu/ontology/ns/DynamicAttrMaxValue> . ");
			queryStr.append("?dmaxAttrValID <http://openiot.eu/ontology/ns/dynamicattrmaxvalueOfOsmo> <"+osmoBean.getId()+"> . "); 
			queryStr.append("?dmaxAttrValID <http://openiot.eu/ontology/ns/dynamicattrmaxvalueName> ?dmaxAttrValName . "); 
			queryStr.append("?dmaxAttrValID <http://openiot.eu/ontology/ns/dynamicattrmaxvalueValue> ?dmaxAttrValValue . ");
			queryStr.append("}");
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual  dynamicAttrMaxValueClassIdv;
	private OntClass ontClsDynamicAttrMaxValue;
	private OntProperty ontPdynamicattrmaxvalueName;
	private OntProperty ontPdynamicattrmaxvalueValue;
	private OntProperty ontPdynamicattrmaxvalueOfOsmo;
	
	private DynamicAttrMaxValueBean dynamicAttrMaxValueBean;
	private OSMOEnt osmoEnt;// not inspec
	
	public DynamicAttrMaxValueEnt()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_DynamicAttrMaxValue();
	}
	public DynamicAttrMaxValueEnt(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_DynamicAttrMaxValue();
	}
	
	private void initOnt_DynamicAttrMaxValue()
	{
		ontClsDynamicAttrMaxValue = ontTemplate.createClass("http://openiot.eu/ontology/ns/DynamicAttrMaxValue");
		ontPdynamicattrmaxvalueName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueName");
		ontPdynamicattrmaxvalueValue = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueValue");
		ontPdynamicattrmaxvalueOfOsmo = ontTemplate.createProperty("http://openiot.eu/ontology/ns/dynamicattrmaxvalueOfOsmo");
	}
	
	////
	public Individual getClassIndividual()
	{
		return dynamicAttrMaxValueClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(dynamicAttrMaxValueBean.getId()==null)
			dynamicAttrMaxValueClassIdv = ontInstance.createIndividual(ontClsDynamicAttrMaxValue);
		else
			dynamicAttrMaxValueClassIdv = ontInstance.createIndividual(dynamicAttrMaxValueBean.getId(),ontClsDynamicAttrMaxValue);
	}
	public void createPdynamicattrmaxvalueName()
	{
		if(dynamicAttrMaxValueBean.getValue()!=null)	
			dynamicAttrMaxValueClassIdv.setPropertyValue(ontPdynamicattrmaxvalueName, ontInstance.getBase().createTypedLiteral(dynamicAttrMaxValueBean.getValue()));
	}
	public void createPdynamicattrmaxvalueValue()
	{
		if(dynamicAttrMaxValueBean.getName()!=null)	
			dynamicAttrMaxValueClassIdv.setPropertyValue(ontPdynamicattrmaxvalueValue, ontInstance.getBase().createTypedLiteral(dynamicAttrMaxValueBean.getName()));
	}
	public void createPdynamicattrmaxvalueOfOsmo()
	{
		if(osmoEnt!=null)	
			dynamicAttrMaxValueClassIdv.addProperty(ontPdynamicattrmaxvalueOfOsmo, osmoEnt.getClassIndividual());
	}
	
	
	public OSMOEnt getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
}
