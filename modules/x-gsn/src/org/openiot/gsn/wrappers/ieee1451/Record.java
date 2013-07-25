package org.openiot.gsn.wrappers.ieee1451;

/**
 * An aggregation or collection of related measurements. Measurements are stored
 * along with a name. This name is used to retrieve the Measurement instance.
 * <p>
 */
public class Record extends ArgArray {
   
   /**
    * Clone the given Record instance.
    * 
    * @param value
    */
   public Record ( Record value ) {
      super( );
      value.cloneContentsTo( this );
   }
   
   /**
    * Clone the given ArgArray instance
    * 
    * @param value
    */
   public Record ( ArgArray value ) {
      super( );
      value.cloneContentsTo( this );
   }
   
   /**
    * Creates a blank record with no comments.
    */
   public Record ( ) {

   }
}
