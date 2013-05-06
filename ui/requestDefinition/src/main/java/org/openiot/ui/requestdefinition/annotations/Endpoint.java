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
package org.openiot.ui.requestdefinition.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openiot.ui.requestdefinition.nodes.enums.AnchorType;
import org.openiot.ui.requestdefinition.nodes.enums.ConnectorType;
import org.openiot.ui.requestdefinition.nodes.enums.EndpointType;

/**
 *
 * @author aana
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {

    /**
     * The type of endpoint
     */
    EndpointType type();

    /**
     * Endpoint anchorType point
     */
    AnchorType anchorType() default AnchorType.Right;
    
    /**
     * Connector type
     */
    ConnectorType connectorType() default ConnectorType.Rectangle;

    /**
     * Max number of connections
     */
    int maxConnections() default 1;

    /**
     * The endpoint label
     */
    String label();

    /**
     * The type of objects that can be connected to this endpoint
     */
    String scope();

    /**
     * Flag whether this field has to be connected
     */
    boolean required();
}
