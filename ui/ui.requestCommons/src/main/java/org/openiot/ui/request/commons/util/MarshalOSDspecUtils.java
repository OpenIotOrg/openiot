package org.openiot.ui.request.commons.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.openiot.commons.osdspec.model.OSDSpec;

/**
 * 
 * @author Stavros Petris (spet) e-mail: spet@ait.edu.gr
 *
 */
public class MarshalOSDspecUtils 
{
	/**
	 * Marshal OSDSpec manually to string using javax.xml.transformer and DOM in order
	 * to wrap strings with CData sections
	 * @param osdSpec
	 * @return string (xml) representation of OSDSpec
	 * @throws Exception
	 */
	public static String marshalOSDSpec(OSDSpec osdSpec) throws Exception{
		
		String osdSpecString = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(OSDSpec.class);
			Marshaller marshaller = jc.createMarshaller();
			//marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			Document document = docBuilderFactory.newDocumentBuilder().newDocument();
			marshaller.marshal(osdSpec, document);
			 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", new Integer(4));
			Transformer nullTransformer = transformerFactory.newTransformer();
			
			java.io.StringWriter sw = new StringWriter();	
			nullTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			nullTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
			//nullTransformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			nullTransformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,"http://www.openiot.eu/osdspec:graphMeta http://www.openiot.eu/osdspec:description query");
			nullTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			nullTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			nullTransformer.transform(new DOMSource(document),new StreamResult(sw));

			osdSpecString = sw.toString();
			return osdSpecString;
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	/* Marshal OSDSpec manually to string using xerces in order to wrap strings with CData sections.
	 * This kind of way was not used since OutputFormat and XMLSerializer are deprecated. The 
	 * xerces library is also needed for this to work
	public static String marshalOSDSpecXerces(OSDSpec osdSpec) throws Exception{
		
		String osdSpecString = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(OSDSpec.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			// configure an OutputFormat to handle CDATA
	        OutputFormat of = new OutputFormat();	       
	        of.setCDataElements(new String[] { "http://www.openiot.eu/osdspec^description","http://www.openiot.eu/osdspec^graphMeta","^query" });
	        //of.setPreserveSpace(true);
	        of.setIndenting(true);
	        
	        // create the serializer
	        java.io.StringWriter sw = new StringWriter();	        
	        XMLSerializer serializer = new XMLSerializer(of);
	        serializer.setOutputCharStream(sw);
			
			
			marshaller.marshal(osdSpec, serializer.asContentHandler());
			osdSpecString = sw.toString();
			return osdSpecString;
		} catch (Exception ex) {
			throw ex;
		}
	}*/
	
	/* Marshal OSDSpec manually to string using JAXB and in order to wrap strings with CData sections.
	 * This kind of way was not used since CDATA annotation needs to be added to the object's fields
	 * in order to show which fields need to be wrapped with CDATA. Also com.sun.xml.internal.bind.characterEscapeHandler 
	 * needs to be imported which sometimes causes runtime exceptions and it might also not allow the project
	 * to compile.
	 * 
	 * Also if this way is used then you need to post the object as string to the scheduler and not allow Resteasy
	 * to marshal it by itself internally. The REST interface will need to return STRING types and not object 
	 * types as well if this way is used. 
	 * OR
	 * You will need to read the Reasteasy documentation and use decorators.
	public static String marshalEscHandler(OSDSpec osdspec) throws Exception 
	{
		String osdSpecString = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(OSDSpec.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(
					"com.sun.xml.internal.bind.characterEscapeHandler",
					new EscapeHandler()					
					);
			java.io.StringWriter sw = new StringWriter();	
			marshaller.marshal(osdspec, sw);
			
			osdSpecString = sw.toString();//.replace("&lt;", "<").replace("&gt;", ">");
			return osdSpecString;
		} catch (Exception ex) {
			throw ex;
		}
	}*/
}
