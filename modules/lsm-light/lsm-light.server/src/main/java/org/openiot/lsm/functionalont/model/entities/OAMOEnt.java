package org.openiot.lsm.functionalont.model.entities;

import java.util.ArrayList;
import java.util.Set;

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

import org.openiot.lsm.functionalont.model.entities.OSDSpecEnt;
import org.openiot.lsm.functionalont.model.entities.OSMOEnt;
import org.openiot.lsm.functionalont.model.beans.OAMOBean;
import org.openiot.lsm.functionalont.model.beans.OSDSpecBean;
import org.openiot.lsm.functionalont.model.beans.OSMOBean;
import org.openiot.lsm.functionalont.model.beans.UserBean;
import org.openiot.lsm.schema.LSMSchema;

public class OAMOEnt 
{
	public static class Queries
	{
		public static ArrayList<OAMOBean> parseOAMO(TupleQueryResult qres)
		{
			ArrayList<OAMOBean> oamoBeanList = new ArrayList<OAMOBean>();
			try 
			{
				while (qres.hasNext())
				{
					BindingSet b = qres.next();
					Set names = b.getBindingNames();
					
					boolean found =false;
					OAMOBean oamoBean = new OAMOBean();
					
					for (Object n : names)
					{						
						if(((String) n).equalsIgnoreCase("oamoID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoBean.setId(str);
							System.out.println("oamoID: "+oamoBean.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoName"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoBean.setName(str);
							System.out.println("oamoName : "+oamoBean.getName()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoDesc"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();							
							oamoBean.setDescription(str);
							System.out.println("oamoDesc : "+oamoBean.getDescription()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoGraphMeta"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							oamoBean.setGraphMeta(str);
							System.out.println("oamoGraphMeta : "+oamoBean.getGraphMeta()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("osmoIDsOfOAMO"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(OAMOBean object : oamoBeanList)
							{
								if(object.getId().equals(oamoBean.getId()))	{
									 //grab already added oamoBean and add  osmo
									object.getOsmoBeanList().add(new OSMOBean(str));
								    found = true;
							    }
							    else {
							    	 //this oamoBean doesnt exist in the list 
							    	oamoBean.getOsmoBeanList().add(new OSMOBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						oamoBeanList.add(oamoBean);					
				}//while
				return oamoBeanList;
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
		
		public static String selectOAMOByOSDspec(String graph,OSDSpecBean osdSpec)
		{
			StringBuilder queryStr = new StringBuilder();
//	        update.append(getNamespaceDeclarations());			
	        
	        queryStr.append("SELECT ?oamoID ?oamoName ?oamoDesc ?oamoGraphMeta ?osmoIDsOfOAMO ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?oamoID rdf:type <http://openiot.eu/ontology/ns/OAMO> . ");
			queryStr.append("?oamoID <http://openiot.eu/ontology/ns/oamoOfOSDSpec> <"+osdSpec.getId()+"> . ");			
			queryStr.append("optional {?oamoID <http://openiot.eu/ontology/ns/oamoName> ?oamoName . }");
			queryStr.append("optional {?oamoID <http://openiot.eu/ontology/ns/oamoDescription> ?oamoDesc . }");
			queryStr.append("optional {?oamoID <http://openiot.eu/ontology/ns/oamoGraphMeta> ?oamoGraphMeta . }");
			queryStr.append("optional {?oamoID <http://openiot.eu/ontology/ns/oamoHasOSMO> ?osmoIDsOfOAMO . }");
			queryStr.append("}");
			
			return queryStr.toString();
		}
		
	}//queries
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual oamoClassIdv;	
	private OntClass ontClsOAMO;
	private OntProperty ontPoamoDescription;
	private OntProperty ontPoamoGraphMeta;
	private OntProperty ontPoamoName;
	private OntProperty ontPoamoOfOSDSpec;
	private OntProperty ontPoamoHasOSMO; 
	
	private OAMOBean oamoBean;
	private OSDSpecEnt osdSpecEnt; 
	private ArrayList<OSMOEnt> osmoList = new ArrayList<OSMOEnt>();
	
	public OAMOEnt(OAMOBean oamoBean,OSDSpecEnt osdSpecEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_Oamo();
		
		setOamoBean(oamoBean);
		setOsdSpecEnt(osdSpecEnt);
	}
	public OAMOEnt(String filePath, OAMOBean oamoBea,OSDSpecEnt osdSpecEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Oamo();
		
		setOamoBean(oamoBean);
		setOsdSpecEnt(osdSpecEnt);
	}

	private void initOnt_Oamo()
	{
		ontClsOAMO = ontTemplate.createClass("http://openiot.eu/ontology/ns/OAMO");
		ontPoamoDescription = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoDescription");
		ontPoamoGraphMeta= ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoGraphMeta");
		ontPoamoName = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoName");
		ontPoamoOfOSDSpec = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoOfOSDSpec");
		ontPoamoHasOSMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/oamoHasOSMO");
	}
	
	////	
	public Individual getClassIndividual()
	{
		return oamoClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(oamoBean.getId()==null)
			oamoClassIdv = ontInstance.createIndividual(ontClsOAMO);
		else
			oamoClassIdv = ontInstance.createIndividual(oamoBean.getId(),ontClsOAMO);
	}
	public void createPoamoDescription()
	{
		if(oamoBean.getDescription()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoDescription, ontInstance.getBase().createTypedLiteral(oamoBean.getDescription()));
	}
	public void createPoamoGraphMeta()
	{
		if(oamoBean.getGraphMeta()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoGraphMeta, ontInstance.getBase().createTypedLiteral(oamoBean.getGraphMeta()));
	}
	public void createPoamoName()
	{
		if(oamoBean.getName()!=null)	
			oamoClassIdv.setPropertyValue(ontPoamoName, ontInstance.getBase().createTypedLiteral(oamoBean.getName()));
	}	
	public void createPoamoOfOSDSpec()
	{
		if(osdSpecEnt!=null)	
			oamoClassIdv.addProperty(ontPoamoOfOSDSpec, osdSpecEnt.getClassIndividual());
	}
	public void createPoamoHasOSMO()
	{
		for(int i=0; i<osmoList.size(); i++)
		{
			oamoClassIdv.addProperty(ontPoamoHasOSMO, osmoList.get(i).getClassIndividual());
		}
	}
	
	
	public OAMOBean getOamoBean() {
		return oamoBean;
	}
	public void setOamoBean(OAMOBean oamoBean) {
		this.oamoBean = oamoBean;
	}

	public OSDSpecEnt getOsdSpecEnt() {
		return osdSpecEnt;
	}
	public void setOsdSpecEnt(OSDSpecEnt osdSpecEnt) {
		this.osdSpecEnt = osdSpecEnt;
	}
	public ArrayList<OSMOEnt> getOsmoList() {
		return osmoList;
	}
	public void setOsmoList(ArrayList<OSMOEnt> osmoList) {
		this.osmoList = osmoList;
	}
}//class