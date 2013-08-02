package org.openiot.gsn.others.visualization.svg;

public interface SVGElement {
   
   /**
    * Draws the SVG tags of this SVGElement and its children on the specified
    * input.
    * 
    * @param input The SVG tags will be written on the input.
    */
   public void drawOn ( StringBuilder input );
   
   /**
    * Gets the name of this SVGElement. The name is typically the toString of
    * the internal Object.
    * 
    * @return Null if the internal object is null or toString() method of the
    * internal object returns null.
    */
   public String getName ( );
   
   /**
    * Gets the Opacity value.
    * 
    * @return
    */
   public double getOpaqeAlpha ( );
   
   /**
    * Sets the opactiy of the SVGElement. Typically the valus is 1. This can be
    * used to generate nice looking graphic effects.
    * 
    * @param value
    */
   
   public void setOpacity ( double value );
   
   /**
    * Sets the URL link associated with the SVG Element.
    * 
    * @param link
    */
   public void setLink ( String link );
   
   /**
    * Sets the internal object inside the SVGElement
    * 
    * @param o
    */
   public void setObject ( Object o );
   
   /**
    * Gets the internal object inside the SVGElement
    * 
    * @return
    */
   public Object getObject ( );
   
   /**
    * If a SVGElement is disable, it will not be used. Set the value for enable
    * field.
    * 
    * @param enable
    */
   public void setEnable ( boolean enable );
   
   public boolean isEnable ( );
}
