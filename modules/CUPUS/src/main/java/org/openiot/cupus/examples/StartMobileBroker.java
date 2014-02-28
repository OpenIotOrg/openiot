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
import org.openiot.cupus.entity.mobilebroker.MobileBroker;
import org.openiot.cupus.entity.subscriber.NotificationListener;

/**
 * 
 * @author Aleksandar
 */
public class StartMobileBroker {

	public static void main(String[] args) {

		// create a new mobile broker and define notification listener (in this
		// example received notifications are printed on the standard output)
		// In notification listener a one can implement application logic
		MobileBroker mb1 = new MobileBroker(new File("."+ File.separator+"config"+File.separator+"mb1.config"));
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
		});

		// connect to the broker
		mb1.connect();

		//announce all announcements which are located in CUPUS\src\main\resources\in
        File inputFolder = new File("." +File.separator + "in");
        for (File content : inputFolder.listFiles()) {
            if (content.getName().startsWith("announcement")) {
            	mb1.announceFromXMLFile(content.getName());
            }
        }
        
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        //now publish all available data
      //publish all publications which are located in CUPUS\src\main\resources\in
        for (File content : inputFolder.listFiles()) {
            if (content.getName().startsWith("publication")) {
            	mb1.publishFromXMLFile(content.getName());
            }
        }

	}
}
