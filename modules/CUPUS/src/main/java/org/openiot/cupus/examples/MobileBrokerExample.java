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

import java.util.HashMap;
import java.util.UUID;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.entity.mobilebroker.MobileBroker;
import org.openiot.cupus.entity.subscriber.NotificationListener;

/**
 *
 * @author Aleksandar
 */
public class MobileBrokerExample {
    public static void main(String[] args) {
       
    	//define a mobile broker entity
    	MobileBroker mb = new MobileBroker("mb1", "localhost", 10000);
        mb.connect();
        mb.setNotificationListener(new NotificationListener() {
            @Override
            public void notify(UUID subscriberId, String subscriberName, Publication publication) {
                HashtablePublication notification = (HashtablePublication) publication;
                HashMap<String, Object> receivedData = notification.getProperties();
                System.out.println("Received publication:");
                System.out.println(publication);
                System.out.println();
            }
        });
        
        //define a new subscription (for more details see the SubscriberExample.java class) 
        //TripletSubscription ts1 = new TripletSubscription(-1, System.currentTimeMillis());
        //ts1.addPredicate(new Triplet("num1", 5.4, Operator.GREATER_OR_EQUAL));
        //mb.subscribe(ts1);
        
        try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        //define an announcement which is unlimited 
        TripletAnnouncement ta = new TripletAnnouncement(-1, System.currentTimeMillis());
        //announce numerical data (i.e. its range is <-inf, +inf> , implementation is <
        ta.addNumericalPredicate("num1");
        //announce previously defined announcement
        mb.announce(ta);
        
        //define a new publication (for more details see the PublisherExample.java class)
        HashtablePublication hp = new HashtablePublication(-1, System.currentTimeMillis());
        hp.setProperty("num1", 6);
		hp.setProperty("num2", 5);
        mb.publish(hp);
        
        hp = new HashtablePublication(-1, System.currentTimeMillis());
        hp.setProperty("num2", 5);
        mb.publish(hp);
    	
        //mb.disconnectFromBroker();
    }
}
