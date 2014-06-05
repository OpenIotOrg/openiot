/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.cupus.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.openiot.cupus.entity.broker.Coordinator;

/**
 *
 * @author Aleksandar
 */
public class StartBroker {

    public static void main(String[] args) throws IOException {

        String configFile = null;
        String classpath = null;
        try {
            configFile = args[0];
            classpath = args[1];
        } catch (Exception e) {
            System.out.println("ERROR! Couldn't start broker.");
            System.out.println("\tTwo command-line arguments needed - path to the config file and classpath!");
            System.exit(-1);
        }

        Coordinator broker = new Coordinator(new File(configFile), classpath);
        broker.start();

        System.out.println("For exit type QUIT");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String input = in.readLine();
            if (input.equalsIgnoreCase("QUIT")) {
                broker.shutdown();
                break;
            };
        }

    }
}
