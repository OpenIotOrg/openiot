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

        //create a publisher and connect it to the broker
        Publisher publisher = new Publisher(new File("."+ File.separator+"config"+File.separator+"pub1.config"));
        publisher.connect();
        
        //publish all publications which are located in CUPUS\src\main\resources\in
        File publicationFolder = new File("." +File.separator + "in");
        for (File content : publicationFolder.listFiles()) {
            if (content.getName().startsWith("publication")) {
                publisher.publishFromXMLFile(content.getName());
            }
        }

        publisher.disconnectFromBroker();
    }
}
