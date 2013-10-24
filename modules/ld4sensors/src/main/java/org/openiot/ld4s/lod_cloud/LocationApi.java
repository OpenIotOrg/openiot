package org.openiot.ld4s.lod_cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.vocabulary.GeoVocab;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.openiot.ld4s.vocabulary.Wgs84Vocab;
import org.restlet.data.MediaType;
import org.restlet.security.User;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class LocationApi extends SearchRouter{


	public static final int MAX_RESULTS = 2;
	public static final String USERNAME = "iammyr";

	public static final String API_HOST = "http://api.geonames.org/";

	/** when parameters N/S/W/E coordinates (bounding box) are given. 
	 * Returns geonameId + other info. */
	public static final String SEARCH_FOR_CITY = "citiesJSON?";

	/** when parameters latitude/longitude are given.
	 * Returns geonameId + other info. */
	public static final String SEARCH_FOR_NEARBY = "findNearbyPlaceNameJSON?";

	/** when parameter country name is given.
	 * Returns geonameId + other info. Not yet supported here. */
	public static final String SEARCH_FOR_COUNTRY = "countryInfoJSON?";

	/** when parameter location name is given.
	 * Returns lat and long to get a geonameId later on. */
	public static final String SEARCH_GENERIC = "postalCodeSearchJSON?";

	/**A geonamId must be appended to this base uri to get a valid link with a GeoNames resource. */
	public static final String BASE_GEONAMES_URI = "http://sws.geonames.org/";


	public static final String COORD_NORTH_FIELD = "north=";
	public static final String COORD_SOUTH_FIELD = "south=";
	public static final String COORD_EAST_FIELD = "east=";
	public static final String COORD_WEST_FIELD = "west=";
	public static final String COORD_LANG_FIELD = "lang=";
	public static final String USERNAME_FIELD = "username="+USERNAME;
	public static final String MAX_RESULTS_FIELD = "maxRows="+MAX_RESULTS; 
	public static final String PLACENAME_FIELD = "placename=";
	public static final String COUNTRY_FIELD = "country=";
	public static final String LATITUDE_FIELD = "lat=";
	public static final String LONGITUDE_FIELD = "lng=";


	public LocationApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Model start() throws Exception {
		String query = null;


		//to search through geonames
		String location_name = null;
		String[] coords = context.getLocation_coords();
		location_name = context.getLocation();


		if (coords != null){
			//check if the search by bounding box is possible
			if (coords.length == 4){
				query = API_HOST+SEARCH_FOR_CITY;
				query += COORD_NORTH_FIELD+coords[0];
				query += "&"+COORD_SOUTH_FIELD+coords[1];
				query += "&"+COORD_WEST_FIELD+coords[2];
				query += "&"+COORD_EAST_FIELD+coords[3];					
			}else //3rd check if the search by lat and long is possible 
				if (coords.length == 2){
					query = API_HOST+SEARCH_FOR_NEARBY
					+LATITUDE_FIELD+coords[0]+"&"+LONGITUDE_FIELD+coords[1];
				}
		}
		if (query == null){					
			//check if the search by location name is possible (most accurate one)
			if (location_name != null){
				query = API_HOST+SEARCH_GENERIC + PLACENAME_FIELD + location_name;
			}
		}
		//if one of the available queries is possible
		if (query != null){
			query += "&"+MAX_RESULTS_FIELD+"&"+USERNAME_FIELD;
			String answer = makeRequest(query, MediaType.APPLICATION_JSON);
			if (answer != null && answer.trim().compareTo("") != 0){
				JSONObject json = new JSONObject(answer);	
				return handleAnswer(json);
			}
		}
		//last attempt
		//Sindice while specifying only cross domain dataset to search against.
		context.setDomains(new Domain[]{Domain.GEOGRAPHY});
		GenericApi gen = new GenericApi(baseHost, context, author, from_resource);
		return gen.start();

	}
	
	protected Model handleAnswer(JSONObject json){
		try {
			if (!json.has("geonames")
					&& json.has("postalCodes")){
				//some api responses do no contain a geonamesId directly
				String lat = null, lng = null;
				JSONArray jarr = json.getJSONArray("postalCodes");
				if (jarr.length() > 0){
					JSONObject jobj = jarr.getJSONObject(0);
					if (jobj.has("lat") && jobj.has("lng")){
						lat = jobj.getString("lat");
						lng = jobj.getString("lng");
					}
				}
				if (lat != null && lng != null){
					String answer = makeRequest(API_HOST+SEARCH_FOR_NEARBY
							+LATITUDE_FIELD+lat
							+"&"+LONGITUDE_FIELD+lng
							+"&"+USERNAME_FIELD, 
							MediaType.APPLICATION_JSON);
					if (answer != null){
						json = new JSONObject(answer);
					}
				}
			}
			if (json.has("geonames")){
			JSONArray results = (JSONArray)json.get("geonames");
			
			JSONObject elem = null;
			// add new linked data
			if (results.length() > 0){
				elem = results.getJSONObject(0);
				return createLink(elem);
			}
		}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return from_resource.getModel();
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
	protected Model createLink(JSONObject to) 
	throws UnsupportedEncodingException, JSONException{
		Model model = from_resource.getModel();
		if (!to.has("geonameId")){
			return model;
		}
		//add what can be taken from the JSON representation
		if (to.has("countryName")){
			from_resource.addProperty(GeoVocab.PARENT_COUNTRY, to.getString("countryName"));
		}
		if (to.has("lat")){
			from_resource.addProperty(Wgs84Vocab.LAT, to.getString("lat"));
		}
		if (to.has("lng")){
			from_resource.addProperty(Wgs84Vocab.LONG, to.getString("lng"));
		}
		if (to.has("population")){
			from_resource.addProperty(GeoVocab.POPULATION, to.getString("population"));
		}
		if (to.has("adminName1")){
			from_resource.addProperty(GeoVocab.PARENT_ADM1, to.getString("adminName1"));
		}
		String link_ext = BASE_GEONAMES_URI+to.getString("geonameId");
		//get the rdf representation by dereferencing the geonameid, since no api currently provides rdf representations
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
				NodeIterator nodes = temp.listObjectsOfProperty(GeoVocab.PARENT_ADM2);
				while (nodes.hasNext()){
					from_resource.addProperty(GeoVocab.PARENT_ADM2, nodes.next());
				}
				nodes = temp.listObjectsOfProperty(GeoVocab.PARENT_ADM3);
				while (nodes.hasNext()){
					from_resource.addProperty(GeoVocab.PARENT_ADM3, nodes.next());
				}
				nodes = temp.listObjectsOfProperty(GeoVocab.PARENT_COUNTRY);
				while (nodes.hasNext()){
					from_resource.addProperty(GeoVocab.PARENT_COUNTRY, nodes.next());
				}
				nodes = temp.listObjectsOfProperty(GeoVocab.NEARBY_FEATURES);
				while (nodes.hasNext()){
					from_resource.addProperty(GeoVocab.NEARBY_FEATURES, nodes.next());
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
				link.setTo(link_ext);
				
				if(to.has("toponymName")){		
					link.setTitle(LD4SDataResource.removeBrackets(to.getString("toponymName")));
				}
				
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





}
