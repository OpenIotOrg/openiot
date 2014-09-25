package org.openiot.gsn.metadata.rdf;

import org.openiot.gsn.metadata.LSM.LSMFieldMetaData;
import org.openiot.gsn.metadata.LSM.LSMSensorMetaData;

public class RdfMetadataTest {
   public static void main(String[] args){
	   SensorMetadata sm= new SensorMetadata();
	   LSMSensorMetaData meta = new LSMSensorMetaData();
	   meta.setSensorID("http://example.org/dig/gogo#ffsdfsdf");
	   meta.setFeatureOfInterest("http://example.org/feature");
	   meta.setSensorName("Name of Sensor");
	   meta.setSourceType("sourceType1");
	   meta.setAuthor("The Author");
	   meta.setSensorType("NewType");
	   meta.setLatitude(20);
	   meta.setLongitude(30);
	   LSMFieldMetaData f=new LSMFieldMetaData();
	   f.setLsmPropertyName("http://example.org/propTemp");
	   f.setLsmUnit("celsius");
	   f.setGsnFieldName("temp");
	   meta.getFields().put("temp", f);
	   
	   sm.createMetadata(meta );
   }
}
