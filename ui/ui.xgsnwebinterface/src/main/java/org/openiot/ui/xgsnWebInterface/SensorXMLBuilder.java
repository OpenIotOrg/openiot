package org.openiot.ui.xgsnWebInterface;

/**
 * Copyright (c) 2011-2014, OpenIoT
 * <p/>
 * This file is part of OpenIoT.
 * <p/>
 * OpenIoT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * OpenIoT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact: OpenIoT mailto: info@openiot.eu
 * @author Luke Herron
 */

import org.openiot.ui.xgsnWebInterface.sensor.VirtualSensor;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.Map;

public class SensorXMLBuilder {

    private VirtualSensor sensor;

    private SensorXMLBuilder() {}

    /**
     * Generates XML file from VirtualSensor attributes
     * @param sensor VirtualSensor which holds values for XML generation
     */
    public SensorXMLBuilder(VirtualSensor sensor) {
        this.sensor = sensor;
    }

    /**
     * Processes VirtualSensor attributes, generating XML values and building XML document
     * @return XML string generated from VirtualSensor attributes
     * @throws XMLStreamException
     */
    public String build() throws XMLStreamException {
        StringWriter XMLString = new StringWriter();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xml = outputFactory.createXMLStreamWriter(XMLString);

        xml.writeStartDocument();

        // Create root element
        xml.writeCharacters("\n");
        xml.writeStartElement("virtual-sensor");
        xml.writeAttribute("name", sensor.getName());
        xml.writeAttribute("priority", "10");

        // Add child elements
        xml.writeCharacters("\n\t");
        xml.writeStartElement("processing-class");

        xml.writeCharacters("\n\t\t");
        xml.writeStartElement("class-name");
        xml.writeCharacters("org.openiot.gsn.vsensor.LSMExporter");
        xml.writeEndElement();

        xml.writeCharacters("\n\t\t");
        xml.writeStartElement("init-params");

        xml.writeCharacters("\n\t\t\t");
        xml.writeStartElement("param");
        xml.writeAttribute("name", "allow-nulls");
        xml.writeCharacters("false");
        xml.writeEndElement();

        xml.writeCharacters("\n\t\t\t");
        xml.writeStartElement("param");
        xml.writeAttribute("name", "publish-to-lsm");
        xml.writeCharacters("true");
        xml.writeEndElement();

        // <init-params> closing element
        xml.writeCharacters("\n\t\t");
        xml.writeEndElement();

        xml.writeCharacters("\n\t\t");
        xml.writeStartElement("output-structure");

        for(Map.Entry<String, String> entry: sensor.getOutputDataValues().entrySet()) {
            xml.writeCharacters("\n\t\t\t");
            xml.writeEmptyElement("field");
            xml.writeAttribute("name", entry.getKey());
            xml.writeAttribute("type", entry.getValue());
        }

        // <output-structure> closing element
        xml.writeCharacters("\n\t\t");
        xml.writeEndElement();

        // <processing-class> closing element
        xml.writeCharacters("\n\t");
        xml.writeEndElement();

        xml.writeCharacters("\n\t");
        xml.writeStartElement("description");
        xml.writeCharacters("test station");
        xml.writeEndElement();

        xml.writeCharacters("\n\t");
        xml.writeEmptyElement("life-cycle");
        xml.writeAttribute("pool-size", "10");

        xml.writeCharacters("\n\t");
        xml.writeStartElement("addressing");
        xml.writeCharacters("\n\t");
        xml.writeEndElement();

        xml.writeCharacters("\n\t");
        xml.writeStartElement("streams");

        xml.writeCharacters("\n\t\t");
        xml.writeStartElement("stream");
        xml.writeAttribute("name", "input1");

        xml.writeCharacters("\n\t\t\t");
        xml.writeStartElement("source");
        xml.writeAttribute("alias", "source1");
        xml.writeAttribute("sampling-rate", "1");
        xml.writeAttribute("storage-size", "1");

        xml.writeCharacters("\n\t\t\t\t");
        xml.writeStartElement("address");
        xml.writeAttribute("wrapper", sensor.getSensorData().getWrapperType());

        for(Map.Entry<String, String> entry: sensor.getSensorData().getPredicateData().asMap().entrySet()) {
            xml.writeCharacters("\n\t\t\t\t\t");
            xml.writeStartElement("predicate");
            xml.writeAttribute("key", entry.getKey());
            xml.writeCharacters(entry.getValue());
            xml.writeEndElement();
        }

        // <address> closing element
        xml.writeCharacters("\n\t\t\t\t");
        xml.writeEndElement();

        xml.writeCharacters("\n\t\t\t\t");
        xml.writeStartElement("query");
        xml.writeCharacters("select * from wrapper");
        xml.writeEndElement();

        // <source> closing element
        xml.writeCharacters("\n\t\t\t");
        xml.writeEndElement();

        xml.writeCharacters("\n\t\t\t");
        xml.writeStartElement("query");
        xml.writeCharacters(sensor.getQuery());
        xml.writeEndElement();

        // <stream> closing element
        xml.writeCharacters("\n\t\t");
        xml.writeEndElement();

        // <streams> closing element
        xml.writeCharacters("\n\t");
        xml.writeEndElement();
        xml.writeCharacters("\n");

        // Close out the root element
        xml.writeEndElement();
        xml.close();

        return XMLString.toString();
    }
}
