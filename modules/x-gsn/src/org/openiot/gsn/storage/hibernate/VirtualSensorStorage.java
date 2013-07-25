package org.openiot.gsn.storage.hibernate;

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
