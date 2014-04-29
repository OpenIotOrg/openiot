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

import java.awt.Color;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.mylar.zest.layout.LayoutEntity;

public class SVGCircle extends AbstractSVGElement implements LayoutEntity {
   
   private final String         TEMPLATE    = "<circle cx=\"$X$\" cy=\"$Y$\" r=\"$R$\" fill=\"#$FILL_COLOR$\" stroke=\"#$STROKE_COLOR$\" stroke-width=\"$STROKE_WIDTH$\" stroke-opacity=\"$STROKE_OPACITY$\" /> \n";
   
   private int                  userX       = -1;
   
   private int                  userY       = -1;
   
   private double               layoutX;
   
   private double               layoutY;
   
   private int                  radios      = -1;
   
   private Color                fillColor;
   
   private Color                borderColor = Color.white;
   
   private float                borderWidth = 0;
   
   private double               opaque      = 1;
   
   private final StringTemplate st          = new StringTemplate( TEMPLATE );
   
   private Object               realObject;
   
   private double               layoutWidth;
   
   private double               layoutHeight;
   
   public SVGCircle ( Object internalObject , Color color ) {
      this.realObject = internalObject;
      this.fillColor = color;
   }
   
   public SVGCircle ( Object internalObject , int x , int y , int radios , Color color ) {
      this.realObject = internalObject;
      this.userX = x;
      this.userY = y;
      this.radios = radios;
      this.fillColor = color;
   }
   
   public SVGCircle ( Object internalObject , int x , int y , int radios , Color color , Color borderColor , float borderWidth ) {
      this.realObject = internalObject;
      this.userX = x;
      this.userY = y;
      this.radios = radios;
      this.fillColor = color;
      this.borderColor = borderColor;
      this.borderWidth = borderWidth;
   }
   
   public void drawOn ( StringBuilder input ) {
      if ( !isEnable( ) ) return;
      printLinkStartElement( input );
      st.reset( );
      st.setAttribute( "X" , getXInLayout( ) + Math.min( getWidthInLayout( ) , getHeightInLayout( ) ) / 2 );
      st.setAttribute( "Y" , getYInLayout( ) + Math.min( getWidthInLayout( ) , getHeightInLayout( ) ) / 2 );
      st.setAttribute( "R" , Math.min( getWidthInLayout( ) , getHeightInLayout( ) ) / 2 );
      st.setAttribute( "FILL_COLOR" , SVGUtils.formatToHex( fillColor.getRGB( ) ) );
      st.setAttribute( "STROKE_COLOR" , SVGUtils.formatToHex( borderColor.getRGB( ) ) );
      st.setAttribute( "STROKE_WIDTH" , borderWidth );
      st.setAttribute( "STROKE_OPACITY" , opaque );
      input.append( st.toString( ) );
      printLinkEndElement( input );
   }
   
   public String getName ( ) {
      if ( realObject != null ) return realObject.toString( );
      return null;
   }
   
   public double getOpaqeAlpha ( ) {
      return opaque;
   }
   
   public void setOpacity ( double opaqeVal ) {
      this.opaque = opaqeVal;
   }
   
   public void setUserX ( int userX ) {
      this.userX = userX;
   }
   
   public void setUserY ( int userY ) {
      this.userY = userY;
   }
   
   public int getRadios ( ) {
      return radios;
   }
   
   public void setRadios ( int radios ) {
      this.radios = radios;
   }
   
   public Color getFillColor ( ) {
      return fillColor;
   }
   
   public void setFillColor ( Color fillColor ) {
      this.fillColor = fillColor;
   }
   
   public Color getBorderColor ( ) {
      return borderColor;
   }
   
   public void setBorderColor ( Color borderColor ) {
      this.borderColor = borderColor;
   }
   
   public float getBorderWidth ( ) {
      return borderWidth;
   }
   
   public void setBorderWidth ( float borderWidth ) {
      this.borderWidth = borderWidth;
   }
   
   public boolean hasPreferredLocation ( ) {
      if ( userX > 0 && userY > 0 ) return true;
      return false;
   }
   
   public double getXInLayout ( ) {
      if ( userX > 0 && userY > 0 ) return userX;
      return layoutX;
   }
   
   public double getYInLayout ( ) {
      if ( userX > 0 && userY > 0 ) return userY;
      return layoutY;
   }
   
   public double getWidthInLayout ( ) {
      if ( radios > 0 ) return radios * 2;
      return layoutWidth;
   }
   
   public double getHeightInLayout ( ) {
      if ( radios > 0 ) return radios * 2;
      return layoutHeight;
   }
   
   public void setLocationInLayout ( double x , double y ) {
      if ( userX > 0 && userY > 0 ) return;
      this.layoutX = x;
      this.layoutY = y;
   }
   
   public void setSizeInLayout ( double width , double height ) {
      if ( radios > 0 ) return;
      layoutWidth = width;
      layoutHeight = height;
   }
   
   public void setObject ( Object o ) {
      this.realObject = o;
   }
   
   public Object getObject ( ) {
      return realObject;
   }
   
   public String toString ( ) {
      return getName( );
   }
   
   public boolean equals ( Object object ) {
      boolean result = false;
      if ( object instanceof AbstractSVGElement ) {
         AbstractSVGElement node = ( AbstractSVGElement ) object;
         result = getName( ).equals( node.getName( ) );
      }
      return result;
   }
}
