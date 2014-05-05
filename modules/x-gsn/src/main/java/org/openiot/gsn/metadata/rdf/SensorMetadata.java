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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SensorMetadata {
  private Model model = ModelFactory.createDefaultModel();  

  private final static String ssn="http://purl.oclc.org/NET/ssnx/ssn#";
  private final static String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  private final static String rdfs="http://www.w3.org/2000/01/rdf-schema#";
  private final static String qu="http://purl.oclc.org/NET/ssnx/qu/qu#";
  private final static String rr="http://www.w3.org/ns/r2rml#";

  Resource ssnSensor=ResourceFactory.createResource(ssn+"Sensor");
  Property rdfType=ResourceFactory.createProperty(rdf+"type");
  Property rdfsLabel=ResourceFactory.createProperty(rdfs+"label");
  Property ssnObserves=ResourceFactory.createProperty(ssn+"observes");
  Property quUnit=ResourceFactory.createProperty(qu+"unit");
  Property rrColumnName=ResourceFactory.createProperty(rr+"columnName");

  public void loadFromFile(String rdfFile) throws FileNotFoundException,TurtleParseException{
	  FileInputStream fis = new FileInputStream(rdfFile);
	  model.read(fis,null,"TURTLE");
  }
  
  public LSMSensorMetaData fillSensorMetadata(){
	  LSMSensorMetaData md=new LSMSensorMetaData();
	  ResIterator ri=model.listSubjectsWithProperty(rdfType, ssnSensor);
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
		  String column=prop.listProperties(rrColumnName).next().getObject().toString();
		  lsmField.setLsmPropertyName(prop.getURI());
		  lsmField.setLsmUnit(unit.getURI());
		  md.getFields().put(column, lsmField);
	  }
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
