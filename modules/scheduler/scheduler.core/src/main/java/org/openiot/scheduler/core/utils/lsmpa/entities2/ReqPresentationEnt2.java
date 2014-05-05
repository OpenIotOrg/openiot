package org.openiot.scheduler.core.utils.lsmpa.entities2;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import org.openiot.lsm.schema.LSMSchema;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class ReqPresentationEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual reqPresentationClassIdv;
	private OntClass ontClsReqPresentation;
	private OntProperty ontPreqPresentationHasWidget;
	private OntProperty ontPreqPresentationOfOSMO; //not in spec
	
	//bean properties
	private String id;
	private ArrayList<WidgetEnt2> widgetEntList = new ArrayList<WidgetEnt2>();
	private OSMOEnt2 osmoEnt;
	
	
	public ReqPresentationEnt2(OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_ReqPres();
		
		setOsmoEnt2(osmoEnt);
	}
	public ReqPresentationEnt2(String filePath,OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_ReqPres();
		
		setOsmoEnt2(osmoEnt);
	}
	
	private void initOnt_ReqPres()
	{
		ontClsReqPresentation = ontTemplate.createClass("http://openiot.eu/ontology/ns/ReqPresentation");
		ontPreqPresentationHasWidget = ontTemplate.createProperty("http://openiot.eu/ontology/ns/reqpresentationHasWidget");
		ontPreqPresentationOfOSMO= ontTemplate.createProperty("http://openiot.eu/ontology/ns/reqpresentationOfOSMO");
	}

	
	////
	public Individual getClassIndividual()
	{
		return reqPresentationClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(getId()==null)
			reqPresentationClassIdv = ontInstance.createIndividual(ontClsReqPresentation);
		else
			reqPresentationClassIdv = ontInstance.createIndividual(getId(),ontClsReqPresentation);
	}
	public void createPreqPresentationHasWidget()
	{
		for(int i=0; i<widgetEntList.size(); i++)
		{
			reqPresentationClassIdv.addProperty(ontPreqPresentationHasWidget, widgetEntList.get(i).getClassIndividual());
		}
	}
	public void createPreqPresentationHasWidgetAsString()
	{
		for(int i=0; i<widgetEntList.size(); i++)
		{
			reqPresentationClassIdv.addProperty(ontPreqPresentationHasWidget, widgetEntList.get(i).toStringIdPreAttr());
		}
	}
	public void createPreqPresentationOfOSMO()
	{
		if(osmoEnt!=null)	
			reqPresentationClassIdv.addProperty(ontPreqPresentationOfOSMO, osmoEnt.getClassIndividual());
	}
	
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String toStringWidget() {
		
		StringBuffer reqPre = new StringBuffer();
//		reqPre.append("{");

			reqPre.append("\"reqPre\":");
			reqPre.append("{");
			
			
			for(WidgetEnt2 widget : widgetEntList) {
				reqPre.append(widget.toStringIdPreAttr());reqPre.append(",");
			}
			int lastIdx = reqPre.lastIndexOf(",");
			reqPre.deleteCharAt(lastIdx);
			
			reqPre.append("}");
			
//		reqPre.append("}");
		
		return reqPre.toString();
	}
	
	public ArrayList<WidgetEnt2> getWidgetEntList() {
		return widgetEntList;
	}
	public void setWidgetEntList(ArrayList<WidgetEnt2> widgetEntList) {
		this.widgetEntList = widgetEntList;
	}
	
	public OSMOEnt2 getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt2(OSMOEnt2 osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
	
	
}
