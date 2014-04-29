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

package org.openiot.gsn.wrappers.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CSVWrapperTest {
	private final String CSV_FILE_NAME =  "test.csv.csv"; 
	private final String CHECK_POINT_DIR = "csv-check-points.csv";

	
	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
//		wrapper.initialize(fields,formats);
	}

	@After
	public void tearDown() throws Exception {
	
	}

	@Test
	public void testIsNull() {
		assertEquals(true,CSVHandler.isNull(new String[] {"NaN","4343"}, null));
		assertEquals(true,CSVHandler.isNull(new String[] {"NaN","4343"}, "nan"));
		assertEquals(true,CSVHandler.isNull(new String[] {"NaN","4343"}, "4343"));
		assertFalse(CSVHandler.isNull(new String[] {"NaN","4343"}, "43434"));
	}
	@Test
	public void testTimeStamp() {
		assertFalse(CSVHandler.isTimeStampFormat("timed"));
		assertFalse(CSVHandler.isTimeStampFormat("timestamp"));
		assertEquals(true,CSVHandler.isTimeStampFormat("timestamp(xyz)"));
		assertEquals("xyz", CSVHandler.getTimeStampFormat("timestamp(xyz)"));
	}
	
	@Test 
	public void testBadFields() {
		assertEquals(true,CSVHandler.validateFormats(new String[] {"numeric"}));
		assertFalse(CSVHandler.validateFormats(new String[] {"doubble"}));
		assertEquals(true,CSVHandler.validateFormats(new String[]  {"Timestamp(d.M.y k:m)"}));
		assertFalse(CSVHandler.validateFormats(new String[] {"Timestamp(d.Mjo0o.y k:m)"}));
		assertEquals(true,CSVHandler.validateFormats(new String[] {"Timestamp(d.M.y k:m)" ,"Numeric", "numeric" , "numeric"}));
	}
	@Test
	public void testFieldConverting() throws IOException {
		String fields = "TIMED, air_temp , TIMED , AiR_TeMp2";
		String formats = "Timestamp(d.M.y ) , Numeric , timestamp(k:m) , numeric    ";
		String badFormat = "Timestamp(d.M.y k:m) , numeric , numeric, numeric,numeric,dollluble ";
		String badFormat2 ="Timestamp(d.Mjo0o.y k:m) , numeric, numeric, numeric";
		
		CSVHandler wrapper = new CSVHandler();
		assertEquals(false,wrapper.initialize("test.csv.csv", fields,badFormat,',','\"',0,"NaN,-1234,4321"));
		assertEquals(false,wrapper.initialize("test.csv.csv", fields,badFormat,',','\"',0,"NaN,-1234,4321"));
		assertEquals(false,wrapper.initialize("test.csv.csv", fields,badFormat2,',','\"',0,"NaN,-1234,4321"));
		
		assertEquals(true,wrapper.initialize("test.csv.csv", fields,formats,',','\"',0,"NaN,-1234,4321"));
		
		FileUtils.writeStringToFile(new File(wrapper.getCheckPointFile()),  "","UTF-8");
		String[] formatsParsed = wrapper.getFormats();
		String[] fieldsParsed =  wrapper.getFields();
		assertEquals(true,compare(fieldsParsed, new String[] {"timed","air_temp","timed","air_temp2"}));
		assertEquals(true,compare(formatsParsed, new String[] {"Timestamp(d.M.y )","Numeric","timestamp(k:m)","numeric"}));
		
		TreeMap<String, Serializable> se = wrapper.convertTo(wrapper.getFormats(),wrapper.getFields(),wrapper.getNulls(),new String[] {} , wrapper.getSeparator());
		assertEquals(wrapper.getFields().length-1, se.keySet().size());//timestamp is douplicated.
		assertEquals(null, se.get("timed"));
		se = wrapper.convertTo(wrapper.getFormats(),wrapper.getFields(),wrapper.getNulls(),new String[] {"","","","-1234","4321","NaN"} , wrapper.getSeparator());
		assertEquals(null, se.get("timed"));
		
		se = wrapper.convertTo(wrapper.getFormats(),wrapper.getFields(),wrapper.getNulls(),new String[] {"","","","-1234","4321","NaN"} , wrapper.getSeparator());
		assertEquals(null, se.get("timed"));
		
		se = wrapper.convertTo(wrapper.getFormats(),wrapper.getFields(),wrapper.getNulls(),new String[] {"01.01.2009","1234","","-4321","ignore-me","NaN"} , wrapper.getSeparator());
		long parsedTimestamp = (Long)se.get("timed");
		assertEquals(true,parsedTimestamp>0);
		assertEquals(1234.0, se.get("air_temp"));
		assertEquals(-4321.0, se.get("air_temp2"));
		
		se = wrapper.convertTo(wrapper.getFormats(),wrapper.getFields(),wrapper.getNulls(),new String[] {"01.01.2009","-1234","10:10","-4321","ignore-me","NaN"} , wrapper.getSeparator());
		assertEquals(true,((Long)se.get("timed"))>parsedTimestamp);
		assertNull(se.get("air_temp"));
	
	}
	@Test
	public void testCheckpoints() throws IOException {
		String fields = "TIMED, air_temp , TIMEd , AiR_TeMp2, comments";
		String formats = "Timestamp(d.M.y ) , Numeric , timestamp(k:m) , numeric ,String   ";
		String data = "01.01.2009,1,10:10,10,\"Ali Salehi\"\n"+
		"01.01.2009,2,10:11,11,\"Ali Salehi\"\n"+
		"01.01.2009,3,10:12,12,\"Ali Salehi\"\n";
		CSVHandler wrapper = new CSVHandler();
		assertEquals(true,wrapper.initialize("test.csv.csv", fields,formats,',','\"',0,"NaN,-1234,4321"));
		ArrayList<TreeMap<String, Serializable>> parsed = wrapper.parseValues(new StringReader(data), -1, 250);
		assertEquals(3, parsed.size());
		assertEquals(wrapper.work(new StringReader(data), CHECK_POINT_DIR, 250).size(), parsed.size());
		assertEquals(true,((Long)parsed.get(0).get("timed"))<((Long)parsed.get(1).get("timed")));
		long recentTimestamp = ((Long)parsed.get(parsed.size()-1).get("timed"));
		data+="01.01.2009,3,10:12,12,\"Ali Salehi\"\n";
		assertEquals(0,wrapper.parseValues(new StringReader(data), recentTimestamp, 250).size());
		assertEquals(0,wrapper.work(new StringReader(data), CHECK_POINT_DIR, 250).size());
		
		data+="01.01.2009,3,10:12,12,\"Ali Salehi\"\n";
		data+="01.01.2009,3,10:11,12,\"Ali Salehi\"\n";
		data+="01.01.2009,3,10:10,12,\"Ali Salehi\"\n";
		assertEquals(0,wrapper.parseValues(new StringReader(data), recentTimestamp, 250).size());
		assertEquals(0,wrapper.work(new StringReader(data), CHECK_POINT_DIR, 250).size());
		data+="01.01.2009,3,10:13,13,\"Ali Salehi\"\n";
		assertEquals(1,wrapper.parseValues(new StringReader(data), recentTimestamp, 250).size());
		assertEquals(1,wrapper.work(new StringReader(data), CHECK_POINT_DIR, 250).size());
		data="###########################\n\n\n\n,,,,,,,,,\n\n\n"; // Empty File.
		wrapper.setSkipFirstXLines(1);
		assertEquals(0,wrapper.parseValues(new StringReader(data), recentTimestamp, 250).size());
		assertEquals(0,wrapper.work(new StringReader(data), CHECK_POINT_DIR, 250).size());
		
	}
	
	@Test
	public void testTimeStampParser() throws IOException {
		DateTime toReturn = CSVHandler.parseTimeStamp("d.M.y k:m","01.10.2008 06:20");
		assertEquals(new DateTime(2008,10,01,6,20,0,0), toReturn);
		assertEquals(0, CSVHandler.generateFieldIdx("", false).length);
	}
	
	@Test
	public void testFileUtils() throws IOException {
		String content = "";
		File f= new File(CHECK_POINT_DIR,CSV_FILE_NAME);
		FileUtils.writeStringToFile(f,content,"UTF-8");
		assertEquals(content,FileUtils.readFileToString(f,"UTF-8"));
		content = Long.toString(1234567600);
		FileUtils.writeStringToFile(f,content,"UTF-8");
		assertEquals(content,FileUtils.readFileToString(f,"UTF-8"));
	}
	
	public boolean compare(String[] a,String[] b) {
		if (a.length!=b.length)
			return false;
		for (int i=0;i<a.length;i++) 
			if (!a[i].equals(b[i]))
				return false;
		return true;
	}
	public boolean compare(HashMap<String, Serializable> a,HashMap<String, Serializable> b) {
		if (a.size()!=b.size())
			return false;
		for (String key : a.keySet())
			if(!a.get(key).equals(b.get(key)))
				return false;
		return true;
	}
}
