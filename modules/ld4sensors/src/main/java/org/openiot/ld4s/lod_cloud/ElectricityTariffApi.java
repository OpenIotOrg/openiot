package org.openiot.ld4s.lod_cloud;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.openiot.ld4s.resource.LD4SDataResource;
import org.openiot.ld4s.resource.link.Link;
import org.openiot.ld4s.vocabulary.SptVocab;
import org.restlet.security.User;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ElectricityTariffApi extends SearchRouter {

	private static final String dataSourcePath = "http://spitfire-project.eu/energy.rdf"; 

	public ElectricityTariffApi(String baseHost, Context context, User author,
			Resource from_resource) {
		super(baseHost, context, author, from_resource);
		// TODO Auto-generated constructor stub
	}

	protected String buildQueryString(){
		String sparqlQuery = "prefix spt: <http://spitfire-project.eu/ontology/ns/> prefix " +
				"xsd: <http://www.w3.org/2001/XMLSchema#> prefix en: <http://spitfire-project.eu/ontology/ns/en/>" +
				"select ?price ?plan ?uom {?plan a en:TariffPlan . ?plan en:forEnergy spt:Electrical .";
//				if (energyType != null){
//					sparqlQuery += energyType;
//				}else{
//					sparqlQuery += " ?entype ";
//				}
				sparqlQuery += "?plan spt:uom ?uom ." ;
				if (context.getCountry() != null){
					sparqlQuery += "?plan en:covers <"+context.getCountry()+"> .";
				}
				if (context.getCompany() != null){
					sparqlQuery += "?plan en:byCompany <"+context.getCompany() +"> . " ;
				}
				sparqlQuery += "?plan en:price ?price . ?plan en:dateRange ?dr .";
				if (context.getDate() != null){
					sparqlQuery += 
							"?plan en:dayTimeRange ?dtr . ?dr en:fromDate ?dstart ; en:toDate ?dend ." +
									"FILTER(?dend >= \""+context.getDate()+"\"^^xsd:date) . FILTER(?dstart <= \""+context.getDate()+"\"^^xsd:date) ."; 
				}
				if (context.getTime() != null){
					sparqlQuery += " ?dtr en:fromDayTime ?dt1 ; en:toDayTime ?dt2 . " +
							" ?dt1 en:fromTime ?tstart1 ; en:toTime ?tend1 .  ?dt2 en:fromTime ?tstart2 ; en:toTime ?tend2 . " +
							"FILTER(?tstart2 >= \""+context.getTime()+"\"^^xsd:time) . " +
							"FILTER(?tstart1 <= \""+context.getTime()+"\"^^xsd:time) . ";
				}

		return sparqlQuery+"}";
		//		return "select * {?s ?p ?o}limit 10";

	}

	protected Model createLink(String to, String price, String uom) {
		Model model = from_resource.getModel();
		if (to == null){
			return model;
		}		
		//gets additional info to the local LD4S reosurce (inherited from the external linked one)
		//i.e. all the dcterms:subject triples		

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
			to_resource.addProperty(model.createProperty(
					"http://spitfire-project.eu/ontology/ns/en/price"), price);
			to_resource.addProperty(model.createProperty(
					"http://spitfire-project.eu/ontology/ns/uom"), uom);
			model.add(to_resource.getModel());
		}
		return model;
	}

	private static boolean isUriAccessible(String uri) {
		HttpURLConnection connection = null;
		int code = -1;
		URL myurl;
		try {
			myurl = new URL(uri);

			connection = (HttpURLConnection) myurl.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(1000);
			code = connection.getResponseCode();
		} catch (MalformedURLException e) {
			System.err.println(uri + " is not accessible.");
		} catch (ProtocolException e) {
			System.err.println(uri + " is not accessible.");
		} catch (IOException e) {
			System.err.println(uri + " is not accessible.");
		}
		return (code == 200) ? true : false;
	}

	@Override
	public Model start() throws Exception {
		Model fmodel = from_resource.getModel();
		String query = buildQueryString();
		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		if (!isUriAccessible(dataSourcePath)){
			return fmodel;
		}
		model.read(dataSourcePath, "RDF/XML");
		QueryExecution qex = null;
		try{
		qex = QueryExecutionFactory.create(query, model);
		ResultSet results = qex.execSelect();
		
		// Output query results	
//		ResultSetFormatter.out(System.out, results, sparqlq);
		System.out.println(results);
		
		QuerySolution row = null;
		//for each price: link to the price
		//for each tariff plan, link to the tariff plan
		while (results.hasNext()) {
		    row= results.next();
		    Resource vplan = (Resource)row.get("plan");
		    Literal vprice = row.getLiteral("price");
		    Literal vuom = row.getLiteral("uom");
		    
		    fmodel.add(createLink(vplan.getURI(), vprice.getString(), vuom.getString()));
		    
		    System.out.println(vplan.getURI()+" has price "+vprice.getString());
		    
		}

		}finally{
			if (qex != null){
				qex.close();
				model.close();
				
			}
		}
		return fmodel;
	}

}
