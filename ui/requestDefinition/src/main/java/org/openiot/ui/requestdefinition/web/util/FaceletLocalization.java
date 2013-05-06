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
package org.openiot.ui.requestdefinition.web.util;

import java.text.MessageFormat;
import java.util.*;
import javax.faces.context.FacesContext;
import org.openiot.ui.requestdefinition.logging.LoggerService;

/**
 *
 * @author aana
 */
public class FaceletLocalization {

    private static String format(final String messageTemplate, final Object[] params) {
        try {
            return new MessageFormat(messageTemplate).format(params);
        } catch (Throwable ex) {
            LoggerService.log(ex);
            return "[LOCALIZATION EXCEPTION]";
        }
    }

    /**
     * Formats a localised message from the given resource bundle
     *
     * @param bundle The resource bundle to use
     * @param messageKey the key to the formatted message
     * @param params replacement parameters for placeholders
     * @return localised formatted message, or the messageKey
     */
    public static String getLocalisedMessage(final ResourceBundle bundle, final String messageKey, final Object... params) {

        try {
            String messageTemplate = bundle.getString(messageKey);
            return format(messageTemplate, params);
        } catch (final MissingResourceException e) {
            return messageKey;
        }
    }

    public static String getLocalisedMessageWithFallback(final ResourceBundle bundle, final String messageKey, final String fallback) {
        if (bundle.containsKey(messageKey)) {
            return getLocalisedMessage(bundle, messageKey);
        }
        return fallback;
    }

    public static String getLocalisedMessage0(final ResourceBundle bundle, final String messageKey) {
        return getLocalisedMessage(bundle, messageKey);
    }

    public static String getLocalisedMessage1(final ResourceBundle bundle, final String messageKey, final Object param1) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1});
    }

    public static String getLocalisedMessage2(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2});
    }

    public static String getLocalisedMessage3(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2, final Object param3) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2, param3});
    }

    public static String getLocalisedMessage4(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2, final Object param3, final Object param4) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2, param3, param4});
    }

    public static String getLocalisedMessage5(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2, final Object param3, final Object param4, final Object param5) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2, param3, param4, param5});
    }

    public static String getLocalisedMessage6(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2, final Object param3, final Object param4, final Object param5, final Object param6) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2, param3, param4, param5, param6});
    }

    public static String getLocalisedMessage7(final ResourceBundle bundle, final String messageKey, final Object param1, final Object param2, final Object param3, final Object param4, final Object param5, final Object param6, final Object param7) {
        return getLocalisedMessage(bundle, messageKey, new Object[]{param1, param2, param3, param4, param5, param6, param7});
    }

    // Single value formatter
    public static String getFormattedValue(final String pattern, final Object value) {
        return format("{0," + pattern + "}", new Object[]{value});
    }

    public static ResourceBundle getLocalizedResourceBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getViewRoot() != null && context.getViewRoot().getLocale() != null) {
            return ResourceBundle.getBundle("org.openiot.ui.requestdefinition.web.i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        }
        return null;
    }

    public static String lookupLabelTranslation(ResourceBundle commonMessages, String fallbackTranslation, String... translationKeys) {
        for (String translationKey : translationKeys) {
            if (commonMessages.containsKey(translationKey)) {
                return commonMessages.getString(translationKey);
            }
        }
        return fallbackTranslation;
    }

    public static String getLabelTranslation1(ResourceBundle commonMessages, String fallbackTranslation, String labelKey0) {
        
        if (commonMessages.containsKey(labelKey0)) {
            return commonMessages.getString(labelKey0);
        }
        
        return fallbackTranslation;
    }
    public static String getLabelTranslation2(ResourceBundle commonMessages, String fallbackTranslation, String labelKey0, String labelKey1) {
        
        if (commonMessages.containsKey(labelKey0)) {
            return commonMessages.getString(labelKey0);
        }else if (commonMessages.containsKey(labelKey1)) {
            return commonMessages.getString(labelKey1);
        }
        
        return fallbackTranslation;
    }

}
