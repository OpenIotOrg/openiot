package org.openiot.gsn.wrappers.ieee1451;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.thoughtworks.xstream.XStream;

public class TedsReader {
   
   
   public static void main ( String [ ] args ) {
      try {
         TEDS teds = readTedsFromXMLFile(new File("micaONE.xml") );
         System.out.println( teds.toHtmlString( ) );
         
      } catch ( Exception e ) {
         e.printStackTrace( );
      }
   }
   
   public static TEDS readTedsFromXMLFile (  File fileName ) {
      XStream xs = new XStream( );
      try {
         TEDS teds = new TEDS( ( Object [ ][ ][ ] ) xs.fromXML( new FileInputStream(  fileName ) ) );
         return teds;
      } catch ( FileNotFoundException e ) {
         e.printStackTrace( );
      }
      return null;
   }
   
   public static TEDS readTedsFromBinaryFile ( String TARGET_DIR,String fileName ) {
      FileInputStream fos;
      try {
         fos = new FileInputStream( new File( TARGET_DIR + fileName ) );
         ObjectInputStream os = new ObjectInputStream( fos );
         Object obj = os.readObject( );
         os.close( );
         return ( new TEDS( ( Object [ ][ ][ ] ) obj ) );
      } catch ( FileNotFoundException e ) {
         e.printStackTrace( );
      } catch ( IOException e ) {
         e.printStackTrace( );
      } catch ( ClassNotFoundException e ) {
         e.printStackTrace( );
      }
      return null;
   }
}
