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
package org.openiot.ui.requestdefinition.web.factory;

import java.util.ResourceBundle;
import org.openiot.ui.requestdefinition.nodes.enums.PropertyType;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.requestdefinition.web.model.EditablePropertyField;
import org.openiot.ui.requestdefinition.web.util.FaceletLocalization;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;

/**
 *
 * @author aana
 */
public class PropertyGridFormFactory {

    public static DynaFormModel generatePropertyGridDynaForm(GraphNode node) {
        DynaFormModel model = new DynaFormModel();

        ResourceBundle mesages = FaceletLocalization.getLocalizedResourceBundle();

        for (GraphNodeProperty property : node.getPropertyDefinitions()) {
            DynaFormRow modelRow = model.createRegularRow();
            DynaFormLabel label = modelRow.addLabel(FaceletLocalization.lookupLabelTranslation(mesages, property.getName(), "UI_NODE_PROPERTY_" + node.getClass().getSimpleName() + "_" + property.getName(), "UI_NODE_PROPERTY_" + property.getName()), false, 1, 1);

            String controlType = property.getAllowedValues() != null ? "StringList" : property.getJavaType().getSimpleName();
            DynaFormControl control = modelRow.addControl(new EditablePropertyField(property.getName(), property.isRequired(), property.getType().equals(PropertyType.Writable), property.getAllowedValues()), controlType, 1, 1);
            label.setForControl(control);
        }

        return model;
    }
}
