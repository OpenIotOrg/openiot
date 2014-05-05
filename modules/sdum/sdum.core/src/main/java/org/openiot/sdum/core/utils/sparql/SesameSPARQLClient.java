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

import org.openiot.commons.util.PropertyManagement;
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
		PropertyManagement propertyManagement = new PropertyManagement();
		
		
		therepository = new SPARQLRepository(propertyManagement.getSdumLsmSparqlEndPoint());
		
		try {
			therepository.initialize();
		} 
		catch (RepositoryException e){			
			logger.error("init sparql repository -"+propertyManagement.getSdumLsmSparqlEndPoint()+"- error",e);
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
			logger.error("sparqlToXml",e);
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
			logger.error("sparqlToQResult",e);
		}
		
		return null;
	}
}//class