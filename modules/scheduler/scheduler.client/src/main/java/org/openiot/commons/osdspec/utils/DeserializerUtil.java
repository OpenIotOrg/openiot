

package org.openiot.commons.osdspec.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openiot.commons.osdspec.model.OSDSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods to deserialize OSDSpec Object
 * 
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class DeserializerUtil {

	/**	logger. */
	 final static Logger logger = LoggerFactory.getLogger(DeserializerUtil.class);

	
	/**
	 * This method deserializes an OLCBProc specification from an input stream.
	 * 
	 * @param inputStream to deserialize
	 * @return OLCBProc
	 * @throws Exception if deserialization fails
	 */
	public static OSDSpec deserializeOSDSpec(InputStream inputStream) throws Exception {

		OSDSpec osdspec = null;
		try {
			
			String JAXB_CONTEXT = "org.openiot.commons.osdspec.model";
			
			// initialize jaxb context and unmarshaller
			JAXBContext context = JAXBContext.newInstance(OSDSpec.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			 
			osdspec = (OSDSpec)unmarshaller.unmarshal( inputStream );

						
		} catch (JAXBException e) {
			e.printStackTrace();
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
	public static OSDSpec deserializeOSDSpecFile(String pathName) throws FileNotFoundException, Exception {
		
		FileInputStream inputStream = new FileInputStream(pathName);
		
		return deserializeOSDSpec(inputStream);
	}	
}