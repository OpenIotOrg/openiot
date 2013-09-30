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

package org.openiot.ui.request.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class StringUtils {

    public static String nullIfEmpty(String in) {
        if (in == null) {
            return null;
        }
        in = in.trim();
        if (in.isEmpty()) {
            in = null;
        }
        return in;
    }

    public static String nullIfEmpty(Object in) {
        if (in == null) {
            return null;
        }
        return nullIfEmpty(in.toString());
    }

    public static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    public static String ucFirst(String in) {
        if (in.isEmpty()) {
            return in;
        }
        return in.substring(0, 1).toUpperCase() + in.substring(1);
    }

    public static String lcFirst(String in) {
        if (in.isEmpty()) {
            return in;
        }
        return in.substring(0, 1).toLowerCase() + in.substring(1);
    }

    public static List<String> splitString(String input, String delimiter) {
        List<String> out = new ArrayList<String>();
        if (input == null || nullIfEmpty(input) == null) {
            return out;
        }

        String[] fieldNames = input.trim().split(delimiter);
        if (fieldNames != null && fieldNames.length > 0) {
            for (String fieldName : fieldNames) {
                String trimmed = fieldName.trim();
                if (StringUtils.nullIfEmpty(trimmed) == null) {
                    continue;
                }
                out.add(trimmed);
            }
        }

        return out;
    }
}
