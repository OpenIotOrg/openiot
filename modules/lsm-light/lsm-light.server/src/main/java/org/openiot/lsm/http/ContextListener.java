package org.openiot.lsm.http;
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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openiot.lsm.pooling.ConnectionManager;
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public final class ContextListener implements ServletContextListener {
 
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionManager.init();
    }
 
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.shutdownConnPool();
    } 
}
