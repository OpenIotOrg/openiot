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

package org.openiot.gsn.others.visualization.svg;

import java.util.ArrayList;
import java.util.Collection;

import org.antlr.stringtemplate.StringTemplate;

public class SVGLayer extends AbstractSVGElement {
   
   private String                   name;
   
   private double                   opacity;
   
   private final StringTemplate     START_TAG = new StringTemplate( "<g opacity=\"$FILL_OPACITY$\" > " );
   
   private final String             END_TAG   = "\n</g>";
   
   private ArrayList < SVGElement > objects   = new ArrayList < SVGElement >( );
   
   public SVGLayer ( String name , float opacity ) {
      this.name = name;
      this.opacity = opacity;
   }
   
   public void drawOn ( StringBuilder input ) {
      
      START_TAG.reset( );
      START_TAG.setAttribute( "FILL_OPACITY" , opacity );
      input.append( START_TAG.toString( ) );
      for ( SVGElement se : objects )
         se.drawOn( input );
      input.append( END_TAG );
   }
   
   public String getName ( ) {
      return name;
   }
   
   public void setOpacity ( double opaqeVal ) {
      this.opacity = opaqeVal;
   }
   
   public double getOpaqeAlpha ( ) {
      return opacity;
   }
   
   protected ArrayList < SVGElement > getChildren ( ) {
      return objects;
   }
   
   public SVGLayer addElements ( Collection < ? extends SVGElement > elements ) {
      for ( SVGElement element : elements )
         addElement( element );
      return this;
   }
   
   public SVGLayer addElement ( SVGElement element ) {
      if ( element == this ) { throw new RuntimeException( " You can't add a layer to itself." ); }
      objects.add( element );
      return this;
   }
   
   public boolean removeElement ( SVGElement element ) {
      return objects.remove( element );
   }
   
   private boolean enable = true;
   
   public void setEnable ( boolean enable ) {
      this.enable = enable;
   }
   
   public boolean isEnable ( ) {
      return enable;
   }
   
   /**
    * ------------------------------------------------------------
    */
   private Object realObject;
   
   public void setObject ( Object userObject ) {
      this.realObject = userObject;
   }
   
   public Object getObject ( ) {
      return realObject;
   }
   
}
