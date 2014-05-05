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

public class QueryControlsEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryControlsClassIdv;
	private OntClass ontClsQueryControls;
	private OntProperty ontPquerycontrolsHasQuerySchedule;
	private OntProperty ontPquerycontrolsTrigger;
	private OntProperty ontPquerycontrolsReportIfEmpty;
	private OntProperty ontPquerycontrolsInitialRecTime;
	private OntProperty ontPquerycontrolsOfOSMO;	

	//bean properties
	private String id;
	private String trigger;
	private Boolean reportIfEmpty;
	private Date initialRecordTime;
	private QueryScheduleEnt2 queryScheduleEnt;
	private OSMOEnt2 osmoEnt;
	
	
	public QueryControlsEnt2(OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryControls();
		
		setOsmoEnt(osmoEnt);
	}
	public QueryControlsEnt2(String filePath,OSMOEnt2 osmoEnt)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QueryControls();
		
		setOsmoEnt(osmoEnt);
	}
	
	private void initOnt_QueryControls()
	{
		ontClsQueryControls = ontTemplate.createClass("http://openiot.eu/ontology/ns/QueryControls");
		ontPquerycontrolsHasQuerySchedule = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsHasQuerySchedule");
		ontPquerycontrolsTrigger = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsTrigger");
		ontPquerycontrolsReportIfEmpty = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsReportIfEmpty");
		ontPquerycontrolsInitialRecTime = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsInitialRecTime");
		ontPquerycontrolsOfOSMO = ontTemplate.createProperty("http://openiot.eu/ontology/ns/querycontrolsOfOSMO");
	}
	
	
	////
	public Individual getClassIndividual()
	{
		return queryControlsClassIdv;
	}
	////
	
	public void createClassIdv()
	{
		if(getId()==null)
			queryControlsClassIdv = ontInstance.createIndividual(ontClsQueryControls);
		else
			queryControlsClassIdv = ontInstance.createIndividual(getId(),ontClsQueryControls);
	}
	public void createPquerycontrolsHasQuerySchedule()
	{
		if(queryScheduleEnt!=null)	
			queryControlsClassIdv.addProperty(ontPquerycontrolsHasQuerySchedule, queryScheduleEnt.getClassIndividual());
	}
	public void createPquerycontrolsTrigger()
	{
		if(getTrigger()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsTrigger, ontInstance.getBase().createTypedLiteral(getTrigger()));
	}
	public void createPquerycontrolsReportIfEmpty()
	{
		if(getReportIfEmpty()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsReportIfEmpty, ontInstance.getBase().createTypedLiteral(getReportIfEmpty().booleanValue()));
	}
	public void createPquerycontrolsInitialRecTime()
	{
		if(getInitialRecordTime()!=null)	
			queryControlsClassIdv.setPropertyValue(ontPquerycontrolsInitialRecTime, ontInstance.getBase().createTypedLiteral(getInitialRecordTime()));
	}
	public void createPquerycontrolsOfOSMO()
	{
		if(osmoEnt!=null)	
			queryControlsClassIdv.addProperty(ontPquerycontrolsOfOSMO, osmoEnt.getClassIndividual());
	}
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public Date getInitialRecordTime() {
		return initialRecordTime;
	}
	public void setInitialRecordTime(Date initialRecordTime) {
		this.initialRecordTime = initialRecordTime;
	}
	public Boolean getReportIfEmpty() {
		return reportIfEmpty;
	}
	public void setReportIfEmpty(Boolean reportIfEmpty) {
		this.reportIfEmpty = reportIfEmpty;
	}
	public String toStringIfEmpty() 
	{
		
		StringBuffer qControl = new StringBuffer();
		qControl.append("\"qControl\":");
		qControl.append("{");

		qControl.append("\"reportIfEmpty\":"+getReportIfEmpty());
			
		qControl.append("}");
		
		return qControl.toString();
	}
	
	
	public OSMOEnt2 getOsmoEnt() {
		return osmoEnt;
	}
	public void setOsmoEnt(OSMOEnt2 osmoEnt) {
		this.osmoEnt = osmoEnt;
	}
	public QueryScheduleEnt2 getQueryScheduleEnt() {
		return queryScheduleEnt;
	}
	public void setQueryScheduleEnt(QueryScheduleEnt2 queryScheduleEnt) {
		this.queryScheduleEnt = queryScheduleEnt;
	}
}
