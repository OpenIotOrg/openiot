/** 
 * This plugin overrides jQuery's height() and width() functions and
 * adds more handy stuff for cross-browser compatibility.
 */

/**
 * Returns the css height value for the first matched element.
 * If used on document, returns the document's height (innerHeight)
 * If used on window, returns the viewport's (window) height
 *
 * @example $("#testdiv").height()
 * @result "200px"
 *
 * @example $(document).height();
 * @result 800
 *
 * @example $(window).height();
 * @result 400
 * 
 * @name height
 * @type Object
 * @cat Plugins/Dimensions
 */
jQuery.fn.height = function() {
	if ( this.get(0) == window )
		return self.innerHeight ||
			jQuery.boxModel && document.documentElement.clientHeight ||
			document.body.clientHeight;
	
	if ( this.get(0) == document )
		return Math.max( document.body.scrollHeight, document.body.offsetHeight );
	
	return this.css("height", arguments[0]);
};

/**
 * Returns the css width value for the first matched element.
 * If used on document, returns the document's width (innerWidth)
 * If used on window, returns the viewport's (window) width
 *
 * @example $("#testdiv").width()
 * @result "200px"
 *
 * @example $(document).width();
 * @result 800
 *
 * @example $(window).width();
 * @result 400
 * 
 * @name width
 * @type Object
 * @cat Plugins/Dimensions
 */
jQuery.fn.width = function() {
	if ( this.get(0) == window )
		return self.innerWidth ||
			jQuery.boxModel && document.documentElement.clientWidth ||
			document.body.clientWidth;
	
	if ( this.get(0) == document )
		return Math.max( document.body.scrollWidth, document.body.offsetWidth );
	
	return this.css("width", arguments[0]);
};

/**
 * Returns the inner height value (without border) for the first matched element.
 * If used on document, returns the document's height (innerHeight)
 * If used on window, returns the viewport's (window) height
 *
 * @example $("#testdiv").innerHeight()
 * @result 800
 * 
 * @name innerHeight
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.innerHeight = function() {
	return this.get(0) == window || this.get(0) == document ?
		this.height() :
		this.get(0).offsetHeight - parseInt(this.css("borderTop") || 0) - parseInt(this.css("borderBottom") || 0);
};

/**
 * Returns the inner width value (without border) for the first matched element.
 * If used on document, returns the document's Width (innerWidth)
 * If used on window, returns the viewport's (window) width
 *
 * @example $("#testdiv").innerWidth()
 * @result 1000
 * 
 * @name innerWidth
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.innerWidth = function() {
	return this.get(0) == window || this.get(0) == document ?
		this.width() :
		this.get(0).offsetWidth - parseInt(this.css("borderLeft") || 0) - parseInt(this.css("borderRight") || 0);
};

/**
 * Returns the outer height value (including border) for the first matched element.
 * Cannot be used on document or window.
 *
 * @example $("#testdiv").outerHeight()
 * @result 1000
 * 
 * @name outerHeight
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.outerHeight = function() {
	return this.get(0) == window || this.get(0) == document ?
		this.height() :
		this.get(0).offsetHeight;	
};

/**
 * Returns the outer width value (including border) for the first matched element.
 * Cannot be used on document or window.
 *
 * @example $("#testdiv").outerWidth()
 * @result 1000
 * 
 * @name outerWidth
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.outerWidth = function() {
	return this.get(0) == window || this.get(0) == document ?
		this.width() :
		this.get(0).offsetWidth;	
};

/**
 * Returns how many pixels the user has scrolled to the right (scrollLeft).
 * Works on containers with overflow: auto and window/document.
 *
 * @example $("#testdiv").scrollLeft()
 * @result 100
 * 
 * @name scrollLeft
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.scrollLeft = function() {
	if ( this.get(0) == window || this.get(0) == document )
		return self.pageXOffset ||
			jQuery.boxModel && document.documentElement.scrollLeft ||
			document.body.scrollLeft;
	
	return this.get(0).scrollLeft;
};

/**
 * Returns how many pixels the user has scrolled to the bottom (scrollTop).
 * Works on containers with overflow: auto and window/document.
 *
 * @example $("#testdiv").scrollTop()
 * @result 100
 * 
 * @name scrollTop
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.scrollTop = function() {
	if ( this.get(0) == window || this.get(0) == document )
		return self.pageYOffset ||
			jQuery.boxModel && document.documentElement.scrollTop ||
			document.body.scrollTop;

	return this.get(0).scrollTop;
};

/**
 * This returns an object with top, left, width, height, borderLeft,
 * borderTop, marginLeft, marginTop, scrollLeft, scrollTop, 
 * pageXOffset, pageYOffset.
 *
 * The top and left values include the scroll offsets but the
 * scrollLeft and scrollTop properties of the returned object
 * are the combined scroll offets of the parent elements 
 * (not including the window scroll offsets). This is not the
 * same as the element's scrollTop and scrollLeft.
 * 
 * For accurate readings make sure to use pixel values.
 *
 * @name offset	
 * @type Object
 * @cat Plugins/Dimensions
 * @author Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 */
/**
 * This returns an object with top, left, width, height, borderLeft,
 * borderTop, marginLeft, marginTop, scrollLeft, scrollTop, 
 * pageXOffset, pageYOffset.
 *
 * The top and left values include the scroll offsets but the
 * scrollLeft and scrollTop properties of the returned object
 * are the combined scroll offets of the parent elements 
 * (not including the window scroll offsets). This is not the
 * same as the element's scrollTop and scrollLeft.
 * 
 * For accurate readings make sure to use pixel values.
 *
 * @name offset	
 * @type Object
 * @param String refElement This is an expression. The offset returned will be relative to the first matched element.
 * @cat Plugins/Dimensions
 * @author Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 */
/**
 * This returns an object with top, left, width, height, borderLeft,
 * borderTop, marginLeft, marginTop, scrollLeft, scrollTop, 
 * pageXOffset, pageYOffset.
 *
 * The top and left values include the scroll offsets but the
 * scrollLeft and scrollTop properties of the returned object
 * are the combined scroll offets of the parent elements 
 * (not including the window scroll offsets). This is not the
 * same as the element's scrollTop and scrollLeft.
 * 
 * For accurate readings make sure to use pixel values.
 *
 * @name offset	
 * @type Object
 * @param jQuery refElement The offset returned will be relative to the first matched element.
 * @cat Plugins/Dimensions
 * @author Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 */
/**
 * This returns an object with top, left, width, height, borderLeft,
 * borderTop, marginLeft, marginTop, scrollLeft, scrollTop, 
 * pageXOffset, pageYOffset.
 *
 * The top and left values include the scroll offsets but the
 * scrollLeft and scrollTop properties of the returned object
 * are the combined scroll offets of the parent elements 
 * (not including the window scroll offsets). This is not the
 * same as the element's scrollTop and scrollLeft.
 * 
 * For accurate readings make sure to use pixel values.
 *
 * @name offset	
 * @type Object
 * @param HTMLElement refElement The offset returned will be relative to this element.
 * @cat Plugins/Dimensions
 * @author Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 */
jQuery.fn.offset = function(refElem) {
	if (!this[0]) throw 'jQuery.fn.offset requires an element.';

	refElem = (refElem) ? jQuery(refElem)[0] : null;
	var x = 0, y = 0, elem = this[0], parent = this[0], sl = 0, st = 0;
	do {
		if (parent.tagName == 'BODY' || parent.tagName == 'HTML') {
			// Safari and IE don't add margin for static and relative
			if ((jQuery.browser.safari || jQuery.browser.msie) && jQuery.css(parent, 'position') != 'absolute') {
				x += parseInt(jQuery.css(parent, 'marginLeft')) || 0;
				y += parseInt(jQuery.css(parent, 'marginTop'))  || 0;
			}
			break;
		}

		x += parent.offsetLeft || 0;
		y += parent.offsetTop  || 0;

		// Mozilla and IE do not add the border
		if (jQuery.browser.mozilla || jQuery.browser.msie) {
			x += parseInt(jQuery.css(parent, 'borderLeftWidth')) || 0;
			y += parseInt(jQuery.css(parent, 'borderTopWidth'))  || 0;
		}

		// Need to get scroll offsets in-between offsetParents
		var op = parent.offsetParent;
		do {
			sl += parent.scrollLeft || 0;
			st += parent.scrollTop  || 0;
			parent = parent.parentNode;
		} while (parent != op);
	} while (parent);

	if (refElem) { // Get the relative offset
		var offset = jQuery(refElem).offset();
		x  = x  - offset.left;
		y  = y  - offset.top;
		sl = sl - offset.scrollLeft;
		st = st - offset.scrollTop;
	}

	// Safari and Opera do not add the border for the element
	if (jQuery.browser.safari || jQuery.browser.opera) {
		x += parseInt(jQuery.css(elem, 'borderLeftWidth')) || 0;
		y += parseInt(jQuery.css(elem, 'borderTopWidth'))  || 0;
	}

	return {
		top:  y - st,
		left: x - sl,
		width:  elem.offsetWidth,
		height: elem.offsetHeight,
		borderTop:  parseInt(jQuery.css(elem, 'borderTopWidth'))  || 0,
		borderLeft: parseInt(jQuery.css(elem, 'borderLeftWidth')) || 0,
		marginTop:  parseInt(jQuery.css(elem, 'marginTopWidth'))  || 0,
		marginLeft: parseInt(jQuery.css(elem, 'marginLeftWidth')) || 0,
		scrollTop:  st,
		scrollLeft: sl,
		pageYOffset: window.pageYOffset || document.documentElement.scrollTop  || document.body.scrollTop  || 0,
		pageXOffset: window.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft || 0
	};
};