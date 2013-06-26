package org.openiot.scheduler.core.utils.sparql;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

//import java.net.URL;
//import java.net.URLConnection;
//import java.util.*;
import java.io.*;

public class SesameSPARQLClient
{	
	
	
    //Initialize the Logger
	final static Logger logger = LoggerFactory.getLogger(SesameSPARQLClient.class.getName()); 
	
	SPARQLRepository therepository = null;
	
	public SesameSPARQLClient(String url)
	{					
		therepository = new SPARQLRepository(url);
		try 
		{
			therepository.initialize();
		} 
		catch (RepositoryException e) 
		{			
			e.printStackTrace();
		}		
		
		
		

	    
	    // print Logger's internal state (not required for initialization)
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
	    StatusPrinter.print(lc);
	   
	    logger.debug("Hello world Debug by Logger. From : {}",SesameSPARQLClient.class.getName());

		
		
		
	}
	
		
	public String sparqlToXml(String queryString)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try
		{
			RepositoryConnection con = therepository.getConnection();			

			try
			{					
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				tupleQuery.evaluate(new SPARQLResultsXMLWriter(out));
			
				 logger.debug("Returned Query: {}", out.toString());
				
				return out.toString();
			} 
			finally
			{
				con.close();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	
	static String lsm = "http://lsm.deri.ie/sparql";	
	static String  dbBedia = "http://dbpedia.org/sparql";         //hosted on virtuoso	
	
	public static String lsmQuery = "select ?s ?p ?o from <http://lsm.deri.ie/metadata#> where {?s ?p ?o.} limit 20";
	public static String dbBediaQuery = "select ?s ?p ?o where {?s ?p ?o.} limit 20";
	
	public static void main(String[] args)
	{
		

	
		
		SesameSPARQLClient sc = new SesameSPARQLClient(lsm);
		sc.sparqlToXml(lsmQuery);
		//System.out.println(sc.sparqlToXml(lsmQuery));
	}
}//class