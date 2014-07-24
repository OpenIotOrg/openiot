/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.integration;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXMLFile {

    String virtualSensorName;
    List<String> parameters;
    List<String> parametersTypes;
    int port;

    public WriteXMLFile(String virtualSensorName, int port, List<String> param, List<String> paramTypes) {
        this.virtualSensorName = virtualSensorName;
        this.port = port;
        this.parameters = param;
        this.parametersTypes = paramTypes;
    }

    public String createXML() {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element virtual-sensor
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("virtual-sensor");
            rootElement.setAttribute("name", virtualSensorName);
            rootElement.setAttribute("priority", "10");
            doc.appendChild(rootElement);

            // processing-class
            Element processingClass = doc.createElement("processing-class");
            rootElement.appendChild(processingClass);

            // class-name 
            Element className = doc.createElement("class-name");
            className.appendChild(doc.createTextNode("org.openiot.gsn.vsensor.LSMExporter"));
            processingClass.appendChild(className);

            // init-params 
            Element initParams = doc.createElement("init-params");
            processingClass.appendChild(initParams);

            // param 1
            Element param = doc.createElement("param");
            param.setAttribute("name", "allow-nulls");
            param.appendChild(doc.createTextNode("false"));
            initParams.appendChild(param);

            // param 2
            Element param2 = doc.createElement("param");
            param2.setAttribute("name", "publish-to-lsm");
            param2.appendChild(doc.createTextNode("true"));
            initParams.appendChild(param2);

            // output-structure 
            Element outputStructure = doc.createElement("output-structure");
            processingClass.appendChild(outputStructure);

            for (int i = 0; i < parameters.size(); i++) {
                // field 
                Element field = doc.createElement("field");
                field.setAttribute("name", parameters.get(i));
                field.setAttribute("type", parametersTypes.get(i));
                outputStructure.appendChild(field);
            }

            // description
            Element description = doc.createElement("description");
            description.appendChild(doc.createTextNode("FER Virtual Sensor for MGRS area"));
            rootElement.appendChild(description);

            // life-cycle
            Element lifeCycle = doc.createElement("life-cycle");
            lifeCycle.setAttribute("pool-size", "1");
            rootElement.appendChild(lifeCycle);

            // addressing
            Element addressing = doc.createElement("addressing");
            rootElement.appendChild(addressing);

            // streams
            Element streams = doc.createElement("streams");
            rootElement.appendChild(streams);

            // stream
            Element stream = doc.createElement("stream");
            stream.setAttribute("name", "input1");
            streams.appendChild(stream);

            // source
            Element sensorSource = doc.createElement("source");
            sensorSource.setAttribute("alias", "source1");
            sensorSource.setAttribute("sampling-rate", "1");
            sensorSource.setAttribute("storage-size", "1");
            stream.appendChild(sensorSource);

            // address
            Element address = doc.createElement("address");
            address.setAttribute("wrapper", "ferudp");
            sensorSource.appendChild(address);

            // predicate 1
            Element aPredicate = doc.createElement("predicate");
            aPredicate.setAttribute("key", "port");
            aPredicate.appendChild(doc.createTextNode(String.valueOf(port)));
            address.appendChild(aPredicate);
//--------------------------------------------------
//            // predicate 1
//		Element aPredicate = doc.createElement("predicate");
//		aPredicate.setAttribute("key", "file");		
//		aPredicate.appendChild(doc.createTextNode("data/station_1056.csv"));				
//		address.appendChild(aPredicate);
//		
//		// predicate 2
//		Element aPredicate2 = doc.createElement("predicate");
//		aPredicate2.setAttribute("key", "fields");		
//		aPredicate2.appendChild(doc.createTextNode("timed, id, temp, humid, pressure, co, no2, so2, pollen, batteryS, batteryMP"));				
//		address.appendChild(aPredicate2);
//		
//		// predicate 3
//		Element aPredicate3 = doc.createElement("predicate");
//		aPredicate3.setAttribute("key", "formats");		
//		aPredicate3.appendChild(doc.createTextNode("timestamp(d/M/y H:m), string, numeric, numeric, numeric, numeric, numeric, numeric, numeric, numeric, numeric"));				
//		address.appendChild(aPredicate3);
//		
//		// predicate 4
//		Element aPredicate4 = doc.createElement("predicate");
//		aPredicate4.setAttribute("key", "bad-values");		
//		aPredicate4.appendChild(doc.createTextNode("NaN,6999,-6999,null"));				
//		address.appendChild(aPredicate4);
//		
//		// predicate 5
//		Element aPredicate5 = doc.createElement("predicate");
//		aPredicate5.setAttribute("key", "timezone");		
//		aPredicate5.appendChild(doc.createTextNode("Etc/GMT-1"));				
//		address.appendChild(aPredicate5);
//		
//		// predicate 6
//		Element aPredicate6 = doc.createElement("predicate");
//		aPredicate6.setAttribute("key", "sampling");		
//		aPredicate6.appendChild(doc.createTextNode("4000"));				
//		address.appendChild(aPredicate6);
//		
//		// predicate 7
//		Element aPredicate7 = doc.createElement("predicate");
//		aPredicate7.setAttribute("key", "check-point-directory");		
//		aPredicate7.appendChild(doc.createTextNode("csv-check-points"));				
//		address.appendChild(aPredicate7);
////----------------------------------------------------------
            // query 1
            Element query = doc.createElement("query");
            query.appendChild(doc.createTextNode("select * from wrapper"));
            sensorSource.appendChild(query);

            // query 2
            Element query2 = doc.createElement("query");
            query2.appendChild(doc.createTextNode("select * from source1"));
            stream.appendChild(query2);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return "";
    }

}

