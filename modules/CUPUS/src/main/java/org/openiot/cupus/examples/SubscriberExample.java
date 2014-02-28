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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.Subscriber;

/**
 * 
 * @author Aleksandar, Eugen
 */
public class SubscriberExample {

	public static void main(String[] args) throws InterruptedException,
			IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		// create a new subscriber and define notification listener (in this
		// example received notifications are printed on the standard output)
		// In notification listener a one can implement application logic
		Subscriber subscriber = new Subscriber("Subscriber", "localhost", 10000);
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
		});

		// connect to the broker
		subscriber.connect();

		// define subscriptions
		TripletSubscription ts1 = new TripletSubscription(-1,
				System.currentTimeMillis());
		ts1.addPredicate(new Triplet("num1", 5.4, Operator.GREATER_OR_EQUAL));

		TripletSubscription ts2 = new TripletSubscription(-1,
				System.currentTimeMillis());
		ts2.addPredicate(new Triplet("num2", 7.297, Operator.LESS_THAN));

		TripletSubscription ts3 = new TripletSubscription(-1,
				System.currentTimeMillis());
		ts3.addPredicate(new Triplet("str", "cAt", Operator.CONTAINS_STRING));

		// alternatively validity time can be set as:
		// "ts1.getStartTime() + [offset in ms]"
		// ts1.setValidity(ts1.getStartTime() + 1000000);

		// send subscriptions to broker;
		subscriber.subscribe(ts1);
		subscriber.subscribe(ts2);
		subscriber.subscribe(ts3);

		// subscription can be defined and subscribed using parameters from an xml
		// file located in <project_directory>/in
		// e.g. "subscription.xml" is the XML file which defines a subscription
		//subscriber.subscribeFromXMLFile("subscription.xml");

		System.out.println("Press enter to unsubscribe...");
		in.readLine();
		// cancel subscriptions
		subscriber.unsubscribe(ts1);

		System.out.println("Press enter to disconnect from broker...");
		in.readLine();
		// disconnect from broker
		subscriber.disconnectFromBroker();

	}
}
