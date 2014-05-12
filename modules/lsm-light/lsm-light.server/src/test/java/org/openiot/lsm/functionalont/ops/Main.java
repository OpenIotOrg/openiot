package org.openiot.lsm.functionalont.ops;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.lsm.sdum.model.entities.Utilities;
import org.openiot.lsm.utils.DateUtil;
import org.openiot.lsm.utils.ObsConstant;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;



public class Main 
{
	private static OSDSpec loadFromFile(String osdSpecFilePathName) 
	{				
		OSDSpec osdSpec = null;
		
		//Open and Deserialize OSDSPec form file
		try {
			osdSpec = Utilities.Deserializer.deserializeOSDSpecFile(osdSpecFilePathName);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		
		return osdSpec;		
	}
			
	
	public static void main(String[] args) 
	{
//		OSDSpec osdSpec = loadFromFile("src/test/resources/spec2.xml");
		
//		SchedulerOps.registerService(osdSpec);
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		OntClass ontObservationValue = model.createClass("http://purl.oclc.org/NET/ssnx/ssn#ObservationValue");
		OntClass ontObservation = model.createClass("http://purl.oclc.org/NET/ssnx/ssn#Observation");
		OntClass ontSensor = model.createClass("http://purl.oclc.org/NET/ssnx/ssn#Sensor");

		GregorianCalendar g = new GregorianCalendar();
		g.setTime(new Date());
		
		OntModel idvModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Individual idvObservation = idvModel.createIndividual("http://services.openiot.eu/resource/8a82919d",ontObservation);		
		idvObservation.addProperty(idvModel.createProperty("http://purl.oclc.org/NET/ssnx/ssn#observedBy"),ontSensor.createIndividual("http://services.openiot.eu/resource/8a82919d3264f4ac013264f4e14501c0"));
		idvObservation.addProperty(idvModel.createProperty("http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest"),idvModel.createResource("http://services.openiot.eu/resource/k245345f"));
		idvObservation.addProperty(idvModel.createProperty("http://purl.oclc.org/NET/ssnx/ssn#observationResultTime"),model.createTypedLiteral(new XSDDateTime(g)));
		
		Individual idvObservationValue = idvModel.createIndividual(ontObservationValue);
		idvObservationValue.addProperty(idvModel.createProperty("http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf"), idvObservation);
		idvObservationValue.addProperty(idvModel.createProperty("http://purl.oclc.org/NET/ssnx/ssn#observedProperty"),ObsConstant.TEMPERATURE);
		idvObservationValue.addProperty(idvModel.createProperty("http://lsm.deri.ie/ont/lsm.owl#value"),idvModel.createTypedLiteral(5.13));
		idvObservationValue.addProperty(idvModel.createProperty("http://lsm.deri.ie/ont/lsm.owl#unit"),idvModel.createTypedLiteral("C"));
		idvObservationValue.addProperty(idvModel.createProperty("http://purl.oclc.org/NET/ssnx/ssn#observationResultTime"),model.createTypedLiteral(DateUtil.date2StandardString(new Date()),XSDDatatype.XSDdateTime));
		
		OutputStream out = new ByteArrayOutputStream();
		idvModel.write(out,"N-TRIPLE");	
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(out.toString());
		
	}
}//class
