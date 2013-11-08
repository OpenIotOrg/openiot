
jQuery.fn.flashNodeBorder = function()
{
    var el = $(this);
    var color = '255,0,0';
    var duration = 800;
    var current = el.css( 'border-color' );
    el.css('border', '2px solid ' + current);
    el.animate( {
        'border-color': 'rgb(' + color + ')'
    }, duration / 2 );
    el.animate( 
    {
        'border-color': current
    }, 
    {
        duration : duration / 2
    }
    );
};

jQuery.fn.flashBorder = function()
{
    var el = $(this);
    var color = '255,0,0';
    var duration = 800;
    var current = el.css( 'border-color' );
    el.css('border', '2px solid ' + current);
    el.animate( {
        'border-color': 'rgb(' + color + ')'
    }, duration / 2 );
    el.animate( 
    {
        'border-color': current
    }, 
    {
        duration : duration / 2, 
        complete : function(){
            el.css('border', '0px solid ' + current);
        }
    }
    );
};

Sensap = {
    /**
     * Creates a widget and load the required resources if not already available.
     * The .js and .css must has the same name as the widget and must be placed inside a directory with also the name.
     * The file and directory names must be completely in lower case.
     * For example: /imageareaselect/imageareaselect.js.
     * 
     * @author Thomas Andraschko
     * @param {string} widgetName The name of the widget. For example: ImageAreaSelect.
     * @param {widgetVar} widgetVar The variable in the window object for accessing the widget.
     * @param {object} cfg An object with options.
     * @param {boolean} hasStyleSheet If the css file should be loaded as well.
     */
    cw : function(widgetName, widgetVar, cfg, hasStyleSheet) {
        Sensap.createWidget(widgetName, widgetVar, cfg, hasStyleSheet);
    },

    /**
     * Creates a widget and load the required resources if not already available.
     * The .js and .css must has the same name as the widget and must be placed inside a directory with also the name.
     * The file and directory names must be completely in lower case.
     * For example: /imageareaselect/imageareaselect.js.
     * 
     * @author Thomas Andraschko
     * @param {string} widgetName The name of the widget. For example: ImageAreaSelect.
     * @param {widgetVar} widgetVar The variable in the window object for accessing the widget.
     * @param {object} cfg An object with options.
     * @param {boolean} hasStyleSheet If the css file should be loaded as well.
     */
    createWidget : function(widgetName, widgetVar, cfg, hasStyleSheet) {            
        if (Sensap.widget[widgetName]) {
            Sensap.initWidget(widgetName, widgetVar, cfg);
        }
    },

    /**
     * Creates the widget or calls "refresh" if already available.
     * 
     * @param {string} widgetName The name of the widget. For example: ImageAreaSelect.
     * @param {string} widgetVar The variable in the window object for accessing the widget.
     * @param {object} cfg An object with options.
     */
    initWidget : function(widgetName, widgetVar, cfg) {
        if (window[widgetVar]) {
            window[widgetVar].refresh(cfg);
        } else {
            window[widgetVar] = new Sensap.widget[widgetName](cfg);
        }
    }
};

/**
 * @namespace Namespace for widgets.
 */
Sensap.widget = {};


/**
 * Node Graph
 */
Sensap.widget.NodeGraph = PrimeFaces.widget.BaseWidget.extend({

    /**
	 * Initializes the widget.
	 * 
	 * @param {object}
	 *            cfg The widget configuration.
	 */
    init : function(cfg) {
        this._super(cfg);
        this.cfg = cfg;
        this.id = cfg.id;
        this.endPoints = {};

        // Init jsPlumb defaults
        jsPlumb.importDefaults({
            DragOptions : {
                cursor: 'pointer', 
                zIndex:2000
            },
            PaintStyle : {
                strokeStyle : '#346789', 
                lineWidth:3
            },
            ConnectionOverlays : [
            ['PlainArrow', {
                location:1.0
            } ]
            ]
        });
        
        // initialize Selection
        this.graphNodeSelector = this.jqId + ' .graph-node';
        this.selectedNodeId = this.cfg.selectedNodeId || null;

        this.unbindConnectionEvents();

        // Render graph
        this.renderGraph();
        
        // Bind behaviors        
        this.bindSelectEvents();
        this.bindMoveEvents();
        this.bindDropEvents();
        this.bindConnectionEvents();
    },
    
    /** 
     * Render graph form encoded data 
     */
    renderGraph : function(){
        var self = this;
        
        // Cleanup
        jsPlumb.removeAllEndpoints($(this.jqId + ' .graph-node'));
        
        // Add all endpoints
        self.endPoints = {};
        jQuery.each( this.cfg.endpoints, function( index, endPointSpec){
        	 var scopes = endPointSpec.scope.split(/\s+/);
             
             endPointSpec.dragOptions = {
                 start : function(){
                     var list = $(self.jqId +' .ui-droppable._jsPlumb_endpoint').filter(function(){
                    	 
                    	 var el = $(this);
                    	
                    	 //
                         var elScopes = el.data('scope');
                         if( !elScopes ){
                        	 return true;
                         }
                         // wildcard scope
                         if(elScopes.indexOf('*') != -1 ){
                        	 return false; // allow connection
                         }
                         elScopes = elScopes.split(/\s+/);
                         for(var i=0;i<scopes.length;i++){
                        	 
                        	 for( var j=0;j<elScopes.length;j++){
	                             if( elScopes[j] == scopes[i]){
	                                 return false; // allow connection
	                             }
                        	 }
                         }
                         return true; // prevent connection
                     });
                     list.droppable('disable');
                 },
                 stop : function(){
                     $(self.jqId +' .ui-droppable._jsPlumb_endpoint').droppable('enable');
                 }
             };
             
             
            var origScope = endPointSpec.scope;
            endPointSpec.scope = 'endpoint';
            var ep = jsPlumb.addEndpoint( endPointSpec.nodeId, endPointSpec);
            ep.setParameter('nodeId', endPointSpec.nodeId);
            ep.setParameter('endPointId', endPointSpec.id);            
            $(ep.canvas).data('scope', origScope);
            self.endPoints[endPointSpec.id] = ep;
        });
        
        // Add all connections
        jQuery.each( this.cfg.connections, function( index, connectionSpec){
            if( typeof connectionSpec === 'undefined' || 
        	typeof connectionSpec.source === 'undefined' || 
        	typeof connectionSpec.target === 'undefined'||
        	typeof self.endPoints[connectionSpec.source] === 'undefined' ||
        	typeof self.endPoints[connectionSpec.target] === 'undefined' ){
        	return true;
    	    }
            
            jsPlumb.connect({
                source : self.endPoints[connectionSpec.source],
                target : self.endPoints[connectionSpec.target],
                parameters : connectionSpec.parameters
            });
        });
    },

    bindSelectEvents: function () {
        var self = this;
        $(document).off('selectstart.graphNode', this.jqid).on('selectstart.graphNode', this.jqId, null, function(){
            return false;
        });
        
        $(document).off('click.graphNode', this.grapNodeSelector).on('click.graphNode', this.graphNodeSelector, null, function(e){
            self.onNodeClicked(this);
            
            e.preventDefault();
            e.stopPropagation();
        });
        
        $(document).off('click.graphWrapper', this.jqId).on('click.graphWrapper', this.jqId, null, function(e){
            self.onClearSelection(this);
        });
    },
    
    bindMoveEvents: function () {
        var self = this;
                
        // Make all items draggable
        jsPlumb.draggable($(this.jqId + ' .graph-node'), {
            start : function(){
                // Find all labels attached to this node and add the drag class
                var srcNodeId = $(this).attr('id');
                jQuery.each(self.endPoints, function(index, ep){
                    if( ep.getParameter('nodeId') == srcNodeId ){
                        $(ep.canvas).closest('._jsPlumb_endpoint').addClass('ui-draggable-dragging');
                        jQuery.each(ep.getOverlays(), function(index, overlay){
                            $(overlay.getElement()).addClass('ui-draggable-dragging');
                        });
                    } 
                });
            },
            stop : function(){
                // Find all labels attached to this node and add the drag class
                var srcNodeId = $(this).attr('id');
                jQuery.each(self.endPoints, function(index, ep){
                    if( ep.getParameter('nodeId') == srcNodeId ){
                        $(ep.canvas).closest('._jsPlumb_endpoint').removeClass('ui-draggable-dragging');
                        jQuery.each(ep.getOverlays(), function(index, overlay){
                            $(overlay.getElement()).removeClass('ui-draggable-dragging');
                        });
                    } 
                });

                self.onNodeDragged(this);
            }
        });
    },
    
    bindDropEvents: function () {
        var self = this;
        
        // Setup ourselves as a drop target
        $(this.jqId).droppable({
            scope: 'new-node-to-graph',            
            drop : function(event, ui){
                var position = ui.position;
                
                // Adjust position to be relative to our container
                var containerOffset = $(self.jqId).offset();
                position.top -= containerOffset.top;
                position.left -= containerOffset.left;
                
                var type = $(ui.draggable).attr('class').split('type_')[1].split(' ')[0];
                self.onNodeInserted(type, position);
            }
        });        
                
        // Make all items draggable
        $('.insertable-node').draggable( {
            helper:  function () { 
                //  use a helper-clone that is append to 'body' so is not 'contained' by a pane
                return jQuery(this).clone().addClass('insertable-node-drag-handle').appendTo('body');
            },
            scope: 'new-node-to-graph',
            cursor : 'move'
        });
    },
    
    bindConnectionEvents: function () {
        var self = this;
                
        jsPlumb.unbind('jsPlumbConnection').bind('jsPlumbConnection', function(connectionData){            
            self.onConnectionEstablished(connectionData);
        });
        
        jsPlumb.unbind('jsPlumbConnectionDetached').bind('jsPlumbConnectionDetached', function(connectionData){            
            self.onConnectionDetached(connectionData);
        });
    },
    
    unbindConnectionEvents: function () {
        jsPlumb.unbind('jsPlumbConnection');        
        jsPlumb.unbind('jsPlumbConnectionDetached');
    },
    
    onNodeInserted : function(type, position){
        if(this.getBehavior("create") ){
            var params =[];
            params.push({
                name : this.id + '_type',
                value : type
            },{
                name : this.id + '_x',
                value : position.left
            },{
                name : this.id + '_y',
                value : position.top
            });
            this.getBehavior("create").call(this, null, {
                params: params
            });
        }
    },
    
    onConnectionEstablished : function(connectionData) {
        // Generate connectionId
        connectionData.connection.setParameter('connectionId', 'graphNodeConnection_' + Date.now());
        if(this.getBehavior("connect") ){
            var params =[];
            params.push({
                name : this.id + '_connectionId',
                value : connectionData.connection.getParameter('connectionId')               
            },{
                name : this.id + '_sourceNodeId',
                value : connectionData.sourceId                
            },{
                name : this.id + '_sourceEndpointId',
                value : connectionData.sourceEndpoint.getParameter('endPointId')
            },{
                name : this.id + '_destinationNodeId',
                value : connectionData.targetId                
            },{
                name : this.id + '_destinationEndpointId',
                value : connectionData.targetEndpoint.getParameter('endPointId')
            });
            this.getBehavior("connect").call(this, null, {
                params: params
            });
        }
    },
    
    onConnectionDetached : function(connectionData) {
        if(this.getBehavior("disconnect") ){
            var params =[];
            params.push({
                name : this.id + '_connectionId',
                value : connectionData.connection.getParameter('connectionId')                
            });
            this.getBehavior("disconnect").call(this, null, {
                params: params
            });
        }
    },
    
    onNodeClicked : function(graphNode) {
        // Check if we clicked an already-selected node
        graphNode = $(graphNode);
        if( graphNode.attr('id') != this.selectedNodeId ){
            $(this.graphNodeSelector + '.graph-node-selected').removeClass('graph-node-selected');
            graphNode.addClass('graph-node-selected');
            this.selectedNodeId = graphNode.attr('id');
            
            if(this.getBehavior("select") ){
                var params =[];
                params.push({
                    name : this.id + '_sourceNodeId',
                    value : this.selectedNodeId
                });
                this.getBehavior("select").call(this, null, {
                    params: params
                });
            }
        }
    },
    
    onClearSelection : function() {
        // Check if we clicked an already-selected node
        if( this.selectedNodeId ){
            $(this.graphNodeSelector + '.graph-node-selected').removeClass('graph-node-selected');
            this.selectedNodeId = null;
            
            if(this.getBehavior("deselect") ){
                var params =[];
                this.getBehavior("deselect").call(this, null, {
                    params: params
                });
            }
        }
    },
    onNodeDragged : function(graphNode) {
        graphNode = $(graphNode);
        if(this.getBehavior("move")){
            var newPos = graphNode.position();
            
            var params =[];
            params.push({
                name : this.id + '_sourceNodeId',
                value : graphNode.attr('id')
            },{
                name : this.id + '_x',
                value : newPos.left
            },{
                name : this.id + '_y',
                value : newPos.top
            });
            this.getBehavior("move").call(this, null, {
                params: params
            });
        }
    },

    /**
     * Delete the selected node
     */
    onSelectedNodeDeleted : function(){
        // Check if we clicked an already-selected node
        if( this.selectedNodeId ){
            
            if(this.getBehavior("delete") ){
                try{
                	jsPlumb.removeAllEndpoints($(this.jqId + ' #' + this.selectedNodeId));
            	}catch(ex){}

                var params =[];
                params.push({
                    name : this.id + '_sourceNodeId',
                    value : this.selectedNodeId
                });
                this.getBehavior("delete").call(this, null, {
                    params: params
                });
                this.selectedeNodeId = null;
            }
        }
    },

    /**
     * Gets behavior callback by name or null.
     * 
     * @param name behavior name
     * @return {Function}
     */
    getBehavior: function (name) {
        return this.cfg.behaviors ? this.cfg.behaviors[name] : null;
    },
    
    /**
     * Flash the appropriate element depending on the passed Id
     */
    flash : function( elementList ){
        var self = this;
        jQuery.each( elementList.split(','), function(index, elementId){
            elementId = elementId.trim();
            if( elementId.indexOf('graphNode_') != -1 ){
                $('#' + elementId).flashNodeBorder();
            } else if( elementId.indexOf('graphNodeEndpoint_') != -1 ){
                jQuery.each(self.endPoints, function(index, ep){
                    if( ep.getParameter('endPointId') == elementId ){
                        $(ep.canvas).flashBorder();
                    }
                });
            }
        });
    }    
});
    
