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

package org.openiot.gsn.wrappers.ieee1451;

import org.openiot.gsn.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.log4j.Logger;

public class TedsToVirtualSensor {
   private final Logger             logger              = Logger.getLogger( TedsToVirtualSensor.class );
   
   /*
    * The directory in which the Virtual Sensor Template files are present.
    */
   public String              templateDir;
   
   /*
    * The Virtual Sensor template file. The template files are usually of the
    * form "filename.st" Here you have to specify the just the "filename"
    * without any extension
    */
   public String              templatefile;
   
   /*
    * The directory in which the generated Virtual Sensor file should be stored.
    */
   public static final String TARGET_VS_DIR = ( Main.DEFAULT_VIRTUAL_SENSOR_DIRECTORY + "/" );
   
   /*
    * This function generates the virtual sensor file from the TEDS input.
    */
   StringTemplate             vstmp;
   
   public TedsToVirtualSensor ( String templateDir , String templateFile ) {
      this.templateDir = templateDir;
      this.templatefile = templateFile;
      StringTemplateGroup grp = new StringTemplateGroup( "myGroup" , templateDir );
      vstmp = grp.getInstanceOf( templateFile );
      
   }
   
   public synchronized TedsToVSResult GenerateVSfromTEDS ( TEDS sensorTEDS ) {
      Channel ch0 = new Channel( );
      vstmp.reset( );
      try {
         // Channel0 metadata
         ArgArray r = sensorTEDS.GetChannel( 0 );
         ch0.name = r.get( MeasAttr.NAME ).toString( );
         ch0.description = r.get( MeasAttr.DESCRIPTION ).toString( );
         ch0.location = r.get( MeasAttr.LOCATION ).toString( );
         ch0.author = r.get( MeasAttr.MANUFACTURER ).toString( );
         ch0.email = "auto-detect-gsn@epfl.ch";
         ch0.NumberOfChannels = Integer.parseInt( r.get( MeasAttr.NUMBER_OF_CHANNELS ).toString( ) );
         if ( r.get( MeasAttr.IP ) == null ) ch0.IP = "localhost";
         else
            ch0.IP = r.get( MeasAttr.IP ).toString( );
         
         vstmp.setAttribute( "CH0" , ch0 );
         
         // Other Channels metadata
         
         for ( int i = 1 ; i < sensorTEDS.NumberOfChannels( ) ; i++ ) {
            r = sensorTEDS.GetChannel( i );
            Channel ch = new Channel( );
            ch.key = i;
            ch.name = r.get( MeasAttr.NAME ).toString( );
            ch.type = r.get( MeasAttr.DATA_TYPE ).toString( );
            ch.description = r.get( MeasAttr.DESCRIPTION ).toString( );
            
            vstmp.setAttribute( "FIELD" , ch );
         }
         // System.out.println(vstmp.toString());
         
         PrintWriter result = null; // Character output stream for writing data.
         
         try {
            // Writing the xml string generated from template file to the VS
            // xml file
            File outputF = new File( TARGET_VS_DIR + ch0.name + ".xml");
            if (!outputF.isFile( ))
               outputF.createNewFile( );
            outputF.deleteOnExit( );
            result = new PrintWriter( new FileWriter( outputF ) );
            result.println( vstmp.toString( ) );
         } catch ( Exception e ) {
            // Some problem reading the data from the input file.
            logger.warn( "Error in generating the VirtualSensor File.",e );
            return new TedsToVSResult( -1 ); // End the program.
         } finally {
            // Finish by closing the files,
            // whatever else may have happened.
            if (result!=null)
               result.close( );
         }
         
      } catch ( Exception e ) {
         e.printStackTrace( );
         return new TedsToVSResult( -1 ); // End the program.
      }
      return new TedsToVSResult( ch0.name + ".xml" , 0 , sensorTEDS.toHtmlString( ) , ch0.name );
   }
   
   /*
    * This function generates the virtual sensor file from the TEDS input.
    */
   public synchronized int GenerateVSfromTEDS_OLD ( TEDS sensorTEDS ) {
      vstmp.reset( );
      try {
         ArgArray r = sensorTEDS.GetChannel( 0 );
         Channel ch0 = new Channel( );
         ch0.name = r.get( MeasAttr.NAME ).toString( );
         ch0.description = r.get( MeasAttr.DESCRIPTION ).toString( );
         ch0.location = r.get( MeasAttr.LOCATION ).toString( );
         ch0.author = r.get( MeasAttr.MANUFACTURER ).toString( );
         ch0.email = "auto-detect-gsn@epfl.ch";
         ch0.NumberOfChannels = Integer.parseInt( r.get( MeasAttr.NUMBER_OF_CHANNELS ).toString( ) );
         
         vstmp.setAttribute( "CH0" , ch0 );
         
         for ( int i = 1 ; i < sensorTEDS.NumberOfChannels( ) ; i++ ) {
            r = sensorTEDS.GetChannel( i );
            Channel ch = new Channel( );
            ch.key = i;
            ch.name = r.get( MeasAttr.NAME ).toString( );
            ch.type = r.get( MeasAttr.DATA_TYPE ).toString( );
            ch.description = r.get( MeasAttr.DESCRIPTION ).toString( );
            
            vstmp.setAttribute( "FIELD" , ch );
         }
         System.out.println( vstmp.toString( ) );
         
         PrintWriter result; // Character output stream for writing data.
         
         try { // Create the output stream.
            result = new PrintWriter( new FileWriter( TARGET_VS_DIR + ch0.name + ".xml" ) );
         } catch ( IOException e ) {
            System.out.println( "Error in VS File operation." );
            System.out.println( e.toString( ) );
            return -1; // End the program.
         }
         
         try {
            // Writing the xml string generated from template file to the VS
            // xml file
            result.println( vstmp.toString( ) );
            
         } catch ( Exception e ) {
            // Some problem reading the data from the input file.
            System.out.println( "Error in generating the VirtualSensor File;" + e.getMessage( ) );
            return -1;
         } finally {
            // Finish by closing the files,
            // whatever else may have happened.
            result.close( );
         }
         
      } catch ( Exception e ) {
         // TODO Auto-generated catch block
         e.printStackTrace( );
         return -1;
      }
      
      return 0;
   }
   
   public TedsToVSResult GenerateVS ( ) {
      
      Object teds[][][] = {
      // chan0 - meta data about the TIM itself
            { { MeasAttr.NAME , "SampleTEDSOne" } , { MeasAttr.DESCRIPTION , "A Simple TEDS" } , { MeasAttr.VERSION , "1.0" } , { MeasAttr.LOCATION , "INM 035" } ,
                  { MeasAttr.NUMBER_OF_CHANNELS , "2" } , { MeasAttr.MANUFACTURER , "GSN-Mica2-Network" } , { MeasAttr.METADATA_ID , "Channel 0" } } ,
            // chan1
            { { MeasAttr.NAME , "DATA" } , { MeasAttr.DESCRIPTION , "Temperature value" } , { MeasAttr.UNITS , "Celcius" } , { MeasAttr.DATA_TYPE , "integer" } ,
                  { MeasAttr.METADATA_ID , "Channel 1" } } ,
            // chan2
            { { MeasAttr.NAME , "Light" } , { MeasAttr.DESCRIPTION , "Light value" } , { MeasAttr.UNITS , "lumens" } , { MeasAttr.DATA_TYPE , "double" } , { MeasAttr.METADATA_ID , "Channel 2" } } ,

      };
      TEDS sensorTEDS = new TEDS( teds );
      // int i = GenerateVSfromTEDS(sensorTEDS);
      TedsToVSResult res = GenerateVSfromTEDS( sensorTEDS );
      if ( res.status == TedsToVSResult.ERROR ) {
         System.out.println( "**Error** in creating TEDS-VS. Check for TedsToVirtualSensor.java File" );
      } else {
         System.out.println( "TEDS-VS generated Successfully" );
      }
      
      return res;
   }
   
   public TedsToVSResult GenerateVS ( Object teds[][][] ) {
      TEDS sensorTEDS = new TEDS( teds );
      // int i = GenerateVSfromTEDS(sensorTEDS);
      TedsToVSResult res = GenerateVSfromTEDS( sensorTEDS );
      if ( res.status == TedsToVSResult.ERROR ) {
         System.out.println( "**Error** in creating TEDS-VS. Check for TedsToVirtualSensor.java File" );
      } else {
         System.out.println( "TEDS-VS generated Successfully" );
      }
      return res;
   }
   
   public TedsToVSResult GenerateVS ( TEDS sensorTEDS ) {
      // int i = GenerateVSfromTEDS(sensorTEDS);
      TedsToVSResult res = GenerateVSfromTEDS( sensorTEDS );
      if ( res.status == TedsToVSResult.ERROR ) {
         System.out.println( "**Error** in creating TEDS-VS. Check for TedsToVirtualSensor.java File" );
      } else {
         System.out.println( "TEDS-VS generated Successfully" );
      }
      return res;
   }
   
   public TedsToVSResult getTedsToVSResult ( TEDS sensorTEDS ) {
      String fileName = sensorTEDS.GetChannel( 0 ).get( MeasAttr.NAME ).toString( );
      return new TedsToVSResult( fileName + ".xml" , 0 , sensorTEDS.toHtmlString( ) , fileName );
   }
   
   public TEDS GenerateVS_OLD ( Object teds[][][] ) {
      TEDS sensorTEDS = new TEDS( teds );
      // int i = GenerateVSfromTEDS(sensorTEDS);
      TedsToVSResult res = GenerateVSfromTEDS( sensorTEDS );
      if ( res.status == TedsToVSResult.ERROR ) {
         System.out.println( "**Error** in creating TEDS-VS. Check for TedsToVirtualSensor.java File" );
      } else {
         System.out.println( "TEDS-VS generated Successfully" );
      }
      return sensorTEDS;
   }
}

class Channel {
   
   public String name;
   
   public String type;
   
   public String description;
   
   public String location;
   
   public String author;
   
   public String email;
   
   public int    key;
   
   public int    NumberOfChannels;
   
   public String IP;
   
   public Channel ( ) {

   }
   
   public Channel ( String name , String type , String description , String location ) {
      this.name = name;
      this.type = type;
      this.description = name;
      this.location = location;
   }
   
}
