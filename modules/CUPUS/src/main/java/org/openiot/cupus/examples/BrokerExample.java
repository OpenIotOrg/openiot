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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openiot.cupus.entity.broker.Coordinator;

/**
 *
 * @author Aco
 */
public class BrokerExample {
  public static void main(String[] args) throws InterruptedException, IOException {
       
    	Coordinator broker = new Coordinator(new File("./src/main/resources/config/test_broker.config"), "./target/CUPUS-1.0-jar-with-dependencies.jar");
        
        System.out.println("For exit type QUIT");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            String input = in.readLine();
            if (input.equalsIgnoreCase("QUIT")) {
            	broker.shutdown();
                break;
            };
        }
    }
}
