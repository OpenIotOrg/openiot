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

package org.openiot.gsn.http.ac;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class UserUtils {

    private static transient Logger logger = Logger.getLogger(UserUtils.class);

    /*
   * Creates a User object following credentials in access control
   * Returns null, if user not registered or password is incorrect
   * */
    public static User allowUserToLogin(String username, String password) {
        User user = null;
        ConnectToDB ctdb = null;

        try {

            ctdb = new ConnectToDB();
            if (ctdb.valueExistsForThisColumnUnderOneCondition(new Column("USERNAME", username), new Column("ISCANDIDATE", "no"), "ACUSER") == true) {
                String enc = Protector.encrypt(password);
                if ((ctdb.isPasswordCorrectForThisUser(username, enc) == false)) {

                    logger.warn("Incorrect password for user : " + username);
                } else {
                    logger.warn("Username and password are correct for user : " + username);
                    user = new User(username, enc, ctdb.getDataSourceListForUserLogin(username), ctdb.getGroupListForUser(username));
                    User userFromBD = ctdb.getUserForUserName(username);
                    user.setLastName(userFromBD.getLastName());
                    user.setEmail(userFromBD.getEmail());
                    user.setFirstName(userFromBD.getFirstName());
                }

            } else {

                if (username.compareToIgnoreCase("null") != 0)
                    logger.warn("This username \"" + username + "\" does not exist !");
            }

        } catch (Exception e) {
            logger.warn("Exception caught : " + e.getMessage());
        } finally {
            if (ctdb != null) {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return user;
    }

    public static boolean userHasAccessToVirtualSensor(String username, String password, String vsname) {
        User user = allowUserToLogin(username, password);
        if (user == null)
            return false;
        else {
            logger.warn("user.isAdmin => " + user.isAdmin());
            logger.warn("user.hasReadAccessRight(" + vsname + ") => " + user.hasReadAccessRight(vsname));
            return (user.hasReadAccessRight(vsname) || user.isAdmin());
        }
    }

    public static boolean userHasAccessToAllVirtualSensorsInList(String reqUsername, String reqPassword, List<String> listOfVirtualSensors) {

        if (listOfVirtualSensors.isEmpty())
            return false;

        Iterator<String> iterator = listOfVirtualSensors.iterator();
        boolean result = true;
        while (iterator.hasNext() && result) {
            result = result && userHasAccessToVirtualSensor(reqUsername, reqPassword, iterator.next());
        }
        return result;
    }

    /*
    * Checks the list of virtual sensors
    * and returns only the ones for which the user has access
    * */
    public static List<String> getAllowedVirtualSensorsForUser(String reqUsername, String reqPassword, List<String> sensors) {
        List<String> allowedSensors = new Vector<String>();

        for (int i = 0; i < sensors.size(); i++) {
            if (userHasAccessToVirtualSensor(reqUsername,reqPassword, sensors.get(i)))
                allowedSensors.add(sensors.get(i));
        }

        return allowedSensors;
    }
}
