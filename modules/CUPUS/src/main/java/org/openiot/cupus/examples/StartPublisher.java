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

import java.io.File;
import java.io.IOException;
import org.openiot.cupus.entity.publisher.Publisher;

/**
 *
 * @author Aleksandar
 */
public class StartPublisher {

    public static void main(String[] args) throws InterruptedException, IOException {

	//reading config file from location provided at runtime
	String configFile = null;
	String publicationFolderPath = null;
	try {
            configFile = args[0];
	    publicationFolderPath = args[1];
        } catch (Exception e) {
            System.out.println("ERROR! Couldn't start publisher.");
            System.out.println("\n Two command-line arguments needed - path to the config file and publication folder!");
            System.exit(-1);
        }
	
        //create a publisher and connect it to the broker
        Publisher publisher = new Publisher(new File(configFile));
        publisher.connect();
        //Thread.sleep(1000);
        //publish all publications which are located in CUPUS\src\main\resources\in
        File publicationFolder = new File(publicationFolderPath);
	//System.out.println(publicationFolderPath);
        for (File content : publicationFolder.listFiles()) {
            if (content.getName().startsWith("publication")) {
                publisher.publishFromXMLFile( publicationFolder + System.getProperty("file.separator") + content.getName());
            }
        }
Thread.sleep(1000);
        publisher.disconnectFromBroker();
    }
}