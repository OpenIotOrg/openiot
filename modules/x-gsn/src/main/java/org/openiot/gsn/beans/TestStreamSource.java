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
 * @author gsn_devs
 * @author Mehdi Riahi
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.openiot.gsn.Main;
import org.openiot.gsn.beans.windowing.WindowType;
import org.openiot.gsn.storage.DataEnumerator;
import org.openiot.gsn.storage.StorageManager;
import org.openiot.gsn.storage.StorageManagerFactory;
import org.openiot.gsn.utils.GSNRuntimeException;
import org.openiot.gsn.wrappers.AbstractWrapper;
import org.openiot.gsn.wrappers.SystemTime;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStreamSource {

	private AbstractWrapper wrapper = new SystemTime();
	private static StorageManager sm =  null;//StorageManager.getInstance();
  private AddressBean[] addressing = new AddressBean[] {new AddressBean("system-time")};
   
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	  PropertyConfigurator.configure ( Main.DEFAULT_GSN_LOG4J_PROPERTIES );
	  DriverManager.registerDriver( new org.h2.Driver( ) );
	  sm = StorageManagerFactory.getInstance("org.h2.Driver","sa","" ,"jdbc:h2:mem:.", Main.DEFAULT_MAX_DB_CONNECTIONS);
		
	}

	@Before
	public void setup() throws SQLException {
		sm.executeCreateTable(wrapper.getDBAliasInStr(), new DataField[] {},true);
		wrapper.setActiveAddressBean(new AddressBean("system-time"));
		assertTrue(wrapper.initialize());
	}
	@After
	public void teardown() throws SQLException {
		sm.executeDropTable(wrapper.getDBAliasInStr());
	}

	@Test
	public void testGetSQLQuery() {
		InputStream is = new InputStream();
		StreamSource ss = new StreamSource();
		ss.setAddressing(addressing);
		ss.setAlias("my-stream");
		ss.setRawHistorySize("10m");
		ss.setInputStream(is);
		assertTrue(ss.getSqlQuery().trim().equals("select * from wrapper"));
		ss = new StreamSource();
		ss.setAddressing(addressing);
		ss.setAlias("my-stream");
		ss.setRawHistorySize("10m");
		ss.setSqlQuery(" ");
		ss.setInputStream(is);
		assertEquals(ss.getSqlQuery().trim(),"select * from wrapper");
	}

	@Test
	public void testValidate() {
		InputStream is = new InputStream();
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10m");
		assertTrue(ss.validate());
		assertFalse(ss.isStorageCountBased());
		assertEquals(ss.getParsedStorageSize(),10*60*1000);
		ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  m").setInputStream(is);
		assertFalse(ss.isStorageCountBased());
		assertEquals(ss.getParsedStorageSize(),10*60*1000);


		ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  s").setInputStream(is);
		assertFalse(ss.isStorageCountBased());
		assertEquals(ss.getParsedStorageSize(),10*1000);
		assertFalse(ss.isStorageCountBased());


		ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("2 h").setInputStream(is);
		assertFalse(ss.isStorageCountBased());
		assertEquals(ss.getParsedStorageSize(),2*60*60*1000);
	}
	@Test (expected=GSNRuntimeException.class)
	public void testBadStreamSources() throws GSNRuntimeException{
		InputStream is = new InputStream();
	StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  min").setInputStream(is);
	}
	
	@Test (expected=GSNRuntimeException.class)
	public void testBadStreamSources2() throws GSNRuntimeException{
		InputStream is = new InputStream();
	StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  m20").setInputStream(is);
	}
	
	@Test (expected=GSNRuntimeException.class)
	public void testBadStreamSources3() throws GSNRuntimeException{
		InputStream is = new InputStream();
	StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("m").setInputStream(is);
	}
	
	@Test
	public void testBadWindowSize() throws GSNRuntimeException{
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  sec");
		assertFalse(ss.validate());
	}
	
	@Test
	public void testBadSlideValue() throws GSNRuntimeException{
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  s").setRawSlideValue("5 sec");
		assertFalse(ss.validate());
	}
	
	@Test
	public void testWindowType1(){
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10 s");
		assertEquals(ss.getWindowingType(), WindowType.TIME_BASED_SLIDE_ON_EACH_TUPLE);
		ss.setRawSlideValue("5 s");
		assertEquals(ss.getWindowingType(), WindowType.TIME_BASED);
		ss.setRawSlideValue("2");
		assertEquals(ss.getWindowingType(), WindowType.TIME_BASED_WIN_TUPLE_BASED_SLIDE);
		ss.setRawSlideValue("");
		assertEquals(ss.getWindowingType(), WindowType.TIME_BASED_SLIDE_ON_EACH_TUPLE);
	}
	
	@Test
	public void testWindowType2(){
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10 ");
		assertTrue(ss.validate());
		assertTrue(ss.validate());
		assertEquals(ss.getWindowingType(), WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE);
		ss.setRawSlideValue("5 s");
		assertEquals(ss.getWindowingType(), WindowType.TUPLE_BASED_WIN_TIME_BASED_SLIDE);
		ss.setRawSlideValue("2");
		assertEquals(ss.getWindowingType(), WindowType.TUPLE_BASED);
		ss.setRawSlideValue("");
		assertEquals(ss.getWindowingType(), WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE);
		ss.setRawHistorySize("");
		ss.setRawSlideValue("");
		assertEquals(ss.getWindowingType(), WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE);
	}
	
	@Test
	public void testUID() {
		InputStream is = new InputStream();
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  s").setInputStream(is);
		assertTrue(ss.validate());
		assertNotNull(ss.getUIDStr());
		ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10min");
		assertFalse(ss.validate());
		assertNull(ss.getUIDStr());
	}
	@Test
	public void testRateZeroQueries() throws SQLException{
		InputStream is = new InputStream();
		StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("10  m").setInputStream(is);
		ss.setSamplingRate(0);
		ss.setWrapper(wrapper);
		assertTrue(ss.validate());
		StringBuilder query = ss.toSql();
		assertTrue(query.toString().toLowerCase().indexOf("mod")<0);
		assertTrue(query.toString().toLowerCase().indexOf("false")>0);
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(),new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()/2) );
		DataEnumerator dm = sm.executeQuery(query, true);
		assertFalse(dm.hasMoreElements());
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(),new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		dm = sm.executeQuery(query, true);
		assertFalse(dm.hasMoreElements());
		wrapper.removeListener(ss);
	}

	@Test(expected=GSNRuntimeException.class)
	public void badSamplingRate() {
		StreamSource 	ss = new StreamSource().setAlias("my-stream").setSqlQuery("select * from wrapper").setRawHistorySize("10  s");
		ss.setSamplingRate(-0.1f);
	}
	@Test(expected=GSNRuntimeException.class)
	public void badSamplingRateBadOrder() throws SQLException {
		StreamSource ss = new StreamSource().setAlias("my-stream").setSqlQuery("select * from wrapper").setRawHistorySize("10  s");
		ss.setWrapper(wrapper);
		ss.setSamplingRate(0.2f);
	}
	@Test
	public void testCountWindowSizeZero() throws SQLException {
		InputStream is = new InputStream();
		StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("0 ").setInputStream(is);

		ss.setWrapper(wrapper);
		assertTrue(ss.validate());
		assertTrue(ss.toSql().toString().toLowerCase().indexOf("false")>0);
		wrapper.removeListener(ss);
	}

	@Test(expected=GSNRuntimeException.class)
	public void testNullWrapper() {
		StreamSource ss = new StreamSource().setAlias("my-stream").setSqlQuery("select * from wrapper").setRawHistorySize("10  s");
		ss.toSql();
	}

	@Test(expected=GSNRuntimeException.class)
	public void testInvalidStreamSource() throws SQLException {
		InputStream is = new InputStream();
		StreamSource ss = new StreamSource().setAlias("my-stream").setSqlQuery("select * from wrapper").setRawHistorySize("10  s").setInputStream(is);
		ss.setWrapper(wrapper);
		ss.toSql();
		wrapper.removeListener(ss);
	}

	@Test
	public void testTimeBasedWindow() throws SQLException{
		InputStream is = new InputStream();
	  StreamSource ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("1  s").setInputStream(is);
		ss.setSamplingRate(1);
		ss.setWrapper(wrapper );
		assertTrue(ss.validate());
		StringBuilder query = ss.toSql();
		assertTrue(query.toString().toLowerCase().indexOf("mod")<0);
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()/2) );
		DataEnumerator dm = sm.executeQuery(query, true);
		assertFalse(dm.hasMoreElements());
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(),new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis())) ;
		Connection conn = sm.getConnection();
		ResultSet rs =sm.executeQueryWithResultSet(query, conn);
		assertTrue(rs.next());
		assertFalse(rs.next());
		dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		dm.nextElement();
		assertFalse(dm.hasMoreElements());
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		dm.nextElement();
		assertTrue(dm.hasMoreElements());
		dm.nextElement();
		assertFalse(dm.hasMoreElements());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dm = sm.executeQuery(query, true);
		assertFalse(dm.hasMoreElements());
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		dm.nextElement();
		assertFalse(dm.hasMoreElements());
		wrapper.removeListener(ss);
	}
	@Test
	public void testCountBasedWindowSize1() throws SQLException{
		InputStream is = new InputStream();
		StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("1").setInputStream(is);
		ss.setSamplingRate(1);
		assertTrue(ss.validate());
		ss.setWrapper(wrapper );
		StringBuilder query = ss.toSql();
		assertTrue(query.toString().toLowerCase().indexOf("mod")<0);
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		DataEnumerator dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		dm.nextElement();
		assertFalse(dm.hasMoreElements());
		long timed = System.currentTimeMillis()+100;
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(),ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},timed) );
		dm = sm.executeQuery(query, true);
		assertTrue(dm.hasMoreElements());
		assertEquals(dm.nextElement().getTimeStamp(), timed);
		assertFalse(dm.hasMoreElements());
		wrapper.removeListener(ss);
	}

	@Test
	public void testCountBasedWindowSize2() throws SQLException{
		InputStream is = new InputStream();
		StreamSource 	ss = new StreamSource().setAlias("my-stream").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);		
		ss.setSamplingRate(1);
		ss.setWrapper(wrapper );
		assertTrue(ss.validate());
		StringBuilder query = ss.toSql();
		assertTrue(query.toString().toLowerCase().indexOf("mod")<0);
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(), new StreamElement(new DataField[] {},new Serializable[] {},System.currentTimeMillis()) );
		long time1 = System.currentTimeMillis()+10;
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(),new StreamElement(new DataField[] {},new Serializable[] {},time1) );
		long time2 = System.currentTimeMillis()+100;
		sm.executeInsert(ss.getWrapper().getDBAliasInStr(), ss.getWrapper().getOutputFormat(),new StreamElement(new DataField[] {},new Serializable[] {},time2) );
		DataEnumerator dm = sm.executeQuery(query, true);
		Connection conn = sm.getConnection();
		ResultSet rs =sm.executeQueryWithResultSet(query, conn);
		assertTrue(rs.next());
		assertTrue(rs.next());
		assertFalse(rs.next());
		assertTrue(dm.hasMoreElements());
		assertEquals(dm.nextElement().getTimeStamp(), time2);
		assertTrue(dm.hasMoreElements());
		assertEquals(dm.nextElement().getTimeStamp(), time1);
		assertFalse(dm.hasMoreElements());
		wrapper.removeListener(ss);
		sm.close(rs.getStatement().getConnection());
	}

	/**
	 * This method is only used for testing purposes.
	 * @param query
	 * @throws SQLException 
	 */
	public static void printTable(StringBuilder query) throws SQLException {
		System.out.println("Printing for Query : "+query);
		DataEnumerator dm = sm.executeQuery(query, true); 
		while (dm.hasMoreElements()) {
			StreamElement se = dm.nextElement();
			for (int i=0;i<se.getData().length;i++) {
				System.out.print(se.getFieldNames()[i]+"="+se.getData()[i]+" , ");
			}
			System.out.println("TimeStamp="+se.getTimeStamp());
		}
	}
}
