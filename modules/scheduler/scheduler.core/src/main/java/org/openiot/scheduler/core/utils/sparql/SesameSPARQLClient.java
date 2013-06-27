package org.openiot.scheduler.core.utils.sparql;


import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

//import java.net.URL;
//import java.net.URLConnection;
//import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SesameSPARQLClient
<<<<<<< HEAD
{
	private SPARQLRepository therepository = null;
		
	public SesameSPARQLClient()
	{					
		therepository = new SPARQLRepository("http://lsm.deri.ie/sparql");
		
		try 
		{
			therepository.initialize();
		} 
		catch (RepositoryException e) 
		{			
			e.printStackTrace();
		}		
	}
=======
{	
	
	
    //Initialize the Logger
	final static Logger logger = LoggerFactory.getLogger(SesameSPARQLClient.class.getName()); 
	
	SPARQLRepository therepository = null;
	
>>>>>>> a0f855027ab27c2ee9735e60106621a36e59db3c
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
<<<<<<< HEAD
=======
		
		
		

	    
	    // print Logger's internal state (not required for initialization)
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
	    StatusPrinter.print(lc);
	   
	    logger.debug("Hello world Debug by Logger. From : {}",SesameSPARQLClient.class.getName());

		
		
		
>>>>>>> a0f855027ab27c2ee9735e60106621a36e59db3c
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
	
	public TupleQueryResult sparqlToQResult(String queryString)
	{		
		try
		{
			RepositoryConnection con = therepository.getConnection();
			try
			{
				TupleQuery query = con.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, queryString);
				TupleQueryResult qres = query.evaluate();
				
				
				return qres;
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
		
	
}//class