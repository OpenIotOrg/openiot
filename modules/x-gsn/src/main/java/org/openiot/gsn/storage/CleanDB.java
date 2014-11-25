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
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.storage;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.ContainerConfig;

import java.util.ArrayList;

import org.openiot.gsn.beans.StorageConfig;
import org.apache.log4j.PropertyConfigurator;
/**
 * Removes the temporary tables, tables starting with underscore.
 */
public class CleanDB {

  public static void main(String[] args) throws Exception {
    ContainerConfig cc =Main.getContainerConfig();
    StorageConfig sc = cc.getSliding() != null ? cc.getSliding().getStorage() : cc.getStorage() ;
    Class.forName(sc.getJdbcDriver());
    StorageManager sm = StorageManagerFactory.getInstance(sc.getJdbcDriver ( ) , sc.getJdbcUsername ( ) , sc.getJdbcPassword ( ) , sc.getJdbcURL ( ), Main.DEFAULT_MAX_DB_CONNECTIONS);
    ArrayList<String> tables = sm.getInternalTables();
    for (String t : tables)
      sm.executeDropTable(t);
    tables = sm.getInternalTables();
    for (String t : tables)
      sm.executeDropView(new StringBuilder(t));

  }
}
