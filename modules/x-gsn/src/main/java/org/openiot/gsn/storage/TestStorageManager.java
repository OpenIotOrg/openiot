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
 * @author Sofiane Sarni
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.storage;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStorageManager {
  
  @Test public void testOracleRewrites() {

    StorageManager db = StorageManagerFactory.getInstance("oracle.jdbc.driver.OracleDriver", "sa", "", "jdbc:oracle:thin:.", 100);
    final String sampleQuery1 = "SELECT * FROM SYSTEM.HELP";
    String oracle = db.addLimit( sampleQuery1, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10", oracle.replace("  ", " "));
    
    final String sampleQuery2 = "SELECT * FROM SYSTEM.HELP ORDER BY TIMED";
    oracle = db.addLimit( sampleQuery2, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10 ORDER BY TIMED", oracle.replace("  ", " "));
   
    final String sampleQuery3 = "SELECT * FROM SYSTEM.HELP GROUP BY TIMED";
    oracle = db.addLimit( sampleQuery3, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10 GROUP BY TIMED", oracle.replace("  ", " "));
    
    final String sampleQuery4 = "SELECT * FROM SYSTEM.HELP WHERE TIMED <0 GROUP BY TIMED";
    oracle = db.addLimit( sampleQuery4, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10 AND (TIMED <0) GROUP BY TIMED", oracle.replace("  ", " "));
    
    final String sampleQuery5 = "SELECT * FROM SYSTEM.HELP WHERE TIMED <0 AND TIMED < 10 OR TIMED >30 GROUP BY TIMED";
    oracle = db.addLimit( sampleQuery5, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10 AND (TIMED <0 AND TIMED < 10 OR TIMED >30) GROUP BY TIMED", oracle.replace("  ", " "));
    
    final String sampleQuery6 = "SELECT * FROM SYSTEM.HELP WHERE TIMED <0 AND TIMED < 10 OR TIMED >30";
    oracle = db.addLimit( sampleQuery6, 10, 0);
    assertEquals("SELECT * FROM SYSTEM.HELP WHERE ROWNUM <= 10 AND (TIMED <0 AND TIMED < 10 OR TIMED >30)", oracle.replace("  ", " "));
    
  }
}
