package org.openiot.gsn.dynamicSensorControl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.utils.PropertiesReader;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * 
 * @author Christos Georgoulis email:cgeo@ait.edu.gr
 * 
 */
public class SparqlClient {

	private static final Logger logger = Logger.getLogger(SparqlClient.class);

	private SPARQLRepository therepository = null;

	/** Default constructor connectos to predefined LSM Repository */
	public SparqlClient() throws RepositoryException {

		String lsmEndPoint = PropertiesReader.readProperty(
				LSMRepository.LSM_CONFIG_PROPERTIES_FILE, "endPoint");
		therepository = new SPARQLRepository(lsmEndPoint);

		try {
			therepository.initialize();
		} catch (RepositoryException e) {
			logger.error(
					"init sparql repository -http://lsm.deri.ie/sparql- error",
					e);
			throw e;
		}
	}

	/** Constructor accepts Repository url String */
	public SparqlClient(String url) throws RepositoryException {
		try {
			therepository.initialize();
		} catch (RepositoryException e) {
			logger.error("init sparql repository -" + url + "- error", e);
			throw e;
		}
	}

	private TupleQueryResult sparqlToQResult(String queryString) {
		try {
			RepositoryConnection con = therepository.getConnection();

			try {
				TupleQuery query = con.prepareTupleQuery(
						org.openrdf.query.QueryLanguage.SPARQL, queryString);
				TupleQueryResult qres = query.evaluate();

				return qres;
			} finally {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Accepts a string query and a Generic Parser and returns a Generic
	 * Collection
	 * 
	 * @param query
	 * @param p
	 * @return
	 */
	public <T> Collection<T> getQueryResults(String query, Parser<T> p) {
		TupleQueryResult tqr = sparqlToQResult(query);
		Collection<T> results = p.parse(tqr);
		return results;
	}

}
