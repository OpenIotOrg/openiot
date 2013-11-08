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

package org.openiot.ui.request.commons.logging;

import java.util.logging.Level;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class LoggerService {

    private static String logPrefix = "OpenIoT: [";
    private static boolean showStackTraceForExceptions = false;
    private static final java.util.logging.Logger sysLog = java.util.logging.Logger.getLogger(LoggerService.class.getName());

    public static void log(String detailMessage, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (ex == null) {
            sb.append("UNKNOWN] ");
        } else {
            StackTraceElement[] trace = ex.getStackTrace();
            if (trace == null || trace.length == 0) {
                sb.append("UNKNOWN] ");
            } else {
                sb.append(trace[0].getFileName()).append(":").append(trace[0].getLineNumber()).append("] ");
            }
        }
        sb.append(detailMessage);
        log(Level.SEVERE, sb.toString());

        if (showStackTraceForExceptions) {
            ex.printStackTrace();
        }
    }

    public static void log(Throwable ex) {
        log(ex.getLocalizedMessage(), ex);
        if (showStackTraceForExceptions) {
            ex.printStackTrace();
        }
    }

    static void log(Throwable ex, boolean trace) {
        log(ex.getLocalizedMessage(), ex);
        ex.printStackTrace();
    }

    public static void log(Exception ex) {
        log(ex.getLocalizedMessage(), ex);
        if (showStackTraceForExceptions) {
            ex.printStackTrace();
        }
    }

    public static void log(Level level, String message) {
        sysLog.log(level, logPrefix + level + "] {0}", message);
    }

    public static void setLevel(Level newLevel) {
        sysLog.setLevel(newLevel);
        log(Level.INFO, "Setting log level to '" + newLevel + "'");
    }

    public static void setShowStackTraceForExceptions(boolean showStackTraceForExceptions) {
        LoggerService.showStackTraceForExceptions = showStackTraceForExceptions;
        if (showStackTraceForExceptions) {
            log(Level.INFO, "Stacktrace dump for exceptions enabled");
        }
    }

    public static void setApplicationName(String logPrefix) {
        LoggerService.logPrefix = logPrefix + ": [";
    }
}
