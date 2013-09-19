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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ControllerServlet extends HttpServlet {

   private static transient Logger                                      logger                             = Logger.getLogger ( ControllerServlet.class );

   /**
    * HTTP RETURN CODES :
    * ---------------------------------------------------------------------
    */

   public static final int    CORRECT_REQUEST                        = 200;

   public static final int    UNSUPPORTED_REQUEST_ERROR              = 400;

   public static final int    MISSING_VSNAME_ERROR                   = 401;

   public static final int    ERROR_INVALID_VSNAME                   = 402;

   public static final int    WRONG_VSFIELD_ERROR                    = 403;
   /**
    * HTTP REQUEST CODE ==================================================
    */

   public static final int    REQUEST_ONE_SHOT_QUERY_WITH_ADDRESSING = 116;

   public static final int    REQUEST_ONE_SHOT_QUERY                 = 114;

   public static final int    REQUEST_OUTPUT_FORMAT                  = 113;

   public static final int    REQUEST_ADDRESSING                     = 115;

   public static final int    REQUEST_GML		             = 901;


   /**
    * getting the request from the web and handling it.
    */
   public void doGet ( HttpServletRequest request , HttpServletResponse response ) throws ServletException , IOException {
      response.setContentType ( "text/xml" );
      // to be sure it isn't cached
      response.setHeader ( "Expires" , "Sat, 6 May 1995 12:00:00 GMT" );
      response.setHeader ( "Cache-Control" , "no-store, no-cache, must-revalidate" );
      response.addHeader ( "Cache-Control" , "post-check=0, pre-check=0" );
      response.setHeader ( "Pragma" , "no-cache" );

      String rawRequest = request.getParameter ( WebConstants.REQUEST );
      int requestType = -1;
      if ( rawRequest == null || rawRequest.trim ( ).length ( ) == 0 ) {
         requestType = 0;
      } else
         try {
            requestType = Integer.parseInt ( ( String ) rawRequest );
         } catch ( Exception e ) {
            logger.debug ( e.getMessage ( ) , e );
            requestType = -1;
         }
      StringBuilder sb = new StringBuilder ( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" );
      response.getWriter ( ).write ( sb.toString ( ) );
      RequestHandler handler;
      if ( logger.isDebugEnabled ( ) ) logger.debug ( "Received a request with code : " + requestType );

      switch ( requestType ) {
      case 0 : //default case pointing to the /org.openiot.gsn
          handler = new ContainerInfoHandler ( );
          if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
          break;
      case REQUEST_GML : //case pointing to gml (v2) output
          handler = new GMLHandler ( );
          if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
          break;
      case REQUEST_ONE_SHOT_QUERY :
            handler = new OneShotQueryHandler ( );
            if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
            break;
         case REQUEST_ONE_SHOT_QUERY_WITH_ADDRESSING :
            handler = new OneShotQueryWithAddressingHandler ( );
            if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
            break;
         case REQUEST_OUTPUT_FORMAT :
            handler = new OutputStructureHandler ( );
            if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
            break;
         case REQUEST_ADDRESSING :
            handler = new AddressingReqHandler ( );
            if ( handler.isValid ( request , response ) ) handler.handle ( request , response );
            break;
         default :
            response.sendError ( UNSUPPORTED_REQUEST_ERROR , "The requested operation is not supported." );
            break;
      }
   }

   public void doPost ( HttpServletRequest request , HttpServletResponse res ) throws ServletException , IOException {
      doGet ( request , res );
   }

}
