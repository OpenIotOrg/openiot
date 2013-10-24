package org.openiot.ld4s.lod_cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.MediaType;
import org.restlet.security.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class PeopleApi extends SearchRouter {


	public static final int MAX_RESULTS = 2;

	public static final String API_HOST = "http://www.foaf-search.net/api/rest?";

	public static final String SEARCH_TYPE_FIELD = "&method=search";

	public static final String ACCESS_KEY = "access_key=evMRvYhxLHudRxShuV2pRPdgotcHFR0Q";

	public static final String QUERY_FIELD = "&query=";

	public static final String RESULT_LINK_DELIMITER_START = "(";
	public static final String RESULT_LINK_DELIMITER_END = ")";

	
	public static final String CITESEER_SPARQL = "http://citeseer.rkbexplorer.com/sparql/?query=";
	public static final String CITESEER_SPARQL_APPEND = "}LIMIT 1";
	public static String BASE_SPARQL_QUERY = "PREFIX akt:  <http://www.aktors.org/ontology/portal#> "
		+"SELECT distinct ?s WHERE { ?s <http://www.aktors.org/ontology/portal#full-name> "; 
	
	
	
		public static final String CITESEER_SEARCH = "http://citeseer.ist.psu.edu/search?q=";
	public static final String CITESEER_SEARCH_APPEND = "&submit=Search&uauth=1&sort=ndocs&t=auth";


	public PeopleApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Model start() throws Exception {
		String query = null;
		String pers = null;
		String resp = null;
		Person person = context.getPerson();
		String link = null;
		
		//search on citeseer
		if (person.getFirstname() != null && person.getSurname() != null){
			pers = "\""+person.getFirstname()+" "+person.getSurname()+"\"";
		}else if (person.getSurname() != null){
			pers = "\""+person.getSurname()+"\"";
		}else if (person.getFirstname() != null){
			pers = "\""+person.getFirstname()+"\"";
		}
		if (pers != null){
			query = CITESEER_SPARQL+URLEncoder.encode(BASE_SPARQL_QUERY + 
				pers + CITESEER_SPARQL_APPEND, "utf-8");
				//get the factory
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document dom = db.parse(query);
					Element docEle = dom.getDocumentElement();
					//search for the uri elem
					NodeList nl = docEle.getElementsByTagName("uri");
					if(nl != null && nl.getLength() > 0) {
						Element el = (Element)nl.item(0);
						if (el != null && el.getFirstChild() != null){
							link = el.getFirstChild().getNodeValue();
						}
					}	
					if (link != null){
//						if (!getRequestResponse(link, MediaType.ALL).getStatus().equals(Status.SUCCESS_OK)){
//							
//						}
						return createLink(null, link);
					}
				}catch(ParserConfigurationException pce) {
					pce.printStackTrace();
				}catch(SAXException se) {
					se.printStackTrace();
				}catch(IOException ioe) {
					ioe.printStackTrace();
				}

//				if (json.getJSONArray("results") != null &&
//						json.getJSONArray("results").length() > 0
//						&& json.getJSONArray("results").getJSONObject(0).getString("link_text") != null
//						//bad but temporary solution
//						&& !json.getJSONArray("results").getJSONObject(0).getString("link_text").contains("twine")){
//					link = json.getJSONArray("results").getJSONObject(0).getString("link_text");
//				}					
			}
		
		//attempt searching through foaf-search.net
		
		int choice = 0;
		while (link == null && choice < 6){
			switch (choice){
			case 0:
				if (person.getEmail() != null){
					pers = URLEncoder.encode(person.getEmail(), "utf-8");
				}
				break;
			case 1:
				if (person.getHomepage() != null){
					pers = URLEncoder.encode(person.getHomepage(), "utf-8");
				}
				break;
			case 2:
				if (person.getWeblog() != null){
					pers = URLEncoder.encode(person.getWeblog(), "utf-8");
				}
				break;
			case 3:
				if (person.getFirstname() != null && person.getSurname() != null){
					pers = URLEncoder.encode(person.getFirstname()+" "+person.getSurname(), "utf-8");
				}	
				break;
			case 4:
				if (person.getNickname() != null){
					pers = URLEncoder.encode(person.getNickname(), "utf-8");
				}
				break;
			default:
				pers = null;
			}
			if (pers != null){
				query = API_HOST+ACCESS_KEY+SEARCH_TYPE_FIELD+QUERY_FIELD+pers;
				resp = makeRequest(query, MediaType.APPLICATION_JSON);
				if (resp != null){
					JSONObject json = new JSONObject(resp);
					if (json.getJSONArray("results") != null &&
							json.getJSONArray("results").length() > 0
							&& json.getJSONArray("results").getJSONObject(0).getString("link_text") != null
							//bad but temporary solution
							&& !json.getJSONArray("results").getJSONObject(0).getString("link_text").contains("twine")){
						link = json.getJSONArray("results").getJSONObject(0).getString("link_text");
					}					
				}
			}
			if (link != null){
				String link1 = null, title = null;
				int start = link.indexOf(RESULT_LINK_DELIMITER_START),
				end = link.indexOf(RESULT_LINK_DELIMITER_END);
				if (start != -1 && end != -1){
					link1 = link.substring(start+1, end);
					title = link.substring(0, start);
				}
				return createLink(title, link1);	 
			}else{
				choice++;	
				pers = null;
			}
		}

		//		$requestURL = createFoafSearchQuery($id);
		//				$response = request($requestURL);
		//				//echo $response;
		//				$responseArray = json_decode($response,	true);
		//				if ($responseArray && $responseArray['results'] && $responseArray['results'][0]
		//				&& $responseArray['results'][0]['link_text']){
		//					$results = $responseArray['results'];
		//					$tot = sizeof($results);
		//					$parenthesis = false;
		//					//			echo 'row='.$results[0]['link_text'];
		//					//			echo 'id='.$id;
		//					for ($ind=0; $ind<$tot && $parenthesis === false; $ind++){
		//						$row = $results[$ind]['link_text'];
		//						//				echo 'row='.$row;
		//						if (strpos(strtolower($row), strtolower($id)) !== false){
		//							//	the uri is among parenthesis
		//							$parenthesis = strrpos($row, ')');
		//							if ($parenthesis !== false){
		//								$row = substr($row, 0, $parenthesis);
		//								$parenthesis = strrpos($row, "(");
		//								if ($parenthesis !== false){
		//									$uri = substr($row, $parenthesis+1);
		//								}
		//								//					echo 'uri='.$uri;
		//							}
		//						}
		//					}
		//					//			$uri = "http://www.foaf-search.net/Profile?personid=".$results[0]['id'];
		//				}
		//			}

		//last attempt
		//Sindice while specifying only cross domain dataset to search against.
		context.setDomains(new Domain[]{Domain.PEOPLE});
		GenericApi gen = new GenericApi(baseHost, context, author, from_resource);
		return gen.start4Foaf();
//		return from_resource.getModel();
	}

	/**
	 * Select relevant results from the result set.
	 * In case additional terms/predicates had been included
	 * in the context, here they are used to filter out
	 * the irrelevant results.
	 * @param answer
	 * @return model or null
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	protected Model createLink(String title, String link_ext) throws JSONException, UnsupportedEncodingException{
		Model model = from_resource.getModel();
		if (link_ext == null || link_ext.trim().compareTo("") == 0){
			return model;
		}
		//get the rdf representation, since no sparql result from coteseer currently provides rdf representations
		try {
			String rdfdownload = makeRequest(link_ext, MediaType.APPLICATION_RDF_XML);
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
				Property prop = 
					temp.createProperty("http://www.aktors.org/ontology/portal#has-author");
				ResIterator nodes = temp.listSubjectsWithProperty(prop);
				while (nodes.hasNext()){
					from_resource.addProperty(FOAF.publications, nodes.next());
				}
				if (!f.delete()){
					System.err.println("Unable to delete the rdf/xml file.");
				}
			
			}
		} catch (FileNotFoundException e) {
		      e.printStackTrace(System.err);
		      System.exit(1);
		    } catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.err.println("Unable to download further information from the linked external resource "
						+link_ext+" - malformed URL.");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.err.println("Unable to download further information from the linked external resource "
						+link_ext);
			}
			
			Resource to_resource = null;
			try {
				//1. get uri of the dataLink resource
				Link link = new Link();
				link.setTo(link_ext);
				if(title != null){		
					link.setTitle(title);
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
				model.add(to_resource.getModel());
			}
			return model;
	}

}
