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

package org.openiot.gsn.wrappers.tinyos;

// Data Converter for Mica2 Sensors.
public class Convert {
   
   // Here are the constant used, maybe you will want to put theme elsewere
   /** *********************************************************************************** */
   static final int    R1     = 10000;
   
   static final double a      = 0.001307050;
   
   static final double b      = 0.000214381;
   
   static final double c      = 0.000000093;
   
   static final int    ADC_FS = 1023;
   
   // Voltage reference for mica2
   static final double RV     = 1.223;
   
   // Calibration for Accalometer
   int                 minus_one_calibrationX;
   
   int                 plus_one_calibrationX;
   
   int                 minus_one_calibrationY;
   
   int                 plus_one_calibrationY;
   
   // The main method call all the converting method --> can show you how
   // it
   // fonctions
   // You may want to use the readabkeValue() method to repr�sente your
   // values
   /** *********************************************************************************** */
   public static void main ( String args[] ) {
      
      Convert cv = new Convert( );
      // System.out.println("Value : "+cv.calculateTempC(500));
      // adc Voltage = 473
      System.out.println( "\nHere is the voltage : " + cv.convertVoltage( 473 ) + " mV\n" );
      
      // Light
      double light = cv.convertLight( 913 , cv.convertVoltage( 473 ) );
      System.out.println( "Here is the light value : " + cv.readableValue( light ) + "\n" );
      
      // Temp
      double kelvinTemp = cv.convertTemprature( 508 );
      System.out.println( "The temp value is : " + cv.readableValue( kelvinTemp ) + " Kelvin degres" );
      System.out.println( "The temp value is : " + cv.readableValue( ( float ) ( kelvinTemp - 273.15 ) ) + " Celsus degres\n" );
      
      // Magnometer magX
      double magX = cv.convertMag( 804 );
      System.out.println( "Here is the mag X value : " + cv.readableValue( magX ) + " mGauss" );
      
      // Magnometer magY
      double magY = cv.convertMag( 805 );
      System.out.println( "Here is the mag Y value : " + cv.readableValue( magY ) + " mGauss\n" );
      
      // Calibrate Accel
      cv.initAccel( 550 , 492 );
      
      // AccelX converting
      double accelX = cv.convertAccelX( 550 );
      System.out.println( "Here is the accel X value : " + cv.readableValue( accelX ) );
      
      // AccelY converting
      double accelY = cv.convertAccelY( 492 );
      System.out.println( "Here is the accel Y value : " + cv.readableValue( accelY ) + "\n" );
      
      //
      
   }
   
   // Tempreture converting
   /** *********************************************************************************** */
   public static double convertTemprature ( double adc ) {
      /**
       * The if(adc>1000) adc/=10; will be applied on tinyos packets generated
       * by tinynode motes.
       */
      if ( adc > 1000 ) adc /= 10;
      double rth = ( R1 * ( ADC_FS - adc ) ) / adc;
      // double rth = ( R1 * ( ADC_FS - adc ) ) / adc ;
      double lnRth = Math.log( rth );
      double x = a + ( b * lnRth );
      double y = c * Math.pow( lnRth , 3 );
      double output = ( ( 1 / ( x + y ) ) - 273.15 );
      if ( Double.isNaN( output ) ) return -1;
      return output;
   }
   
   // Light converting
   /** *********************************************************************************** */
   
   public static double convertLight ( int adcData , double batteryInVoltage ) {
      return ( adcData * batteryInVoltage ) / ADC_FS;
   }
   
   // Voltage converting
   /** *********************************************************************************** */
   public static double convertVoltage ( int data ) {
      // returns the voltage value in volte
      double x = ( RV * ADC_FS );
      double y = 1252352;
      return ( float ) ( x / data ); // Volt
      // return ( float ) ( y / data ) ; // milliVolt
   }
   
   // Change the values to a reabable format with only 2 digits after the
   // dot
   /** *********************************************************************************** */
   public static double readableValue ( double x ) {
      return x;
      // return ( float ) (Math.ceil( x * 100 )/100) ;
   }
   
   // Magnometer mag converter
   /** ********************************************************************************** */
   public static double convertMag ( int adcData ) {
      return ( adcData / ( 1.023 * 2.262 * 3.2 ) );
   }
   
   /*
    * - First initialize the calibration values (with the non moving mote) -
    * After you can use the converting methods
    */

   // Accel calibration : initializes the class fiels for calibration
   /** ********************************************************************************** */
   public void initAccel ( int accelX_adcData , int accelY_adcData ) {
      minus_one_calibrationX = accelX_adcData + 60;
      plus_one_calibrationX = accelX_adcData - 60;
      minus_one_calibrationY = accelY_adcData + 60;
      plus_one_calibrationY = accelY_adcData - 60;
   }
   
   // AccelX converting
   /** *******************************************************************490***615******* */
   public float convertAccelX ( int adcData ) {
      double zero_value = ( plus_one_calibrationX - minus_one_calibrationX ) / 2;
      double reading = ( zero_value - ( plus_one_calibrationX - adcData ) ) / zero_value;
      return ( float ) reading;
   }
   
   // AccalY converting
   /** ********************************************************************432****552***** */
   public double convertAccelY ( int adcData ) {
      double zero_value = ( plus_one_calibrationY - minus_one_calibrationY ) / 2;
      double reading = ( zero_value - ( plus_one_calibrationY - adcData ) ) / zero_value;
      return reading;
      
   }
   
}
