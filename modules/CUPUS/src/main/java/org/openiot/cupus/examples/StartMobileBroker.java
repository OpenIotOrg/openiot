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
import org.openiot.cupus.entity.mobilebroker.MobileBroker;
import org.openiot.cupus.entity.subscriber.NotificationListener;

/**
 * 
 * @author Aleksandar
 */
public class StartMobileBroker {

	public static void main(String[] args) {

//reading config file from location provided at runtime
	String configFile = null;
	String inputFolderPath = null;
	try {
            configFile = args[0];
	    inputFolderPath = args[1];
        } catch (Exception e) {
            System.out.println("ERROR! Couldn't start publisher.");
            System.out.println("\n Two command-line arguments needed - path to the config file and input folder!");
            System.exit(-1);
        }

		// create a new mobile broker and define notification listener (in this
		// example received notifications are printed on the standard output)
		// In notification listener a one can implement application logic
		MobileBroker mb1 = new MobileBroker(new File(configFile));
		mb1.setNotificationListener(new NotificationListener() {
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
                        
                    }
		});

		// connect to the broker
		mb1.connect();

		//announce all announcements which are located in an input folder
        File inputFolder = new File(inputFolderPath);
        for (File content : inputFolder.listFiles()) {
            if (content.getName().startsWith("announcement")) {
            	mb1.announceFromXMLFile(inputFolderPath + System.getProperty("file.separator") + content.getName());
            }
        }
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        //now publish all available data
      //publish all publications which are located in an input folder
        for (File content : inputFolder.listFiles()) {
            if (content.getName().startsWith("publication")) {
            	mb1.publishFromXMLFile(inputFolderPath + System.getProperty("file.separator") + content.getName());
            }
        }

	}
}