package org.openiot.gsn.others.visualization.svg;

import java.awt.Color;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.mylar.zest.layout.LayoutEntity;

public class SVGRectangle extends AbstractSVGElement implements LayoutEntity {
   
   private Object               realObject;
   
   private double               userX       = -1 , userY = -1 , userWidth = -1 , userHeight = -1;
   
   private double               layoutX , layoutY , layoutWidth , layoutHeight;
   
   private double               opaque      = 1;
   
   private Color                borderColor = Color.BLACK;
   
   private float                borderWidth = 0;
   
   private Color                fillColor   = Color.GRAY;
   
   private int                  roundness   = 1;
   
   private final StringTemplate TEMPLATE    = new StringTemplate(
                                               "<rect fill=\"#$FILL_COLOR$\" fill-opacity=\"$OPAQUE$\" width=\"$WIDTH$\" height=\"$HEIGHT$\" rx=\"$ROUNDNESS$\" ry=\"$ROUNDNESS$\" stroke-width=\"$STROKE_WIDTH$\" stroke=\"#$STROKE_COLOR$\" x=\"$X$\" y=\"$Y$\" />" );
   
   public void drawOn ( StringBuilder input ) {
      if ( !isEnable( ) ) return;
      printLinkStartElement( input );
      TEMPLATE.reset( );
      TEMPLATE.setAttribute( "X" , getXInLayout( ) );
      TEMPLATE.setAttribute( "Y" , getYInLayout( ) );
      TEMPLATE.setAttribute( "WIDTH" , getWidthInLayout( ) );
      TEMPLATE.setAttribute( "HEIGHT" , getHeightInLayout( ) );
      TEMPLATE.setAttribute( "ROUNDNESS" , roundness );
      TEMPLATE.setAttribute( "STROKE_WIDTH" , borderWidth );
      TEMPLATE.setAttribute( "STROKE_COLOR" , SVGUtils.formatToHex( borderColor.getRGB( ) ) );
      TEMPLATE.setAttribute( "FILL_COLOR" , SVGUtils.formatToHex( fillColor.getRGB( ) ) );
      TEMPLATE.setAttribute( "OPAQUE" , opaque );
      input.append( TEMPLATE.toString( ) );
      printLinkEndElement( input );
   }
   
   public SVGRectangle ( Object internalObject , double x , double y , double width , double height ) {
      this.realObject = internalObject;
      this.userX = x;
      this.userY = y;
      this.userWidth = width;
      this.userHeight = height;
   }
   
   public SVGRectangle ( Object internalObject ) {
      this.realObject = internalObject;
      this.userX = -1;
      this.userY = -1;
      this.userWidth = -1;
      this.userHeight = -1;
      
   }
   
   public String getName ( ) {
      if ( realObject != null ) return realObject.toString( );
      return null;
   }
   
   public void setOpacity ( double opaqeVal ) {
      this.opaque = opaqeVal;
   }
   
   public void setBorderColor ( Color borderColor ) {
      this.borderColor = borderColor;
   }
   
   public void setBorderWidth ( float borderWidth ) {
      this.borderWidth = borderWidth;
   }
   
   public void setFillColor ( Color fillColor ) {
      this.fillColor = fillColor;
   }
   
   public void setRoundness ( int roundness ) {
      this.roundness = roundness;
   }
   
   public Color getBorderColor ( ) {
      return borderColor;
   }
   
   public float getBorderWidth ( ) {
      return borderWidth;
   }
   
   public Color getFillColor ( ) {
      return fillColor;
   }
   
   public int getRoundness ( ) {
      return roundness;
   }
   
   /**
    * ----------------------------------------------------------
    */
   public void setUserX ( int userX ) {
      this.userX = userX;
   }
   
   public void setUserY ( int userY ) {
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
   
   public double getWidthInLayout ( ) {
      if ( userWidth > 0 && userHeight > 0 ) return userWidth;
      return layoutWidth;
   }
   
   public double getHeightInLayout ( ) {
      if ( userWidth > 0 && userHeight > 0 ) return userHeight;
      return layoutHeight;
   }
   
   public void setSizeInLayout ( double width , double height ) {
      if ( userWidth > 0 && userHeight > 0 ) return;
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
   
   public double getOpaqeAlpha ( ) {
      return opaque;
   }
}
