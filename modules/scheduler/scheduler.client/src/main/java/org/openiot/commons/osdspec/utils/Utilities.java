package org.openiot.commons.osdspec.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container utilities class
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *
 */
public class Utilities 
{
	/**
	 * This class provides methods to deserialize OSDSpec Object
	 * 
	 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr	 *
	 *
	 */	
	public static class Deserializer
	{
		//logger
		final static Logger logger = LoggerFactory.getLogger(Deserializer.class);
		
		/**
		 * This method deserializes an OLCBProc specification from an input stream.
		 * 
		 * @param inputStream to deserialize
		 * @return OLCBProc
		 * @throws Exception if deserialization fails
		 */
		public static OSDSpec deserializeOSDSpec(InputStream inputStream) throws Exception 
		{
			OSDSpec osdspec = null;
			try {
				
				String JAXB_CONTEXT = "org.openiot.commons.osdspec.model";
				
				// initialize jaxb context and unmarshaller
				JAXBContext context = JAXBContext.newInstance(OSDSpec.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();	
				 
				osdspec = (OSDSpec)unmarshaller.unmarshal( inputStream );

							
			} catch (JAXBException e) {
				logger.error("Error deserializing OSDSpec",e);
			}
			return osdspec;
		}
		
		/**
		 * This method deserializes an APDL Spec from a file.
		 * 
		 * @param pathName of the file containing the APDL Spec
		 * @return OLCBProc object
		 * @throws FileNotFoundException if the file could not be found
		 * @throws Exception if deserialization fails
		 */
		public static OSDSpec deserializeOSDSpecFile(String pathName) throws FileNotFoundException, Exception 
		{			
			FileInputStream inputStream = new FileInputStream(pathName);
			
			return deserializeOSDSpec(inputStream);
		}	
	}//class
	
	
	/**
	 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
	 *
	 */
	public static class Serializer 
	{
		//logger
		final static Logger logger = LoggerFactory.getLogger(Serializer.class);
		
		/**
		 * This method serializes an OLCBProc Object to an xml and writes it into a file.
		 * 
		 * @param spec
		 *            the OLCBProc to be written into a file
		 * @param pathName
		 *            the file where to store
		 * @throws IOException
		 *             whenever an io problem occurs
		 */
		public static void serializeOLCBProc(OSDSpec osdSpec, Writer writer) throws IOException
		{
			ObjectFactory objectFactory = new ObjectFactory();
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(OSDSpec.class);
//				JAXBElement<OSDSpec> item = objectFactory.createOLCBProc(osdSpec);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//				marshaller.marshal(item, writer);
			}
			catch (JAXBException e) {
				logger.error("error serializing OSDSpec",e);				
			}
		}
	}//class
}//class
