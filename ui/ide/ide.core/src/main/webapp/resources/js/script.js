/**
 * Created by Chris on 3/4/2014.
 */

//$(document).ready(function() {
//
//	var links = $(".ui-menuitem-link");
//
//	links.each(function() {
//		var value = links.params[0].value;
//	});
//});

//var checkIfAlive = function(url) {
//	$.ajax({
//			url: url,
//			type: 'HEAD',
//			error: function() {
//				url.hide();
//			},
//			success: function() {
//				url.show();
//			}
//		});
//};

var hasLoaded = false;

function start() {
	hasLoaded = false;
	setTimeout("showDialog();", 700);
	//statusDialog.show();
}

function hideDialog() {
	hasLoaded = true;

	if (statusDialog != undefined)
		statusDialog.hide();
}

function showDialog() {
	//alert("has loaded = " + hasLoaded);
	if (!hasLoaded)
		statusDialog.show();
}
