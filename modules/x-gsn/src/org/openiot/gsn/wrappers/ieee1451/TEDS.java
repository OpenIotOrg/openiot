package org.openiot.gsn.wrappers.ieee1451;

import java.util.Hashtable;
import java.util.Vector;

public class TEDS {
   
   /**
    * Holds the meta data for a transducer. One is a Record to hand out to
    * getMetaData() requests while the other is a Vector to provide number based
    * indexing.
    */
   public Record       metaData       = new Record( );
   
   /**
    * A list of all the current meta data for the TIM. Index zero contains meta
    * data for the TIM itself.
    */
   protected Vector    metaDataVector = new Vector( );
   
   /**
    * A helper object containing a mapping from channel name to channel number.
    */
   protected Hashtable namesToChan    = new Hashtable( );
   
   public TEDS ( Object teds[][][] ) {
      
      for ( int ch = 0 ; ch < teds.length ; ch++ ) {
         Object rawChanTeds[][] = teds[ ch ];
         Measurement chanTeds = new Measurement( );
         for ( int i = 0 ; i < rawChanTeds.length ; i++ ) {
            chanTeds.put( ( String ) rawChanTeds[ i ][ 0 ] , rawChanTeds[ i ][ 1 ] );
         }
         addChannelTeds( ch , chanTeds );
      }
   }
   
   /**
    * Add a channel of meta data to the overall meta data collection. Also
    * update the internal namesToChan hashtable for quick lookup later.
    * 
    * @param chNum - The integer channel number used for reads. Use -1 to allow
    * automatic number incrementing and replacment of duplicate channel name
    * teds.
    * @param chanMetaData - The meta data for this channel
    */
   @SuppressWarnings( "unchecked" )
   public synchronized void addChannelTeds ( int chNum , Measurement chanMetaData ) {
      String name = chanMetaData.getString( MeasAttr.NAME );
      
      if ( chNum == -1 && ( metaDataVector.size( ) == 0 ) ) chNum = 0;
      
      if ( chNum == 0 ) {
         chanMetaData.put( MeasAttr.METADATA_TYPE , MeasAttr.METADATA_T_TIM );
      } else {
         chanMetaData.put( MeasAttr.METADATA_TYPE , MeasAttr.METADATA_T_CHANNEL );
      }
      chanMetaData.lock( );
      
      // // test to see if the name is already added. This is a fatal error if
      // // chNum!= -1 as we cannot have two channels with the same name
      // Object test = metaData.get(name);
      // if (test != null) {
      // if (chNum != -1) {
      // String msg = "Duplicate channel name for " + name;
      // ArrayStoreException e = new ArrayStoreException(msg);
      // // log.log(Log.SEVERE, msg, e);
      // throw e;
      // } else {
      // // We have a duplicate name. Replace this chan teds with the new
      // // chan teds.
      // Integer chan = (Integer) namesToChan.get(name);
      // if (chan != null)
      // chNum = chan.intValue();
      // }
      // }
      metaData.put( name , chanMetaData );
      
      // Check for auto chan numbering
      if ( chNum == -1 ) chNum = metaDataVector.size( );
      
      // check to see if we need to grow the vector
      if ( chNum >= metaDataVector.size( ) ) metaDataVector.setSize( chNum + 1 );
      metaDataVector.setElementAt( chanMetaData , chNum );
      namesToChan.put( new Integer( chNum ) , name );
   }
   
   public int NumberOfChannels ( ) {
      return namesToChan.size( );
   }
   
   public ArgArray GetChannel ( int channelNo ) {
      return ( ArgArray ) metaData.get( namesToChan.get( new Integer( channelNo ) ).toString( ) );
      
   }
   
   public String toString ( ) {
      return metaData.toString( );
   }
   
   public String toHtmlString ( ) {
      return metaData.toHtmlString( );
   }
   
}
