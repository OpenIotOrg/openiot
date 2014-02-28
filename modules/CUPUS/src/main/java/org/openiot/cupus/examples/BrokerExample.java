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

package org.openiot.cupus.examples;

import java.io.File;
import java.io.IOException;

import org.openiot.cupus.entity.broker.CloudBroker;

/**
 *
 * @author Aco
 */
public class BrokerExample {
  public static void main(String[] args) throws InterruptedException, IOException {
       
    	CloudBroker broker = new CloudBroker(new File("./config/test_broker.config"), "./bin;../CUPUSCommon/bin");
    	
    }
}
