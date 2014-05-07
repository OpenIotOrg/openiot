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

package org.openiot.gsn.wrappers.cameras.usb;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ImageWrapper implements Serializable {
   
   private transient BufferedImage bimg;
   
   private transient Image         image;
   
   public ImageWrapper ( Image image ) {
      this.image = image;
   }
   
   public boolean equals ( Object obj ) {
      return image.equals( ( ( ImageWrapper ) obj ).image );
   }
   
   public BufferedImage getBufferedImage ( ) {
      if ( bimg == null ) {
         bimg = new BufferedImage( image.getWidth( null ) , image.getHeight( null ) , BufferedImage.TYPE_INT_RGB );
         bimg.createGraphics( );
      }
      bimg.getGraphics( ).drawImage( image , 0 , 0 , null );
      return bimg;
   }
   
   public Image getImage ( ) {
      return image;
   }
   
   public int hashCode ( ) {
      return image.hashCode( );
   }
   
   public void setImage ( Image image ) {
      this.image = image;
   }
   
   private void readObject ( ObjectInputStream stream ) throws java.io.IOException {
      try {
         stream.defaultReadObject( ); // read non-transient, non-static data
         Dimension dim = ( Dimension ) stream.readObject( );
         Object img = stream.readObject( );
         
         int [ ] pix = ( int [ ] ) img;
         
         Toolkit toolKit = Toolkit.getDefaultToolkit( );
         
         image = toolKit.createImage( new MemoryImageSource( dim.width , dim.height , pix , 0 , dim.width ) );
      } catch ( ClassNotFoundException e ) {
         throw new java.io.IOException( );
      }
   }
   
   private void writeObject ( ObjectOutputStream stream ) throws java.io.IOException {
      stream.defaultWriteObject( ); // write non-transient, non-static data
      PixelGrabber grabber = new PixelGrabber( image , 0 , 0 , -1 , -1 , true );
      
      try {
         grabber.grabPixels( );
      } catch ( InterruptedException e ) {}
      Object pix = grabber.getPixels( );
      Dimension dim = new Dimension( image.getWidth( null ) , image.getHeight( null ) );
      stream.writeObject( dim );
      stream.writeObject( pix );
   }
}
