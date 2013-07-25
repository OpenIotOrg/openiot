package org.openiot.gsn.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * This class is designed to call Ant targets from any Java application.
 *  1. Initialize a new Project by calling "init" 
 *  2. Feed Ant with some properties
 * by calling "setProperties" (optional) 
 * 3. Run an Ant target by calling
 * "runTarget" Example : 
 * try { 
 * 	//init 
 * 	init("/home/me/build.xml","/home/me/");
 * 	//properties 
 * 	HashMap m = new HashMap();
 * 	m.put("event", "test");
 * 	m.put("subject", "sujet java 3"); 
 * 	m.put("message", "message java 3");
 * 	setProperties(m, false);
 * 	//run runTarget("test"); 
 * } catch (Exception e) {
 * 	e.printStackTrace(); 
 * }
 */

public class AntRunner {
    public static void nonBlockingAntTaskExecution(Map properties,
	    String taskName) throws Exception {
	AntRunner antRunner = new AntRunner();
	antRunner.init(null, null);
	if (properties != null)
	    antRunner.setProperties(properties, true);
	antRunner.runTarget(taskName, true);
    }

    public static void blockingAntTaskExecution(Map properties, String taskName)
	    throws Exception {
	AntRunner antRunner = new AntRunner();
	antRunner.init(null, null);
	if (properties != null)
	    antRunner.setProperties(properties, true);
	antRunner.runTarget(taskName, false);
    }

    private Project project;

    /**
         * Initializes a new Ant Project.
         * 
         * @param _buildFile
         *                The build File to use. If none is provided, it will be \
         *                defaulted to "build.xml".
         * @param _baseDir
         *                The project's base directory. If none is provided,
         *                will be \ defaulted to "." (the current directory).
         * @throws Exception
         *                 Exceptions are self-explanatory (read their Message)
         */
    public void init(String _buildFile, String _baseDir) throws Exception {
	// Create a new project, and perform some default initialization
	project = new Project();
	try {
	    project.init();
	} catch (BuildException e) {
	    throw new Exception("The default task list could not be loaded.");
	}

	// Set the base directory. If none is given, "." is used.
	if (_baseDir == null)
	    _baseDir = new String(".");
	try {
	    project.setBasedir(_baseDir);
	} catch (BuildException e) {
	    throw new Exception(
		    "The given basedir doesn't exist, or isn't a directory.");
	}

	if (_buildFile == null)
	    _buildFile = new String("build.xml");
	try {
	    ProjectHelper.getProjectHelper().parse(project,
		    new File(_buildFile));
	} catch (BuildException e) {
	    throw new Exception("Configuration file " + _buildFile
		    + " is invalid, or cannot be read.");
	}
    }

    /**
         * Sets the project's properties. May be called to set project-wide
         * properties, or just before a target call to \ set target-related
         * properties only.
         * 
         * @param _properties
         *                A map containing the properties' name/value couples
         * @param _overridable
         *                If set, the provided properties values may be
         *                overriden \ by the config file's values
         * @throws Exception
         *                 Exceptions are self-explanatory (read their Message)
         */
    public void setProperties(Map _properties, boolean _overridable)
	    throws Exception {
	// Test if the project exists
	if (project == null)
	    throw new Exception(
		    "Properties cannot be set because the project has not been initialized. Please call the 'init' method first !");

	// Property hashmap is null
	if (_properties == null)
	    throw new Exception("The provided property map is null.");

	// Loop through the property map
	Set propertyNames = _properties.keySet();
	Iterator iter = propertyNames.iterator();
	while (iter.hasNext()) {
	    // Get the property's name and value
	    String propertyName = (String) iter.next();
	    String propertyValue = (String) _properties.get(propertyName);
	    if (propertyValue == null)
		continue;

	    // Set the properties
	    if (_overridable)
		project.setProperty(propertyName, propertyValue);
	    else
		project.setUserProperty(propertyName, propertyValue);
	}
    }

    /**
         * Runs the given Target.
         * 
         * @param _target
         *                The name of the target to run. If null, the project's
         *                default \ target will be used.
         * @throws Exception
         *                 Exceptions are self-explanatory (read their Message)
         */
    public void runTarget(String _target, boolean newThread) throws Exception {
	// Test if the project exists
	if (project == null)
	    throw new Exception(
		    "No target can be launched because the project has not been initialized. Please call the 'init' method first !");
	// If no target is specified, run the default one.
	if (_target == null)
	    _target = project.getDefaultTarget();
	// Run the target
	final String targetToRun = _target;
	project.addBuildListener(new BuildListener() {

	    public void buildFinished(BuildEvent arg0) {
		
	    }

	    public void buildStarted(BuildEvent arg0) {
		
	    }

	    public void messageLogged(BuildEvent arg0) {

	    }

	    public void targetFinished(BuildEvent arg0) {
		
	    }

	    public void targetStarted(BuildEvent arg0) {

	    }

	    public void taskFinished(BuildEvent arg0) {
		
	    }

	    public void taskStarted(BuildEvent arg0) {
		
	    }});
	if (newThread == true)
	    try {
		Thread t = new Thread(new Runnable() {
		    public void run() {
			project.executeTarget(targetToRun);
		    }
		});
		t.start();
	    } catch (BuildException e) {
		throw new Exception(e.getMessage());
	    }
	else
	    project.executeTarget(targetToRun);
    }

}
