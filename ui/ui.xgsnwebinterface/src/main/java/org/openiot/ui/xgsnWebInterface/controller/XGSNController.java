package org.openiot.ui.xgsnWebInterface.controller;

import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Copyright (c) 2011-2014, OpenIoT
 * <p/>
 * This file is part of OpenIoT.
 * <p/>
 * OpenIoT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * OpenIoT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact: OpenIoT mailto: info@openiot.eu
 * @author Zachary Williamson
 */

@ManagedBean(name = "xgsn")
@ViewScoped()
public class XGSNController {

    private final EventBus eventBus = EventBusFactory.getDefault().eventBus();

    // starts XGSN by running the existing bash script
    public void startXGSN() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", ". ./gsn-start.sh; echo $!; wait");
        pb.directory(new File("/home/openiot/openiot/modules/x-gsn/"));
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // stream the bash process' output over a PrimeFaces Push EventBus
        publishXGSNLogs(p.getInputStream());
    }

    // stops XGSN by running the existing bash script
    public void stopXGSN() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", ". ./gsn-stop.sh; echo $!; wait");
        pb.directory(new File("/home/openiot/openiot/modules/x-gsn/"));
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // stream the bash process' output to the PrimeFaces Push EventBus
        publishXGSNLogs(p.getInputStream());
    }

    // publish the XGSN scripts output to the PrimeFaces Push EventBus
    // this is done in a separate thread to allow the logs to stream in th background
    private void publishXGSNLogs(final InputStream is) {
        new Thread(new Runnable() {
            public void run() {
                Scanner logs = new Scanner(is);

                while (logs.hasNextLine()) {
                    eventBus.publish("/logs", logs.nextLine());
                }
            }
        }).start();
    }
}
