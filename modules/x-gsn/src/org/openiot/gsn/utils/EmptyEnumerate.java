package org.openiot.gsn.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EmptyEnumerate < T > implements Enumeration {
   
   public boolean hasMoreElements ( ) {
      return false;
   }
   
   public Object nextElement ( ) throws NoSuchElementException {
      return new NoSuchElementException( "This is an Empty Enumerator" );
   }
   
}
