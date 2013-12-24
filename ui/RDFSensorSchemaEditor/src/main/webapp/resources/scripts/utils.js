function showWorkingIndicator(data) {
	showIndicatorRegion(data, "workingIndicator");
}

function showIndicatorRegion(data, regionId) {
	if (data.status == "begin") {
		showElement(regionId);
	} else if (data.status == "success") {
		hideElement(regionId);
	}
}

function showElement(id) {
	document.getElementById(id).style.display
	= "inline";
}

function hideElement(id) {
	document.getElementById(id).style.display
	= "none";
}