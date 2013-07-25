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
