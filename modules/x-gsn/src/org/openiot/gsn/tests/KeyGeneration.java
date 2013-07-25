package org.openiot.gsn.tests;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGeneration {
   
   public static void main ( String [ ] args ) throws Exception {
      
      FileInputStream keyfis = new FileInputStream( args[ 0 ] );
      byte [ ] encKey = new byte [ keyfis.available( ) ];
      keyfis.read( encKey );
      keyfis.close( );
      
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec( encKey );
      KeyFactory keyFactory = KeyFactory.getInstance( "DSA" , "SUN" );
      PublicKey pubKey = keyFactory.generatePublic( pubKeySpec );
      
      keyfis = new FileInputStream( args[ 1 ] );
      encKey = new byte [ keyfis.available( ) ];
      keyfis.read( encKey );
      keyfis.close( );
      
      PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec( encKey );
      keyFactory = KeyFactory.getInstance( "DSA" , "SUN" );
      PrivateKey privKey = keyFactory.generatePrivate( privKeySpec );
      
      // Signing start.
      Signature dsa = Signature.getInstance( "SHA1withDSA" , "SUN" );
      dsa.initSign( privKey );
      dsa.update( new String( "Select * from bla" ).getBytes( ) );
      byte [ ] signature = dsa.sign( );
      // System.out.println (new String (coded)) ;
      
      // Verification start.
      Signature sig = Signature.getInstance( "SHA1withDSA" , "SUN" );
      sig.initVerify( pubKey );
      sig.update( "Select * from bla".getBytes( ) );
      
      boolean verifies = sig.verify( signature );
      System.out.println( "signature verifies: " + verifies );
      
   }
}
