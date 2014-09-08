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
 */

package org.openiot.cupus.examples;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.Subscriber;

/**
 * 
 * @author Aleksandar
 */
public class StartSubscriber {

	public static void main(String[] args) {

//reading config file from location provided at runtime
	String configFile = null;
	String subscriptionFolderPath = null;
	try {
            configFile = args[0];
	    subscriptionFolderPath = args[1];
        } catch (Exception e) {
            System.out.println("ERROR! Couldn't start publisher.");
            System.out.println("\n Two command-line arguments needed - path to the config file and subscription folder!");
            System.exit(-1);
        }

		// create a new subscriber and define notification listener (in this
		// example received notifications are printed on the standard output)
		// In notification listener a one can implement application logic
		Subscriber subscriber = new Subscriber(new File(configFile));
		subscriber.setNotificationListener(new NotificationListener() {
			@Override
			public void notify(UUID subscriberId, String subscriberName,
					Publication publication) {
				HashtablePublication notification = (HashtablePublication) publication;
				HashMap<String, Object> receivedData = notification
						.getProperties();
				System.out.println("Received publication:");
				System.out.println(publication);
				System.out.println();
			}

                    @Override
                    public void notify(UUID subscriberId, String subscriberName, Subscription subscription) {
                        //not used by the subscriber entity
                    }
		});

		// connect to the broker
		subscriber.connect();

		//subscribe to all subscriptions which are located in an input folder
        File subscriptionFolder = new File(subscriptionFolderPath);
        for (File content : subscriptionFolder.listFiles()) {
            if (content.getName().startsWith("subscription")) {
            	subscriber.subscribeFromXMLFile(subscriptionFolderPath + System.getProperty("file.separator") + content.getName());
            }
        }

	}
}