/*
 *
 * Copyright (c) 2006 Sam Collett (http://www.texotela.co.uk)
 * Licensed under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 * 
 */

 
/*
 * jQuery ToolTip Demo. Demo of how to add elements and get mouse coordinates
 *	There is also a ToolTip plugin found at http://interface.eyecon.ro/,
 *	  which uses a CSS class to style the tooltip, but shows it below the input/anchor, rather than where the mouse is
 *
 *
 * @name     ToolTipDemo
 * @param    bgcolour  Background colour
 * @param    fgcolour  Foreground colour (i.e. text colour)
 * @author   Sam Collett (http://www.texotela.co.uk)
 * @example  $("a,input").ToolTipDemo('#fff');
 *
 */
$.fn.ToolTip = function()
{
	this.mouseover(
		function(e)
		{
			if((!this.title && !this.alt) && !this.tooltipset) return;
			// get mouse coordinates
			// based on code from http://www.quirksmode.org/js/events_properties.html
			var mouseX = e.pageX || (e.clientX ? e.clientX + document.body.scrollLeft : 0);
			var mouseY = e.pageY || (e.clientY ? e.clientY + document.body.scrollTop : 0);
			mouseX += 10;
			mouseY += 10;
			// if there is no div containing the tooltip
			if(!this.tooltipdiv)
			{
				// create a div and style it
				var div = document.createElement("div");
				this.tooltipdiv = div;
				$(div).css({position: "absolute"})
				.addClass("tooltip")
				// add the title/alt attribute to it
				.html((this.title || this.alt));
				this.title = "";
				this.alt = "";
				$("body").append(div);
				this.tooltipset = true;
			}
			$(this.tooltipdiv).css({left: mouseX + "px", top: mouseY + 3 + "px"}).show();
		}
	).mouseout(
		function()
		{
			if(this.tooltipdiv)
			{
				$(this.tooltipdiv).hide();
			}
		}
	);
	return this;
}