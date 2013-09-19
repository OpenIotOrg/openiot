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

package org.openiot.gsn.http;

import org.apache.log4j.Logger;

public class VirtualSensorDataForwarder {
   
   public static final String     VIRTUAL_SENSOR_NAME = "vsname";
   
   public static final String     DATA_ITEM           = "data";
   
   private final transient Logger logger              = Logger.getLogger( VirtualSensorDataForwarder.class );
   
   // public ActionForward execute ( ActionMapping mapping , ActionForm
   // form ,
   // HttpServletRequest req , HttpServletResponse res ) throws Exception {
   // String data = req.getParameter ( DATA_ITEM ) ;
   // String vsName = req.getParameter ( WebConstants.VIRTUAL_SENSOR_NAME )
   // ;
   // String srcPageName = req.getParameter ( WebConstants.PAGE_ID ) ;
   // int srcPageID = - 1 ;
   // try {
   // srcPageID = Integer.parseInt ( srcPageName ) ;
   // } catch ( NumberFormatException e ) {
   // return mapping.findForward ( "fail" ) ;
   // }
   // if ( data == null || vsName == null )
   // return mapping.findForward ( "fail" ) ;
   // VSensorInstance vs = Mappings.getVSensorInstanceByVSName ( vsName ) ;
   // if ( vs == null )
   // return mapping.findForward ( "fail" ) ;
   // try {
   // vs.getPool ( ).borrowObject ( ).dataFromWeb ( data ) ;
   // } catch ( PoolIsFullException e ) {
   // if ( logger.isInfoEnabled ( ) )
   // logger.info ( new StringBuilder ( ).append ( "The command for "
   // ).append (
   // vsName ).append (
   // " virtual sensor dropped b/c not enough virtual sensors available in
   // the
   // pool. (MAX POOL SIZE : " ).append ( vs.getPool ( ).getMaxSize ( ) )
   // .toString ( ) ) ;
   // return mapping.findForward ( "fail" ) ;
   // } catch ( VirtualSensorInitializationFailedException e2 ) {
   // logger.info ( new StringBuilder ( ).append ( "The command for "
   // ).append (
   // vsName ).append (
   // " virtual sensor dropped b/c the virtual sensor's initialization is
   // failed
   // " ).append ( e2.getMessage ( ) ) ) ;
   // return mapping.findForward ( "fail" ) ;
   // }
   // return mapping.findForward ( "success" ) ;
   // }
   
}
