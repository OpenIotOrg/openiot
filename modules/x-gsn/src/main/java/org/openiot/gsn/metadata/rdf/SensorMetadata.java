/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn.metadata.rdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import org.openiot.gsn.metadata.LSM.LSMFieldMetaData;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;

import com.hp.hpl.jena.n3.turtle.TurtleParseException;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SensorMetadata {
  private Model model = ModelFactory.createDefaultModel();  

  private final static String ssn="http://purl.oclc.org/NET/ssnx/ssn#";
  private final static String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  private final static String rdfs="http://www.w3.org/2000/01/rdf-schema#";
  private final static String qu="http://purl.oclc.org/NET/ssnx/qu/qu#";
  private final static String rr="http://www.w3.org/ns/r2rml#";
  private final static String prov="http://www.w3.org/ns/prov#";
  private final static String lsm="http://openiot.eu/ontology/ns#";
  private final static String dul="http://www.loa-cnr.it/ontologies/DUL.owl#";
  private final static String wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#";
  private final static String lgdata="http://linkedgeodata.org/property/";
  Resource ssnSensor=ResourceFactory.createResource(ssn+"Sensor");
  Resource dulPlace=ResourceFactory.createResource(dul+"Place");
  Property rdfType=ResourceFactory.createProperty(rdf+"type");
  Property rdfsLabel=ResourceFactory.createProperty(rdfs+"label");
  Property ssnObserves=ResourceFactory.createProperty(ssn+"observes");
  Property ssnOfFeature=ResourceFactory.createProperty(ssn+"ofFeature");
  Property quUnit=ResourceFactory.createProperty(qu+"unit");
  Property rrColumnName=ResourceFactory.createProperty(rr+"columnName");
  //Property provPerformedAt=ResourceFactory.createProperty(prov+"PerformedAt");
  Property provWasGeneratedBy=ResourceFactory.createProperty(prov+"wasGeneratedBy");
  //Property lsmHasSourceType=ResourceFactory.createProperty(lsm+"hasSourceType");
  //Property lsmHasSensorType=ResourceFactory.createProperty(lsm+"hasSensorType");
  Property dulHasLocation=ResourceFactory.createProperty(dul+"hasLocation");
  Property wgs84Lat=ResourceFactory.createProperty(wgs84+"lat");
  Property wgs84Long=ResourceFactory.createProperty(wgs84+"long");
  Property lsmFieldName=ResourceFactory.createProperty(lsm+"fieldName");
 

  public void loadFromFile(String rdfFile) throws FileNotFoundException,TurtleParseException{
	  FileInputStream fis = new FileInputStream(rdfFile);
	  model.read(fis,null,"TURTLE");
  }
  
  public void createMetadata(LSMSensorMetaData meta){
	  Resource sensorUri=model.createResource(meta.getSensorID());
	  model.add(sensorUri, RDFS.subClassOf, ssnSensor);
	  model.add(sensorUri,ssnOfFeature,model.createResource(meta.getFeatureOfInterest()));
	  model.add(sensorUri,rdfsLabel,meta.getSensorName());
	  //model.add(sensorUri,lsmHasSourceType,meta.getSourceType());
	  model.add(sensorUri,provWasGeneratedBy,meta.getAuthor());
	  System.out.println(sensorUri.getLocalName());
	  Resource sType=model.createResource(sensorUri.getNameSpace()+meta.getSensorType());
	  model.add(sensorUri,rdfType,sType);
	  //model.add(sType,rdfsLabel,meta.getSensorType());
	  for (LSMFieldMetaData f:meta.getFields().values()){
		  Resource prop=model.createResource(f.getLsmPropertyName());
		  model.add(sensorUri,ssnObserves,prop);
		  model.add(prop,quUnit,f.getLsmUnit());
		  model.add(prop,lsmFieldName,f.getGsnFieldName());
	  }
	  Resource location=model.createResource(sensorUri.getURI()+"_location");
	  model.add(location,rdfType,dulPlace);
	  model.add(sensorUri,dulHasLocation,location);
	  model.add(location,wgs84Lat,""+meta.getLatitude());
	  model.add(location,wgs84Long,""+meta.getLongitude());
	  Resource city =model.createResource(sensorUri.getNameSpace()+"unknownCity");
	  Resource province=model.createResource(sensorUri.getNameSpace()+"unknownProvince");
	  Resource country=model.createResource(sensorUri.getNameSpace()+"unknownCountry");
	  Resource continent=model.createResource(sensorUri.getNameSpace()+"unknownContinent");
      model.add(location,model.createProperty(lsm+"is_in_city"),city);
      model.add(city,rdfsLabel,"unknowncity");
      model.add(location,model.createProperty(lgdata+"is_in_province"),province);
      model.add(province,rdfsLabel,"unknownprovince");
      model.add(location,model.createProperty(lgdata+"is_in_country"),country);
      model.add(country,rdfsLabel,"unknowncountry");
      model.add(location,model.createProperty(lgdata+"is_in_continent"),continent);
      model.add(continent,rdfsLabel,"unknowncontinent");
	  model.write(System.out,"TURTLE");
	  
  }
  
  public LSMSensorMetaData fillSensorMetadata(){
	  LSMSensorMetaData md=new LSMSensorMetaData();
	  ResIterator ri=model.listSubjectsWithProperty(RDFS.subClassOf, ssnSensor);
	  if (!ri.hasNext()){
		  throw new IllegalArgumentException("The rdf graph contains no instance of: "+ssnSensor);
	  }
	  Resource sensor=ri.next();
	  md.setSensorID(sensor.getURI());
	  model.listResourcesWithProperty(rdfsLabel);
	  NodeIterator obs= model.listObjectsOfProperty(ssnObserves);
	  
	  while (obs.hasNext()){
		  LSMFieldMetaData lsmField = new LSMFieldMetaData();
		  Resource prop=obs.next().asResource();
		  NodeIterator units=model.listObjectsOfProperty(prop, quUnit);
		  if (!units.hasNext())
			  throw new IllegalArgumentException("The property "+prop+" has no unit");
		  Resource unit=prop.getPropertyResourceValue(quUnit);
		  //String column=prop.listProperties(rrColumnName).next().getObject().toString();		  
		  NodeIterator fnames=model.listObjectsOfProperty(prop, lsmFieldName);
		  if (!fnames.hasNext())
			  throw new IllegalArgumentException("The property "+prop+" has no associated GSN field name");
		  Literal fn=fnames.next().asLiteral();
		  lsmField.setGsnFieldName(fn.getString());
		  
		  
		  lsmField.setLsmPropertyName(prop.getURI());
		  lsmField.setLsmUnit(unit.getURI());
		  md.getFields().put(prop.getURI(), lsmField);
	  }
	  Resource feature=sensor.getPropertyResourceValue(ssnOfFeature);
	  if (feature!=null)
	    md.setFeatureOfInterest(feature.getURI());
	  else 
		  md.setFeatureOfInterest("nofeature");
 	  md.setSensorName("");
 	  
      return md;
	  
  }

  
  
  public void load(InputStream rdfStream){
	  model.read(rdfStream,null,"TURTLE");
  }
  
  public String serializeRDF(){
	  StringWriter sw=new StringWriter();
	  model.write(sw, "N-TRIPLES");
	  return sw.toString();
  }
}
