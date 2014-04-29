// Patch growl so that it doesnt remove existing messages when growl.show()
// is invoked with no messages
PrimeFaces.widget.Growl = PrimeFaces.widget.Growl.extend({
	show : function(msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}

		var _self = this;

		this.jq.css('z-index', ++PrimeFaces.zindex);

		// clear previous messages
		this.removeAll();

		$.each(msgs, function(index, msg) {
			_self.renderMessage(msg);
		});
	},
	bindEvents : function(message) {
		var _self = this, sticky = this.cfg.sticky;

		message.mouseover(function() {
			var msg = $(this);

			// visuals
			if (!msg.is(':animated')) {
				msg.find('div.ui-growl-icon-close:first').show();
			}
		}).mouseout(function() {
			// visuals
			$(this).find('div.ui-growl-icon-close:first').hide();
		});

		// remove message on click of the container
		message.find('div.ui-growl-item').click(function() {
			_self.removeMessage(message);

			// clear timeout if removed manually
			if (!sticky) {
				clearTimeout(message.data('timeout'));
			}
		});

		// hide the message after given time if not sticky
		if (!sticky) {
			this.setRemovalTimeout(message);
		}
	}
});

function lineChartExtender() {
	this.cfg.axes = {
		xaxis : {
			renderer : $.jqplot.CategoryAxisRenderer,
			rendererOptions : {
				tickRenderer : $.jqplot.CanvasAxisTickRenderer
			},
			labelOptions : {
				fontFamily : 'Verdana',
				fontSize : '8pt'
			},
			labelRenderer : $.jqplot.CanvasTextRenderer,
			tickOptions : {
				fontSize : '8pt',
				fontFamily : 'Tahoma',
				angle : 90
			},
			numberTicks : 15
		}
	};
	this.cfg.axes.xaxis.ticks = this.cfg.categories;
}

// Update each widget using an ajax call
function refreshAllWidgets() {
	$('.widget').each(function() {
		var widget = $(this);
		// Widget is currently updating... ignore
		if (widget.hasClass('updating')) {
			return true;
		}

		var cls = $(this).attr('class');
		var serviceId = cls.substr(cls.indexOf('service_') + 8);
		widget.addClass('updating');
		updateWidget([ {
			name : 'serviceId',
			value : serviceId
		} ]);
	});
}

function refreshClickedWidget() {
	var widget = $(this).closest('.widget');
	if (widget.length === 0) {
		return;
	}

	var widget = $(this);
	// Widget is currently updating... ignore
	if (widget.hasClass('updating')) {
		return true;
	}

	var cls = $(this).attr('class');
	var serviceId = cls.substr(cls.indexOf('service_') + 8);
	widget.addClass('updating');
	updateWidget([ {
		name : 'serviceId',
		value : serviceId
	} ]);
}

function clearMapOverlays(widget) {
	if (typeof widget.cfg.circles !== 'undefined') {
		for ( var ind = 0; ind < widget.cfg.circles.length; ind++) {
			var marker = widget.cfg.circles[ind];
			if (marker != null) {
				marker.setMap(null);
			}
		}
		widget.cfg.circles = [];
	}

	if (typeof widget.cfg.markers !== 'undefined') {
		for ( var ind = 0; ind < widget.cfg.markers.length; ind++) {
			var marker = widget.cfg.markers[ind];
			if (marker != null) {
				marker.setMap(null);
			}
		}
		widget.cfg.markers = [];
	}
}
function addMapMarker(widget, id, lat, lng) {
	widget.cfg.markers = widget.cfg.markers || [];
	var marker = new google.maps.Marker({
		id : id,
		position : new google.maps.LatLng(lat, lng),
		map : widget.map
	});
	widget.cfg.markers.push(marker);
}
function addMapCircle(widget, id, lat, lng, radius, strokeColor, strokeOpacity, fillColor, fillOpacity) {
	widget.cfg.circles = widget.cfg.circles || [];
	var marker = new google.maps.Circle({
		id : id,
		center : new google.maps.LatLng(lat, lng),
		radius : radius,
		strokeColor : strokeColor,
		strokeOpacity : strokeOpacity,
		strokeWeight : 1,
		fillColor : fillColor,
		fillOpacity : fillOpacity,
		map : widget.map
	});
	widget.cfg.circles.push(marker);
}
function addMapEventListeners(widget){
	if(typeof widget.cfg.markers == 'undefined'){
		return;
	}
	widget.configureMarkers();
}