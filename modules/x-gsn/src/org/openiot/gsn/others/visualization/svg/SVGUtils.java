package org.openiot.gsn.others.visualization.svg;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.StringTokenizer;

import org.eclipse.mylar.zest.layout.HorizontalTreeLayoutAlgorithm;
import org.eclipse.mylar.zest.layout.InvalidLayoutConfiguration;
import org.eclipse.mylar.zest.layout.LayoutAlgorithm;
import org.eclipse.mylar.zest.layout.LayoutEntity;
import org.eclipse.mylar.zest.layout.LayoutRelationship;
import org.eclipse.mylar.zest.layout.LayoutStyles;
import org.eclipse.mylar.zest.layout.RadialLayoutAlgorithm;
import org.eclipse.mylar.zest.layout.TreeLayoutAlgorithm;

public class SVGUtils {
   
   public static final TreeLayoutAlgorithm           TREE_VERT  = new TreeLayoutAlgorithm( LayoutStyles.NONE );
   
   public static final HorizontalTreeLayoutAlgorithm TREE_HORIZ = new HorizontalTreeLayoutAlgorithm( LayoutStyles.NONE );
   
   public static final RadialLayoutAlgorithm         RADIAL     = new RadialLayoutAlgorithm( LayoutStyles.NONE );
   
   public static StringBuilder formatToHex ( int rgb ) {
      StringBuilder toReturn = new StringBuilder( Integer.toHexString( rgb & 0xffffff ) );
      while ( toReturn.length( ) < 6 ) {
         toReturn.insert( 0 , 0 );
      }
      return toReturn;
   }
   
   public static StringBuilder hyperLinkingAddress ( String URL ) {
      StringBuilder toReturn = new StringBuilder( "<a   xlink:href=\"" + URL + "\">" );
      return toReturn;
   }
   
   public static void performLayout ( Collection < ? extends LayoutEntity > entities , Collection < ? extends LayoutRelationship > relationships , LayoutAlgorithm currentLayoutAlgorithm , int width ,
                                      int height ) {
      try {
         LayoutEntity [ ] layoutEntities = new LayoutEntity [ entities.size( ) ];
         entities.toArray( layoutEntities );
         LayoutRelationship [ ] layoutRelationships = new LayoutRelationship [ relationships.size( ) ];
         relationships.toArray( layoutRelationships );
         for ( LayoutRelationship rel : relationships ) {
            if ( !entities.contains( rel.getSourceInLayout( ) ) || !entities.contains( rel.getDestinationInLayout( ) ) ) throw new RuntimeException(
               "The is a node in the relations that is not included in the list of the nodes." );
         }
         currentLayoutAlgorithm.applyLayout( layoutEntities , layoutRelationships , 0 , 0 , width , height , false , false );
      } catch ( InvalidLayoutConfiguration e ) {
         System.out.println( "Not a valid layout configuration:\nlayout='" + currentLayoutAlgorithm.toString( ) + "', continuous='" + false + "', asynchronous='" );
      } catch ( StackOverflowError e ) {
         e.printStackTrace( );
      }
   }
   
   public static Width_Height_Decendent_ValueBean getBoundsOfAString ( String s , Font f ) {
      int width = 0;
      s = s.replace( "\t" , "   " );
      StringTokenizer stringTokenizer = new StringTokenizer( s , "\n" , false );
      FontRenderContext context = new FontRenderContext( null , false , false );
      String [ ] tokens = new String [ stringTokenizer.countTokens( ) ];
      int [ ] heights = new int [ stringTokenizer.countTokens( ) ];
      int [ ] decendents = new int [ stringTokenizer.countTokens( ) ];
      int i = 0;
      while ( stringTokenizer.hasMoreElements( ) ) {
         String nextLine = stringTokenizer.nextToken( ).trim( );
         // TextLayout textLayout = new TextLayout ( nextLine, f, context
         // );
         Rectangle2D temp = f.getStringBounds( nextLine , context );
         // Rectangle2D temp = f.createGlyphVector ( context, nextLine
         // ).getLogicalBounds ();
         decendents[ i ] = ( int ) f.getLineMetrics( s , context ).getDescent( );
         width = Math.max( width , ( int ) temp.getWidth( ) );
         heights[ i ] = ( int ) temp.getHeight( );
         tokens[ i++ ] = nextLine;
         
      }
      return new Width_Height_Decendent_ValueBean( tokens , width , heights , decendents );
   }
}
