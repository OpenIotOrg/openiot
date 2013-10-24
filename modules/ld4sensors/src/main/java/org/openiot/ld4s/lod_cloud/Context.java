package org.openiot.ld4s.lod_cloud;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONObject;
import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.data.Form;

import com.hp.hpl.jena.ontology.ObjectProperty;



public class Context {
	public static final String[] LogicOperators = new String[]{"AND", "OR"};

	/** Type of relation that can intercur between different concepts of a context. */
	public static final String[] spaceRelations = {"IN", "UNDER", "OVER", "NEAR", "OF"};

	public static HashMap<Domain, LinkedList<String>> domain2Uri = null;

	public static HashMap<Domain, String> domain_resources = null;

	public static enum Domain {GEOGRAPHY, CROSSDOMAIN, LIFESCIENCE, PUBLICATION,
		GOVERNMENT, MEDIA, USERGENERATED, WEATHER, PEOPLE, ENCYCLOPEDIC, ALL, LOCATION,
		FEATURE, UNIT, ELECTRICITY_TARIFF};

		public static final String[] relation_sem = {SptVocab.IN.getURI(), 
			SptVocab.UNDER.getURI(), SptVocab.OVER.getURI(), 
			SptVocab.NEAR.getURI(), "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl/isPartOf"};



		/** Location at different levels of granularity (e.g., city, country) and from 
		 * different perspectives (e.g., surrounding locations). 
		 * Each element is a string of space relations each associated with a list
		 * of space names by a prefix notation. */
		private String[] space_names_prefix = null;
		
		/** In case there is only one location and no specific space relation 
		 * (dul:hasLocation will be used rather than in, nearby, under, etc.)*/
		private String location = null;
		
		private String[] location_coords = null;

		/** The subject of interest at different levels of granularity (e.g., color of 
		 * cotton of the sleeves of a shirt). */
		private String thing = null;
		
		/** Additional terms that can be generically used to restrict the query. */
		private String[][] additionalPredicateTerms = null;

		/** Date start and end.*/
		private Date[] time_range = null;

		/** Application domains associatable with this context. */
		private Domain[] domains = null;

		/** Application domains not associatable with this context. */
		private String excluded_domains = null;

		private double confidence = 0.0;
		
		private Person person = null;
		
		private String company = null;
		
		private String time = null;
		
		private String date = null;
		
		private String country = null;
		
		

		public Context(String localhost){
			if (domain2Uri == null){
				initDomain2Uri();
			}
			if (domain_resources == null){
				initDomainResources(localhost);
			}
		}

		public static void initDomain2Uri(){
			domain2Uri = new HashMap<Domain, LinkedList<String>>();
			LinkedList<String> list = new LinkedList<String>();
			list.add(getBaseDomain("http://aemet.linkeddata.es"));
			list.add(getBaseDomain("http://geo.linkeddata.es"));
			list.add(getBaseDomain("http://ecowlim.tfri.gov.tw/"));
			list.add(getBaseDomain("http://sws.geonames.org/"));
			domain2Uri.put(Domain.GEOGRAPHY, list);
			list = new LinkedList<String>();
			list.add(getBaseDomain("http://data.southampton.ac.uk/"));
			list.add(getBaseDomain("http://dbpedia.org/"));
			list.add(getBaseDomain("http://factforge.net/"));
			domain2Uri.put(Domain.CROSSDOMAIN, list);
			list = new LinkedList<String>();
			list.add(getBaseDomain("http://dbpedia.org/"));
			domain2Uri.put(Domain.ENCYCLOPEDIC, list);
		}

		public static void initDomainResources(String host){
			domain_resources = new HashMap<Domain, String>();
			domain_resources.put(Domain.GEOGRAPHY, LD4SDataResource.getResourceUri(host,"resource/domain", "geo"));
			domain_resources.put(Domain.LOCATION, LD4SDataResource.getResourceUri(host,"resource/domain", "geo"));
			domain_resources.put(Domain.PUBLICATION, LD4SDataResource.getResourceUri(host,"resource/domain", "pub"));
			domain_resources.put(Domain.GOVERNMENT, LD4SDataResource.getResourceUri(host,"resource/domain", "gov"));
			domain_resources.put(Domain.MEDIA, LD4SDataResource.getResourceUri(host,"resource/domain", "med"));
			domain_resources.put(Domain.USERGENERATED, LD4SDataResource.getResourceUri(host,"resource/domain", "ug"));
			domain_resources.put(Domain.PEOPLE, LD4SDataResource.getResourceUri(host,"resource/domain", "ug"));
			domain_resources.put(Domain.LIFESCIENCE, LD4SDataResource.getResourceUri(host,"resource/domain", "ls"));
			domain_resources.put(Domain.CROSSDOMAIN, LD4SDataResource.getResourceUri(host,"resource/domain", "cross"));
			domain_resources.put(Domain.ENCYCLOPEDIC, LD4SDataResource.getResourceUri(host,"resource/domain", "encyc"));
			domain_resources.put(Domain.ALL, LD4SDataResource.getResourceUri(host,"resource/domain", "all"));
			domain_resources.put(Domain.UNIT, LD4SDataResource.getResourceUri(host,"resource/domain", "quantity"));
		}

		public Domain string2Domain(String name){
			if (name.compareToIgnoreCase(Domain.GEOGRAPHY.toString()) == 0){
				return Domain.GEOGRAPHY;
			}
			if (name.compareToIgnoreCase(Domain.CROSSDOMAIN.toString()) == 0){
				return Domain.CROSSDOMAIN;
			}
			if (name.compareToIgnoreCase(Domain.LIFESCIENCE.toString()) == 0){
				return Domain.LIFESCIENCE;
			}
			if (name.compareToIgnoreCase(Domain.PUBLICATION.toString()) == 0){
				return Domain.PUBLICATION;
			}
			if (name.compareToIgnoreCase(Domain.GOVERNMENT.toString()) == 0){
				return Domain.GOVERNMENT;
			}
			if (name.compareToIgnoreCase(Domain.USERGENERATED.toString()) == 0){
				return Domain.USERGENERATED;
			}
			if (name.compareToIgnoreCase(Domain.MEDIA.toString()) == 0){
				return Domain.MEDIA;
			}
			return null;
		}

		private static String getBaseDomain(String uri){
			//skip the initial http://
			if (uri.startsWith("http://")){
				uri = uri.substring("http://".length(), uri.length());
			}
			if (uri.startsWith("www.")){
				uri = uri.substring("www.".length(), uri.length());
			}
			if (uri.endsWith("/")){
				uri = uri.substring(0, uri.length()-1);
			}
			//cut whatever follows the first next slash (if there is one)
			int indslash = uri.indexOf("/");
			if (indslash != -1){
				uri = uri.substring(0, indslash);
			}
			return uri;
		}

		public static Domain uri2Domain(String uri){
			Domain ret = null, key = null;
			if (uri != null){
				uri = getBaseDomain(uri);
				LinkedList<String> list = null;
				Iterator<Domain> it = domain2Uri.keySet().iterator();
				while (it.hasNext() && ret == null){
					key = it.next();
					list = domain2Uri.get(key);
					if (list.contains(uri)){
						ret = key; 
					}	
				}
			}
			return ret;
		}

		public static ObjectProperty domain2RDFProperty(Domain domain){
			ObjectProperty ret = null;
			if (domain != null){
				switch(domain){
				case GEOGRAPHY:
					ret = SptVocab.GEOGRAPHY;
					break;
				case PUBLICATION:
					ret = SptVocab.PUBLICATION;
					break;
				case LIFESCIENCE:
					ret = SptVocab.LIFESCIENCE;
					break;
				case MEDIA:
					ret = SptVocab.MEDIA;
					break;
				case PEOPLE:
				case USERGENERATED:
					ret = SptVocab.USER_GEN;
					break;
				case GOVERNMENT:
					ret = SptVocab.GOV;
					break;
				case UNIT:
					ret = SptVocab.QUANTITY;
					break;
				default:
					ret = SptVocab.CROSS;
				}
			}
			return ret;
		}
		/**
		 * 
		 * @param rel predicate URI
		 * @return
		 */
		public static String spaceRel2RDFProperty(String rel){
			if (rel == null || rel.trim().compareTo("")==0){
				return null;
			}
			String ret = null;
			for (int i=0; i<spaceRelations.length&&ret==null ;i++){
				if (rel.compareToIgnoreCase(spaceRelations[i])==0){
					ret = relation_sem[i];
				}
			}
			return ret;
		}

		public void setSpace(String[] space) {
			this.space_names_prefix = space;
		}

		public void setSpace(String query) throws Exception {
			this.space_names_prefix = getQueryComponents(query);
		}


		private String[] getQueryComponents(String query) throws Exception{
			String[] ret = null;
			//get positions in the query of each relation
			ArrayList<Integer> positions = new ArrayList<Integer>();
			int indrel = -1;
			for (int i=0; i<spaceRelations.length ;i++){
				//if the current type of relation is among the filters
				indrel = query.indexOf(spaceRelations[i]);
				if (indrel != -1){
					positions.add(indrel);
				}
			}
			Collections.sort(positions);
			Integer[] positionsarr = new Integer[positions.size()];
			positionsarr = positions.toArray(positionsarr);
			//cut off the logic expression object of each relation
			ret = new String[positions.size()];
			int ind = 0, relend = -1, relid=0;
			String curr_rel = "", logicexpr = null;
			for (int c=0; c<positionsarr.length ;c++){
				if(c+1<positionsarr.length){
					logicexpr = query.substring(positionsarr[c], positionsarr[c+1]);
				}else{
					logicexpr = query.substring(positionsarr[c], query.length());
				}
				//get the current rel in infix notation with the logic expression
				relend = logicexpr.indexOf("(");
				if (relend == -1){
					throw new Exception("Invalid query for filtering space data");
				}
				curr_rel = logicexpr.substring(0, relend); 
				logicexpr = logicexpr.substring(relend+1, logicexpr.length()-1);
				for (int i=0; i<spaceRelations.length ;i++){
					if (spaceRelations[i].compareTo(curr_rel) == 0){
						relid = i;
					}
				}
				//check that the logic expression is in prefix notation

				if (isPrefix(logicexpr)){
					ret[ind++] = fromPrefixToInfix(logicexpr, "* <"+relation_sem[relid]+">");
				}else{
					throw new Exception("Invalid query for filtering space data: the logic expression is not represented in prefix notation.");
				}
			}
			return ret;
		}

		public static boolean isPrefix(String logicexpr){
			boolean prefix = false;
			for (int i = 0; 
			i<LogicOperators.length
			&&
			!(prefix=logicexpr.contains(LogicOperators[i]+"(")); i++)
				;
			return prefix;
		}

		public String[] getSpace() {
			return space_names_prefix;
		}

		public void setThing(String query) throws Exception {
			if (isPrefix(query)){
				query = fromPrefixToInfix(query, "");
			}
			this.thing = query;
		}

		public String getThing() {
			return thing;
		}

		public void setDomains(Domain[] domains) {
			if (domains != null && domains.length > 0)
				this.domains = domains;
		}

		/**
		 * 
		 * @param domains string containing domains separated by "%20OR%20"
		 */
		public void setDomains(String domains) {
			String[] temp = null;
			if (domains != null && domains.trim().compareTo("null")!=0){
				temp = domains.split("%20OR%20");
				if (temp == null || temp.length == 1){
					temp = domains.split(" OR ");
				}
			}
			if (temp != null){
				setDomains(temp);
			}
		}

		public void setDomains(String[] sdomains) {
			if (sdomains != null && sdomains.length > 0){
				Domain[] domains = new Domain[sdomains.length];
				for (int i=0; i<sdomains.length ;i++){
					if (sdomains[i].startsWith("http://")){
						domains[i] = uri2Domain(sdomains[i]);
					}else{
						domains[i] = string2Domain(sdomains[i]);
					}			
				}
				this.domains = domains;
			}
		}

		public Domain[] getDomains() {
			return domains;
		}

		/**
		 * Unstable functionality.
		 * 
		 * @param excluded_domains
		 */
		public void setExcluded_domains(String excluded_domains) {
			this.excluded_domains = excluded_domains;
		}

		public String getExcluded_domains() {
			return excluded_domains;
		}

		public void setTime_range(Date[] time_range) {
			this.time_range = time_range;
		}

		public void setTime_range(String time_range, String split) throws ParseException{
			String[] sp = time_range.split(split);
			if (sp.length == 2){
				this.time_range = new Date[2];
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
				this.time_range[0] = dateFormat.parse(sp[0]);
				this.time_range[1] = dateFormat.parse(sp[1]);
			}else{
				throw new ParseException("Time range set wrongly.", -1);
			}
		}

		public Date[] getTime_range() {
			return time_range;
		}

		public static void main(String[] args){
			String q = "AND(q,w,e,r,OR(t,y,o),u,i,OR(o,p,AND(a,s,d,f,g),h,k,j),b,v)";
			try {
				System.out.println(fromPrefixToInfix(q, "* under"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Transform a logic expression (including an unlimited number of nested ones) 
		 * representation from prefix to infix notation. In the logic expression, 
		 * the logic operators defined in this class are declare in prefix notation.
		 * @param q logic expression represented in prefix notation
		 * @param subj_predicate subject and predicate that the logic expression is referred to
		 * @return logic expression in infix notation
		 * @throws Exception 
		 */
		public static String fromPrefixToInfix(String q, String subj_predicate)
		throws Exception{
			String beforeafter = "", nestedrel=null, whatisnested, temp, finalquery = null;
			//substring until the first ')' not matched by any other '('
			String rel = null;
			for (int r = 0; r<LogicOperators.length&&rel==null ;r++){
				if (q.startsWith(LogicOperators[r])){
					rel = LogicOperators[r];
				}
			}
			if (rel == null){
				throw new Exception("Invalid logic expression: missing logic operator.");
			}
			q = q.substring(q.indexOf(rel)+rel.length()+1,q.length()-1);
			//		while (subq.contains("AND") || subq.contains("OR")){
			int indopen = q.indexOf("("),indclosed=q.indexOf(")");
			//while there is any nested relation, dig into it
			while (indopen < indclosed && indopen>0){
				//get the type of nested relation
				temp = q.substring(0, indopen);
				for (int r = 0; r<LogicOperators.length ;r++){
					if (temp.endsWith(LogicOperators[r])){
						nestedrel = LogicOperators[r];
						beforeafter += q.substring(0, indopen-nestedrel.length());
					}
				}
				if (nestedrel == null){
					throw new Exception("Parentheses opened without matching with any logic operator.");
				}
				//get what is nested
				whatisnested = nestedrel+"(";
				temp = q.substring(indopen+1, q.length()); 
				indopen = temp.indexOf("("); 
				indclosed = temp.indexOf(")");
				while (indopen<indclosed&&indopen >= 0){
					whatisnested += temp.substring(0, indclosed+1);
					temp = temp.substring(indclosed+1, temp.length());
					indopen = temp.indexOf("("); 
					indclosed = temp.indexOf(")");
				}
				//if a closing parentheses is missing
				if (indclosed < 0){
					throw new Exception("Invalid logic expression: missing ')'.");
				}
				whatisnested += temp.substring(0, indclosed+1);
				if (finalquery != null){
					finalquery += " "+rel+" ";
				}else{
					finalquery = "";
				}
				finalquery += fromPrefixToInfix(whatisnested, subj_predicate);
				//continue with what follows the nested relation
				q = temp.substring(indclosed+1, temp.length());
				indopen = q.indexOf("(");
				indclosed=q.indexOf(")");
			}			
			//		}
			q = beforeafter + q;
			StringBuilder ret = new StringBuilder("(");
			String[] members = q.split(",");
			for (int i = 0; i<members.length ;i++){
				if (members[i].trim().compareTo("") != 0){
					ret.append(subj_predicate).append(" ").append(members[i]);
					if (i+1<members.length){
						ret.append(" ").append(rel).append(" ");
					}
				}
			}
			if (finalquery != null){
				ret.append(" ").append(rel).append(" ").append(finalquery);
			}
			ret.append(")");
			return ret.toString();
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		public double getConfidence() {
			return confidence;
		}

		/**
		 * Form that can be easily extracted from a query string of type:
		 * ?d=dbpedia.org%20OR%20rkbexplorer.com..
		&s=NEAR<any nested series of OR and AND in prefix notation and items separated by commas>
		UNDER<any nested series of OR and AND in prefix notation and items separated by commas>
		OVER<any nested series of OR and AND in prefix notation and items separated by commas>
		&th=<any nested series of OR and AND in prefix notation and items separated by commas>
		&trange=<start datetime>_<end datetime>
		 * @param qform
		 * @throws Exception
		 */
		public Context(Form qform, String localhost) throws Exception{
			if (qform != null){
				if (domain2Uri == null){
					initDomain2Uri();
				}
				if (domain_resources == null){
					initDomainResources(localhost);
				}
				if (qform.getFirst("s") != null)
					this.setSpace(qform.getFirst("s").getValue());
				if (qform.getFirst("trange") != null)
					this.setTime_range(qform.getFirst("trange").getValue(), "_");
				if (qform.getFirst("th") != null)
					this.setThing(qform.getFirst("th").getValue());
				if (qform.getFirst("d") != null){
					String doms = qform.getFirst("d").getValue();
					if (doms != null){
						this.setDomains(doms);
					}
				}
				if (qform.getFirst("nod") != null){
					String nodoms = qform.getFirst("nod").getValue();
					if (nodoms != null){
						this.setExcluded_domains(nodoms);
					}
				}
			}
		}

		/**
		 * String of type:
		 * d=dbpedia.org%20OR%20rkbexplorer.com..
		&s=NEAR<any nested series of OR and AND in prefix notation and items separated by commas>
		UNDER<any nested series of OR and AND in prefix notation and items separated by commas>
		OVER<any nested series of OR and AND in prefix notation and items separated by commas>
		&th=<any nested series of OR and AND in prefix notation and items separated by commas>
		&trange=<start datetime>_<end datetime>
		 * @param qform
		 * @throws Exception
		 */
		public Context(String str, String localhost) throws Exception{
			if (str != null){
				if (domain2Uri == null){
					initDomain2Uri();
				}
				if (domain_resources == null){
					initDomainResources(localhost);
				}
				String[] components = str.split("&");
				for (int i=0; i<components.length ;i++){
					if (components[i].trim().startsWith("s=")){
						components[i] = components[i].substring(2,components[i].length());
						this.setSpace(components[i]);
					}else if (components[i].trim().startsWith("th=")){
						components[i] = components[i].substring(3,components[i].length());
						this.setThing(components[i]);
					}else if (components[i].trim().startsWith("trange=")){
						components[i] = components[i].substring(7,components[i].length());
						this.setTime_range(components[i], "_");
					} else if (components[i].trim().startsWith("d=")){
						components[i] = components[i].substring(2,components[i].length());
						this.setDomains(components[i]);
					}  
				}
			}
		}

		/**
		 * JSON data of type:
		 * ?d=dbpedia.org%20OR%20rkbexplorer.com..
		&s=NEAR<any nested series of OR and AND in prefix notation and items separated by commas>
		UNDER<any nested series of OR and AND in prefix notation and items separated by commas>
		OVER<any nested series of OR and AND in prefix notation and items separated by commas>
		&th=<any nested series of OR and AND in prefix notation and items separated by commas>
		&trange=<start datetime>_<end datetime>
		 * @param qform
		 * @throws Exception
		 */
		public Context(JSONObject json, String localhost) throws Exception{
			if (json != null){
				if (domain2Uri == null){
					initDomain2Uri();
				}
				if (domain_resources == null){
					initDomainResources(localhost);
				}
				if (json.has("s") && json.getString("s") != null)
					this.setSpace(json.getString("s"));
				if (json.has("trange") && json.getString("trange") != null)
					this.setTime_range(json.getString("trange"), "_");
				if (json.has("th") && json.getString("th") != null)
					this.setThing(json.getString("th"));
				if (json.has("d") && json.getString("d") != null){
					String doms = json.getString("d");
					if (doms != null){
						this.setDomains(doms);
					}
				}
			}
		}

		public boolean isEmpty(){
			return (this.domains == null
					|| this.domains.length > 0)
					&& this.space_names_prefix == null
					&& this.time_range == null
					&& this.thing == null;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Person getPerson() {
			return person;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation_coords(String[] location_coords) {
			this.location_coords = location_coords;
		}

		public String[] getLocation_coords() {
			return location_coords;
		}

		public void setAdditionalTerms(String[][] additionalTerms) {
			this.additionalPredicateTerms = additionalTerms;
		}

		public String[][] getAdditionalTerms() {
			return additionalPredicateTerms;
		}

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}
}
