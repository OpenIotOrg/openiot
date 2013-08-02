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
