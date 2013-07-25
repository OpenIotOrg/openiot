
function DisplayAlert(id,left,top) {
document.getElementById(id).style.left=left+'px';
document.getElementById(id).style.top=top+'px';
document.getElementById(id).style.display='block';
}
function DisplayAlertByClass(id,left,top) {
document.getElementByClass(id).style.left=left+'px';
document.getElementByClass(id).style.top=top+'px';
document.getElementByClass(id).style.display='block';
}
var newwindow;
function poptastic(url)
{
	newwindow=window.open(url,'name','height=400,width=700,top=100,resizable=yes,scrollbars=yes,toolbar=yes,status=yes');
	if (window.focus) {newwindow.focus()}
}


cookieName="page_scroll"
expdays=365

// An adaptation of Dorcht's cookie functions.

function setCookie(name, value, expires, path, domain, secure){
if (!expires){expires = new Date()}
document.cookie = name + "=" + escape(value) +
((expires == null) ? "" : "; expires=" + expires.toGMTString()) +
((path == null) ? "" : "; path=" + path) +
((domain == null) ? "" : "; domain=" + domain) +
((secure == null) ? "" : "; secure")
}

function getCookie(name) {
var arg = name + "="
var alen = arg.length
var clen = document.cookie.length
var i = 0
while (i < clen) {
var j = i + alen
if (document.cookie.substring(i, j) == arg){
return getCookieVal(j)
}
i = document.cookie.indexOf(" ", i) + 1
if (i == 0) break
}
return null
}

function getCookieVal(offset){
var endstr = document.cookie.indexOf (";", offset)
if (endstr == -1)
endstr = document.cookie.length
return unescape(document.cookie.substring(offset, endstr))
}

function deleteCookie(name,path,domain){
document.cookie = name + "=" +
((path == null) ? "" : "; path=" + path) +
((domain == null) ? "" : "; domain=" + domain) +
"; expires=Thu, 01-Jan-00 00:00:01 GMT"
}

function saveScroll(){ // added function
var expdate = new Date ()
expdate.setTime (expdate.getTime() + (expdays*24*60*60*1000)); // expiry date

var x = (document.pageXOffset?document.pageXOffset:document.body.scrollLeft)
var y = (document.pageYOffset?document.pageYOffset:document.body.scrollTop)
Data=x + "_" + y
setCookie(cookieName,Data,expdate)
}

function loadScroll(){ // added function
inf=getCookie(cookieName)
if(!inf){return}
var ar = inf.split("_")
if(ar.length == 2){
window.scrollTo(parseInt(ar[0]), parseInt(ar[1]))
}
}








