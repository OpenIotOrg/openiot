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
