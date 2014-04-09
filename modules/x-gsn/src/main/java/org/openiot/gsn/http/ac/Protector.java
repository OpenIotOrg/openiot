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
 * @author Behnaz Bostanipour
 * @author Julien Eberle
*/

package org.openiot.gsn.http.ac;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 14, 2010
 * Time: 4:39:13 PM
 * To change this template use File | Settings | File Templates.
 */
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import sun.misc.*;


/* This class helps to encrypt user posswords using AES encryption algo, we use a salt for a more robust encryption, salt is stored in a property file "acuserpassword.properties"*/
public class Protector
{
    private static transient Logger logger                             = Logger.getLogger( Protector.class );

    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 2;


    private static final byte[] keyValue =new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        String salt=getSalt();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);

        String valueToEnc = null;
        String eValue = value;
        for (int i = 0; i < ITERATIONS; i++)
        {
            valueToEnc = salt + eValue;
            byte[] encValue = c.doFinal(valueToEnc.getBytes());
            eValue = new BASE64Encoder().encode(encValue);
        }
        return eValue;
    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        String salt=getSalt();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);

        String dValue = null;
        String valueToDecrypt = value;
        for (int i = 0; i < ITERATIONS; i++)
        {
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);
            byte[] decValue = c.doFinal(decordedValue);
            dValue = new String(decValue).substring(salt.length());
            valueToDecrypt = dValue;
        }
        return dValue;
    }
    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
    private static String getSalt()
    {
        
        return System.getProperty("salt");
       
    }
}

