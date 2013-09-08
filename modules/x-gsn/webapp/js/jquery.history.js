/*
 * jQuery history plugin
 *
 * Copyright (c) 2006 Taku Sano (Mikage Sawatari)
 * Licensed under the MIT License:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 */


jQuery.extend({
	historyCurrentHash: undefined,
	historyCallback: undefined,
	historyInit: function(callback){
		jQuery.historyCallback = callback;
		var current_hash = location.hash;
		jQuery.historyCurrentHash = current_hash;
		if(jQuery.browser.msie) {
			// add hidden iframe for IE
			$("body").prepend('<iframe id="jQuery_history" style="display: none;"></iframe>');
			var history = $("#jQuery_history")[0];
			var iframe = history.contentWindow.document;
			iframe.open();
			iframe.close();
			iframe.location.hash = current_hash;
		}
		jQuery.historyCallback(current_hash.replace(/^#/, ''));
		setInterval(jQuery.historyCheck, 100);
	},
	historyCheck: function(){
		if(jQuery.browser.msie) {
			// On IE, check for location.hash of iframe
			var history = $("#jQuery_history")[0];
			var iframe = history.contentDocument || history.contentWindow.document;
			var current_hash = iframe.location.hash;
			if(current_hash != jQuery.historyCurrentHash) {
				location.hash = current_hash;
				jQuery.historyCurrentHash = current_hash;
				jQuery.historyCallback(current_hash.replace(/^#/, ''));
			}
		} else {
			// otherwise, check for location.hash
			var current_hash = location.hash;
			if(current_hash != jQuery.historyCurrentHash) {
				jQuery.historyCurrentHash = current_hash;
				jQuery.historyCallback(current_hash.replace(/^#/, ''));
			}
		}
	},
	historyLoad: function(hash){
		var newhash = '#' + hash;
		location.hash = newhash;
		jQuery.historyCurrentHash = newhash;
		if(jQuery.browser.msie) {
			var history = $("#jQuery_history")[0];
			var iframe = history.contentWindow.document;
			iframe.open();
			iframe.close();
			iframe.location.hash = newhash;
		}
		jQuery.historyCallback(hash);
	}
});


