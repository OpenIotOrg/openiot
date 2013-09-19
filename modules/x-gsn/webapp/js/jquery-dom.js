// DOM element creator for jQuery and Prototype by Michael Geary
// http://mg.to/topics/programming/javascript/jquery
// Inspired by MochiKit.DOM by Bob Ippolito
// Free beer and free speech. Enjoy!

$.defineTag = function( tag ) {
	$[tag.toUpperCase()] = function() {
		return $._createNode( tag, arguments );
	}
};

(function() {
	var tags = [
		'a', 'br', 'button', 'canvas', 'div', 'fieldset', 'form',
		'h1', 'h2', 'h3', 'hr', 'img', 'input', 'label', 'legend',
		'li', 'ol', 'optgroup', 'option', 'p', 'pre', 'select',
		'span', 'strong', 'table', 'tbody', 'td', 'textarea',
		'tfoot', 'th', 'thead', 'tr', 'tt', 'ul','dd','dt','dl' ];
	for( var i = tags.length - 1;  i >= 0;  i-- ) {
		$.defineTag( tags[i] );
	}
})();

$.NBSP = '\u00a0';

$._createNode = function( tag, args ) {
	var fix = { 'class':'className', 'Class':'className' };
	var e;
	try {
		var attrs = args[0] || {};
		e = document.createElement( tag );
		for( var attr in attrs ) {
			var a = fix[attr] || attr;
			e[a] = attrs[attr];
		}
		for( var i = 1;  i < args.length;  i++ ) {
			var arg = args[i];
			if( arg == null ) continue;
			if( arg.constructor != Array ) append( arg );
			else for( var j = 0;  j < arg.length;  j++ )
				append( arg[j] );
		}
	}
	catch( ex ) {
		alert( 'Cannot create <' + tag + '> element:\n' +
			args.toSource() + '\n' + args );
		e = null;
	}
	
	function append( arg ) {
		if( arg == null ) return;
		var c = arg.constructor;
		switch( typeof arg ) {
			case 'number': arg = '' + arg;  // fall through
			case 'string': arg = document.createTextNode( arg );
		}
		e.appendChild( arg );
	}
	
	return e;
};
