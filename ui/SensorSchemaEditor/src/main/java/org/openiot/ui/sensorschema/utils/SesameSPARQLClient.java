package org.openiot.ui.sensorschema.utils;

/**
 *    Copyright (c) 2011-2014, OpenIoT
 *    
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 */


import java.io.ByteArrayOutputStream;

import org.openiot.ui.sensorschema.rdf.OpeniotVocab;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SesameSPARQLClient
{	
	final static Logger logger = LoggerFactory.getLogger(SesameSPARQLClient.class); 

	private SPARQLRepository therepository = null;	


	public SesameSPARQLClient() throws RepositoryException
	{					
		
		therepository = new SPARQLRepository(OpeniotVocab.LSM_SPARQL_ENDPOINT);

		try {
			therepository.initialize();
		} 
		catch (RepositoryException e){			
			logger.error("init sparql repository -http://lsm.deri.ie/sparql- error",e);
			throw e;
		}
	}
	public SesameSPARQLClient(String url) throws RepositoryException
	{		
		therepository = new SPARQLRepository(url);
		try {
			therepository.initialize();
		} 
		catch (RepositoryException e){			
			logger.error("init sparql repository -"+url+"- error",e);
			throw e;
		}
	}


	public SPARQLRepository getTherepository() {
		return therepository;
	}	


	public String sparqlToXml(String queryString)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try	{
			RepositoryConnection con = therepository.getConnection();			

			try	{					
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				tupleQuery.evaluate(new SPARQLResultsXMLWriter(out));

				return out.toString();
			} finally {
				con.close();
			}

		} catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}
	public TupleQueryResult sparqlToQResult(String queryString)
	{		
		try{
			RepositoryConnection con = therepository.getConnection();

			try	{
				TupleQuery query = con.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, queryString);
				TupleQueryResult qres = query.evaluate();				

				return qres;
			} 
			finally	{
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}

		return null;
	}
}//class
