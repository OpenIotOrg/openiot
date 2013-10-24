package org.openiot.ld4s.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.openiot.ld4s.vocabulary.LD4SConstants;
import org.restlet.security.Role;

/**
 * Provides access to the values stored in the ld4s.properties file.
 *
 * @author Myriam Leggieri
 */
public class ServerProperties {
	
	public final static Role PUBLISHER = new Role("publisher", "publisher");
	public final static Role ADMINISTRATOR = new Role("admin", "administrator");
	public final static Role ANONYMOUS= new Role("anonymous", "anonymous");


	public static String SERVER = "http://0.0.0.0";
	public static final int PORT = 8182;
	public static final String CONTEXT_ROOT = "ld4s";

	/** The ld4s server admin e-mail */
	public static final String ADMIN_EMAIL_KEY = "ld4s.admin.email";
	/** The ld4s hostname key. */
	public static final String HOSTNAME_KEY = "ld4s.hostname";
	/** The ld4s context root. */
	public static final String CONTEXT_ROOT_KEY = "ld4s.context.root";
	/** The logging level key. */
	public static final String LOGGING_LEVEL_KEY = "ld4s.logging.level";
	/** The ld4s port key. */
	public static final String PORT_KEY = "ld4s.port";
	/** The RDF directory key. */
	public static final String RDF_DIR_KEY = "ld4s.rdf.dir";
	/** The directory containing the Unit of Measurement file. */
	public static final String UOM_FILE_KEY = "ld4s.uom.file";
	/** The Restlet Logging key. */
	public static final String RESTLET_LOGGING_KEY = "ld4s.restlet.logging";
	/** The dpd port key during testing. */
	public static final String TEST_PORT_KEY = "ld4s.test.port";
	/** The test installation key. */
	public static final String TEST_INSTALL_KEY = "ld4s.test.install";
	/** The test installation key. */
	public static final String TEST_HOSTNAME_KEY = "ld4s.test.hostname";

	/** The path to the ontology module for client-defined types */
	public static final String NEW_TYPES_ONTOLOGY = "ld4s.new.types.ontology";

	/** The path of the RDF schema. */
	// public static final String SCHEMA_FULLPATH_KEY = "ld4s.schema.path";

	//	  /** Indicates whether SensorBaseClient caching is enabled. */
	//	  public static final String CACHE_ENABLED = "ld4s.cache.enabled";
	//	  /** The maxLife in days for each instance in each SensorBaseClient cache. */
	//	  public static final String CACHE_MAX_LIFE = "ld4s.cache.max.life";
	//	  /** The total capacity of each SensorBaseClient cache. */
	//	  public static final String CACHE_CAPACITY = "ld4s.cache.capacity";
	/** Whether or not the front side cache is enabled. */
	public static final String FRONTSIDECACHE_ENABLED = "ld4s.cache.frontside.enabled";

	/** Where we store the properties. */
	private Properties properties;
	
	private final String foldername = System.getProperty("user.home") + LD4SConstants.SYSTEM_SEPARATOR
	+".ld4s";

	/**
	 * Creates a new ServerProperties instance. Prints an error to the console if problems occur on
	 * loading.
	 */
	public ServerProperties() {
		try {
			initializeProperties();
//			initTDB();
		}
		catch (Exception e) {
			System.out.println("Error initializing server properties: " + e.getMessage());
		}
	}

	

	/**
	 * Reads in the properties in ~/.hackystat/ld4s/ld4s.properties if this
	 * file exists, and provides default values for all properties.
	 *
	 * @throws Exception if errors occur.
	 */
	private void initializeProperties() throws Exception {
		String userDir = System.getProperty("user.dir");
		String propFile = getFoldername()+LD4SConstants.SYSTEM_SEPARATOR
		+"ld4s.properties";
		this.properties = new Properties();
		// Set defaults
		properties.setProperty(HOSTNAME_KEY, InetAddress.getLocalHost().getHostAddress());
		ServerProperties.SERVER = properties.getProperty(HOSTNAME_KEY);
		properties.setProperty(PORT_KEY, String.valueOf(PORT));
		properties.setProperty(CONTEXT_ROOT_KEY, CONTEXT_ROOT);
		properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
		properties.setProperty(RDF_DIR_KEY, userDir + "/.ld4s/rdf");
		properties.setProperty(UOM_FILE_KEY, userDir + "/.ld4s/uom/"+"ucum-essence.xml");
		properties.setProperty(TEST_PORT_KEY, "9875");
		properties.setProperty(TEST_HOSTNAME_KEY, "0.0.0.0");
		properties.setProperty(FRONTSIDECACHE_ENABLED, "true");
		//	    properties.setProperty(CACHE_ENABLED, "true");
		//	    properties.setProperty(CACHE_MAX_LIFE, "365");
		//	    properties.setProperty(CACHE_CAPACITY, "500000");
		properties.setProperty(ADMIN_EMAIL_KEY, "admin");
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(propFile);
			System.out.println("Loading LD4Sensors properties from: " + propFile);
			properties.load(stream);
		}
		catch (IOException e) {
			System.out.println(propFile + " not found.");
			//try to create the required folder and file
			File folder = new File(getFoldername());
			if (!folder.exists()){
				folder.mkdir();
			}
			File file = new File (propFile);
			if (folder.exists() && !file.exists()){
				try{if(file.createNewFile()){
					trimProperties(properties);
					properties.store(new FileOutputStream(propFile), null);
					System.out.println("File "+propFile+" created.");
				}}catch(IOException e1){ System.out.println("Unable to create "+propFile); }
			}
			System.out.println("Using default ld4s properties.");
		}
		finally {
			if (stream != null) {
				stream.close();
			}
		}

		trimProperties(properties);
		// Now add to System properties.
		Properties systemProperties = System.getProperties();
		systemProperties.putAll(properties);
		System.setProperties(systemProperties);
	}

	/**
	 * Sets the following properties' values to their "test" equivalent.
	 * <ul>
	 * <li>HOSTNAME_KEY
	 * <li>PORT_KEY
	 * <li>DEFINITIONS_DIR
	 * </ul>
	 * Also sets TEST_INSTALL_KEY's value to "true".
	 */
	public void setTestProperties() {
		properties.setProperty(HOSTNAME_KEY, properties.getProperty(TEST_HOSTNAME_KEY));
		properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
		properties.setProperty(TEST_INSTALL_KEY, "true");
		//	    properties.setProperty(CACHE_ENABLED, falseString);
		properties.setProperty(FRONTSIDECACHE_ENABLED, "false");
		trimProperties(properties);
	}

	/**
	 * Returns the value of the Server Property specified by the key.
	 *
	 * @param key Should be one of the public static final strings in this class.
	 * @return The value of the key, or null if not found.
	 */
	public String get(String key) {
		return this.properties.getProperty(key);
	}

	/**
	 * Ensures that the there is no leading or trailing whitespace in the property values. The fact
	 * that we need to do this indicates a bug in Java's Properties implementation to me.
	 *
	 * @param properties The properties.
	 */
	private void trimProperties(Properties properties) {
		// Have to do this iteration in a Java 5 compatible manner. no stringPropertyNames().
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String propName = (String) entry.getKey();
			properties.setProperty(propName, properties.getProperty(propName).trim());
		}
	}

	/**
	 * Returns the fully qualified host name, such as "http://0.0.0.0:9877/ld4s/".
	 *
	 * @return The fully qualified host name.
	 */
	public String getFullHost() {
		return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY) + "/";
	}

	//	  /**
	//	   * Returns true if caching is enabled in this service.
	//	   *
	//	   * @return True if caching enabled.
	//	   */
	//	  public boolean isCacheEnabled() {
	//	    return "True".equalsIgnoreCase(this.properties.getProperty(CACHE_ENABLED));
	//	  }

	/**
	 * Returns true if front side caching is enabled in this service.
	 *
	 * @return True if caching enabled.
	 */
	public boolean isFrontSideCacheEnabled() {
		return "True".equalsIgnoreCase(this.properties.getProperty(FRONTSIDECACHE_ENABLED));
	}

	//	  /**
	//	   * Returns the caching max life as a double. If the property has an illegal value, then return the
	//	   * default.
	//	   *
	//	   * @return The max life of each instance in the cache.
	//	   */
	//	  public double getCacheMaxLife() {
	//	    String maxLifeString = this.properties.getProperty(CACHE_MAX_LIFE);
	//	    double maxLife = 0;
	//	    try {
	//	      maxLife = Double.valueOf(maxLifeString);
	//	    }
	//	    catch (Exception e) {
	//	      System.out.println("Illegal cache max life: " + maxLifeString + ". Using default.");
	//	      maxLife = 365D;
	//	    }
	//	    return maxLife;
	//	  }

	//	  /**
	//	   * Returns the in-memory capacity for each cache. If the property has an illegal value, then
	//	   * return the default.
	//	   *
	//	   * @return The in-memory capacity.
	//	   */
	//	  public long getCacheCapacity() {
	//	    String capacityString = this.properties.getProperty(CACHE_CAPACITY);
	//	    long capacity = 0;
	//	    try {
	//	      capacity = Long.valueOf(capacityString);
	//	    }
	//	    catch (Exception e) {
	//	      System.out.println("Illegal cache capacity: " + capacityString + ". Using default.");
	//	      capacity = 500000L;
	//	    }
	//	    return capacity;
	//	  }

	/**
	 * Returns a string containing all current properties in alphabetical order.
	 *
	 * @return A string with the properties.
	 */
	public String echoProperties() {
		String cr = System.getProperty("line.separator");
		String eq = " = ";
		String pad = "                ";
		// Adding them to a treemap has the effect of alphabetizing them.
		TreeMap<String, String> alphaProps = new TreeMap<String, String>();
		for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
			String propName = (String) entry.getKey();
			String propValue = (String) entry.getValue();
			alphaProps.put(propName, propValue);
		}
		StringBuffer buff = new StringBuffer(30);
		buff.append("LD4Sensors Properties:").append(cr);
		for (String key : alphaProps.keySet()) {
			buff.append(pad).append(key).append(eq).append(get(key)).append(cr);
		}
		return buff.toString();
	}



	public String getFoldername() {
		return foldername;
	}
}