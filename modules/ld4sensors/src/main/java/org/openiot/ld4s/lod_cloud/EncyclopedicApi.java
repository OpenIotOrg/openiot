package org.openiot.ld4s.lod_cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.security.User;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class EncyclopedicApi extends SearchRouter {
	public static final String DBPEDIA_API_HOST = "http://dbpedia.org/sparql?query=";
	public static final String DBPEDIA_RESOURCE_BASE = "http://dbpedia.org/resource/";
	public static final String DBPEDIA_PAGE_BASE = "http://dbpedia.org/page/";
	//	public static final String FORMAT = "&format=json";
	public static final String APPEND = "LIMIT 1";
	public static final String WIKIPEDIA_BASE = "http://en.wikipedia.org/wiki/";
	private String DBPEDIA_DISAMBIGUATION_SUFFIX = "_(disambiguation)";
	public static String BASE_SPARQL_QUERY = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
	"SELECT ?subj WHERE { {?subj rdfs:label ?label";



	public EncyclopedicApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
	}

	protected String buildQueryString() throws UnsupportedEncodingException {
		if (getContext() == null || getBaseHost() == null){
			return null;
		}
		String retquery = BASE_SPARQL_QUERY, toappend = "}}";

		if (getContext().getThing() != null){
			retquery += " . FILTER (regex(?label, \""+getContext().getThing()+"\", \"i\"))" ;
		}
		retquery += toappend;
		retquery = URLEncoder.encode(retquery, "utf-8")
		//		+FORMAT
		;
		return DBPEDIA_API_HOST+retquery+APPEND;
	}


	private String getFirstLetterUppercaseResource(String in){
		String[] toks = in.split(" ");
		String thing = "";
		if (toks.length > 0){
			toks[0] = toks[0].trim();
			thing = Character.toUpperCase(toks[0].charAt(0))+toks[0].substring(1, toks[0].length());
		}
		for (int i = 1; i<toks.length-1 ;i++){
			toks[i] = toks[i].trim();
			if (toks[i].compareTo("") != 0){
				thing += "_"+toks[i];
			}
		}
		return thing;
	}

	private String getAllLowercaseResource(String in){
		String[] toks = in.split(" ");
		String thing = "";
		for (int i = 0; i<toks.length ;i++){
			thing += toks[0].trim().toLowerCase();
			if (i+1<toks.length){
				thing += "_";
			}
		}
		return thing;
	}

	private String getAllUppercaseResource(String in){
		String[] toks = in.split(" ");
		String thing = "";
		for (int i = 0; i<toks.length ;i++){
			thing += toks[0].trim().toUpperCase();
			if (i+1<toks.length){
				thing += "_";
			}
		}
		return thing;
	}

	private String getAllFirstUppercaseResource(String in){
		String[] toks = in.split(" ");
		String thing = "";
		for (int i = 0; i<toks.length ;i++){
			toks[i] = toks[i].trim();
			if (toks[i].compareTo("") != 0){
				thing += Character.toUpperCase(toks[i].charAt(0))+toks[i].substring(1, toks[i].length());
				if (i+1<toks.length){
					thing += "_";
				}
			}
		}
		return thing;
	}

	public static String getWikipediaRedirectionID(String id){
		if (id == null){
			return null;
		}
		String ret = null, query = WIKIPEDIA_BASE+id;
		try{
			Client client = new Client(Protocol.HTTP); 
			if (client.getContext() == null){
				client.setContext(new org.restlet.Context());
			}
			client.getContext().getParameters().add("followRedirects", "true");
			Request request = new Request(Method.GET, query);
			Response repr = client.handle(request);
			int attempts = 0;
			//follow up to 4 multiple redirects
			while (!repr.getStatus().equals(Status.SUCCESS_OK) && (attempts++) < 4){
				if (repr.getLocationRef() != null){
					query = repr.getLocationRef().toString(true, false);
					request = new Request(Method.GET, query);
					repr = client.handle(request);
				}
			}
			//get the wikipedia canonical URI (even when status=OK the canonical URI might be a different one)
			String html = repr.getEntityAsText(), canonical = "<link rel=\"canonical\" href=\"";
			int startcanonical = html.indexOf(canonical);
			if (startcanonical != -1){
				String temp = html.substring(startcanonical+canonical.length());
				temp = temp.substring(0, temp.indexOf("\""));
				ret = temp.substring(temp.lastIndexOf("/")+1);
			}else{			
				List<String> l = null;
				if ((l=repr.getLocationRef().getSegments()) != null){
					ret = l.get(l.size()-1);
				}
			}
		}catch (Exception e){
			System.err.println("GET request to "+query+" did not succeed.");
		}
		return ret;
	}

	/**
	 * Check resource existence only on DBpedia by:
	 * - getting Wikipedia ID after tracking eventual redirections
	 * - returning the related DBpedia URI
	 */
	@Override
	public Model start() throws Exception {
		String thing = null;
		Status status = null;

		if (context.getThing() == null){
			return from_resource.getModel();
		}
		Response resp = null;
		String id = getWikipediaRedirectionID(context.getThing());
		if (id != null){
			resp = getRequestResponse(DBPEDIA_PAGE_BASE+id, MediaType.ALL);
			status = resp.getStatus();
			//if the resource does exist then link to it
			if (status.equals(Status.SUCCESS_OK)){
				return createLink(DBPEDIA_RESOURCE_BASE+id);
			}
		}
		//otherwise:
		//1st attempt: first char of first token uppercase and the others in lower case
		thing = getFirstLetterUppercaseResource(context.getThing());
		if (thing.length() <= 2){
			thing += getDBPEDIA_DISAMBIGUATION_SUFFIX();
		}

		resp = getRequestResponse(DBPEDIA_PAGE_BASE+thing, MediaType.ALL);
		status = resp.getStatus();
		//if the resource does exist then link to it
		if (status.equals(Status.SUCCESS_OK)){
			return createLink(DBPEDIA_RESOURCE_BASE+thing);
		}

		//2nd attempt: all chars lower case
		thing = getAllLowercaseResource(context.getThing());
		if (thing.length() <= 2){
			thing += getDBPEDIA_DISAMBIGUATION_SUFFIX();
		}
		//check resource existence only on DBpedia
		resp = getRequestResponse(DBPEDIA_PAGE_BASE+thing, MediaType.ALL);
		status = resp.getStatus();
		//if the resource does exist then link to it
		if (status.equals(Status.SUCCESS_OK)){
			return createLink(DBPEDIA_RESOURCE_BASE+thing); 
		}

		//3rd attempt: all chars upper case
		thing = getAllUppercaseResource(context.getThing());
		if (thing.length() <= 2){
			thing += getDBPEDIA_DISAMBIGUATION_SUFFIX();
		}
		//check resource existence only on DBpedia
		resp = getRequestResponse(DBPEDIA_PAGE_BASE+thing, MediaType.ALL);
		status = resp.getStatus();
		//if the resource does exist then link to it
		if (status.equals(Status.SUCCESS_OK)){
			return createLink(DBPEDIA_RESOURCE_BASE+thing); 
		}

		//4th attempt: all first chars of words in upper case
		thing = getAllFirstUppercaseResource(context.getThing());
		if (thing.length() <= 2){
			thing += getDBPEDIA_DISAMBIGUATION_SUFFIX();
		}
		//check resource existence only on DBpedia
		resp = getRequestResponse(DBPEDIA_PAGE_BASE+thing, MediaType.ALL);
		status = resp.getStatus();
		//if the resource does exist then link to it
		if (status.equals(Status.SUCCESS_OK)){
			return createLink(DBPEDIA_RESOURCE_BASE+thing); 
		}

		//last attempt: generic search on sindice, helped by the additional terms
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
		context.setThing(addterms+context.getThing());
		GenericApi gen = new GenericApi(baseHost, context, author, from_resource);
		return gen.start();

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
	protected Model createLink(String to) {
		Model model = from_resource.getModel();
		if (to == null){
			return model;
		}		
		//gets additional info to the local LD4S reosurce (inherited from the external linked one)
		//i.e. all the dcterms:subject triples		
		try {
			String rdfdownload = makeRequest(to, MediaType.APPLICATION_RDF_XML);
			if (rdfdownload != null){	
				Model temp = ModelFactory.createDefaultModel();
				File f = new File("C:\\test1.rdf");
				f.setWritable(true);
				FileWriter fw = new FileWriter(f);
				fw.write(rdfdownload);
				fw.close();
				FileReader fr = new FileReader(f);
				temp.read(fr, null);
				fr.close();
				NodeIterator nodes = temp.listObjectsOfProperty(DCTerms.subject);
				while (nodes.hasNext()){
					from_resource.addProperty(DCTerms.subject, nodes.next());
				}
				if (!f.delete()){
					System.err.println("Unable to delete the dbpedia rdf/xml file.");
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to download further information from the linked external resource "
					+to+" - malformed URL.");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Unable to download further information from the linked external resource "
					+to);
		}
		Resource to_resource = null;
		try {
			//1. get uri of the dataLink resource
			Link link = new Link();
			link.setTo(to);
			Resource[] resarr = LD4SDataResource.createDataLinkResource(
					from_resource, baseHost, link, SptVocab.SAME_AS_LINK, null);
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
			model.add(to_resource.getModel());
		}
		return model;
	}

	public void setDBPEDIA_DISAMBIGUATION_SUFFIX(
			String dBPEDIA_DISAMBIGUATION_SUFFIX) {
		DBPEDIA_DISAMBIGUATION_SUFFIX = dBPEDIA_DISAMBIGUATION_SUFFIX;
	}

	public String getDBPEDIA_DISAMBIGUATION_SUFFIX() {
		return DBPEDIA_DISAMBIGUATION_SUFFIX;
	}


}
