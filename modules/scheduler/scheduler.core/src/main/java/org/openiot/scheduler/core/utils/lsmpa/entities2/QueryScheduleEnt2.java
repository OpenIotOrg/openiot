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

public class QueryScheduleEnt2 
{
	private LSMSchema  ontTemplate;	
	private LSMSchema  ontInstance;
	
	private Individual queryScheduleClassIdv;
	private OntClass ontClsQuerySchedule;
	private OntProperty ontPqueryscheduleSecond;
	private OntProperty ontPqueryscheduleMinute;
	private OntProperty ontPqueryscheduleHour;		
	private OntProperty ontPqueryscheduleDayOfMonth;
	private OntProperty ontPqueryscheduleMonth;
	private OntProperty ontPqueryscheduleDayOfWeek;
	private OntProperty ontPqueryscheduleOfQueryControls;
	
	//bean properties
	private String id;
	private String second;
	private String minute;
	private String hour;
	private String dayOfMonth;
	private String month;
	private String dayOfWeek;
	private QueryControlsEnt2 queryControlsEnt;
	
	public QueryScheduleEnt2()
	{
		this.ontTemplate=new  LSMSchema();
		this.ontInstance=new  LSMSchema();
		
		initOnt_QuerySchedule();
	}
	public QueryScheduleEnt2(String filePath)
	{
		this.ontTemplate=new  LSMSchema(filePath, OntModelSpec.OWL_DL_MEM,"TURTLE");
		this.ontInstance=new  LSMSchema();
		
		initOnt_QuerySchedule();		
	}
	
	private void initOnt_QuerySchedule()
	{
		ontClsQuerySchedule = ontTemplate.createClass("http://openiot.eu/ontology/ns/QuerySchedule");
		ontPqueryscheduleSecond = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleSecond");
		ontPqueryscheduleMinute = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleMinute");
		ontPqueryscheduleDayOfMonth = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleDayOfMonth");
		ontPqueryscheduleMonth = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleMonth");
		ontPqueryscheduleDayOfWeek = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleDayOfWeek");
		ontPqueryscheduleOfQueryControls = ontTemplate.createProperty("http://openiot.eu/ontology/ns/queryscheduleOfQueryControls");
	}

	
	////
	public Individual getClassIndividual()
	{
		return queryScheduleClassIdv;
	}
	////
	
	
	public void createClassIdv()
	{
		if(getId()==null)
			queryScheduleClassIdv = ontInstance.createIndividual(ontClsQuerySchedule);
		else
			queryScheduleClassIdv = ontInstance.createIndividual(getId(),ontClsQuerySchedule);
	}
	public void createPqueryscheduleSecond()
	{
		if(getSecond()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleSecond, ontInstance.getBase().createTypedLiteral(getSecond()));
	}
	public void createPqueryscheduleMinute()
	{
		if(getMinute()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleMinute, ontInstance.getBase().createTypedLiteral(getMinute()));
	}
	public void createPqueryscheduleHour()
	{
		if(getHour()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleHour, ontInstance.getBase().createTypedLiteral(getHour()));
	}	
	public void createPqueryscheduledayOfMonth()
	{
		if(getDayOfMonth()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleDayOfMonth, ontInstance.getBase().createTypedLiteral(getDayOfMonth()));
	}
	public void createPqueryscheduleMonth()
	{
		if(getMonth()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleMonth, ontInstance.getBase().createTypedLiteral(getMonth()));
	}
	public void createPqueryscheduledayOfWeek()
	{
		if(getDayOfWeek()!=null)	
			queryScheduleClassIdv.setPropertyValue(ontPqueryscheduleDayOfWeek, ontInstance.getBase().createTypedLiteral(getDayOfWeek()));
	}
	public void createPqueryscheduleOfQControls()
	{
		if(queryControlsEnt!=null)	
			queryScheduleClassIdv.addProperty(ontPqueryscheduleOfQueryControls,queryControlsEnt.getClassIndividual());
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public OntProperty getOntPqueryscheduleOfQueryControls() {
		return ontPqueryscheduleOfQueryControls;
	}
	public void setOntPqueryscheduleOfQueryControls(
			OntProperty ontPqueryscheduleOfQueryControls) {
		this.ontPqueryscheduleOfQueryControls = ontPqueryscheduleOfQueryControls;
	}
}
