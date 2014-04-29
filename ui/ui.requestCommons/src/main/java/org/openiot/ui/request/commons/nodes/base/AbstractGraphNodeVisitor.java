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

package org.openiot.ui.request.commons.nodes.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;

/**
 *
 * @author archie
 */
public abstract class AbstractGraphNodeVisitor {

    public abstract void defaultVisit(GraphNode node);

    public void visitViaReflection(GraphNode node) {
        try {
            Method downPolymorphic = this.getClass().getMethod("visit", new Class[]{ node.getClass()});

            if (downPolymorphic == null) {
                defaultVisit(node);
            } else {
                downPolymorphic.invoke(this, new Object[]{node});
            }
        } catch (NoSuchMethodException e) {
            defaultVisit(node);
        } catch (InvocationTargetException e) {
        	e.printStackTrace();        	
            defaultVisit(node);
        } catch (IllegalAccessException e) {
            defaultVisit(node);
        }
    }
}
