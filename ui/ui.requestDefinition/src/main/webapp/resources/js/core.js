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
