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

package org.openiot.cupus.message.external;

import org.openiot.cupus.message.Message;

import java.util.UUID;

/**
 * This class is a subtype of class message and it is used for sending
 * information during subscriber registration process.
 * 
 * @author Marko & Aleksandar
 * 
 */
public class MobileBrokerRegisterGCMMessage implements Message {

	private static final long serialVersionUID = 8716233142831946666L;

	private String entityName;
	private UUID entityID;
	private String registrationID;
	/**
	 * Constructor
	 *
	 * @param entityName
	 *            Name of entity sending message
	 * @param entityID
	 *            ID of entity sending message
	 * @param registrationID
	 *
	 */
    public MobileBrokerRegisterGCMMessage(String entityName, UUID entityID, String registrationID) {
        this.entityName = entityName;
        this.entityID = entityID;
        this.registrationID = registrationID;
    }

    public String getEntityName() {
        return entityName;
    }

    public UUID getEntityID() {
        return entityID;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityID(UUID entityID) {
        this.entityID = entityID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    @Override
    public UUID getID() {
        return entityID;
    }

    @Override
	public String toString() {
		return entityName + "(" + registrationID + ")";
	}
}
