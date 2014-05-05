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

package org.openiot.gsn.vsensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.openiot.gsn.http.WebConstants;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class TestContainerImpl {
   
   @After
   public void clean ( ) {

   }
   
   @Test
   public void gettingListOfVirtualSensors ( ) throws Exception {
      WebConversation wc = new WebConversation( );
      WebRequest request = new GetMethodWebRequest( "http://localhost:22001/gsn" );
      request.setParameter( "REQUEST" , WebConstants.REQUEST_LIST_VIRTUAL_SENSORS + "" );
      WebResponse response = wc.getResponse( request );
      assertTrue( response.getResponseMessage( ).contains( "<gsn>" ) );
      // assertNotNull( response.getHeaderField( Container.RESPONSE ) );
   }
   
   @Test
   public void oneShotQueryExecution ( ) throws Exception {
      WebConversation wc = new WebConversation( );
      WebRequest request = new GetMethodWebRequest( "http://localhost:22001/gsn" );
      request.setHeaderField( "REQUEST" , WebConstants.REQUEST_ONE_SHOT_QUERY + "" );
      request.setHeaderField( "VS_QUERY" , "select * from LocalSystemTime" );
      WebResponse response = wc.getResponse( request );
      assertEquals( response.getHeaderField( WebConstants.RESPONSE_STATUS ) , WebConstants.REQUEST_HANDLED_SUCCESSFULLY );
      assertNull( response.getHeaderField( WebConstants.RESPONSE ) );
   }
   
   @Before
   public void setup ( ) {

   }
   
   @BeforeClass
   public static void init ( ) {

   }
   
   @AfterClass
   public static void cleanAll ( ) {

   }
   
   public static junit.framework.Test suite ( ) {
      return new JUnit4TestAdapter( TestContainerImpl.class );
   }
   
}
