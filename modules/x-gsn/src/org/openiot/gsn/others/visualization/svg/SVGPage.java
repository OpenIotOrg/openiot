package org.openiot.gsn.others.visualization.svg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.antlr.stringtemplate.StringTemplate;

public class SVGPage {
   
   private int                           width           = -1;
   
   private int                           height          = -1;
   
   private float                         borderWidth     = 0;
   
   private String                        backgroundImage;
   
   private Color                         backgroundColor = Color.white;
   
   private TreeMap < String , SVGLayer > layers          = new TreeMap( new Comparator( ) {
                                                            
                                                            public int compare ( Object o1 , Object o2 ) {
                                                               if ( o1 == null || o2 == null ) return -1;
                                                               String input1 = ( String ) o1;
                                                               String input2 = ( String ) o2;
                                                               return input1.compareToIgnoreCase( input2 );
                                                               
                                                            }
                                                         } );
   
   public SVGPage ( String backgroundImage ) {
      this.backgroundImage = backgroundImage;
   }
   
   private final static String  SVG_HEADER_TEMPLATE = "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n"
                                                       + "         \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<svg  xmlns=\"http://www.w3.org/2000/svg\" \n"
                                                       + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"$WIDTH$\" height=\"$HEIGHT$\" preserveAspectRatio=\"none\" > \n";
   
   private static final String  DEF_BEGIN           = "<defs>";
   
   private StringTemplate [ ]   defines             = new StringTemplate [ ] { new StringTemplate(
                                                       "<marker id=\"MidMarker\" viewBox=\"0 0 10 10\" refX=\"1\" refY=\"5\" markerUnits=\"strokeWidth\" orient=\"auto\" >   <polyline points=\"0,0 10,5 0,10 1,5\" fill=\"black\" />  </marker>" ) };
   
   private static final String  DEF_END             = "</defs>";
   
   private final static String  SVG_END             = "\n</svg>";
   
   private final StringTemplate stringTemplate      = new StringTemplate( SVG_HEADER_TEMPLATE );
   
   public SVGPage ( int width , int height , Color backgroundColor ) throws IOException {
      this.width = width;
      this.height = height;
      this.backgroundColor = backgroundColor;
   }
   
   public SVGPage ( int width , int height ) {
      this.width = width;
      this.height = height;
   }
   
   public SVGPage ( int width , int height , int borderWidth ) throws IOException {
      this.width = width;
      this.height = height;
      this.borderWidth = borderWidth;
      
   }
   
   public SVGPage ( int width , int height , int borderWidth , Color backgroundColor ) throws IOException {
      this.width = width;
      this.height = height;
      this.borderWidth = borderWidth;
      this.backgroundColor = backgroundColor;
   }
   
   public int getWidth ( ) {
      return width;
   }
   
   public int getHeight ( ) {
      return height;
   }
   
   public float getBorderWidth ( ) {
      return borderWidth;
   }
   
   public String getBackgroundImage ( ) {
      return backgroundImage;
   }
   
   public Color getBackgroundColor ( ) {
      return backgroundColor;
   }
   
   public void setWidth ( int width ) {
      this.width = width;
   }
   
   public void setHeight ( int height ) {
      this.height = height;
   }
   
   public void setBorderWidth ( int borderWidth ) {
      this.borderWidth = borderWidth;
   }
   
   public void setBackgroundImage ( String backgroundImage ) {
      this.backgroundImage = backgroundImage;
   }
   
   public void setBackgroundColor ( Color backgroundColor ) {
      this.backgroundColor = backgroundColor;
   }
   
   public void drawOn ( StringBuilder input ) {
      stringTemplate.reset( );
      StringBuilder result = new StringBuilder( );
      try {
         if ( backgroundImage != null ) {
            InputStream is = new FileInputStream( backgroundImage );
            BufferedImage bi = ImageIO.read( is );
            width = bi.getWidth( );
            height = bi.getHeight( );
            // TODO Setting the background image
         }
      } catch ( IOException e ) {
         e.printStackTrace( );
         System.exit( 1 );
      }
      if ( width > 0 && height > 0 ) {
         stringTemplate.setAttribute( "WIDTH" , width );
         stringTemplate.setAttribute( "HEIGHT" , height );
         result.append( stringTemplate.toString( ) );
      }
      result.append( DEF_BEGIN );
      for ( StringTemplate defs : defines ) {
         result.append( defs.toString( ) );
      }
      result.append( DEF_END );
      
      if ( backgroundImage != null ) {

      }
      Iterator < SVGLayer > it = layers.values( ).iterator( );
      while ( it.hasNext( ) ) {
         it.next( ).drawOn( result );
      }
      result.append( SVG_END );
      input.append( result );
   }
   
   public String getName ( ) {
      return "SVGPAGE";
   }
   
   public float getOpaqeAlpha ( ) {
      return 1;
   }
   
   public SVGPage addLayer ( SVGLayer layer ) {
      layers.put( layer.getName( ) , layer );
      return this;
   }
   
   public SVGPage removeLayer ( SVGLayer layer ) {
      layers.remove( layer.getName( ) );
      return this;
   }
   
   public SVGPage removeLayer ( String layerName ) {
      layers.remove( layerName );
      return this;
   }
   
   public void drawInFile ( String s ) throws IOException {
      File file = new File( s );
      StringBuilder stringBuilder = new StringBuilder( );
      drawOn( stringBuilder );
      FileWriter fileWriter = new FileWriter( file );
      fileWriter.write( stringBuilder.toString( ) );
      fileWriter.close( );
   }
}
