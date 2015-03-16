package org.openiot.ui.request.definition.web.model.nodes.impl.comparators;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.openiot.ui.request.commons.annotations.Endpoint;
import org.openiot.ui.request.commons.annotations.Endpoints;
import org.openiot.ui.request.commons.annotations.GraphNodeClass;
import org.openiot.ui.request.commons.annotations.NodeProperties;
import org.openiot.ui.request.commons.annotations.NodeProperty;
import org.openiot.ui.request.commons.nodes.base.DefaultGraphNode;
import org.openiot.ui.request.commons.nodes.enums.AnchorType;
import org.openiot.ui.request.commons.nodes.enums.EndpointType;
import org.openiot.ui.request.commons.nodes.enums.PropertyType;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

@GraphNodeClass(label = "CompareNumber", type = "COMPARATOR", scanProperties = true)
@Endpoints({
    @Endpoint(type = EndpointType.Input, anchorType = AnchorType.Left, scope = "cmp_sensor_value", label = "IN", required = true),  
})
@NodeProperties({
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.String.class, name = "OPERATOR", allowedValues = {"<", "<=", ">", ">="}, required = true),
    @NodeProperty(type = PropertyType.Writable, javaType = java.lang.Number.class, name = "CMP_VALUE", required = true)
})

public class CompareNumber extends DefaultGraphNode implements Serializable, Observer{

	public CompareNumber() {
		super();

        // Listen for property change events
        addPropertyChangeObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Map<String, Object> propertyMap = getPropertyValueMap();
        if (propertyMap.get("OPERATOR") != null && propertyMap.get("CMP_VALUE") != null) {
        	
        	Number value = (Number) propertyMap.get("CMP_VALUE");        	
            setLabel(propertyMap.get("OPERATOR") + "<br/>" + value);
        } else {
            setLabel("Compare Numbers");
        }
	}

}
