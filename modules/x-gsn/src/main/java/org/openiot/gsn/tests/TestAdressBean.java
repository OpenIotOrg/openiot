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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.utils.KeyValueImp;

import org.apache.commons.collections.KeyValue;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestAdressBean {
	private AddressBean addressBean1;
	private AddressBean addressBean2;
	
	@BeforeClass 
	public static void init(){
		
	}

	@Test
	public void testHashCode() {
		KeyValue[] predicates1 = new KeyValue[2];
		KeyValue[] predicates2 = new KeyValue[2];
		predicates1[0] = new KeyValueImp("key1", "val1");
		predicates2[0] = new KeyValueImp("key1", "val1");
		
		predicates1[1] = new KeyValueImp("key2", "val2");
		predicates2[1] = new KeyValueImp("key2", "val2");
		addressBean1 = new AddressBean("wrapper", predicates1);
		addressBean2 = new AddressBean("wrapper", predicates2);
		assertEquals(addressBean1.hashCode(), addressBean2.hashCode());
		predicates1[0] = new KeyValueImp("val1", "key1");
		assertTrue(addressBean1.hashCode() != addressBean2.hashCode());
		addressBean1 = new AddressBean("wrapper", new KeyValueImp("key1", "key2"));
		assertTrue(addressBean1.hashCode() != addressBean2.hashCode());		
	}

	@Test
	public void testEqualsObject() {
		KeyValue[] predicates1 = new KeyValue[2];
		KeyValue[] predicates2 = new KeyValue[2];
		predicates1[0] = new KeyValueImp("key1", "val1");
		predicates2[0] = new KeyValueImp("key1", "val1");
		
		predicates1[1] = new KeyValueImp("key2", "val2");
		predicates2[1] = new KeyValueImp("key2", "val2");
		addressBean1 = new AddressBean("wrapper", predicates1);
		addressBean2 = new AddressBean("wrapper", predicates2);
		assertEquals(addressBean1, addressBean2);
		predicates1[0] = new KeyValueImp("val1", "key1");
		assertFalse(addressBean1.equals(addressBean2));
		addressBean1 = new AddressBean("wrapper", new KeyValueImp("key1", "key2"));
		assertFalse(addressBean1.equals(addressBean2));	
	}

}
