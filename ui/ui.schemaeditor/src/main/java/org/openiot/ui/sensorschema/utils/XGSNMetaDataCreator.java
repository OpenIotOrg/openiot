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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import org.openiot.ui.sensorschema.bean.FieldMetaDataBean;
import org.openiot.ui.sensorschema.bean.SensorMetaDataBean;

import com.hp.hpl.jena.n3.turtle.TurtleParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class XGSNMetaDataCreator{

	  private Model model = ModelFactory.createDefaultModel();  
  
	  public String createMetadata(SensorMetaDataBean meta){
		  StringWriter metadata = new StringWriter();
		  
		  Resource sensorUri=model.createResource(meta.getSensorID());
		  model.add(sensorUri, RDFS.subClassOf, OpeniotVocab.ssnSensor);
		  model.add(sensorUri,OpeniotVocab.ssnOfFeature,model.createResource(meta.getFeatureOfInterest()));
		  model.add(sensorUri,OpeniotVocab.rdfsLabel,meta.getSensorName());
		  //model.add(sensorUri,lsmHasSourceType,meta.getSourceType());
		  model.add(sensorUri,OpeniotVocab.provWasGeneratedBy,meta.getAuthor());
		  System.out.println(sensorUri.getLocalName());
		  Resource sType=model.createResource(sensorUri.getNameSpace()+meta.getSensorType());
		  model.add(sensorUri,OpeniotVocab.rdfType,sType);
		  //model.add(sType,rdfsLabel,meta.getSensorType());
		  for (FieldMetaDataBean f:meta.getFields().values()){
			  Resource prop=model.createResource(f.getLsmPropertyName());
			  model.add(sensorUri,OpeniotVocab.ssnObserves,prop);
			  model.add(prop,OpeniotVocab.quUnit,f.getLsmUnit());
		  }
		  Resource location=model.createResource(sensorUri.getURI()+"_location");
		  model.add(location,OpeniotVocab.rdfType,OpeniotVocab.dulPlace);
		  model.add(sensorUri,OpeniotVocab.dulHasLocation,location);
		  model.add(location,OpeniotVocab.wgs84Lat,""+meta.getLatitude());
		  model.add(location,OpeniotVocab.wgs84Long,""+meta.getLongitude());
		  Resource city =model.createResource(sensorUri.getNameSpace()+"unknownCity");
		  Resource province=model.createResource(sensorUri.getNameSpace()+"unknownProvince");
		  Resource country=model.createResource(sensorUri.getNameSpace()+"unknownCountry");
		  Resource continent=model.createResource(sensorUri.getNameSpace()+"unknownContinent");
	      model.add(location,model.createProperty(OpeniotVocab.lsm+"is_in_city"),city);
	      model.add(city,OpeniotVocab.rdfsLabel,"unknowncity");
	      model.add(location,model.createProperty(OpeniotVocab.lgdata+"is_in_province"),province);
	      model.add(province,OpeniotVocab.rdfsLabel,"unknownprovince");
	      model.add(location,model.createProperty(OpeniotVocab.lgdata+"is_in_country"),country);
	      model.add(country,OpeniotVocab.rdfsLabel,"unknowncountry");
	      model.add(location,model.createProperty(OpeniotVocab.lgdata+"is_in_continent"),continent);
	      model.add(continent,OpeniotVocab.rdfsLabel,"unknowncontinent");
		  //model.write(System.out,"TURTLE");
		  		  
		  model.write(metadata, "TURTLE");		  
		  return metadata.toString();
		  
	  }
	  
	  public SensorMetaDataBean fillSensorMetadata(){
		  SensorMetaDataBean md=new SensorMetaDataBean();
		  ResIterator ri=model.listSubjectsWithProperty(RDFS.subClassOf, OpeniotVocab.ssnSensor);
		  if (!ri.hasNext()){
			  throw new IllegalArgumentException("The rdf graph contains no instance of: "+OpeniotVocab.ssnSensor);
		  }
		  Resource sensor=ri.next();
		  md.setSensorID(sensor.getURI());
		  model.listResourcesWithProperty(OpeniotVocab.rdfsLabel);
		  NodeIterator obs= model.listObjectsOfProperty(OpeniotVocab.ssnObserves);
		  
		  while (obs.hasNext()){
			  FieldMetaDataBean lsmField = new FieldMetaDataBean();
			  Resource prop=obs.next().asResource();
			  NodeIterator units=model.listObjectsOfProperty(prop, OpeniotVocab.quUnit);
			  if (!units.hasNext())
				  throw new IllegalArgumentException("The property "+prop+" has no unit");
			  Resource unit=prop.getPropertyResourceValue(OpeniotVocab.quUnit);
			  //String column=prop.listProperties(rrColumnName).next().getObject().toString();		  
			  lsmField.setLsmPropertyName(prop.getURI());
			  lsmField.setLsmUnit(unit.getURI());
			  md.getFields().put(prop.getURI(), lsmField);
		  }
		  Resource feature=sensor.getPropertyResourceValue(OpeniotVocab.ssnOfFeature);
		  if (feature!=null)
		    md.setFeatureOfInterest(feature.getURI());
		  else 
			  md.setFeatureOfInterest("nofeature");
	 	  md.setSensorName("");
	 	  
	      return md;
		  
	  }
	  
	  public String serializeRDF(){
		  StringWriter sw=new StringWriter();
		  model.write(sw, "N-TRIPLES");
		  return sw.toString();
	  }
}
