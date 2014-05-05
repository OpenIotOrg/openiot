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
 * @author Ali Salehi
*/

/*
 * 
 * $RCSfile: MeasAttr.java $	
 *
 * Copyright (c) 2003, 2004, 2005, Agilent Technologies, Inc. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 *    -	Redistributions of source code must retain the above 
 *      copyright notice, this list of conditions and the following 
 *      disclaimer. 
 *    -	Redistributions in binary form must reproduce the above 
 *      copyright notice, this list of conditions and the following 
 *      disclaimer in the documentation and/or other materials provided 
 *      with the distribution. 
 *    -	Neither the name of Agilent Technologies, Inc. nor the names 
 *      of its contributors may be used to endorse or promote products 
 *      derived from this software without specific prior written 
 *      permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.openiot.gsn.wrappers.ieee1451;

/**
 * Contains enumerations for the different attributes that a measurement can
 * contain. When setting or getting measurement attributes, you should always
 * use these static constants rather than the actual string values as the actual
 * string values may change.
 * 
 * @see Measurement
 */
public class MeasAttr {
   
   /**
    * measurement value.
    */
   public static final String VALUE                     = "value";
   
   /**
    * measurement ID. should be unique.
    */
   public static final String ID                        = "id";
   
   /**
    * ID of the metadata
    */
   public static final String METADATA_ID               = "mid";
   
   /**
    * Last updated timestamp for metadata.
    */
   public static final String METADATA_TIMESTAMP        = "midt";
   
   /**
    * human readable name of measurement. does not need to be unique.
    */
   public static final String NAME                      = "name";
   
   /**
    * Another name field.
    */
   public static final String SHORT_NAME                = "shortName";
   
   /**
    * Short description of this measurement.
    */
   public static final String ERROR                     = "error";
   
   public static final String DESCRIPTION               = "description";
   
   public static final String NUMBER_OF_CHANNELS        = "NumberOfChannels";
   
   public static final String IP                        = "IpAddress";
   
   public static final String INTERPRETATION            = "interpretation";
   
   public static final String BUFFERING                 = "buffering";
   
   // The unit string should really not be plural but it's grandfathered in
   // deployed code...
   public static final String UNITS                     = "units";
   
   public static final String DATA_TYPE                 = "dataType";
   
   public static final String METADATA_TYPE             = "metaType";
   
   public static final String SCALE_TYPE                = "scaleType";
   
   public static final String VERSION                   = "version";
   
   public static final String MANUFACTURER              = "mfg";
   
   public static final String ENUM_DESC                 = "enumDesc";
   
   public static final String ERROR_INFO                = "errorInfo";
   
   // enumeration of METADATA_TYPE values
   public static final String METADATA_T_NCAP           = "NCAP";
   
   public static final String METADATA_T_FBLOCK         = "Fblock";
   
   public static final String METADATA_T_TIM            = "TIM";
   
   public static final String METADATA_T_CHANNEL        = "Channel";
   
   /**
    * The dimension of a measurement, such as mass, capacitance, charge, etc.
    * Typically, we infer a measurement's dimension from its unit. It is
    * sometimes useful to declare a measurement to have a particular dimension
    * before we've seen any values (and thus any units). Or we may want to force
    * it to have a particular dimension: we will accept any unit associated with
    * that dimension, but reject all others. Once we can restrict a measurement
    * (strictly, the record containing a measurement) to a dimension, then we
    * can make use of the ability to specialize dimensions. This lets us keep
    * output voltages separate from input voltages. You can still compute the
    * ratio of those two; but if you tried to include the wrong thing in a
    * moving average, say, you'd get an exception.
    */
   public static final String DIMENSION                 = "dimension";
   
   public static final String UPPER_LIMIT               = "upperLimit";
   
   public static final String LOWER_LIMIT               = "lowerLimit";
   
   public static final String UNCERTAINTY               = "uncertainty";
   
   public static final String NUMBER_OF_OCTETS          = "numberOfOctets";
   
   public static final String NUMBER_OF_SIGBITS         = "numberOfSigBits";
   
   public static final String RIGHT_JUSTIFIED_FLAG      = "rightJustifiedFlag";
   
   public static final String COORDINATE_SYSTEM         = "coordinateSystem";
   
   public static final String ABS_INCREMENT             = "abscissaIncrement";
   
   public static final String ABC_INCREMENT_UNCERTAINTY = "abscissaIncrementUncertainty";
   
   public static final String ABC_ORIGIN                = "abscissaOrigin";
   
   public static final String ABS_ORIGIN_UNCERTAINTY    = "abscissaOriginUncertainty";
   
   public static final String ABS_UNITS                 = "abscissaUnits";
   
   public static final String TIMESTAMP                 = "timestamp";
   
   public static final String TIMESTAMP_INTERVAL        = "timestampInterval";
   
   public static final String TIMESTAMP_UNCERTAINTY     = "timestampUncertainty";
   
   public static final String LOCATION                  = "location";
   
   public static final String SCHEDULED_TIMESTAMP       = "scheduledTimestamp";
   
   public static final String OFFSET                    = "offset";
   
   public static final String SIZE                      = "size";
} // end
