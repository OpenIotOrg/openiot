package org.openiot.ld4s.lod_cloud;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.server.ServerProperties;
import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.openiot.ld4s.vocabulary.MuoVocab;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.security.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class UomApi extends SearchRouter {
	//	public static final String UCUM_FILE = "ucum-essence.xml";
	public static final String UCUM_FILE_SOURCE = "http://unitsofmeasure.org/ucum-essence.xml";
//	"http://aurora.regenstrief.org/~ucum/ucum-essence.xml";
	private static final String DBPEDIA_DISAMBIGUATION_SUFFIX = "_(disambiguation)";

	/*Document to parse the file containing unit-of-measurement details */
	private static Document dom = null;


	public UomApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
		if (dom == null){
			try{
				File uomfile = new File(LD4SConstants.UOM_FILE_PATH);
				if (!uomfile.exists()){
					int endIndex = LD4SConstants.UOM_FILE_PATH.lastIndexOf(LD4SConstants.SYSTEM_SEPARATOR);
					String dirs = LD4SConstants.UOM_FILE_PATH.substring(0, endIndex);
					File directories = new File(dirs);
					if (!directories.exists()){
						directories.mkdirs();
					}
					if (!directories.exists()){
						System.err.println("Unable to create the directories for the Unit-of-Measurement file at "+ServerProperties.UOM_FILE_KEY);
					}else{					
						URL website = new URL(UCUM_FILE_SOURCE);
						org.apache.commons.io.FileUtils.copyURLToFile(website, uomfile);
					}
				}
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				if (uomfile.exists()){
					dom = db.parse(LD4SConstants.UOM_FILE_PATH);
				}
			} catch (ParserConfigurationException e) {
				System.err.println("Unable to load the Unit-of-Measurement file from "+LD4SConstants.UOM_FILE_PATH);
				e.printStackTrace();
			} catch (SAXException e) {
				System.err.println("Unable to load the Unit-of-Measurement file from "+LD4SConstants.UOM_FILE_PATH);
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Unable to load the Unit-of-Measurement file from "+LD4SConstants.UOM_FILE_PATH);
				e.printStackTrace();
			}
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 */
	private Uom getUnit(NodeList list, String searched) {
		if (list == null){
			return null;
		}
		Uom ret = null;
		Element elem = null;
		String match = null;
		NodeList nl = null;
		int choice = -1;
		for (int i = 0; i<list.getLength() && ret==null ;i++){
			//search for Code(attr) | CODE(attr) | printSymbol | name | value+Unit(attr of value)
			elem = (Element)list.item(i);
			if (elem != null){			
				match = null;
				choice = 0;
				while (match == null || (searched.compareToIgnoreCase(match)!=0 && !searched.startsWith(match))
						&& choice < 6){
					switch (choice){
					case 0:
						match = elem.getAttribute("Code");
						break;
					case 1:
						match = elem.getAttribute("CODE");
						break;
					case 2:
						match = getTextValue(elem, "printSymbol");
						break;
					case 3:
						match = getTextValue(elem, "name");
						break;
					case 4:
						nl = elem.getElementsByTagName("value");
						if (nl.getLength() > 0){
							match = ((Element)nl.item(0)).getAttribute("Unit");
						}							
						break;
					case 5:
						nl = elem.getElementsByTagName("value");
						if (nl.getLength() > 0){
							match = ((Element)nl.item(0)).getAttribute("UNIT");
						}
						break;
					default:
						break;
					}

					if ((searched.compareToIgnoreCase(match)==0 || searched.startsWith(match)
							&& match != null && match.trim().compareTo("") != 0)){
						ret = new Uom();
						switch (choice){
						case 0:
						case 1:
							ret.code = match;
							break;
						case 2:
						case 4:
						case 5:
							ret.printSymbol = match;
							break;
						case 3:
							ret.name = match;
							break;
						default:
							break;
						}
						ret.property = getTextValue(elem, "property");
						ret.name = getTextValue(elem, "name");
						ret.printSymbol = getTextValue(elem, "printSymbol");
					}else{
						choice++;
					}
				}

			}
		}
		return ret;
	}

	/**
	 * Look for the tag and get the text content
	 * i.e for <prefix><name>John</name></prefix> xml snippet if
	 * the Element points to prefix node and tagName is 'name', this will return John
	 * @param ele xml element
	 * @param tagName tag name
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		if (ele != null && tagName != null){
			NodeList nl = ele.getElementsByTagName(tagName);
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				if (el != null && el.getFirstChild() != null){
					textVal = el.getFirstChild().getNodeValue();
				}
			}
		}
		return textVal;
	}



	@Override
	public Model start() throws Exception {
		String thing = context.getThing();
		if (dom == null){
			return null;
		}
		Element docEle = dom.getDocumentElement();
		Uom uom = null;
		//search for either Code/CODE in unit (remove eventual [])
		// or name in unit
		// printSymbol in unit
		// or Unit/UNIT in value in unit
		uom = getUnit(docEle.getElementsByTagName("unit"), thing);
		if (uom == null){
			//check if there is a redirection on Wikipedia since it would indicate 
			//the existence of a synonym or a more common term.
			thing = EncyclopedicApi.getWikipediaRedirectionID(thing);
			//if it finds it, get the wikipedia ID of the redirection field
			//repeats the search in the stardard units file (getUnit(..))
			uom = getUnit(docEle.getElementsByTagName("unit"), thing);
			//				useless because this would return just a prefix, meaning nothing specific
			//				//search for either Code/CODE in prefix (remove eventual [])
			//				//or name
			//				//or printSymbol in prefix
			//				uom = getUnit(docEle.getElementsByTagName("prefix"), thing);

		}
		if (uom != null){
			return createLink(uom);
		}


		//last attempt: generic search on sindice, helped by adding "unit" to the query search
		context.setDomains(new Domain[]{Domain.CROSSDOMAIN});
		String addterms = "";
		String[][] criteria = context.getAdditionalTerms();
		if (criteria != null){
			for (int row=0; row<criteria.length ;row++){
				for (int col=0; col<criteria.length ;col++){
					if (criteria[row][col] != null){
						addterms += criteria[row][col]+" ";
					}
				}
			}
		}else if (thing.length() <= 2){
			thing += getDBPEDIA_DISAMBIGUATION_SUFFIX();
		}
		context.setThing(addterms+context.getThing()+ " unit");
		GenericApi gen = new GenericApi(baseHost, context, author, from_resource);
		return gen.start();

	}


	private String getDBPEDIA_DISAMBIGUATION_SUFFIX() {
		// TODO Auto-generated method stub
		return DBPEDIA_DISAMBIGUATION_SUFFIX;
	}

	/**
	 * Create an RDF link (a predicate carefully chosen to express what is 
	 * actually causing the existence of this link itself) 
	 * from an LD4S resource to a Link resource having
	 * LD4S server hostname + encoded uri of the "from" resource + "_" + 
	 * encoded title of the "to" resource.
	 * The Link resource description is created according to the search results and it includes:
	 * <ul>
	 * <li> "from" resource uri</li>
	 * <li> "to" resource uri (entries included in a Sindice query response)</li>
	 * <li> author (name or uri)</li>
	 * <li> datetime when this link was instantiated first </li>
	 * <li> list of user-feedbacks resource </li>
	 * <li> linking criteria (context) </li>
	 * <li> title of the "to" resource </li>
	 * <li> bytes expected for accessing the "to" resource. </li>
	 *  </ul>
	 *  It is implemented to explicitly handle the JSON answers to Sindice requests.
	 * @param to JSON object containing information about the "to" resource
	 * @param from_resource RDF representation of the "from" resource
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	protected Model createLink(Uom uom) {
		Model model = from_resource.getModel();
		if (uom == null){
			return model;
		}
		Resource to_resource = null;
		try {
			//1. get uri of the dataLink resource
			Link link = new Link();
			link.setTo(UCUM_FILE_SOURCE);
			if(uom.name != null){		
				link.setTitle(uom.name);
			}
			Resource[] resarr = LD4SDataResource.createDataLinkResource(
					from_resource, baseHost, link, SptVocab.SEE_ALSO_LINK, null);
			if (resarr != null && resarr.length == 2){
				to_resource = resarr[0];
				if (from_resource != null){
					from_resource = resarr[1];
				}
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.err.println("Unable to create a DataLink resource");
		}
		if (to_resource != null){
			//eventually add more properties
			if (uom.printSymbol != null){
				from_resource.addProperty(MuoVocab.PREF_SYMBOL, uom.printSymbol);
			}
			if (uom.property != null){
				from_resource.addProperty(MuoVocab.MEASURES_QUALITY, uom.property);
			}

			model.add(to_resource.getModel());
		}
		return model;

	}

}