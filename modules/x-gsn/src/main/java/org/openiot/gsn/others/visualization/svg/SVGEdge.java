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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.mylar.zest.layout.LayoutEntity;
import org.eclipse.mylar.zest.layout.LayoutRelationship;

public class SVGEdge extends AbstractSVGElement implements LayoutRelationship {
   
   private boolean              directed       = false;
   
   private double               opaque         = 1;
   
   private int                  linewidth      = 1;
   
   private Color                color          = Color.BLACK;
   
   private final StringTemplate stringTemplate = new StringTemplate(
                                                  "\n<path stroke=\"#$COLOR$\" stroke-width=\"$WIDTH$\"    d=\"M $X1$,$Y1$ L $M1$,$M2$ L $X2$,$Y2$\" OPAQUE=\"$OPAQUE$\" marker-mid = \"url(#MidMarker)\"/>\n" );
   
   /**
    * A list of layout dependent attributes
    */
   private Map                  attributes;
   
   /**
    * The sourceEntity of this SimpleRelation.
    */
   protected LayoutEntity       sourceEntity;
   
   /**
    * The object of this SimpleRelation.
    */
   protected LayoutEntity       destinationEntity;
   
   /**
    * The weight given to this relation.
    */
   private double               weight;
   
   private Object               internalRelationshipForLayout;
   
   private Object               internalObject;
   
   /**
    * Constructor.
    * 
    * @param sourceEntity The sourceEntity of this SimpleRelation.
    * @param destinationEntity The object of this SimpleRelation.
    * @param bidirectional Determines if the <code>sourceEntity</code> and
    * <code>destinationEntity</code> are equal(exchangeable).
    * @throws NullPointerException If either <code>sourceEntity
    *                              </code> or
    * <code>destinationEntity</code> is <code>null</code>.
    */
   public SVGEdge ( Object o , LayoutEntity sourceEntity , LayoutEntity destinationEntity , boolean bidirectional ) {
      this( o , sourceEntity , destinationEntity , bidirectional , 1 );
   }
   
   public SVGEdge ( LayoutEntity sourceEntity , LayoutEntity destinationEntity , boolean bidirectional ) {
      this( null , sourceEntity , destinationEntity , bidirectional , 1 );
   }
   
   /**
    * Constructor.
    * 
    * @param sourceEntity The sourceEntity of this SimpleRelation.
    * @param destinationEntity The destinationEntity of this SimpleRelation.
    * <code>destinationEntity</code> are equal(exchangeable).
    * @throws NullPointerException If either <code>sourceEntity
    *                              </code> or
    * <code>destinationEntity</code> is <code>null</code>.
    */
   public SVGEdge ( Object o , LayoutEntity sourceEntity , LayoutEntity destinationEntity , boolean directed , double weight ) {
      this.destinationEntity = destinationEntity;
      this.sourceEntity = sourceEntity;
      this.weight = weight;
      this.attributes = new HashMap( );
      this.internalObject = o;
   }
   
   public void setDirected ( boolean directed ) {
      this.directed = directed;
   }
   
   public void setOpacity ( double opaqeVal ) {
      this.opaque = opaqeVal;
   }
   
   public void setWidth ( int width ) {
      this.linewidth = width;
   }
   
   public void setColor ( Color color ) {
      this.color = color;
   }
   
   public boolean isDirected ( ) {
      return directed;
   }
   
   public int getWidth ( ) {
      return linewidth;
   }
   
   public Color getColor ( ) {
      return color;
   }
   
   public void drawOn ( StringBuilder input ) {
      if ( !isEnable( ) ) return;
      printLinkStartElement( input );
      stringTemplate.reset( );
      double x1 , x2 , y1 , y2;
      x1 = sourceEntity.getXInLayout( ) + sourceEntity.getWidthInLayout( ) / 2.0;
      y1 = sourceEntity.getYInLayout( ) + sourceEntity.getHeightInLayout( ) / 2.0;
      x2 = destinationEntity.getXInLayout( ) + destinationEntity.getWidthInLayout( ) / 2.0;
      y2 = destinationEntity.getYInLayout( ) + destinationEntity.getHeightInLayout( ) / 2.0;
      stringTemplate.setAttribute( "X1" , x1 );
      stringTemplate.setAttribute( "X2" , x2 );
      stringTemplate.setAttribute( "Y1" , y1 );
      stringTemplate.setAttribute( "Y2" , y2 );
      double midX = ( x1 + x2 ) / 2.0;
      double midY = ( y1 + y2 ) / 2.0;
      stringTemplate.setAttribute( "M1" , midX );
      stringTemplate.setAttribute( "M2" , midY );
      stringTemplate.setAttribute( "WIDTH" , linewidth );
      stringTemplate.setAttribute( "COLOR" , SVGUtils.formatToHex( color.getRGB( ) ) );
      stringTemplate.setAttribute( "OPAQUE" , opaque );
      input.append( stringTemplate.toString( ) );
      printLinkEndElement( input );
   }
   
   public double getOpaqeAlpha ( ) {
      return opaque;
   }
   
   /**
    * -----------------------------------------------------------------------
    */
   
   /**
    * Gets the sourceEntity of this SimpleRelation whether the relation is
    * exchangeable or not.
    * 
    * @return The sourceEntity.
    */
   public LayoutEntity getSourceInLayout ( ) {
      return sourceEntity;
   }
   
   /**
    * Gets the destinationEntity of this SimpleRelation whether the relation is
    * exchangeable or not.
    * 
    * @return The destinationEntity of this SimpleRelation.
    */
   public LayoutEntity getDestinationInLayout ( ) {
      return destinationEntity;
   }
   
   public void setWeightInLayout ( double weight ) {
      this.weight = weight;
   }
   
   public double getWeightInLayout ( ) {
      return weight;
   }
   
   /**
    * An algorithm may require a place to store information. Use this structure
    * for that purpose.
    */
   public void setAttributeInLayout ( String attribute , Object value ) {
      attributes.put( attribute , value );
   }
   
   /**
    * An algorithm may require a place to store information. Use this structure
    * for that purpose.
    */
   public Object getAttributeInLayout ( String attribute ) {
      return attributes.get( attribute );
   }
   
   public String toString ( ) {
      String arrow = ( isBidirectionalInLayout( ) ? " <-> " : " -> " );
      return "(" + sourceEntity + arrow + destinationEntity + ")";
   }
   
   public Object getLayoutInformation ( ) {
      return internalRelationshipForLayout;
   }
   
   public void setLayoutInformation ( Object layoutInformation ) {
      this.internalRelationshipForLayout = layoutInformation;
   }
   
   public Object getObject ( ) {
      return internalObject;
   }
   
   public String getName ( ) {
      if ( internalObject != null ) return internalObject.toString( );
      return null;
   }
   
   public void setObject ( Object internalObject ) {
      this.internalObject = internalObject;
   }
   
   public void setLinewidth ( int linewidth ) {
      this.linewidth = linewidth;
   }
   
   public boolean isBidirectionalInLayout ( ) {
      return !directed;
   }
   
   public Map getAttributes ( ) {
      return attributes;
   }
   
   public void setAttributes ( Map attributes ) {
      this.attributes = attributes;
   }
   
   public LayoutEntity getSourceEntity ( ) {
      return sourceEntity;
   }
   
   public void setSourceEntity ( LayoutEntity sourceEntity ) {
      this.sourceEntity = sourceEntity;
   }
   
   public LayoutEntity getDestinationEntity ( ) {
      return destinationEntity;
   }
   
   public void setDestinationEntity ( LayoutEntity destinationEntity ) {
      this.destinationEntity = destinationEntity;
   }
   
   public Object getInternalObject ( ) {
      return internalObject;
   }
   
   public void setInternalObject ( Object internalObject ) {
      this.internalObject = internalObject;
   }
}
