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

import java.util.List;
import java.util.Set;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.TripletSubscription;

/**
 * 
 * @author Martina
 */

public interface QoSManagerInterface {
	
	public void shutdown();
	
	public void setBatteryLevels (double highPriorityLevel, double lowPriorityLevel);
	
	public void setNumberOfActiveSensors (int numOfActiveSensors);
	
	public Set<String> getAllAvailableSensors ();
	
	public Set<String> getAllSensorsInArea (String area);
	
	public Set<String> getActiveSensorsInArea (String area);
	
	public List<TripletSubscription> getAllSubscriptionsInArea (String area);
	
	public HashtablePublication getAverageSensorReadingsInArea(String area);
	
	public List<Float> getLatLongFromArea (String area);
	
	public String getAreaFromLatLong (double lat, double lng, int accuracy);
	
}
