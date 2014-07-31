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
package org.openiot.qos;


import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.entity.subscriber.NotificationListener;

/**
 * 
 * @author Martina
 */
public class QoSNotificationListener implements NotificationListener {
     
    private MonitoringAndManagement matcher;
  	
	public QoSNotificationListener(MonitoringAndManagement matcher){
		this.matcher = matcher;
	}
	 
    public void notify(UUID subscriberId, String subscriberName, Publication publication) {
		if (publication instanceof HashtablePublication) {
			HashtablePublication sensorPublication = (HashtablePublication) publication;
                        matcher.notify(sensorPublication);
		}
    }
	
    public void notify(UUID subscriberId, String subscriberName, Subscription subscription) {
    	if (subscription instanceof TripletSubscription){
	    	TripletSubscription sensorSubscription = (TripletSubscription) subscription;
	    	matcher.notify(sensorSubscription);			
    	}
    }
	
}
