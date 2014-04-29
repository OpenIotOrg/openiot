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
import java.awt.Font;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.mylar.zest.layout.LayoutEntity;

public class SVGText extends AbstractSVGElement implements LayoutEntity {
   
   private Object                           realObject;
   
   private double                           userX           = -1 , userY = -1 , cachedWidth = -1 , cachedHeight = -1;
   
   private double                           layoutX , layoutY;
   
   private String [ ]                       tokens;
   
   private Width_Height_Decendent_ValueBean cachedDimention;
   
   private double                           opaque          = 1;
   
   private int                              fontSize        = 12;
   
   private int                              fontStyle       = Font.PLAIN;
   
   private String                           text            = "";
   
   private Color                            fillColor       = Color.BLACK;
   
   private boolean                          border          = false;
   
   private Color                            borderColor     = Color.white;
   
   private Color                            backgroundColor = Color.white;
   
   private final String                     TEMPLATE        = "\n<text x=\"$X$\" y=\"$Y$\" font-family=\"arial\" font-size=\"$FONT_SIZE$px\" fill=\"#$FILL_COLOR$\" >";
   
   private static final String              END_TEMPLATE    = "</text>";
   
   private final StringTemplate             stringTemplate  = new StringTemplate( TEMPLATE );
   
   private int                              roundness       = 5;
   
   private final int                        marginX         = 3;
   
   public String getName ( ) {
      if ( realObject != null ) return realObject.toString( );
      return null;
   }
   
   public Color getBorderColor ( ) {
      return borderColor;
   }
   
   public void setBorderColor ( Color borderColor ) {
      this.borderColor = borderColor;
   }
   
   public Color getBackgroundColor ( ) {
      return backgroundColor;
   }
   
   public void setBackgroundColor ( Color backgroundColor ) {
      this.backgroundColor = backgroundColor;
   }
   
   public SVGText ( Object internalObject , int x , int y , String text ) {
      this.realObject = internalObject;
      this.userX = x;
      this.userY = y;
      this.text = text;
   }
   
   public SVGText ( Object internalObject , String text ) {
      this.realObject = internalObject;
      this.text = text;
      userX = userY = cachedWidth = cachedHeight = -1;
   }
   
   public double getOpaqeAlpha ( ) {
      return opaque;
   }
   
   public float getFontSize ( ) {
      return fontSize;
   }
   
   public void setFontSize ( int fontSize ) {
      this.fontSize = fontSize;
   }
   
   public int getFontStyle ( ) {
      return fontStyle;
   }
   
   public void setFontStyle ( int fontStyle ) {
      this.fontStyle = fontStyle;
   }
   
   public String getText ( ) {
      return text;
   }
   
   public Color getFillColor ( ) {
      return fillColor;
   }
   
   public void setFillColor ( Color fillColor ) {
      this.fillColor = fillColor;
   }
   
   public void enableBorder ( boolean b ) {
      this.border = b;
   }
   
   public boolean isBorder ( ) {
      return border;
   }
   
   public void setBorder ( boolean border ) {
      this.border = border;
   }
   
   public int getRoundness ( ) {
      return roundness;
   }
   
   public void setRoundness ( int roundness ) {
      this.roundness = roundness;
   }
   
   /**
    * -----------------------------------------
    */
   public void setUserX ( double userX ) {
      this.userX = userX;
   }
   
   public void setUserY ( double userY ) {
      this.userY = userY;
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
   
   public void setLocationInLayout ( double x , double y ) {
      if ( userX > 0 && userY > 0 ) return;
      this.layoutX = x;
      this.layoutY = y;
   }
   
   public void setSizeInLayout ( double width , double height ) {
      return;
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
   
   private synchronized void initialize ( ) {
      Font font = new Font( "arial" , Font.PLAIN , fontSize + 1 );
      this.cachedDimention = SVGUtils.getBoundsOfAString( text , font );
      this.cachedHeight = cachedDimention.getTotalHeightUpTo( cachedDimention.getHeights( ).length ) + 5;
      this.cachedWidth = cachedDimention.getWidth( ) + 2 * marginX;
      tokens = cachedDimention.getStringTokenizer( );
   }
   
   public synchronized void setText ( String text ) {
      this.text = text;
      initialize( );
   }
   
   public double getWidthInLayout ( ) {
      if ( cachedWidth == -1 ) initialize( );
      return cachedWidth;
   }
   
   public double getHeightInLayout ( ) {
      if ( cachedHeight == -1 ) initialize( );
      return cachedHeight;
   }
   
   public void drawOn ( StringBuilder input ) {
      if ( !isEnable( ) ) return;
      SVGRectangle svgRectangle = new SVGRectangle( "" , getXInLayout( ) , getYInLayout( ) , getWidthInLayout( ) , getHeightInLayout( ) );
      svgRectangle.setBorderColor( borderColor );
      svgRectangle.setFillColor( backgroundColor );
      svgRectangle.setBorderWidth( 0.5f );
      svgRectangle.setOpacity( opaque );
      svgRectangle.setRoundness( roundness );
      svgRectangle.drawOn( input );
      printLinkStartElement( input );
      for ( int i = 0 ; i < tokens.length ; i++ ) {
         stringTemplate.reset( );
         stringTemplate.setAttribute( "X" , getXInLayout( ) + marginX );
         stringTemplate.setAttribute( "Y" , getYInLayout( ) + ( cachedDimention.getTotalHeightUpTo( i + 1 ) - cachedDimention.getDecendents( )[ i ] ) + 1 );
         stringTemplate.setAttribute( "FONT_SIZE" , fontSize );
         stringTemplate.setAttribute( "FILL_OPACITY" , opaque );
         stringTemplate.setAttribute( "FILL_COLOR" , SVGUtils.formatToHex( fillColor.getRGB( ) ) );
         input.append( stringTemplate.toString( ) );
         input.append( tokens[ i ] );
         input.append( END_TEMPLATE );
         printLinkEndElement( input );
      }
   }
   
   public void setOpacity ( double opaqeVal ) {
      this.opaque = opaqeVal;
   }
}
