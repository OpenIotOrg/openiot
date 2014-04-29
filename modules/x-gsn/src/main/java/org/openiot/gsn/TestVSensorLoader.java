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

package org.openiot.gsn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.InputStream;
import org.openiot.gsn.beans.StreamSource;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.storage.StorageManager;
import org.openiot.gsn.storage.StorageManagerFactory;
import org.openiot.gsn.wrappers.MockWrapper;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestVSensorLoader {

    private static StorageManager sm = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DriverManager.registerDriver( new org.h2.Driver( ) );
		sm = StorageManagerFactory.getInstance( "org.hsqldb.jdbcDriver","sa","" ,"jdbc:hsqldb:mem:.", Main.DEFAULT_MAX_DB_CONNECTIONS);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

  private AddressBean[] addressing= new AddressBean[] {new AddressBean("mock-test")};

	@Before
	public void setUp() throws Exception {
	  Properties p = new Properties();
	  p.put("mock-test", "org.openiot.gsn.wrappers.MockWrapper");
	  p.put("system-time", "org.openiot.gsn.wrappers.SystemTime");
	  Main.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testCreateInputStreams() {

	}

	@Test
	public void testPrepareWrapper() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		VSensorLoader loader = new VSensorLoader();
		MockWrapper wrapper = (MockWrapper) loader.findWrapper(addressing[0]);
		assertNotNull(wrapper);
	}

	@Test
	public void testPrepareStreamSource() {

	}

	@Test
	public void testStopLoading() throws IOException {
		VSensorConfig  sensorConfig = new VSensorConfig();
		sensorConfig.setName("test");
		File someFile = File.createTempFile("bla", ".xml");
		sensorConfig.setMainClass("org.openiot.gsn.vsensor.BridgeVirtualSensor");
		sensorConfig.setFileName(someFile.getAbsolutePath());
		VirtualSensor pool = new VirtualSensor(sensorConfig);
		InputStream is = new InputStream();
		is.setInputStreamName("t1");
		is.setQuery("select * from my-stream1");
		StreamSource 	ss1 = new StreamSource().setAlias("my-stream1").setAddressing(new AddressBean[] {new AddressBean("mock-test")}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);		
		ss1.setSamplingRate(1);
		assertTrue(ss1.validate());
		is.setSources(ss1);
		assertTrue(is.validate());
		sensorConfig.setInputStreams(is);
		assertTrue(sensorConfig.validate());
		
	}
	@Test
	public void testOneInputStreamUsingTwoStreamSources() throws InstantiationException, IllegalAccessException, SQLException {
		VSensorLoader loader = new VSensorLoader();
		InputStream is = new InputStream();
		StreamSource 	ss1 = new StreamSource().setAlias("my-stream1").setAddressing(new AddressBean[] {new AddressBean("mock-test")}).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);		
		ss1.setSamplingRate(1);
		assertTrue(ss1.validate());
//		assertTrue(loader.prepareStreamSource(is,ss1));
		StreamSource 	ss2 = new StreamSource().setAlias("my-stream2").setAddressing(new AddressBean[] {new AddressBean("mock-test")}).setSqlQuery("select * from wrapper").setRawHistorySize("20").setInputStream(is);		
		ss2.setSamplingRate(1);
		assertTrue(ss2.validate());
//		assertTrue(loader.prepareStreamSource(is,ss2));
		ss1.getWrapper().releaseResources();
		assertFalse(sm.tableExists(ss1.getWrapper().getDBAliasInStr()));
	}
	
	@Test
	public void testReloadingVirtualSensor() throws InstantiationException, IllegalAccessException, SQLException {
		VSensorLoader loader = new VSensorLoader();
		InputStream is = new InputStream();
		StreamSource 	ss = new StreamSource().setAlias("my-stream1").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);		
		ss.setSamplingRate(1);
		assertTrue(ss.validate());
//		assertTrue(loader.prepareStreamSource(is,ss));
		assertTrue(sm.tableExists(ss.getWrapper().getDBAliasInStr()));
		assertTrue(sm.tableExists(ss.getUIDStr()));
		assertFalse(is.getRenamingMapping().isEmpty());
		loader.releaseStreamSource(ss);
		assertTrue(is.getRenamingMapping().isEmpty());
		assertFalse(sm.tableExists(ss.getUIDStr()));
		assertFalse(ss.getWrapper().isActive());
		assertFalse(sm.tableExists(ss.getWrapper().getDBAliasInStr()));
		assertTrue(is.getRenamingMapping().isEmpty());
		ss = new StreamSource().setAlias("my-stream1").setAddressing(addressing).setSqlQuery("select * from wrapper").setRawHistorySize("2").setInputStream(is);		
		ss.setSamplingRate(1);
//		assertTrue(loader.prepareStreamSource(is,ss));
		
	}
}
