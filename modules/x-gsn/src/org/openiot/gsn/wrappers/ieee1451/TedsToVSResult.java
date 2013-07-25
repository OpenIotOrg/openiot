package org.openiot.gsn.wrappers.ieee1451;

public class TedsToVSResult {
   
   public static String ERROR   = "Error";
   
   public static String ADDED   = "Added";
   
   public static String REMOVED = "Removed";
   
   public static String NOTHING = "Nothing";
   
   public String        fileName;
   
   public String        status;
   
   public String        tedsHtmlString;
   
   /**
    * Possible values for TedsID are : <br>
    * MicaTWO, MicaONE,MicaTHREE
    */
   public String        tedsID;
   
   public TedsToVSResult ( String fileName , int status , String tedsHtmlString , String tedsID ) {
      this.fileName = fileName;
      this.status = statusString( status );
      this.tedsHtmlString = tedsHtmlString;
      this.tedsID = tedsID;
   }
   
   public TedsToVSResult ( int status ) {
      this.status = statusString( status );
   }
   
   private String statusString ( int status ) {
      String result;
      switch ( status ) {
         case -1 :
            result = ERROR;
            break;
         case 0 :
            result = ADDED;
            break;
         case 1 :
            result = REMOVED;
            break;
         default :
            result = NOTHING;
            
      }
      return result;
      
   }
}
