package org.openiot.commons.osdspec.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.openiot.commons.osdspec.model.OSDSpec;
import org.openiot.commons.osdspec.model.ObjectFactory;



/**
 * @author Nikos Kefalakis (nkef) e-mail: nkef@ait.edu.gr
 *
 */
public class Serializer {

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
	public static void serializeOLCBProc(OSDSpec osdSpec, Writer writer) throws IOException {

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(OSDSpec.class);
//			JAXBElement<OSDSpec> item = objectFactory.createOLCBProc(osdSpec);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			marshaller.marshal(item, writer);
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
