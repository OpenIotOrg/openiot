/*******************************************************************************
 * Copyright (c) 2011-2014, OpenIoT
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it either under the terms of the GNU Lesser General Public
 *  License version 2.1 as published by the Free Software Foundation
 *  (the "LGPL"). If you do not alter this
 *  notice, a recipient may use your version of this file under the LGPL.
 *  
 *  You should have received a copy of the LGPL along with this library
 *  in the file COPYING-LGPL-2.1; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 *  This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 *  OF ANY KIND, either express or implied. See the LGPL  for
 *  the specific language governing rights and limitations.
 *  
 *  Contact: OpenIoT mailto: info@openiot.eu
 ******************************************************************************/
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
