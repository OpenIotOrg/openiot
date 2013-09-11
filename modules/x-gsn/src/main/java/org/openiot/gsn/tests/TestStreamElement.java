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


public class TestStreamElement {

//  @Test
//  This method has to be modified to be adapted to httpclient/httpcore library version 4.0
//  public void testRequestStructure() throws IOException {    
//    PostMethod post = new PostMethod("http://localhost:3000/gsn/structure/test1");
//    HttpClient client = new HttpClient();
//    int status = client.executeMethod(post);
//    String[][] out = GSNRequestHandler.parseOutputStructureFromREST(post.getResponseBodyAsString());
//    assertEquals(7, out.length); // 7 is the number of the fields specified for test1 virtual sensor inside the virtual-sensors-tests.
//    assertEquals(out[6][0],"field_7" );
//    assertEquals(out[6][1],"binary:test/test" );
//    assertEquals(201,status);
//  }


//  @Test
//  public void testRegisterQuery() throws IOException, EncoderException {
//    String originalQuery = "select * from wrapper";
//    boolean result = GSNRequestHandler.sendRegisterQueryRequest(originalQuery, "test1", "1", "1", "localhost", 3000, "localhost", 3000, 123456);
//    assertEquals(true,result);
//    assertEquals(1, Mappings.getContainer( ).getNotificationRequests("test1").size());
//    assertEquals(originalQuery, Mappings.getContainer( ).getNotificationRequests("test1").get(0).getOriginalQuery().toString());
//
//  }

//  @Test
//  public void testRestToAndFromBehaviors() throws IOException, NoSuchAlgorithmException {
//    FileInputStream fis = new FileInputStream("webapp/img/button_cancel.png");
//    byte[] binary = new byte[fis.available()];
//    fis.read(binary);
//    fis.close();
//
//    String testString = "ABCDEFGHIJKLMNOPQSTUVWXYZ!@#$%^&*()+_)(*&^%$#@!~}{|'\":?><";
//    StreamElement se = new StreamElement(
//        new String[] {"field_1","field_2","field_3","field_4","field_5","field_6","field_7"},
//        new Byte[] {DataTypes.BIGINT,DataTypes.TINYINT,DataTypes.INTEGER,DataTypes.DOUBLE,DataTypes.CHAR,DataTypes.VARCHAR,DataTypes.BINARY},
//        new Serializable[] {123456789392873l,(byte)123,1234567,1234.12345,"A",testString,binary},123456789l);
//    Part[] toRest = se.toREST();
//    assertEquals( se.getFieldNames().length+1,toRest.length);
//    String digestedCode = md5Digest(binary);
//    
//    MockWrapper wrapper = new MockWrapper();
//    DataField[] structure = new DataField[se.getData().length];
//    for (int i=0;i<structure.length;i++) {
//      structure[i] = new DataField(se.getFieldNames()[i],se.getFieldTypes()[i]);
//    }
//    wrapper.setOutputStructure(structure );
//    Mappings.getContainer().addRemoteStreamSource(123456,wrapper );
//    PostMethod post = new PostMethod("http://localhost:3000/gsn/notify/123456");
//    post.setRequestEntity(new MultipartRequestEntity(toRest,post.getParams()));
//    HttpClient client = new HttpClient();
//    int status = client.executeMethod(post);
//    if (status !=201)
//      System.out.println(post.getResponseBodyAsString());
//    assertEquals(201,status);
//    assertEquals(1, wrapper.getStreamElements().size());
//    StreamElement consumedSe = wrapper.getStreamElements().get(0);
//    assertEquals(se.getTimeStamp() ,consumedSe.getTimeStamp() );
//    for (int i=0;i<structure.length;i++) {
//      assertEquals(se.getFieldNames()[i] ,consumedSe.getFieldNames()[i] );
//      assertEquals(se.getFieldTypes()[i] ,consumedSe.getFieldTypes()[i] );
//      assertEquals(se.getData()[i].getClass() ,consumedSe.getData()[i].getClass() );
//      if(se.getData()[i] instanceof byte[]) {
//        String newDigestedCode = md5Digest((byte[])consumedSe.getData()[i]);
//        assertEquals(digestedCode ,newDigestedCode);
//      }else {
//        assertEquals(se.getData()[i] ,consumedSe.getData()[i] );
//      }
//    }
//
//  }

//  private static RailsRunner runner = new RailsRunner();
//  @BeforeClass public static void startWebApp() throws InterruptedException {
//    Main.DEFAULT_VIRTUAL_SENSOR_DIRECTORY="virtual-sensors-tests";
//    runner.start();
//    System.out.println("NOW TESTING");
//
//  }
//
//  @AfterClass public static void stopWebApp() throws InterruptedException {
//    runner.stop();
//  }
//  public static String md5Digest(byte[] input) throws NoSuchAlgorithmException {
//    MessageDigest digest=MessageDigest.getInstance("MD5");
//    digest.update(input);
//    String digestedCode = new BigInteger(1,digest.digest()).toString(16);
//    return digestedCode;
//  }
}
