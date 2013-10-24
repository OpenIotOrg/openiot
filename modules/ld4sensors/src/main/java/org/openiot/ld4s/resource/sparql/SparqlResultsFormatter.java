package org.openiot.ld4s.resource.sparql;

import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SparqlResultsFormatter {
	
	/**
	 * The Sparql XML result format is the only W3C recommendation regarding Sparql result formats
	 * http://www.w3.org/TR/rdf-sparql-XMLres/ even if someone (such as Dbpedia) provides Sparql
	 * results also in RDF/n3 anticipating the W3C verdict about this new format proposed
	 **/
	public static  Object[] xmlResults(ResultSet results) {
	    Object[] ret = null;
	    LinkedList<String> values = new LinkedList<String>();
	    String head = "head", variable = "variable", variable_attribute = "name", resultsStr = "results", resultStr = "result", binding = "binding";
	    Document doc = createDomDocument();

	    // Insert the root element node
	    Element root = doc.createElement("sparql"), headElem = null, subheadElem = null, resultsElem = null, resultElem = null, bindingElem = null;
	    root.setAttribute("xmlns", "\"http://www.w3.org/2005/sparql-results#\"");
	    doc.appendChild(root);

	    // Insert the head section containing all the variables
	    headElem = doc.createElement(head);
	    for (String str : results.getResultVars()) {
	      subheadElem = doc.createElement(variable);
	      subheadElem.setAttribute(variable_attribute, str);
	      headElem.appendChild(subheadElem);
	    }
	    root.appendChild(headElem);

	    // Insert the result section containing all the result variables binded to values
	    /**
	     * The element that contains values should be: RDF URI Reference U
	     * <binding><uri>U</uri></binding> RDF Literal S <binding><literal>S</literal></binding> RDF
	     * Literal S with language L <binding><literal xml:lang="L">S</literal></binding> RDF Typed
	     * Literal S with datatype URI D <binding><literal datatype="D">S</literal></binding> Blank Node
	     * label I <binding><bnode>I</bnode></binding>
	     */
	    resultsElem = doc.createElement(resultsStr);
	    RDFNode x = null;
	    com.hp.hpl.jena.rdf.model.Resource r = null;
	    Element valueElem = null;
	    QuerySolution soln = null;
	    for (; results.hasNext();) {
	      soln = results.nextSolution();
	      resultElem = doc.createElement(resultStr);
	      // Get a result variable by name.
	      for (String str : results.getResultVars()) {
	        x = soln.get(str);
	        bindingElem = doc.createElement(binding);
	        bindingElem.setAttribute(variable_attribute, str);
	        if (x.isLiteral()) {
	          valueElem = doc.createElement("literal");
	          valueElem.appendChild(doc.createTextNode(((Literal) x).getLexicalForm()));
	          values.add(((Literal) x).getLexicalForm());
	        }
	        else if (x.isResource()) {
	          r = (com.hp.hpl.jena.rdf.model.Resource) x;
	          if (!r.isAnon()) {
	            valueElem = doc.createElement("uri");
	            valueElem.appendChild(doc.createTextNode(r.getURI()));
	            values.add(r.getURI());
	          }
	        }
	        if (valueElem != null){
	        	bindingElem.appendChild(valueElem);
	        }
	        if (bindingElem != null){
	        	resultElem.appendChild(bindingElem);
	        }
	      }
	      if (resultElem != null){
	    	  resultsElem.appendChild(resultElem);
	      }
	    }
	    root.appendChild(resultsElem);
	    ret = new Object[] { doc, values };
	    return ret;
	  }
	
	/**
	 *
	 * @return
	 */
	private static Document createDomDocument() {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			return doc;
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
