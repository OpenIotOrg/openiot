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
 * @author Sofiane Sarni
*/

package org.openiot.gsn.utils;

import org.openiot.gsn.beans.GSNSessionAddress;
import org.openiot.gsn.beans.VSensorMonitorConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/*
* GSN Monitor
* Monitors a series of Virtual Sensors (last update time)
* and generates a short report shown on standard output
* You can put this program in a Cron job and run it regularely to check status.
* It can also be used from Nagios
* Syntax for calling:
* java -jar VSMonitor.jar <config_file>
*  e.g.  java -jar VSMonitor.jar conf/monitoring.cfg
* As input, a config file containing list sensors described by:
* name of sensor,
* timeout (expressing expected update period, for example every 1h),
* gsn session address (e.g. www.server.com:22001/gsn)
* and possibly a username and password, in case session requires authentication.
* Syntax for configuration file
* # Servers without authentication
* sensor1@10d@www.server.com:22001/gsn
* sensor2@20m@www.server2.com:22003/gsn
* # Server with authentication
* sensor3@1h@www.server4.com:22005/gsn@httpusername:httppassword
* */
public class GSNMonitor {

    public static final String CONFIG_SEPARATOR = "@";

    public static final int STATUS_OK = 0;
    public static final int STATUS_WARNING = 1;
    public static final int STATUS_CRITICAL = 2;
    public static final int STATUS_UNKNOWN = 3;

    private static int status = STATUS_OK;

    private static transient final Logger logger = Logger.getLogger(VSMonitor.class);

    public static HashMap<String, VSensorMonitorConfig> monitoredSensors = new HashMap<String, VSensorMonitorConfig>();
    public static HashMap<String, Long> sensorsUpdateDelay = new HashMap<String, Long>();
    public static List<GSNSessionAddress> listOfGSNSessions = new Vector<GSNSessionAddress>();
    public static List<String> listOfMails = new Vector<String>();

    public static StringBuilder errorsBuffer = new StringBuilder();
    public static StringBuilder warningsBuffer = new StringBuilder();
    public static StringBuilder infosBuffer = new StringBuilder();
    public static StringBuilder summary = new StringBuilder();
    public static StringBuilder report = new StringBuilder();

    private static final String GSN_REALM = "GSNRealm";
    private static String gmail_username;
    private static String gmail_password;
    private static final String SMTP_GMAIL_COM = "smtp.gmail.com";
    private static int nHostsDown = 0;
    private static int nSensorsLate = 0;

    /*
    * Reads config file and initializes:
    * e-mail config (gmail_username, gmail_password)
    * lists
    * - monitoredSensors: list of monitored sensors (including expected update delay)
    * - sensorsUpdateDelay: list of sensors update delays (initialized to -1)
    * - listOfGSNSessions: list of GSN sessions
    * */
    public static void initFromFile(String fileName) {
        long timeout;
        String vsensorname;
        String host;
        String path;
        int port;
        boolean needspassword;
        String password;
        String username;
        logger.warn("Trying to initialize VSMonitor from file <" + fileName + ">");
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            while ((str = in.readLine()) != null) {
                // ignore comments starting with #
                if (str.trim().indexOf("#") == 0) {
                    continue;
                }
                if (str.trim().indexOf("@gmail-username") >= 0) {
                    gmail_username = str.trim().split(" ")[1];
                    //System.out.println("GMAIL Username: "+ gmail_username);
                    continue;
                }
                if (str.trim().indexOf("@gmail-password") >= 0) {
                    gmail_password = str.trim().split(" ")[1];
                    //System.out.println("GMAIL password: "+ gmail_password);
                    continue;
                }
                //@gmail-username
                //@gmail-password
                String[] s = str.trim().split(CONFIG_SEPARATOR);
                if (s.length < 3) {
                    logger.warn("Malformed monitoring line in file <" + fileName + "> : " + str);
                    //System.out.println("Malformed monitoring line in file <"+ fileName + "> : "+str);
                } else {
                    //System.out.println(s.length+" Elements found");

                    vsensorname = s[0].trim();
                    timeout = VSensorMonitorConfig.timeOutFromString(s[1].trim());
                    //System.out.println("\""+s[2]+"\"");
                    String[] host_port_path = s[2].split(":");
                    //System.out.println(host_port_path.length);
                    if (host_port_path.length != 2) {
                        logger.warn("Malformed monitoring line in file <" + fileName + "> : " + str);
                        //System.out.println("Malformed monitoring line in file <"+ fileName + "> : "+str);
                        continue;
                    } else {
                        //System.out.println("["+host_port_path[0].trim()+"]["+host_port_path[1].trim()+"]");
                        host = host_port_path[0].trim();
                        int j = host_port_path[1].trim().indexOf("/");
                        String portStr = host_port_path[1].trim().substring(0, j);
                        //System.out.println("Port:"+portStr);
                        path = host_port_path[1].trim().substring(j);
                        //System.out.println("Path:"+"\""+path+"\"");

                        try {
                            port = Integer.parseInt(portStr);
                            //System.out.println(">>"+port);
                        } catch (NumberFormatException e) {
                            logger.warn("Malformed monitoring line in file <" + fileName + "> : " + str);
                            //System.out.println("Malformed monitoring line in file <"+ fileName + "> : "+str);
                            continue;
                        }

                        if (s.length > 3) { // needs password
                            needspassword = true;
                            String[] username_password = s[3].split(":");
                            if (username_password.length > 1) {
                                username = username_password[0].trim();
                                password = username_password[1].trim();
                            } else {
                                logger.warn("Malformed monitoring line in file <" + fileName + "> : " + str);
                                //System.out.println("Malformed monitoring line in file <"+ fileName + "> : "+str);
                                continue;
                            }
                        } else {
                            needspassword = false;
                            username = "";
                            password = "";
                        }
                        // DEBUG INFO
                        //System.out.println("TIMEOUT: "+timeout);
                        //System.out.println("Creating object with : "+vsensorname+" "+host+" "+port+" "+timeout+" "+path+" "+needspassword+" "+username+" "+password);
                        monitoredSensors.put(vsensorname, new VSensorMonitorConfig(vsensorname, host, port, timeout, path, needspassword, username, password));

                        // DEBUG INFO
                        //System.out.println("RESULT: "+ monitoredSensors.get(vsensorname).toString());

                        sensorsUpdateDelay.put(vsensorname, new Long(-1)); // not yes initialized, to be initialized when web server is queried


                        GSNSessionAddress gsnSessionAdress = new GSNSessionAddress(host, path, port, needspassword, username, password); //TODO: insitialize it

                        if (!listOfGSNSessions.contains(gsnSessionAdress)) {
                            listOfGSNSessions.add(gsnSessionAdress);
                        }


                        //System.out.println("VS: "+"\""+vsensorname+"\""+" timeout: " + Long.toString(timeout));
                        //System.out.println("Added: "+ monitoredSensors.get(vsensorname));
                        logger.warn("Added:" + monitoredSensors.get(vsensorname));
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("IO Exception while trying to open file <" + fileName + "> " + e);
        }
    }


    /*
    * Queries a GSN session for status
    * Uses Http Get method to read xml status (usually under /gsn)
    * and initializes global variables :
    * - errorsBuffer
    * - noErrrorsBuffer
    * - nHostsDown
    * */
    public static void readStatus(GSNSessionAddress gsnSessionAddress) throws Exception {

        String httpAddress = gsnSessionAddress.getURL();

        DefaultHttpClient client = new DefaultHttpClient();

        if (gsnSessionAddress.needsPassword()) {
            client.getCredentialsProvider().setCredentials(
                    new AuthScope(gsnSessionAddress.getHost(), gsnSessionAddress.getPort()/*, GSN_REALM*/),
                    new UsernamePasswordCredentials(gsnSessionAddress.getUsername(), gsnSessionAddress.getPassword())
            );
        }

        logger.warn("Querying server: " + httpAddress);
        HttpGet get = new HttpGet(httpAddress);

        try {
            // execute the GET, getting string directly
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = client.execute(get, responseHandler);

            parseXML(responseBody);

        } catch (HttpResponseException e) {
            errorsBuffer.append("HTTP 401 Authentication Needed for : ")
                    .append(httpAddress)
                    .append("\n");
        } catch (UnknownHostException e) {
            errorsBuffer.append("Unknown host: ")
                    .append(httpAddress)
                    .append("\n");
            nHostsDown++;
        } catch (ConnectException e) {
            errorsBuffer.append("Connection refused to host: ")
                    .append(httpAddress)
                    .append("\n");
            raiseStatusTo(STATUS_CRITICAL);
            nHostsDown++;
        } finally {
            // release any connection resources used by the method
            client.getConnectionManager().shutdown();
        }
    }

    public static int raiseStatusTo(int newStatus) {
        if (status<newStatus)
            status = newStatus;
        return status;
    }

    /*
    * Checks update times
    * */
    public static void checkUpdateTimes() {
        for (int i = 0; i < sensorsUpdateDelay.size(); i++) {
            Long lastUpdated = (Long) sensorsUpdateDelay.values().toArray()[i];
            String sensorName = (String) sensorsUpdateDelay.keySet().toArray()[i];
            if (lastUpdated.longValue() > monitoredSensors.get(sensorName).getTimeout()) {

                raiseStatusTo(STATUS_WARNING);

                warningsBuffer.append(sensorName)
                        .append("@")
                        .append(monitoredSensors.get(sensorName).getHost())
                        .append(":")
                        .append(monitoredSensors.get(sensorName).getPort())
                        .append(" not updated for ")
                        .append(VSensorMonitorConfig.ms2dhms(sensorsUpdateDelay.get(sensorName)))
                        .append(" (expected <")
                        .append(VSensorMonitorConfig.ms2dhms(monitoredSensors.get(sensorName).getTimeout()))
                        .append(")\n");
                nSensorsLate++;
            } else {
                infosBuffer.append(sensorName).append("@")
                        .append(monitoredSensors.get(sensorName).getHost())
                        .append(":")
                        .append(monitoredSensors.get(sensorName).getPort())
                        .append(" (on time)\n");
            }
        }
    }

    /*
    * Sends an e-mail to recipients specified in command line
    * (through global listOfMails)
    * with the global summary as subject
    * and global errorsBuffer as body
    * */
    private static void sendMail() throws EmailException {

        SimpleEmail email = new SimpleEmail();
        //email.setDebug(true);
        email.setHostName(SMTP_GMAIL_COM);
        email.setAuthentication(gmail_username, gmail_password);
        //System.out.println(gmail_username +" "+ gmail_password);
        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
        email.getMailSession().getProperties().put("mail.smtp.auth", "true");
        email.getMailSession().getProperties().put("mail.debug", "true");
        email.getMailSession().getProperties().put("mail.smtp.port", "465");
        email.getMailSession().getProperties().put("mail.smtp.socketFactory.port", "465");
        email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        email.getMailSession().getProperties().put("mail.smtp.socketFactory.fallback", "false");
        email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");

        for (String s : listOfMails) {
            email.addTo(s);
        }
        email.setFrom(gmail_username + "@gmail.com", gmail_username);

        email.setSubject("[GSN Alert] " + summary.toString());
        email.setMsg(report.toString());
        email.send();

    }

    /*
    * parses the XML string
    * and initializes sensorsUpdateDelay
    * with update delays for relevant sensors
    * */
    public static void parseXML(String s) {
        try {

            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(s));

            Document document = documentBuilder.parse(inputSource);
            NodeList nodes = document.getElementsByTagName("virtual-sensor");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                String sensor_name = element.getAttribute("name");


                if (!sensorsUpdateDelay.containsKey(sensor_name))
                    continue;                         // skip sensors that are not monitored

                logger.warn("Sensor: " + sensor_name);

                NodeList listOfField = element.getElementsByTagName("field");
                for (int j = 0; j < listOfField.getLength(); j++) {
                    Element line = (Element) listOfField.item(j);

                    if (line.getAttribute("name").indexOf("timed") >= 0) {
                        String last_updated_as_string = line.getTextContent();

                        try {
                            Long last_updated_as_Long = GregorianCalendar.getInstance().getTimeInMillis() - VSensorMonitorConfig.datetime2timestamp(last_updated_as_string);
                            logger.warn(new StringBuilder(last_updated_as_string)
                                    .append(" => ")
                                    .append(VSensorMonitorConfig.ms2dhms(last_updated_as_Long))
                                    .toString());

                            sensorsUpdateDelay.put(sensor_name, last_updated_as_Long);
                        } catch (ParseException e) {
                            errorsBuffer.append("Last update time for sensor ")
                                    .append(sensor_name)
                                    .append(" cannot be read. Error while parsing > ")
                                    .append(last_updated_as_string)
                                    .append(" <\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Exception while parsing XML\n");
            e.printStackTrace();
        }
    }

    /*
   * Prints the stack trace of the exception to a string.
   * */
    public static String getStackTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        t.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }


    public static void main(String[] args) {

        String configFileName;

        if (args.length >= 2) {
            configFileName = args[0];
            System.out.println("Using config file: " + configFileName);
            for (int i = 1; i < args.length; i++) {
                System.out.println("Adding e-mail: " + args[i]);
                listOfMails.add(args[i]);
            }
        } else {
            System.out.println("Usage java -jar VSMonitor.jar <config_file> <list_of_mails>");
            System.out.println("e.g.  java -jar VSMonitor.jar conf/monitoring.cfg user@gmail.com admin@gmail.com");
            return;
        }

        initFromFile(configFileName);

        // for each monitored GSN server
        Iterator iter = listOfGSNSessions.iterator();
        while (iter.hasNext()) {

            try {
                readStatus((GSNSessionAddress) iter.next());
            } catch (Exception e) {
                logger.error("Exception: " + e.getMessage());
                logger.error("StackTrace:\n" + getStackTrace(e));
            }
        }

        checkUpdateTimes();

        // Generate Report
        report.append("\n[ERROR]\n" + errorsBuffer)
                .append("\n[WARNING]\n" + warningsBuffer)
                .append("\n[INFO]\n" + infosBuffer);

        if ((nSensorsLate > 0) || (nHostsDown > 0)) {
            summary.append("WARNING: ");
            if (nHostsDown > 0)
                summary.append(nHostsDown + " host(s) down. ");
            if (nSensorsLate > 0)
                summary.append(nSensorsLate + " sensor(s) not updated. ");

            // Send e-mail only if there are errors
            try {
                sendMail();
            } catch (EmailException e) {
                logger.error("Cannot send e-mail. " + e.getMessage());
                logger.error("StackTrace:\n" + getStackTrace(e));
            }
        }

        // Showing report
        System.out.println(summary);
        System.out.println(report);

        System.exit(status);
    }
}
