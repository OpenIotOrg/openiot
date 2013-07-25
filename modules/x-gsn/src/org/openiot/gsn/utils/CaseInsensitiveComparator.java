package org.openiot.gsn.utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Note that this class will trim all the space characters surrounding the key
 * and value pairs hence you don't need to call trim when putting or getting a
 * value to/from the hashmap.
 */
public class CaseInsensitiveComparator implements Comparator,Serializable {
   
private static final long serialVersionUID = 2540687777213332025L;

public int compare ( Object o1 , Object o2 ) {
      if ( o1 == null && o2 == null ) return 0;
      if ( o1 == null ) return -1;
      if ( o2 == null ) return 1;
      String input1 = o1.toString( ).trim( );
      String input2 = o2.toString( ).trim( );
      return input1.compareToIgnoreCase( input2 );
   }
}
