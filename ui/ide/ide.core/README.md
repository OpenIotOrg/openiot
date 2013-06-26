OpenIoT Scheduler
=================

What is it?
-----------
Global Scheduler formulates the request based on the user inputs (request definition). It parses each service request and accordingly it interacts with the rest of the OpenIoT platform through the Cloud Database (DB). In particular, the Scheduler performs the following functions:
* Sensor and ICO selection. As part of this function the Scheduler queries the OpenIoT discovery service through the �availableSensors� entity in order to find the set of sensors (physical or virtual) and ICOs that fulfil the criteria specified in the scope of service request. Note that the service discovery functionality is based on the semantic properties of the sensors.  The Service Discovery components will return to the Scheduler a list of sensors (along with their unique identifiers in the OpenIoT system) that could be used for delivering the specified service (i.e., a list of sensors fulfilling the criteria set as part of the service request).
* Virtual Sensor �indirect� activation. Following the selection/identification of the sensors and ICOs that meet the specified criteria and at the request formulation time, the Scheduler will inform the virtual sensors (GSN nodes) about which of them are used by the service being scheduled. This information is kept in the �sensorServiceRelation� entity and is accessed by the virtual sensors.
* Request Storing and Activation. The Scheduler is also responsible to provide information to the Service Delivery & Utility Manager (SD&UM) regarding the services to be delivered. This is done through the cloud storage and more specifically the �serviceDeliveryDescription� entity where all the information related to a specific service is stored. 
* Service Status Update. Through a service lifecycle the Scheduler update its status at the �serviceStatus� entity. Moreover, it is able to retrieve a service status to inform the User. 



System requirements
-------------------

All you need to build this project is Java 7.0 (Java SDK 1.7) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7.1. 

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.md#mavenconfiguration) before testing the quickstarts.


Start JBoss Enterprise Application Platform 6 or JBoss AS 7.1 with the Web Profile
-------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat

 
Build and Deploy the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy

4. This will deploy `target/ide.core.war` to the running instance of the server.
 

Access the application 
---------------------

The application will be running at the following URL: <http://localhost:8080/ide.core/>.


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Run the Arquillian Tests 
-------------------------

This quickstart provides Arquillian tests. By default, these tests are configured to be skipped as Arquillian tests require the use of a container. 

_NOTE: The following commands assume you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Run the Arquillian Tests](../README.md#arquilliantests) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type the following command to run the test goal with the following profile activated:

        mvn clean test -Parq-jbossas-remote 


Run the Quickstart in JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](../README.md#useeclipse) 


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

