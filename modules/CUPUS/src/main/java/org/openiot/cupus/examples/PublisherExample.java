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
package org.openiot.cupus.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

/**
 *
 * @author Aleksandar, Eugen
 */
public class PublisherExample {

    public static void main(String[] args) throws InterruptedException, IOException {

        Publisher publisher = new Publisher("pub1", "localhost", 10000);

        //connect to broker
        publisher.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
Random r = new Random();
        
        double num1;
        double num2;
        HashtablePublication hp = null;
        
        while (true) {

            System.out.print("Enter q to exit: ");
            String inStr = in.readLine();
            if (inStr.equalsIgnoreCase("q")) {
                break;
            }

            //define new subscription
            hp = new HashtablePublication(-1, System.currentTimeMillis());

			//alternatively validity time can be set as: "hp.getStartTime() + [offset in ms]"
            //hp.setValidity(hp.getStartTime() + 1000000);
			//set publication propertis as name-value pairs;
            //each publication can contain an arbitrary number of properties
            try {
                System.out.print("Enter the ID (number): ");
                num1 = Double.parseDouble(in.readLine()); 
            } catch (NumberFormatException e) {
                System.out.println("You enetered something wrong. Try again...\n");
                continue;
            }
            
            hp.setProperty("ID", num1);
            hp.setProperty("Type", "SensorReading");
            System.out.println();

            //send publication to broker
            publisher.publish(hp);
        }

		//publication can be defined and published using parameters from an xml file located in <project_directory>/in
        //e.g. "publication.xml" is the XML file which defines a publication
        //publisher.publishFromXMLFile("publication.xml");
        System.out.println("Press enter to unpublish last publication...");
        in.readLine();
        System.out.println("Unpublishing...");
        publisher.unpublish(hp);

        System.out.println("Press enter to disconnect from broker...");
        in.readLine();
        //disconnect from broker
        publisher.disconnectFromBroker();
    }
}
