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

package org.openiot.gsn.beans;

import java.text.ParseException;

import org.apache.log4j.Logger;

public class VSensorMonitorConfig {

    private String SEPARATOR = "@";

    protected String name;
    protected String host;
    protected int port;
    protected long timeout;
    protected String path;
    protected boolean needspassword;
    protected String username;
    protected String password;

    private transient final Logger logger = Logger.getLogger(VSensorMonitorConfig.class);

    public VSensorMonitorConfig(String name, String host, int port, long timeout, String path, boolean needspassword, String username, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.path = path;
        this.needspassword = needspassword;
        this.username = username;
        this.password = password;
    }

    public VSensorMonitorConfig() {
        this.password = "";
        this.name = "";
        this.host = "";
        this.port = 0;
        this.timeout = 0;
        this.path = "";
        this.needspassword = false;
        this.username = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean needsPassword() {
        return needspassword;
    }

    public void setNeedsPassword(boolean needspassword) {
        this.needspassword = needspassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimeoutAsString() {

        //System.out.println("getTimeoutAsString: "+ timeout +"=>" + ms2dhms(timeout));
        return ms2dhms(timeout);
    }

    public int hashCode() {
        if (name != null) {
            return name.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public String toString() {
        if (this.needspassword)
            return name + SEPARATOR + getTimeoutAsString() + SEPARATOR + "http://" + host + ":" + port + path + SEPARATOR + username + ":" + password;
        else
            return name + SEPARATOR + getTimeoutAsString() + SEPARATOR + "http://" + host + ":" + port + path;
    }

    public boolean equals(Object obj) {
        if (obj instanceof VSensorMonitorConfig) {
            VSensorMonitorConfig vSensorMonitorConfig = (VSensorMonitorConfig) obj;
            return name.equals(vSensorMonitorConfig.getName());
        }
        return false;
    }

    public static long timeOutFromString(String s) {
        return dhms2ms(s);
    }

    /* converts timeout given as a long value (in msecs)
    *  into a string of the form #d#h#m#s# (days, hours, minutes, seconds, msecs) 
    */
    public static String ms2dhms(long l) {
        StringBuilder sb = new StringBuilder();
        long d, h, m, s, ms;

        ms = l % 1000L;
        l = l / 1000L;
        s = l % 60L;
        l = l / 60L;
        m = l % 60L;
        l = l / 60L;
        h = l % 24L;
        d = l / 24L;
        if (d > 0L)
            sb.append(d).append("d ");
        if (h > 0L)
            sb.append(h).append("h ");
        if (m > 0L)
            sb.append(m).append("m ");
        if (s > 0L)
            sb.append(s).append("s ");
        if (ms > 0L)
            sb.append(ms);
        return sb.toString();
    }

    /* converts timeout given as a string
    *  with (days, hours, minutes, seconds, msecs)
    * to msecs
    */
    public static long dhms2ms(String str) {
        int index;
        long d = 0L;
        long h = 0L;
        long m = 0L;
        long s = 0L;
        long ms = 0L;
        StringBuilder sb = new StringBuilder(str.toLowerCase());

        // days
        index = sb.indexOf("d");
        if (index > 0) {
            d = Long.parseLong(sb.substring(0, index));
            sb.delete(0, index + 1); // removes ####d
        }

        // hours
        index = sb.indexOf("h");
        if (index > 0) {
            h = Long.parseLong(sb.substring(0, index));
            sb.delete(0, index + 1); // removes ####h
        }

        // minutes
        index = sb.indexOf("m");
        if (index > 0) {
            m = Long.parseLong(sb.substring(0, index));
            sb.delete(0, index + 1); // removes ####m
        }

        // seconds
        index = sb.indexOf("s");
        if (index > 0) {
            s = Long.parseLong(sb.substring(0, index));
            sb.delete(0, index + 1); // removes ####s
        }

        //millisecs
        if (sb.length() > 0) {
            ms = Long.parseLong(sb.toString());
        }

        return (d * 86400L + h * 3600L + m * 60L + s) * 1000L + ms;
    }

    /*
   * converts a date and times in the format 06/02/2008 23:50:00 +0100
   * into a Unix timestamp
   * */
    public static long datetime2timestamp(String s) throws ParseException {

        return  new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(s).getTime();

        /*
        Pattern datePattern = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})");
        Matcher dateMatcher = datePattern.matcher(s);
        if (dateMatcher.find()) {
            System.out.println("Month is: " + dateMatcher.group(2));
            System.out.println("Day is:   " + dateMatcher.group(1));
            System.out.println("Year is:  " + dateMatcher.group(3));

            System.out.println("Hour is:  " + dateMatcher.group(4));
            System.out.println("Minute is:  " + dateMatcher.group(5));
            System.out.println("Second is:  " + dateMatcher.group(6));
        */
    }



}


