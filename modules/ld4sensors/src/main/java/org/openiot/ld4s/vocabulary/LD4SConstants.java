package org.openiot.ld4s.vocabulary;


import org.openiot.ld4s.server.ServerProperties;

public class LD4SConstants {
	  public static final String SINDICE_VOCAB = "http://sindice.com/vocab/fields#";
	  public static final String SINDICE_SEARCH_VOCAB = "http://sindice.com/vocab/search#";

	  
	public static final String SYSTEM_SEPARATOR = System.getProperty("file.separator");
	public static enum Roles {ADMIN, PUBLISHER}; 
	public static final String PINGTEXT = "LD4S up";
	public static final String AUTHENTICATED_PINGTEXT = "LD4S Authenticated as ";

	/** as stated at: http://www.w3.org/2008/01/rdf-media-types **/
	  public static final String MEDIA_TYPE_TURTLE = "application/x-turtle";
	  public static final String MEDIA_TYPE_NTRIPLES = "text/plain";
	  public static final String MEDIA_TYPE_SPARQL_RESULTS = "application/sparql-results+xml";
	  public static final String MEDIA_TYPE_RDF_JSON = "application/rdf+json";
	  public static final String LANG_N3 = "N3";
	  public static final String LANG_TURTLE = "TURTLE";
	  public static final String LANG_RDFXML = "RDF/XML";
	  public static final String LANG_RDFJSON = "RDF/JSON";
	  public static final String LANG_NTRIPLE = "N-TRIPLE";
	  public static final String LANG_RDFXML_ABBREV = "RDF/XML-ABBREV";
	  
	  public static final String RESOURCE_URI_BASE = "http://"
	      + System.getProperty(ServerProperties.SERVER) + ":"
	      + System.getProperty(String.valueOf(ServerProperties.PORT)) + "/"
	      + System.getProperty(ServerProperties.CONTEXT_ROOT) + "/";
	  
	  
	  
	  /** VoID dataset description URI. */
	  public static final String voIDURI = RESOURCE_URI_BASE + "void";

	  public static final String SEPARATOR1_ID = " ";
	  public static final String SEPARATOR2_ID = "__";
	  public static final String JSON_SEPARATOR = "_";
	  
	  public static String UOM_FILE_PATH = null;
	  
	  public static void setUomFile(String path){
		  LD4SConstants.UOM_FILE_PATH = path;
	  }
}