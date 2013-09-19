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

package org.openiot.gsn.vsensor;

import org.openiot.gsn.ContainerImpl;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;

import java.io.Serializable;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class AbstractVirtualSensor {

	private static final transient Logger logger           = Logger.getLogger( AbstractVirtualSensor.class );

	private VSensorConfig                 virtualSensorConfiguration;

	private long                          lastVisitiedTime = 0;

	/**
	 * Called once while initializing an instance of the virtual sensor
	 * 
	 * @return True if the initialization is done successfully.
	 */
	public abstract boolean initialize ( );

	private void validateStreamElement ( StreamElement streamElement ,boolean adjust) {
		if ( !compatibleStructure( streamElement, getVirtualSensorConfiguration( ).getOutputStructure( ),adjust ) ) {
			StringBuilder exceptionMessage = new StringBuilder( ).append( "The streamElement produced by :" ).append( getVirtualSensorConfiguration( ).getName( ) ).append(
			" Virtual Sensor is not compatible with the defined streamElement.\n" );
			exceptionMessage.append( "The expected stream element structure (specified in " ).append( getVirtualSensorConfiguration( ).getFileName( ) ).append( " is [" );
			for ( DataField df : getVirtualSensorConfiguration( ).getOutputStructure( ) ) 
				exceptionMessage.append( df.getName( ) ).append( " (" ).append( DataTypes.TYPE_NAMES[ df.getDataTypeID( ) ] ).append( ") , " );
			exceptionMessage.append( "] but the actual stream element received from the " + getVirtualSensorConfiguration( ).getName( ) ).append( " has the [" );
			for ( int i = 0 ; i < streamElement.getFieldNames( ).length ; i++ )
				exceptionMessage.append( streamElement.getFieldNames( )[ i ] ).append( "(" ).append( DataTypes.TYPE_NAMES[ streamElement.getFieldTypes( )[ i ] ] ).append( ")," );
			exceptionMessage.append(" ] thus the stream element dropped !!!" );
			throw new RuntimeException( exceptionMessage.toString( ) );
		}
	}
	/**
	 * if Adjust is true then system checks the output structure of the virtual sensor and
	 * only publishes the fields defined in the output structure of the virtual sensor and 
	 * ignores the rest. IF the adjust is set to false, the system will enforce strict
	 * compatibility of the output and the produced value.
	 * 
	 * @param streamElement
	 * @param adjust Default is false.
	 */
	protected synchronized void dataProduced ( StreamElement streamElement,boolean adjust ) {
		try {
			validateStreamElement( streamElement,adjust );
		} catch ( Exception e ) {
			logger.error( e.getMessage( ) , e );
			return;
		}
		if ( !streamElement.isTimestampSet( ) ) streamElement.setTimeStamp( System.currentTimeMillis( ) );

		final int outputStreamRate = getVirtualSensorConfiguration( ).getOutputStreamRate( );
		final long currentTime = System.currentTimeMillis( );
		if ( ( currentTime - lastVisitiedTime ) < outputStreamRate ) {
			if ( logger.isInfoEnabled( ) ) logger.info( "Called by *discarded* b/c of the rate limit reached." );
			return;
		}
		lastVisitiedTime = currentTime;

		try {
			ContainerImpl.getInstance().publishData( this ,streamElement);
		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("duplicate entry"))
				logger.info(e.getMessage(),e);
			else
				logger.error(e.getMessage(),e);
		}
	}
	/**
	 * Calls the dataProduced with adjust = false.
	 * @param streamElement
	 */
	protected synchronized void dataProduced ( StreamElement streamElement ) {
		dataProduced(streamElement,true);
	}
	/**
	 * First checks compatibility of the data type of each output data item in the stream element with the
	 * defined output in the VSD file. (this check is done regardless of the value for adjust flag).
	 * <p>
	 * If the adjust flag is set to true, the method checks the newly generated stream element
	 * and returns true if and only if the number of data items is equal to the number of output
	 * data structure defined for this virtual sensor.
	 * If the adjust=true, then this test is not performed.
	 * 
	 * @param se
	 * @param outputStructure
	 * @param adjust default is false.
	 * @return
	 */
	private static boolean compatibleStructure ( StreamElement se ,  DataField [] outputStructure ,boolean adjust ) {
		if (!adjust && outputStructure.length != se.getFieldNames().length ) {
			logger.warn( "Validation problem, the number of field doesn't match the number of output data strcture of the virtual sensor" );
			return false;
		}
		int i =-1;
		for (DataField field: outputStructure) {
			Serializable value = se.getData(field.getName());
			i++;
			if (value==null)
				continue;
			if ( ( (  field.getDataTypeID() == DataTypes.BIGINT ||
					field.getDataTypeID() == DataTypes.DOUBLE ||
					field.getDataTypeID() == DataTypes.INTEGER||
					field.getDataTypeID() == DataTypes.SMALLINT||
					field.getDataTypeID() == DataTypes.TINYINT ) &&!(value instanceof Number)) 
					||
					( (field.getDataTypeID() == DataTypes.VARCHAR || field.getDataTypeID() == DataTypes.CHAR) && !(value instanceof String)) ||
					( (field.getDataTypeID() == DataTypes.BINARY) && !(value instanceof byte[])) 
			){ 
				logger.warn( "Validation problem for output field >" + field.getName( ) + ", The field type declared as >" + field.getType()+"< while in VSD it is defined as >"+DataTypes.TYPE_NAMES[outputStructure[ i ].getDataTypeID( )]);
				return false;
			}
		}
		return true;
	}

	/**
	 * Called when the container want to stop the pool and remove it's resources.
	 * The container will call this method once on each install of the virtual
	 * sensor in the pool. The progrmmer should release all the resouce used by
	 * this virtual sensor instance in this method specially those resouces
	 * aquired during the <code>initialize</code> call. <p/> Called once while
	 * finalizing an instance of the virtual sensor
	 */
	public abstract void dispose ( );

	public boolean dataFromWeb ( String action,String[] paramNames, Serializable[] paramValues ) {
		return false;
	}

	/**
	 * @return the virtualSensorConfiguration
	 */
	public VSensorConfig getVirtualSensorConfiguration ( ) {
		if ( virtualSensorConfiguration == null ) { throw new RuntimeException( "The VirtualSensorParameter is not set !!!" ); }
		return virtualSensorConfiguration;
	}

	/**
	 * @param virtualSensorConfiguration the virtualSensorConfiguration to set
	 */
	public void setVirtualSensorConfiguration ( VSensorConfig virtualSensorConfiguration ) {
		this.virtualSensorConfiguration = virtualSensorConfiguration;
	}

	/**
	 * This method is going to be called by the container when one of the input
	 * streams has a data to be delivered to this virtual sensor. After receiving
	 * the data, the virutal sensor can do the processing on it and this
	 * processing could possibly result in producing a new stream element in this
	 * virtual sensor in which case the virutal sensor will notify the container
	 * by simply adding itself to the list of the virtual sensors which have
	 * produced data. (calling <code>container.publishData(this)</code>. For
	 * more information please check the <code>AbstractVirtalSensor</code>
	 * @param inputStreamName is the name of the input stream as specified in the
	 * configuration file of the virtual sensor. @param inputDataFromInputStream
	 * is actually the real data which is produced by the input stream and should
	 * be delivered to the virtual sensor for possible processing.
	 */
	public abstract void dataAvailable ( String inputStreamName , StreamElement streamElement );
}
