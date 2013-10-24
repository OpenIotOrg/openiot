package org.openiot.ld4s.lod_cloud;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ld4s.lod_cloud.Context.Domain;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.vocabulary.FoafVocab;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.MediaType;
import org.restlet.security.User;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Handler of requests for the Sindice API.
 *
 * @author Myriam Leggieri.
 *
 */
public class GenericApi extends SearchRouter{

	private String query;

	public static final int MAX_RESULTS = 2;

	public static final int MAX_ADDITIONAL_TOKENS = 1;

	public static final String FORMAT = "&format=json";

	public static final String SINDICE_API_HOST = "http://api.sindice.com/v3/search?";

	public static final String TERM_SEARCH = "q";

	public static final String NTRIPLE_SEARCH = "nq";

	public static final String FILTER_SEARCH = "fq";

	public static final String FILTER_DOMAIN = "domain:";

	public static final String FILTER_TIME = "date:";

	public static final String DEFAULT_OP = "%20OR%20";

	public static final String FIELDS = "";


	public GenericApi(String baseHost, Context context,
			User author, Resource from_resource) {
		super(baseHost, context, author, from_resource);
	}

	@Override
	public Model start() throws UnsupportedEncodingException, JSONException{
		if (getContext() == null || getBaseHost() == null || getContext().isEmpty()){
			return null;
		}
		this.query = buildQueryString();
		if (query == null || query.trim().compareTo("") == 0) {
			return null;
		}
		System.out.println("********\n"+query+"\n********");
		//get the json first 5 answers
		String answer = makeRequest(this.query, MediaType.APPLICATION_JSON);
		return	handleAnswer(answer);
	}

	public Model start4Foaf() throws UnsupportedEncodingException, JSONException{
		if (getContext() == null || getBaseHost() == null){
			return null;
		}
		this.query = buildQueryString4Foaf();
		if (query == null || query.trim().compareTo("") == 0  || query.startsWith("null")) {
			return null;
		}
		System.out.println("********\n"+query+"\n********");
		//get the json first 5 answers
		String answer = makeRequest(this.query, MediaType.APPLICATION_JSON);
		return	handleAnswer(answer);
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
	private Model handleAnswer(String answer) throws JSONException, UnsupportedEncodingException{
		Model ret = from_resource.getModel();;
		if (answer == null || answer.trim().compareTo("") == 0){
			return null;
		}
		JSONObject json = new JSONObject(answer);			
		JSONArray results = (JSONArray)json.get("entries");
		JSONObject elem = null;
		// add new linked data
		int inserted = 0;
		//		int toks = -1;
		//		if (context.getThing() != null){
		//			toks = new StringTokenizer(context.getThing()).countTokens();
		//		}
		//		int tosatisfy = -1;
		//		if (context.getAdditionalTerms() != null){
		//			tosatisfy = context.getAdditionalTerms().length;
		//		}
		for (int i=0; i<results.length()&&inserted<MAX_RESULTS	;i++){
			elem = results.getJSONObject(i);
			//			if(isEligibleLink(elem, 
			//							context.getAdditionalTerms(),
			//							tosatisfy,
			//							toks)){
			//Bad but temporary solution:
			if (elem.has("link") && !elem.getString("link").contains("twitter")){
				ret = createLink(elem);
				inserted++;	
			}
			//			}
		}
		return ret;
	}

	//	/**
	//	 * Check whether a search result entry is eligible to be
	//	 * linked with the local resource, based on the level of
	//	 * matching with the given criteria 
	//	 * @param elem search result entry
	//	 * @param criteria couples of <predicate, object>
	//	 * @param criteriaToBeMatched amount of given criteria that 
	//	 * have to be satisfied for considering the entry, eligible
	//	 * to be linked
	//	 * @param originalToks amount of tokens in the local resource
	//	 * that has to be linked with external ones
	//	 * @return
	//	 */
	//	private boolean isEligibleLink(JSONObject elem, 
	//			String[][] criteria, int criteriaToBeMatched,
	//			int originalToks){
	//		int matched = 0;
	//		String title = null;
	//		try {			
	//			JSONArray temp = elem.getJSONArray("title");
	//			if (temp != null){
	//				JSONObject tempobj = temp.getJSONObject(0);
	//				if (tempobj != null){
	//					title = LD4SDataResource.removeBrackets(tempobj.getString("value"));
	//					if (new StringTokenizer(title).countTokens() 
	//							<= originalToks+MAX_ADDITIONAL_TOKENS
	//							&& title.contains(context.getThing())){
	//						matched += 1;
	//					}
	////					better to consider the additional terms only in the original search query
	////					if (criteria != null){
	////						for (int row=0; row<criteria.length
	////						&& matched < criteriaToBeMatched ;row++){
	////							for (int col=0; col<criteria.length
	////							&& matched < criteriaToBeMatched ;col++){
	////								if (criteria[row][col] != null
	////										&& criteria[row][col].compareTo("") != 0
	////										&& title.contains(criteria[row][col])){
	////									matched++;
	////								}
	////							}
	////						}
	////					}
	//				}
	//			}
	//		} catch (JSONException e) {
	//			e.printStackTrace();
	//			System.err.println("Unable to properly check the external link eligibility for the search result "+elem);
	//		}
	////		return matched >= criteriaToBeMatched;
	//		return matched > 0;
	//	}

	protected static String buildQueryString(String query, Domain[] item, String queryType, String filterType) throws UnsupportedEncodingException {
		String itemstr = "";
		if (item == null){
			return null;
		}
		for (int i=0; i<item.length ;i++){
			if (item[i] != null && item[i].name().compareTo("null")!=0
					&& item[i].name().compareTo("")!=0){
				LinkedList<String> domainUris = Context.domain2Uri.get(item[i]);
				Iterator<String> it = domainUris.iterator();
				while(it.hasNext()){
					itemstr += it.next();
					if (it.hasNext()){
						itemstr += " OR ";
					}
				}
				if (i+1<item.length){
					itemstr += " OR ";
				}
			}
		}
		// if the items were not valid
		if (itemstr.compareTo("") == 0){
			queryType = null;
		}
		return buildQueryString(query, itemstr, queryType, filterType);
	}

	protected static String buildQueryString(String query, String item, String queryType, String filterType) throws UnsupportedEncodingException {
		if (item == null){
			return null;
		}
		if (filterType == null){
			filterType = "";
		}
		StringBuilder ret = new StringBuilder();
		if (query != null){			
			if (query.contains(queryType+"=")){
				ret.append(DEFAULT_OP);
			}else{
				ret.append("&").append(queryType).append("=");
			}
		}else if (queryType != null){
			query = SINDICE_API_HOST;
			ret.append(queryType).append("=");
		}else{
			return null;
		}
		if (item != null && item.trim().compareTo("null")!=0)
			ret.append(filterType).append("(").append(URLEncoder.encode(item,"utf8")).append(")");
		query += ret.toString();
		return query;
	}
	/**
	 *
	 * @param elem
	 * @param andorOperator
	 * @param className
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	protected String buildQueryString() throws UnsupportedEncodingException {
		String qapi = null, temp = null;
		if (context == null){
			return "";
		}
		//search for resources to link that refer to the same Thing
		temp = buildQueryString(qapi, context.getThing(), TERM_SEARCH, null);
		if (temp != null && temp.trim().compareTo("") != 0){
			qapi = temp;
		}
		//search for resources to link that refer to the same Space
		String[] items = context.getSpace();
		if (items != null){
			for (int i=0; i<items.length ;i++){
				if (items[i] != null){
					temp = buildQueryString(qapi, items[i], FILTER_SEARCH, null);
					if (temp != null && temp.trim().compareTo("") != 0){
						qapi = temp;
					}
				}
			}
		}
		//search for resources to link that refer to the same Domain
		if (context.getDomains() != null && context.getDomains().length > 0){
			temp = buildQueryString(qapi, context.getDomains(), FILTER_SEARCH, FILTER_DOMAIN);
			if (temp != null && temp.trim().compareTo("") != 0){
				qapi = temp;
			}
		}
		return qapi+FORMAT+FIELDS;
	}


	/**
	 *
	 * @param email
	 * @param homepage
	 * @param weblog
	 * @param name
	 * @param surname
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	protected String buildQueryString4Foaf() throws UnsupportedEncodingException {
		String qapi = null,
		//		appendice = "&qt=advanced",
		foafName = null, foafSurname = null, foafHomepage = null, foafWeblog = null, foafEmail = null, query = "", wildcard = "*", space = " ", ang1 = "<", ang2 = ">", virg = "\"", nl = "\n", and = "AND", par1 = "(", par2 = ")", or = "OR";

		Person person = context.getPerson();
		String name = person.getFirstname(), surname = person.getSurname(),
		email = person.getEmail(), homepage = person.getHomepage(), 
		weblog = person.getWeblog();
		if (person.getFirstname() != null || surname != null) {
			if (name != null) {
				foafName = FoafVocab.FIRST_NAME.getURI();
				query += par1 + wildcard + space + ang1 + foafName + ang2 + space + virg + name + virg + nl
				+ or + nl;
				foafName = FoafVocab.NAME.getURI();
				query += wildcard + space + ang1 + foafName + ang2 + space + virg + name + virg + par2;
			}
			if (surname != null) {
				if (!query.equals("")) {
					query += nl + and + nl;
				}
				foafSurname = FoafVocab.SURNAME.getURI();
				query += par1 + wildcard + space + ang1 + foafSurname + ang2 + space + virg + surname
				+ virg + nl + or + nl;
				foafSurname = FoafVocab.FAMILY_NAME.getURI();
				query += wildcard + space + ang1 + foafSurname + ang2 + space + virg + surname + virg
				+ par2;
			}
			if (name != null && surname != null) {
				if (!query.equals("")) {
					query += nl + or + nl;
				}
				foafName = FoafVocab.NAME.getURI();
				query += wildcard + space + ang1 + foafName + ang2 + space + virg + name + space + surname
				+ virg;
			}
		}
		else {
			/**
			 * Sindice rarely finds correct results when a foaf profile is searched by mean of a homepage
			 * or weblog or email. That's why if a name and surname are available here, a query that
			 * involves only them is preferred.
			 **/
			if (email != null) {
				if (!query.equals("")) {
					query += nl + and + nl;
				}
				foafEmail = FoafVocab.MBOX.getURI();
				query += wildcard + space + ang1 + foafEmail + ang2 + space + virg + email + virg;
			}
			if (homepage != null) {
				if (!query.equals("")) {
					query += nl + and + nl;
				}
				foafHomepage = FoafVocab.HOMEPAGE.getURI();
				query += wildcard + space + ang1 + foafHomepage + ang2 + space + virg + homepage + virg;
			}
			if (weblog != null) {
				if (!query.equals("")) {
					query += nl + and + nl;
				}
				foafWeblog = FoafVocab.WEBLOG.getURI();
				query += wildcard + space + ang1 + foafWeblog + ang2 + space;
			}
		}
		qapi = SINDICE_API_HOST+GenericApi.NTRIPLE_SEARCH+
		URLEncoder.encode(query, "utf-8")+FORMAT+FIELDS;

		return qapi;
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
	protected Model createLink(JSONObject to) 
	throws UnsupportedEncodingException, JSONException{
		Model model = from_resource.getModel();
		if (!to.has("link")){
			return model;
		}
		Resource to_resource = null;
		try {
			//1. get uri of the dataLink resource
			Link link = new Link();
			link.setTo(to.getString("link"));
			String to_title = null;
			JSONArray tarr = null;
			if(to.has("title") && (tarr=to.getJSONArray("title")) != null
					&& tarr.getJSONObject(0) != null
					&& tarr.getJSONObject(0).has("value")){
				to_title = LD4SDataResource.removeBrackets(
						tarr.getJSONObject(0).getString("value"));
			}
			if (to_title != null){
				link.setTitle(to_title);
			}
			link.setFrom(from_resource.getURI());
			if (to.has("explicit_content_length")){
				link.setBytes(Double.valueOf(to.getString("explicit_content_length")));
			}
			if (getAuthor() != null){
				Person person = new Person();
				person.setUri(getAuthor().getLexicalForm());
				link.setAuthor(person);
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
			if (to.has("updated")){
				to_resource.addProperty(DCTerms.temporal, 
						to.getString("updated"));			
			}
			model.add(to_resource.getModel());
		}
		
		return model;
	}

}
