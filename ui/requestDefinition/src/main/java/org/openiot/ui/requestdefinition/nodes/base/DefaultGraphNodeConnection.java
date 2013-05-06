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
package org.openiot.ui.requestdefinition.nodes.base;

import java.io.Serializable;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNode;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.requestdefinition.nodes.interfaces.GraphNodeEndpoint;

/**
 *
 * @author aana
 */
public class DefaultGraphNodeConnection implements GraphNodeConnection, Serializable {
	private static final long serialVersionUID = 1L;

    private String UID = "graphNodeConnection_" + System.nanoTime();
    private GraphNode sourceNode;
    private GraphNode destinationNode;
    private GraphNodeEndpoint sourceEndpoint;
    private GraphNodeEndpoint destinationEndpoint;

    public DefaultGraphNodeConnection() {
    }

    public DefaultGraphNodeConnection(GraphNode sourceNode, GraphNodeEndpoint sourceEndpoint, GraphNode destinationNode, GraphNodeEndpoint destinationEndpoint) {
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.sourceEndpoint = sourceEndpoint;
        this.destinationEndpoint = destinationEndpoint;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public GraphNode getSourceNode() {
        return sourceNode;
    }

    public GraphNodeEndpoint getSourceEndpoint() {
        return sourceEndpoint;
    }

    public GraphNode getDestinationNode() {
        return destinationNode;
    }

    public GraphNodeEndpoint getDestinationEndpoint() {
        return destinationEndpoint;
    }

    public void setSourceNode(GraphNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setDestinationNode(GraphNode destinationNode) {
        this.destinationNode = destinationNode;
    }

    public void setSourceEndpoint(GraphNodeEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    public void setDestinationEndpoint(GraphNodeEndpoint destinationEndpoint) {
        this.destinationEndpoint = destinationEndpoint;
    }
}
