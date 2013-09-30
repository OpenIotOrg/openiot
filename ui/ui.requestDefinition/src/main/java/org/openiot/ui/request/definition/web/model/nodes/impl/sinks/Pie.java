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

package org.openiot.ui.request.definition.web.model.nodes.impl.sinks;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.models.ObservableMap;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNodeProperty;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.ConnectorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

/**
 *
 * @author Achilleas Anagnostopoulos (aanag) email: aanag@sensap.eu
 */
@GraphNodeClass(label = "Pie", type = "SINK", scanProperties = true)
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "TITLE", required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "SERIES", required = true, allowedValues = {"1", "2","3", "4", "5", "6", "7", "8", "9", "10"}),
})
public class Pie extends DefaultGraphNode implements Serializable, Observer {
	private static final long serialVersionUID = 1L;
    
    private GraphModel model;

    public Pie() {
        super();

        // Setup some defaults
        setProperty("TITLE", Pie.class.getSimpleName());
        setProperty("SERIES", "1");
        
        addPropertyChangeObserver(this);
        validateSeries();
    }
    
    public void validateSeries(){
    	int seriesCount = Integer.valueOf((String)getPropertyValueMap().get("SERIES"));
    	int i = 0;
    	for( ; i<seriesCount;i++){
    		// If we are missing the required endpoints and properties create them now
    		String epLabel = "y" + (i+1);
    		GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
    		if( ep == null ){
    			ep = new DefaultGraphNodeEndpoint();
    			ep.setType(EndpointType.Input);
    			ep.setAnchor(AnchorType.Left);
    			ep.setConnectorType(ConnectorType.Rectangle);
    			ep.setScope("agr_Number agr_Integer agr_Long, agr_Float agr_Double");
    			ep.setLabel(epLabel);
    			ep.setRequired(true);
    			getEndpointDefinitions().add(ep);
    			
    	        GraphNodeProperty prop = new DefaultGraphNodeProperty();
    	        String propKey = "SERIES_" + i + "_LABEL";
    	        prop.setType(PropertyType.Writable);    	        
    	        prop.setName(propKey);
    	        prop.setJavaType(java.lang.String.class);
    	        prop.setRequired(true);
    	        getPropertyDefinitions().add(prop);    		
    	        
    	        ((ObservableMap<String, Object>)getPropertyValueMap()).getWrappedMap().put(propKey, "Series " + (i+1));
    		}
    	}
    	
    	// If we reduced the number of series, get rid of the old series
    	int maxSeries = Integer.valueOf((String)getPropertyByName("SERIES").getAllowedValues()[ getPropertyByName("SERIES").getAllowedValues().length - 1 ]);
    	for( ; i< maxSeries; i++){
    		String epLabel = "y" + (i+1);
    		GraphNodeEndpoint ep = getEndpointByLabel(epLabel);
    		if( ep != null ){
                // If we have a connection to this node, kill it
                if( model != null ){
                    List<GraphNodeConnection> connections = model.findGraphEndpointConnections(ep);
                    if (!connections.isEmpty()) {
                        GraphNodeConnection connection = connections.get(0);
                        model.disconnect(connection);
                    }
                }
                
                getEndpointDefinitions().remove(ep);
                
                String propKey = "SERIES_" + i + "_LABEL";
                GraphNodeProperty prop = getPropertyByName(propKey);
                if( prop != null ){
                	getPropertyDefinitions().remove(prop);
                	((ObservableMap<String, Object>)getPropertyValueMap()).getWrappedMap().remove(propKey);
                }
    		}
    	}
    }
    
    public void update(Observable o, Object arg) {
        validateSeries();
    }
}
