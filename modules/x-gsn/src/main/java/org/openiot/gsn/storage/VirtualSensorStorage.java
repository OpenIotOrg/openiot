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

package org.openiot.gsn.storage;

import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.utils.GSNRuntimeException;

import java.io.Serializable;

public interface VirtualSensorStorage {


    public boolean init();

    /**
     * @param se The {@link org.openiot.gsn.beans.StreamElement} to be stored.
     * @return The the generated identifier for the primary key.
     * @throws GSNRuntimeException if the {org.openiot.gsn.beans.StreamElement} could not be stored (for instance due to a constraint violation).
     */
    public Serializable saveStreamElement(StreamElement se) throws GSNRuntimeException ;

    /**
     * @param pk the primary key.
     * @return the StreamElement associated to the pk primary key or null if it does not exists.
     * @throws GSNRuntimeException
     */
    public StreamElement getStreamElement(Serializable pk) throws GSNRuntimeException ;

    /**
     * @return The number of {@link org.openiot.gsn.beans.StreamElement} in the storage.
     * @throws GSNRuntimeException
     */
    public long countStreamElement() throws GSNRuntimeException ;

    //public void getStreamElements() throws GSNRuntimeException ;


    //TODO native SQL query.

}
