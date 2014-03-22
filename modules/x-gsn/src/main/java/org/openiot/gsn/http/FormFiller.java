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

package org.openiot.gsn.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class FormFiller {
   
   public static void fillFromRequest ( HttpServletRequest req , Object form ) {
      Enumeration < String > params = req.getParameterNames( );
      while ( params.hasMoreElements( ) ) {
         String paramName = params.nextElement( );
         StringBuilder stringBuffer = new StringBuilder( paramName );
         stringBuffer.replace( 0 , 1 , "set" + Character.toUpperCase( paramName.charAt( 0 ) ) );
         Method method;
         try {
            method = form.getClass( ).getMethod( stringBuffer.toString( ) , new Class [ ] { String.class } );
            method.invoke( form , new Object [ ] { req.getParameter( paramName ) } );
         } catch ( SecurityException e ) {
            continue;
         } catch ( NoSuchMethodException e ) {
            continue;
         } catch ( IllegalArgumentException e ) {
            continue;
         } catch ( IllegalAccessException e ) {
            continue;
         } catch ( InvocationTargetException e ) {
            continue;
         }
         
      }
   }
}
