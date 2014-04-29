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

import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.utils.ParamParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * The plot should be introduced in the init-param part of the configuration
 * file in which this virtual sensor is used. The paramter name is PLOT and the
 * value should have the following syntax
 * INPUT_STREAM_VAR_NAME:CHART_NAME[TYPE@SIZE]{WIDTH;HEIGHT} The typcal values
 * for width and height are 640 and 480. The Size means how many values the
 * system should use for plotting the diagram. <br>
 * VERY IMPORTANT : THIS A GENERAL PLOT DRAWING VIRTUAL SENSOR AND NOT
 * MEMORY/CPU FRIENDLY. ONE CAN USE THIS VIRTUAL SENSOR AS A STARTING POINT FOR
 * WRITING MORE ADVANCED AND OPTIMIZED CHART DRAWING PACKAGES. <br>
 * VERY IMPORTANT : IN THIS IMPLEMENTATION, THE LARGER THE SIZE OF THE HISTORY
 * USED FOR DRAWING, THE BIGGER THE OUTPUT PLOT SIZE (IN KILOBYTES) AND THE
 * HIGHER PROCESSING TIME.
 * 
 */
public class ChartVirtualSensor extends AbstractVirtualSensor {
   
   private  final transient Logger               logger                             = Logger.getLogger( this.getClass() );
   
   /**
    * The <code>GENERATE_COUNT</code> represents after how many inputs, the
    * virtual sensor should generate data. By default it set to 1 meaning that
    * for each stream element received, the virtual sensor plots a new diagram.
    * If you want to make the virtual sensor plot after receiving each K stream
    * elements, set <code>GENERATE_COUNT</code> to K.
    */
   private final int                                   GENERATE_COUNT                     = 4;
   
   private long                                        counter                            = 0;
   
   private final HashMap < String , ChartInfo > input_stream_name_to_ChartInfo_map = new HashMap < String , ChartInfo >( );
   
   private int                                         counter_pref                       = 0;
   
   public boolean initialize ( ) {
      /**
       * TODO : Checking if the user provides the arguements currectly. TODO :
       * This can now plot only for one input stream value.
       */
      TreeMap <  String , String > params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );
      ChartInfo chartInfo = new ChartInfo( );
      chartInfo.setInputStreamName( params.get( "input-stream" ) );
      
      chartInfo.setPlotTitle( params.get( "title" ) );
      chartInfo.setType( params.get( "type" ) );
      chartInfo.setHeight( ParamParser.getInteger( params.get( "height" ) , 480 ) );
      chartInfo.setWidth( ParamParser.getInteger( params.get( "width" ) , 640 ) );
      chartInfo.setVerticalAxisTitle( params.get( "vertical-axis" ) );
      chartInfo.setHistorySize( ParamParser.getInteger( params.get( "history-size" ) , 10 ) );
      input_stream_name_to_ChartInfo_map.put( chartInfo.getInputStreamName( ) , chartInfo );
      chartInfo.initialize( );
      return true;
   }
   
   public void dataAvailable ( String inputStreamName , StreamElement streamElement ) {
      if ( logger.isDebugEnabled( ) ) logger.debug( new StringBuilder( "data received under the name *" ).append( inputStreamName ).append( "* to the ChartVS." ).toString( ) );
      /**
       * Finding the appropriate ChartInfo object for this input stream.
       */
      ChartInfo chartInfo = input_stream_name_to_ChartInfo_map.get( inputStreamName );
      /**
       * If there is not chartInfo configured for this input stream, the virtual
       * sensor doesn't produce any values. Note that if this virtual sensor is
       * intended to produce output other than plots (e.g., if output of this
       * virtual sensor also container integers), then one might comment the
       * following line.
       */
      
      if ( chartInfo == null ) {
         logger.warn( "ChartVS drops the input because there is no chart specification defined for the specific input." );
         return;
      }
      /**
       * Sending the data to the chartInfo.
       */
      chartInfo.addData( streamElement );
      /**
       * counter checks to see if it's the time to do the plotting or not.
       */
      
      if ( ++counter % GENERATE_COUNT != 0 ) return;
      /**
       * Creating the stream element(s) for output. For creating a stream
       * element one need to provide the field names (in the form of string
       * array) and their types (in the form of integer array). This virtual
       * sensor just produces plots therefore the output is in the form of
       * binary data thus we set the type of the output stream element to
       * Types.Binary.
       */
      
      String [ ] fieldNames = input_stream_name_to_ChartInfo_map.keySet( ).toArray( new String [ ] {} );
      Byte [ ] fieldTypes = new Byte [ fieldNames.length ];
      Serializable [ ] charts = new Serializable [ fieldNames.length ];
      for ( int i = 0 ; i < fieldTypes.length ; i++ ) {
         /**
          * We set the type of the output stream element to Types.Binary because
          * we are producing images.
          */
         fieldTypes[ i ] = DataTypes.BINARY;
      }
      /**
       * Creating an stream element with the specified fieldnames, fieldtypes
       * and using the current time as the timestamp of the stream element.
       */
      
      /**
       * In here our stream element's relation contains just one row of data and
       * it's filled using the binary data which contains the plots. Note that
       * this virtual sensor plots one diagram for each InputStreamName. Also
       * Note that, each InputStreamName can have one or more variables inside
       * it's stream elements's relation thus having one plot for several
       * variables.
       */
      
      for ( int i = 0 ; i < fieldNames.length ; i++ ) {
         ChartInfo chart = input_stream_name_to_ChartInfo_map.get( fieldNames[ i ] );
         charts[ i ] = chart.writePlot( ).toByteArray( );
      }
      StreamElement output = new StreamElement( fieldNames , fieldTypes , charts , System.currentTimeMillis( ) );
      
      /**
       * Informing container about existance of a stream element.
       */
      dataProduced( output );
      /**
       * For debugging purposes.
       */
      if ( logger.isDebugEnabled( ) ) logger.debug( new StringBuilder( ).append( "Data received under the name: " ).append( inputStreamName ).toString( ) );
   }
   
   public void dispose ( ) {

   }
   
}

/**
 * This class represents a chart. The class is initialized using a String with a
 * predefined syntax. The class acts as a proxy between the Virtual Sensor and
 * the JFreeChart library which is used for plotting diagrams.
 */

class ChartInfo {
   
   private static final String             SYNTAX          = "INPUT_STREAM_VAR_NAME:CHART_NAME:VERTICAL_AXIS_TITLE [TYPE@SIZE] {WIDTH;HEIGHT}";
   
   private  final transient Logger   logger          = Logger.getLogger( this.getClass() );
   
   private String                          plotTitle;
   
   private int                             width;
   
   private int                             height;
   
   private int                             historySize;
   
   private String                          type;
   
   private String                          rowData;
   
   private String                          inputStreamName;
   
   private TimeSeriesCollection            dataCollectionForTheChart;
   
   private HashMap < String , TimeSeries > dataForTheChart = new HashMap < String , TimeSeries >( );
   
   private ByteArrayOutputStream           byteArrayOutputStream;
   
   private JFreeChart                      chart;
   
   private boolean                         changed         = true;
   
   private boolean                         ready           = false;
   
   private String                          verticalAxisTitle;
   
   public ChartInfo ( ) {
      byteArrayOutputStream = new ByteArrayOutputStream( 64 * 1024 ); // Grows
      // as
      // needed
      byteArrayOutputStream.reset( );
      dataCollectionForTheChart = new TimeSeriesCollection( );
      rowData = "";
   }
   
   public void setWidth ( int width ) {
      if ( !ready ) this.width = width;
   }
   
   public void setHeight ( int height ) {
      if ( !ready ) this.height = height;
   }
   
   public void setHistorySize ( int history ) {
      if ( !ready ) historySize = history;
   }
   
   public void setVerticalAxisTitle ( String title ) {
      if ( !ready ) verticalAxisTitle = title;
   }
   
   public void setType ( String type ) {
      if ( !ready ) this.type = type;
   }
   
   public void setPlotTitle ( String plotTitle ) {
      if ( !ready ) this.plotTitle = plotTitle;
   }
   
   public void setInputStreamName ( String inputStreamName ) {
      if ( !ready ) this.inputStreamName = inputStreamName;
   }
   
   public void initialize ( ) {
      if ( !ready ) {
         chart = ChartFactory.createTimeSeriesChart( plotTitle , "Time" , verticalAxisTitle , dataCollectionForTheChart , true , true , false );
         chart.setBorderVisible( true );
         ready = true;
         if ( logger.isDebugEnabled( ) ) logger.debug( "The Chart Virtual Sensor is ready." );
      }
   }
   
   /**
    * This method adds the specified stream elements to the timeSeries of the
    * appropriate plot.
    * 
    * @param streamElement
    */
   public synchronized void addData ( StreamElement streamElement ) {
      for ( int i = 0 ; i < streamElement.getFieldNames( ).length ; i++ ) {
         TimeSeries timeSeries = dataForTheChart.get( streamElement.getFieldNames( )[ i ] );
         if ( timeSeries == null ) {
            dataForTheChart.put( streamElement.getFieldNames( )[ i ] , timeSeries = new TimeSeries( streamElement.getFieldNames( )[ i ] , org.jfree.data.time.FixedMillisecond.class ) );
            timeSeries.setMaximumItemCount( historySize );
            dataCollectionForTheChart.addSeries( timeSeries );
         }
         try {
            timeSeries.addOrUpdate( new FixedMillisecond( new Date( streamElement.getTimeStamp( ) ) ) , Double.parseDouble( streamElement.getData( )[ i ].toString( ) ) );
         } catch ( SeriesException e ) {
            logger.warn( e.getMessage( ) , e );
         }
         
      }
      changed = true;
   }
   
   /**
    * Plots the chart and sends it in the form of ByteArrayOutputStream to
    * outside.
    * 
    * @return Returns the byteArrayOutputStream.
    */
   public synchronized ByteArrayOutputStream writePlot ( ) {
      if ( !changed ) return byteArrayOutputStream;
      byteArrayOutputStream.reset( );
      try {
         ChartUtilities.writeChartAsPNG( byteArrayOutputStream , chart , width , height , false , 8 );
         
      } catch ( IOException e ) {
         logger.warn( e.getMessage( ) , e );
      }
      return byteArrayOutputStream;
   }
   
   public boolean equals ( Object obj ) {
      if ( obj == null && !( obj instanceof ChartInfo ) ) return false;
      return ( obj.hashCode( ) == hashCode( ) );
   }
   
   int cachedHashCode = -1;
   
   public int hashCode ( ) {
      if ( rowData != null && cachedHashCode == -1 ) cachedHashCode = rowData.hashCode( );
      return cachedHashCode;
   }
   
   /**
    * @return Returns the inputStreamName.
    */
   public String getInputStreamName ( ) {
      return inputStreamName;
   }
   
   public String toString ( ) {
      StringBuffer buffer = new StringBuffer( );
      try {
         if ( plotTitle != null ) buffer.append( "Plot-Title : " ).append( plotTitle ).append( "\n" );
         if ( inputStreamName != null ) {
            buffer.append( "Input-Stream Name : " ).append( inputStreamName ).append( "\n" );
         }
         buffer.append( "Width : " ).append( width ).append( "\n" );
         buffer.append( "Height : " ).append( height ).append( "\n" );
         if ( type != null ) buffer.append( "Type : " ).append( type ).append( "\n" );
         buffer.append( "History-size : " ).append( historySize ).append( "\n" );
      } catch ( Exception e ) {
         buffer.insert( 0 , "ERROR : Till now the ChartVirtualSensor instance could understand the followings : \n" );
      }
      return buffer.toString( );
   }
}