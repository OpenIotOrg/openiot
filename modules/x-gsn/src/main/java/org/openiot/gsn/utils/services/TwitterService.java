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

package org.openiot.gsn.utils.services;

import org.apache.log4j.Logger;
import winterwell.jtwitter.Twitter;

import java.util.HashMap;

/**
 * This class provides an access to the Tweeter Notification service.
 * The implementation is based on the JTwitter - the Java library for the Twitter API
 * http://www.winterwell.com/software/jtwitter.php
 * TODO Cache the Twitter wrapper instances
 */
public class TwitterService {

    private static final transient Logger logger = Logger.getLogger(TwitterService.class);

    private static HashMap<String, Twitter> twitters = new HashMap<String, Twitter>();

        /**
     * Update the user public status on tweeter
     * @param username
     * @param password
     * @param message The new status.
     */
    public static void updateTwitterStatus(String username, String password, String message) {
        new Twitter(username, password).updateStatus(message);
    }

    /**
     * Send a private message to the recipient tweeter user.
     * @param username
     * @param password
     * @param message
     * @param recipient
     */
    public static void sendTwitterMessage(String username, String password, String message, String recipient) {
        new Twitter(username, password).sendMessage(recipient, message);
    }

}
