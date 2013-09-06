package org.openiot.sdum.core.utils.sparql;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class SesameSPARQLClient
{
	//Initialize the Logger
	final static Logger logger = LoggerFactory.getLogger(SesameSPARQLClient.class.getName()); 
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
		
		 // print Logger's internal state (not required for initialization)
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
	    StatusPrinter.print(lc);
	   
	    logger.debug("Hello world Debug by Logger. From : {}",SesameSPARQLClient.class.getName());
	}	
	
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