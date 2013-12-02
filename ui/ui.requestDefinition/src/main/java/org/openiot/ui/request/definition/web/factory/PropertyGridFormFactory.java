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

package org.openiot.ui.request.definition.web.factory;

import java.util.ResourceBundle;

import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;
import org.openiot.ui.request.definition.web.model.EditablePropertyField;
import org.openiot.ui.request.definition.web.util.FaceletLocalization;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
public class PropertyGridFormFactory {

    public static DynaFormModel generatePropertyGridDynaForm(GraphNode node) {
        DynaFormModel model = new DynaFormModel();

        ResourceBundle mesages = FaceletLocalization.getLocalizedResourceBundle();

        for (GraphNodeProperty property : node.getPropertyDefinitions()) {
            DynaFormRow modelRow = model.createRegularRow();
            DynaFormLabel label = modelRow.addLabel(FaceletLocalization.lookupLabelTranslation(mesages, property.getName(), "UI_NODE_PROPERTY_" + node.getClass().getSimpleName() + "_" + property.getName(), "UI_NODE_PROPERTY_" + property.getName()), false, 1, 1);
            String controlType = property.getAllowedValues() != null ? "StringList" : property.getJavaType().getSimpleName();

            DynaFormControl control = modelRow.addControl(new EditablePropertyField(node.getType(), property), controlType, 1, 1);
            label.setForControl(control);
        }

        return model;
    }
}
