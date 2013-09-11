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

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;


public class DemoVSensor extends AbstractVirtualSensor {

	private static final transient Logger logger                  = Logger.getLogger( DemoVSensor.class );

	private ArrayList < String >          fields                  = new ArrayList < String >( );

	private ByteArrayOutputStream         outputStream            = new ByteArrayOutputStream( 24 * 1024 );

	private ByteArrayInputStream          input;

	private static final String           IMAGE_OUTPUT_FIELD      = "image";

	private static final int              IMAGE_OUTPUT_FIELD_TYPE = DataTypes.BINARY;

	private static final String [ ]       OUTPUT_FIELDS           = new String [ ] { IMAGE_OUTPUT_FIELD };

	private static final Byte [ ]      OUTPUT_TYPES            = new Byte [ ] { IMAGE_OUTPUT_FIELD_TYPE };

	private static BufferedImage          cachedBufferedImage     = null;

	private static int                    counter                 = 0;

	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		if ( inputStreamName.equalsIgnoreCase( "SSTREAM" ) ) {
			String action = ( String ) data.getData( "STATUS" );
			/**
			 * 
			 */
			String moteId = ( String ) data.getData( "ID" );
			if ( moteId.toLowerCase( ).indexOf( "mica" ) < 0 ) return;
			if ( action.toLowerCase( ).indexOf( "add" ) >= 0 ) counter++;
			if ( action.toLowerCase( ).indexOf( "remove" ) >= 0 ) counter--;
		}
		if ( inputStreamName.equalsIgnoreCase( "CSTREAM" ) ) {

			BufferedImage bufferedImage = null;
			outputStream.reset( );
			byte [ ] rawData = ( byte [ ] ) data.getData( "IMAGE" );
			input = new ByteArrayInputStream( rawData );
			try {
				bufferedImage = ImageIO.read( input );
			} catch ( IOException e ) {
				e.printStackTrace( );
			}
			Graphics2D graphics = ( Graphics2D ) bufferedImage.getGraphics( );
			int size = 30;
			int locX = 0;
			int locY = 0;
			if ( counter < 0 ) counter = 0;
			switch ( counter ) {
			case 0 :
				graphics.setColor( Color.RED );
				break;
			case 1 :
				graphics.setColor( Color.ORANGE );
				break;

			case 2 :
				graphics.setColor( Color.YELLOW );
				break;

			case 3 :
				graphics.setColor( Color.GREEN );
				break;
			default :
				logger.warn( new StringBuilder( ).append( "Shouldn't happen.>" ).append( counter ).append( "<" ).toString( ) );
			}
			graphics.fillOval( locX , locY , size , size );
			try {
				ImageIO.write(bufferedImage,"jpeg", outputStream);
				outputStream.close();

			}catch (Exception e) {
				logger.error(e.getMessage(),e);
			}

			StreamElement outputSE = new StreamElement( OUTPUT_FIELDS , OUTPUT_TYPES , new Serializable [ ] { outputStream.toByteArray( ) } , data.getTimeStamp( ) );
			dataProduced( outputSE );
		}
		if ( logger.isInfoEnabled( ) ) logger.info( new StringBuilder( ).append( "Data received under the name: " ).append( inputStreamName ).toString( ) );
	}

	public boolean initialize ( ) {
		for ( DataField field : getVirtualSensorConfiguration( ).getOutputStructure( ) )
			fields.add( field.getName( ) );
		return true;
	}

	public void dispose ( ) {

	}
}
