package org.openiot.scheduler.core.utils.lsmpa.entities2;

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

import org.openiot.lsm.schema.LSMSchema;

public class WidgetEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;

	private Individual widgetClassIdv;
	private OntClass ontClsWidget;
	private OntProperty ontPwidgetHasPresentationAttr;
	private OntProperty ontPwidgetOfRequestPresentation;
	
	
	private String id;
	private ArrayList<PresentationAttrEnt2> presentationAttrEntList = new ArrayList<PresentationAttrEnt2>();
	private ReqPresentationEnt2 reqPresentationEnt;
	

	
	public WidgetEnt2(ReqPresentationEnt2 reqPresentationEnt )
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_Widget();
		
		setReqPresentationEnt2(reqPresentationEnt);
	}
	public WidgetEnt2(String filePath , ReqPresentationEnt2 reqPresentationEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_Widget();
		
		setReqPresentationEnt2(reqPresentationEnt);
	}
	
	private void initOnt_Widget()
	{
		ontClsWidget = ontTemplate.createClass("http://openiot.eu/ontology/ns/WIDGET");
		ontPwidgetHasPresentationAttr = ontTemplate.createProperty("http://openiot.eu/ontology/ns/widgetHasPresAttr");
		ontPwidgetOfRequestPresentation = ontTemplate.createProperty("http://openiot.eu/ontology/ns/widgetOfRequestPresentation");
	}
	
	////
	public Individual getClassIndividual()
	{
		return widgetClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(getId()==null)
			widgetClassIdv = ontInstance.createIndividual(ontClsWidget);
		else
			widgetClassIdv = ontInstance.createIndividual(getId(),ontClsWidget);
	}
	public void createPwidgetHasPresAttr()
	{
		for(int i=0; i<presentationAttrEntList.size(); i++)
		{
			widgetClassIdv.addProperty(ontPwidgetHasPresentationAttr, presentationAttrEntList.get(i).getClassIndividual());
		}
	}
	public void createPwidgetHasPresAttrAsString()
	{
		for(int i=0; i<presentationAttrEntList.size(); i++)
		{
			widgetClassIdv.addProperty(ontPwidgetHasPresentationAttr, presentationAttrEntList.get(i).toStringValName());
		}
	}
	public void createPpresentationAttrName()
	{
		if(reqPresentationEnt!=null)	
			widgetClassIdv.addProperty(ontPwidgetOfRequestPresentation, reqPresentationEnt.getClassIndividual());
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String toStringIdPreAttr() {
		
		StringBuffer widget = new StringBuffer();
		widget.append("\"widget\":");
		widget.append("{");
		widget.append("\"id\":"+getId());widget.append(",");
		
			widget.append("\"preAttrs\":");
			widget.append("{");
			for(PresentationAttrEnt2 preAttrr : presentationAttrEntList) {
				widget.append(preAttrr.toStringValName());widget.append(",");
			}
			
			int lastIdx = widget.lastIndexOf(",");
			widget.deleteCharAt(lastIdx);
			
			widget.append("}");
			
		widget.append("}");
		
		return widget.toString();
	}
	
	public ArrayList<PresentationAttrEnt2> getPresentationAttrEntList() {
		return presentationAttrEntList;
	}
	public void setPresentationAttrEntList(
			ArrayList<PresentationAttrEnt2> presentationAttrEntList) {
		this.presentationAttrEntList = presentationAttrEntList;
	}
	public ReqPresentationEnt2 getReqPresentationEnt() {
		return reqPresentationEnt;
	}
	public void setReqPresentationEnt2(ReqPresentationEnt2 reqPresentationEnt) {
		this.reqPresentationEnt = reqPresentationEnt;
	}
}
