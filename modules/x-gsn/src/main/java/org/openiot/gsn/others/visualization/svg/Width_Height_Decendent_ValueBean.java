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

package org.openiot.gsn.others.visualization.svg;

public class Width_Height_Decendent_ValueBean {
   
   String [ ] stringTokenizer;
   
   int        width;
   
   int [ ]    heights;
   
   int [ ]    decendents;
   
   public Width_Height_Decendent_ValueBean ( String [ ] stringTokenizer , int width , int heights[] , int decendents[] ) {
      this.stringTokenizer = stringTokenizer;
      this.width = width;
      this.heights = heights;
      this.decendents = decendents;
   }
   
   public String [ ] getStringTokenizer ( ) {
      return stringTokenizer;
   }
   
   public void setStringTokenizer ( String [ ] stringTokenizer ) {
      this.stringTokenizer = stringTokenizer;
   }
   
   public int getWidth ( ) {
      return width;
   }
   
   public void setWidth ( int width ) {
      this.width = width;
   }
   
   public int [ ] getHeights ( ) {
      return heights;
   }
   
   public void setHeights ( int [ ] heights ) {
      this.heights = heights;
   }
   
   public int [ ] getDecendents ( ) {
      return decendents;
   }
   
   public void setDecendents ( int [ ] decendents ) {
      this.decendents = decendents;
   }
   
   /**
    * Starts from zero to length of the total available heights The
    * getTotalHeightUpTo(0) is zero.
    */
   public int getTotalHeightUpTo ( int lineCounter ) {
      if ( lineCounter < 0 || lineCounter > stringTokenizer.length ) throw new RuntimeException( "Outof the bound exception : " + lineCounter );
      
      int toReturn = 0;
      for ( int i = 0 ; i < lineCounter ; i++ )
         toReturn += heights[ i ];
      return toReturn;
   }
}
