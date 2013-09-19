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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.eclipse.mylar.zest.layout.HorizontalTreeLayoutAlgorithm;
import org.eclipse.mylar.zest.layout.LayoutAlgorithm;
import org.eclipse.mylar.zest.layout.LayoutEntity;
import org.eclipse.mylar.zest.layout.LayoutRelationship;
import org.eclipse.mylar.zest.layout.LayoutStyles;
import org.eclipse.mylar.zest.layout.RadialLayoutAlgorithm;
import org.eclipse.mylar.zest.layout.TreeLayoutAlgorithm;

public class Main {
   
   public static final TreeLayoutAlgorithm           TREE_VERT      = new TreeLayoutAlgorithm( LayoutStyles.NONE );
   
   public static final HorizontalTreeLayoutAlgorithm TREE_HORIZ     = new HorizontalTreeLayoutAlgorithm( LayoutStyles.NONE );
   
   public static final RadialLayoutAlgorithm         RADIAL         = new RadialLayoutAlgorithm( LayoutStyles.NONE );
   
   private List < LayoutAlgorithm >                  algorithms     = new ArrayList < LayoutAlgorithm >( );
   
   private List < String >                           algorithmNames = new ArrayList < String >( );
   
   private JFrame                                    mainFrame;
   
   private JPanel                                    mainPanel;
   
   private List < LayoutEntity >                     entities;
   
   private List < LayoutRelationship >               relationships;
   
   private JToolBar                                  toolBar;
   
   protected Point                                   selectedEntityPositionAtMouseDown;
   
   private SVGPage                                   svgPage        = new SVGPage( 600 , 600 );
   
   protected void addAlgorithm ( LayoutAlgorithm algorithm , String name , boolean animate ) {
      algorithms.add( algorithm );
      algorithmNames.add( name );
   }
   
   public void swingDemo ( ) {
      addAlgorithm( TREE_VERT , "Tree-V" , false );
      addAlgorithm( TREE_HORIZ , "Tree-H" , false );
      addAlgorithm( RADIAL , "Radial" , false );
      
      mainFrame = new JFrame( );
      toolBar = new JToolBar( );
      mainFrame.getContentPane( ).setLayout( new BorderLayout( ) );
      mainFrame.getContentPane( ).add( toolBar , BorderLayout.NORTH );
      
      for ( int i = 0 ; i < algorithms.size( ) ; i++ ) {
         final LayoutAlgorithm algorithm = algorithms.get( i );
         final String algorithmName = algorithmNames.get( i );
         JButton algorithmButton = new JButton( algorithmName );
         algorithmButton.addActionListener( new ActionListener( ) {
            
            public void actionPerformed ( ActionEvent e ) {
               algorithm.setEntityAspectRatio( ( double ) svgPage.getWidth( ) / ( double ) svgPage.getHeight( ) );
               SVGUtils.performLayout( entities , relationships , algorithm , svgPage.getWidth( ) , svgPage.getHeight( ) );
               mainPanel.paintImmediately( 0 , 0 , svgPage.getWidth( ) , svgPage.getHeight( ) );
            }
         } );
         toolBar.add( algorithmButton );
      }
      createMainPanel( );
      mainFrame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
      /**
       * =====PREPARING THE GRAPH START
       */
      entities = new ArrayList < LayoutEntity >( );
      relationships = new ArrayList < LayoutRelationship >( );
      // There is a bug in size determination.
      SVGCircle layoutEntity = new SVGCircle( "A" , Color.YELLOW );
      layoutEntity.setRadios( 80 );
      SVGCircle layoutEntity2 = new SVGCircle( "B" , Color.BLACK );
      layoutEntity2.setRadios( 10 );
      SVGCircle layoutEntity3 = new SVGCircle( "C" , Color.YELLOW );
      layoutEntity3.setRadios( 10 );
      SVGCircle layoutEntity4 = new SVGCircle( "D" , Color.YELLOW );
      layoutEntity4.setRadios( 10 );
      SVGRectangle layoutEntity5 = new SVGRectangle( "E" );
      
      SVGEdge rel1 = new SVGEdge( "f" , layoutEntity , layoutEntity2 , true );
      SVGEdge rel3 = new SVGEdge( "g" , layoutEntity3 , layoutEntity4 , true );
      SVGEdge rel35 = new SVGEdge( "h" , layoutEntity3 , layoutEntity5 , true );
      
      SVGLayer contents = new SVGLayer( "i" , 1 );
      SVGText svgText = new SVGText( "test1" , "Node ID : 23\nParent ID : 34\nTempreature : 34" );
      svgText.setFontSize( 10 );
      svgText.enableBorder( true );
      svgText.setOpacity( 0.3 );
      svgText.setBackgroundColor( Color.white );
      svgText.setBorderColor( Color.GRAY );
      contents.addElement( svgText );
      contents.addElement( layoutEntity ).addElement( layoutEntity2 ).addElement( layoutEntity3 ).addElement( layoutEntity4 ).addElement( layoutEntity5 );
      contents.addElement( rel1 ).addElement( rel3 ).addElement( rel35 );
      
      svgPage.addLayer( contents );
      
      StringBuilder stringBuilder = new StringBuilder( );
      entities.add( layoutEntity );
      entities.add( layoutEntity2 );
      entities.add( layoutEntity3 );
      entities.add( layoutEntity4 );
      entities.add( layoutEntity5 );
      relationships.add( rel1 );
      relationships.add( rel35 );
      relationships.add( rel3 );
      SVGUtils.performLayout( entities , relationships , TREE_VERT , svgPage.getWidth( ) , svgPage.getHeight( ) );
      svgText.setUserX( layoutEntity2.getXInLayout( ) + layoutEntity2.getWidthInLayout( ) / 2 );
      svgText.setUserY( layoutEntity2.getYInLayout( ) + layoutEntity2.getHeightInLayout( ) / 2 );
      
      mainFrame.pack( );
      mainFrame.setVisible( true );
      try {
         svgPage.drawInFile( "/tmp/s.svg" );
      } catch ( Exception e ) {
         e.printStackTrace( );
         return;
      }
      System.out.println( stringBuilder.toString( ) );
      
   }
   
   private void createMainPanel ( ) {
      mainPanel = new JPanel( ) {
         
         protected void paintChildren ( Graphics g ) {
            for ( LayoutRelationship relations : relationships ) {
               g.setColor( Color.GREEN );
               g.drawLine( ( int ) ( relations.getSourceInLayout( ).getXInLayout( ) + relations.getSourceInLayout( ).getWidthInLayout( ) / 2 ) , ( int ) ( relations.getSourceInLayout( )
                     .getYInLayout( ) + relations.getSourceInLayout( ).getHeightInLayout( ) / 2 ) , ( int ) ( relations.getDestinationInLayout( ).getXInLayout( ) + relations.getDestinationInLayout( )
                     .getWidthInLayout( ) / 2 ) , ( int ) ( relations.getDestinationInLayout( ).getYInLayout( ) + relations.getDestinationInLayout( ).getHeightInLayout( ) / 2 ) );
            }
            for ( LayoutEntity entity : entities ) {
               g.setColor( Color.BLUE );
               if ( entity instanceof SVGCircle ) {
                  g.drawOval( ( int ) entity.getXInLayout( ) , ( int ) entity.getYInLayout( ) , ( int ) entity.getWidthInLayout( ) , ( int ) entity.getHeightInLayout( ) );
               } else if ( entity instanceof SVGRectangle ) {
                  g.drawRect( ( int ) entity.getXInLayout( ) , ( int ) entity.getYInLayout( ) , ( int ) entity.getWidthInLayout( ) , ( int ) entity.getHeightInLayout( ) );
               }
               
            }
         }
      };
      mainPanel.setPreferredSize( new Dimension( svgPage.getWidth( ) , svgPage.getHeight( ) ) );
      mainPanel.setLayout( null );
      mainFrame.getContentPane( ).add( new JScrollPane( mainPanel ) , BorderLayout.CENTER );
   }
   
   public static void main ( String [ ] args ) {
      ( new Main( ) ).swingDemo( );
   }
}
