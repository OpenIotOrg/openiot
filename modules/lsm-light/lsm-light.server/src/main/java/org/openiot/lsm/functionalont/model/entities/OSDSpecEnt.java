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

import org.openiot.lsm.functionalont.model.entities.OAMOEnt;
import org.openiot.lsm.functionalont.model.entities.UserEnt;
import org.openiot.lsm.functionalont.model.beans.OAMOBean;
import org.openiot.lsm.functionalont.model.beans.OSDSpecBean;
import org.openiot.lsm.functionalont.model.beans.UserBean;
import org.openiot.lsm.schema.LSMSchema;

public class OSDSpecEnt 
{
	public static class Queries
	{
		public static ArrayList<OSDSpecBean> parseUserData(TupleQueryResult qres)
		{
			ArrayList<OSDSpecBean> osdSpecList = new ArrayList<OSDSpecBean>();
			try 
			{
				while (qres.hasNext()) // iterates over all the tuples
				{
					BindingSet b = qres.next();
					Set<String> names = b.getBindingNames();
					
					boolean found =false;
					OSDSpecBean osdSpec = new OSDSpecBean();
					
					for (Object n : names) // iterates over a tuple's elements
					{						
						if(((String) n).equalsIgnoreCase("osdSepcID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							osdSpec.setId(str);
							System.out.println("user id: "+osdSpec.getId()+" ");	
						}
						else if(((String) n).equalsIgnoreCase("oamoID"))
						{
							String str = (b.getValue((String) n)==null) ? null : b.getValue((String) n).stringValue();
							
							for(OSDSpecBean object : osdSpecList)
							{
								if(object.getId().equals(osdSpec.getId()))	{
									 //grab already added osdpec and add oamo 
									object.getOamoBeanList().add(new OAMOBean(str));
								    found = true;
							    }
							    else {
							    	 //this spec doesnt exist in the list 
									osdSpec.getOamoBeanList().add(new OAMOBean(str));
							        found = false;
							    }
							}
						}
					}//for
					
					if(!found)
						osdSpecList.add(osdSpec);					
				}//while
				return osdSpecList;
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
		
//		private static String graph = "http://lsm.deri.ie/OpenIoT/testSchema#";
		
//		private static String getNamespaceDeclarations() 
//		{
//	        StringBuilder declarations = new StringBuilder();
//	        declarations.append("PREFIX : <" + "http://openiot.eu/ontology/ns/" + "> \n");
//	        //declarations.append("PREFIX spt: <" + "http://spitfire-project.eu/ontology/ns/" + "> \n");
//	        declarations.append("PREFIX rdf: <" + RDF.getURI() + "> \n");//http://www.w3.org/1999/02/22-rdf-syntax-ns#
//	        declarations.append("PREFIX rdfs: <" + RDFS.getURI() + "> \n");//http://www.w3.org/2000/01/rdf-schema#
//	        declarations.append("PREFIX xsd: <" + XSD.getURI() + "> \n");
//	        //declarations.append("PREFIX owl: <" + OWL.getURI() + "> \n");
//	        //declarations.append("PREFIX ssn: <" + "http://purl.oclc.org/NET/ssnx/ssn#" + "> \n");
//	        //declarations.append("PREFIX dul: <" + "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#" + "> \n");
//	        //declarations.append("PREFIX oiot: <" + "http://openiot.eu/ontology/ns/" + "> \n");
//	        //declarations.append("base oiot: <" + "http://openiot.eu/ontology/ns/clouddb" + "> \n");	       
//	        declarations.append("\n");
//	        
//	        return declarations.toString();
//	    }
		
		public static String selectOSDSpecByUser(String graph,UserBean user)
		{
			StringBuilder queryStr = new StringBuilder();
	        //queryStr.append(getNamespaceDeclarations());
			
			queryStr.append("SELECT ?osdSepcID ?oamoID ");
			queryStr.append("from <"+graph+"> ");
			queryStr.append("WHERE ");
			queryStr.append("{");
			queryStr.append("?osdSepcID rdf:type <http://openiot.eu/ontology/ns/OSDSPEC> . ");								
			queryStr.append("?osdSepcID <http://openiot.eu/ontology/ns/osdspecOfUser> <"+user.getId()+"> ");
			queryStr.append("optional { ?osdSepcID <http://openiot.eu/ontology/ns/osdpsecHasOamo> ?oamoID. }");
			queryStr.append("}");								
			
			return queryStr.toString();
		}
	}
	
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual osdspecClassIdv;
	private OntClass ontClsOSDSpec;	
	private OntProperty ontPosdpsecOfUser;
	private OntProperty ontPosdpsecHasOamo;
	
	private OSDSpecBean osdSpecBean;
	private UserEnt userEnt;
	private ArrayList<OAMOEnt> oamoEntList = new ArrayList<OAMOEnt>();
	

	
	public OSDSpecEnt(OSDSpecBean osdSpecBean,UserEnt userEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_OSDSpecEnt();
		
		setOSDSpecBean(osdSpecBean);
		setUserEnt(userEnt);
	}
	public OSDSpecEnt(String filePath,OSDSpecBean osdSpecBean,UserEnt userEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_OSDSpecEnt();
		
		setOSDSpecBean(osdSpecBean);
		setUserEnt(userEnt);
	}
	
	private void initOnt_OSDSpecEnt()
	{
		ontClsOSDSpec = ontTemplate.createClass("http://openiot.eu/ontology/ns/OSDSPEC");
		ontPosdpsecOfUser = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osdspecOfUser");
		ontPosdpsecHasOamo = ontTemplate.createProperty("http://openiot.eu/ontology/ns/osdpsecHasOamo");
	}
	
	////
	public Individual getClassIndividual()
	{
		return osdspecClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(osdSpecBean.getId()==null)
			osdspecClassIdv = ontInstance.createIndividual(ontClsOSDSpec);
		else
			osdspecClassIdv = ontInstance.createIndividual(osdSpecBean.getId(),ontClsOSDSpec);
	}
	public void createPosdpsecOfUser()
	{
		if(userEnt!=null)	
			osdspecClassIdv.addProperty(ontPosdpsecOfUser, userEnt.getClassIndividual());
	}
	public void createPosdpsecHasOamo()
	{
		for(int i=0; i<oamoEntList.size(); i++)
		{
			osdspecClassIdv.addProperty(ontPosdpsecHasOamo, oamoEntList.get(i).getClassIndividual());
		}
	}
	

	
	
	public OSDSpecBean getOsdSpecBean() {
		return osdSpecBean;
	}
	public void setOSDSpecBean(OSDSpecBean osdSpecBean) {
		this.osdSpecBean = osdSpecBean;
	}
	

	public UserEnt getUserEnt() {
		return userEnt;
	}
	public void setUserEnt(UserEnt userEnt) {
		this.userEnt = userEnt;
	}
	public ArrayList<OAMOEnt> getOamoEntList() {
		return oamoEntList;
	}

}//class
