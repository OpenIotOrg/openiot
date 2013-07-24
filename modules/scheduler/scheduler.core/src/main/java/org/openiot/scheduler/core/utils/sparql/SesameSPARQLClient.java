package org.openiot.scheduler.core.utils.sparql;


import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SesameSPARQLClient
{	
	final static Logger logger = LoggerFactory.getLogger(SesameSPARQLClient.class); 
		
	private SPARQLRepository therepository = null;	
	
	
	public SesameSPARQLClient() throws RepositoryException
	{					
		therepository = new SPARQLRepository("http://lsm.deri.ie/sparql");
		
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