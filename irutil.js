"use strict";
//
var ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
var MONTH_NAMES = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ];
var DAY_NAMES = [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat' ];
var now = new Date();
/** ir: ir utility functions and values */
var ir = { 
    accent_map : { 'à' : 'a', 'á' : 'a', 'â' : 'a', 'ã' : 'a', 'ä' : 'a', 'å' : 'a', 'ç' : 'c', 'è' : 'e', 
        'é' : 'e', 'ê' : 'e', 'ë' : 'e', 'ì' : 'i', 'í' : 'i', 'î' : 'i', 'ï' : 'i', 'ñ' : 'n', 'ò' : 'o',
        'ó' : 'o', 'ô' : 'o', 'õ' : 'o', 'ö' : 'o', 'ø' : 'o', 'ß' : 's', 'ù' : 'u', 'ú' : 'u', 'û' : 'u',
        'ü' : 'u', 'ÿ' : 'y', 'À' : 'A', 'Â' : 'A', 'Ä' : 'A', 'È' : 'E', 'É' : 'E', 'Ê' : 'E', 'Ë' : 'E',
        'Î' : 'I', 'Ï' : 'I', 'Ô' : 'O', 'Œ' : 'O', 'Ù' : 'U', 'Û' : 'U', 'Ü' : 'U', 'Ÿ' : 'Y', 
    }, 
    checkboxLabelMap : null, 
    checkmark : "&#x2713;", 
    fileTooBig : "File {0} size {1} bytes is larger than maximum allowed {2} bytes.", 
    fileWrongType : "File {0} content type {1} does not match required type {2}.", 
    iOSDevices : { "idevice" : true, "ipad" : true, "iphone" : true }, 
    isLocalHost : null, 
    localIp:null,
    midnight : new Date(
        now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0),        
    pageNameSaved : null, 
    deAccent : function(s) {
        if (s == null || s == "") {
            return "";
        }
        return s.replace(/[^\w ]/g, function(char) {
            return ir.accent_map[char] || char;
        });
    }, 
    addEvent : function(objectOrId, eventType, functionRef) {
	    var obj = ir.get(objectOrId);
	    if (obj==null) {
			ajax({fn:"logError",error:"cannot addEvent to " + objectOrId + ": not found."}, function(){});
	        return;
	    }
	    var startsWithOn = left(eventType, 2).toLowerCase() == "on";
	    if (startsWithOn) {
	        eventType = eventType.substr(2);
	    }
	    if (obj.addEventListener) {
	        obj.addEventListener(eventType, functionRef, false);
	    } else if (obj.attachEvent) {
	        obj.attachEvent("on" + eventType, functionRef);
	    }
}, 
allowDrop:function(event) {
    event.preventDefault();
},
bool : function(id) {
    return isTrue(ir.v(id));
}, brace : function(expr) {
    expr = ir.trim(expr);
    if (expr == "") {
        return "";
    }
    if (!isNaN(expr)) {
        return expr;
    }
    if (expr.charAt(0) == '(' && expr.charAt(expr.length - 1) == ')') {
        return expr;
    }
    return "(" + expr + ")";
}, browserSessionGet : function(key) {
    if (sessionStorage) {
        return sessionStorage[key];
    }
    if (window.browserSession) {
        return window.browserSession[key];
    }
    return navigator.browserSession[key];
}, browserSessionSet : function(key, val) {
    if (sessionStorage) {
        sessionStorage[key] = val;
    } else if (window.ActiveXObject) {
        if (!window.browserSession) {
            window.browserSession = {};
        }
        window.browserSession[key] = val;
    } else {
        if (!navigator.browserSession) {
            navigator.browserSession = {};
        }
        navigator.browserSession[key] = val;
    }
}, checkBold : function(checkBoxId) {
    var b = document.getElementById(checkBoxId);
    if (b == null) {
        return;
    }
    var sib = b.nextSibling || b.previousSibling;
    if (sib && sib.parentNode && sib.parentNode.tagName == "LABEL") {
        var style = sib.parentNode.style;
        style.fontWeight = b.checked ? "bold" : "normal";
        style.textDecoration = b.checked ? "none" : "underline";
    } else {
        if (ir.checkboxLabelMap == null) {
            ir.checkBoldInit();
        } else {
            var label = ir.checkboxLabelMap[checkBoxId];
            if (label) {
                label.style.fontWeight = b.checked ? "bold" : "normal";
            }
        }
    }
}, checkBoldInit : function() {
    if (ir.checkboxLabelMap == null) {
        var m = {};
        var labels = document.getElementsByTagName("label");
        for (var i = 0, z = labels.length; i < z; i++) {
            var label = labels[i];
            var cbId = label.getAttribute("for");
            if (cbId > "") {
                var cb = document.getElementById(cbId);
                if (cb) {
                    label.style.fontWeight = cb.checked ? "bold" : "normal";
                    m[cbId] = label;
                }
            }
        }
        ir.checkboxLabelMap = m;
        //
        // loop for <label>xasfjksdlfkj [x]</label> with no "for" attribute
        //        
        var cba = document.querySelectorAll('input[type="checkbox"]');
        for (var i = 0, z = cba.length; i < z; i++) {
            var cb = cba[i];
            if (cb.id>"" && !m[cb.id]) {
                ir.checkBold(cb.id);
            }
        }
    }
}, 
/** checks if email is valid format, returns false if invalid*/
checkEmail : function(email)
{
	if (email == null || email.length < 6)
	{
		return false;
	}
	var atAt = email.indexOf("@");
	if (atAt < 0)
	{
		return false;
	}
	if (email.indexOf('@', atAt + 1) > -1)
	{
		return false;
	}
	var dotAt = email.lastIndexOf(".");
	return dotAt > atAt + 1 && dotAt < email.length - 1;
},
clearChildren : function(parentElement) {
    while (parentElement.childNodes.length > 0) {
        parentElement.removeChild(parentElement.firstChild);
    }
},
/**
 * clears the value or the innerHTML of the object or id passed.
 */
clear : function(idOrCtl) {//
    var ctl = idOrCtl;
    if (typeof (ctl) == "string") {
        ctl = ir.get(idOrCtl);
        if (ctl == null) {
            ir.log("ir.clear cannot find " + idOrCtl);
            return;
        }
    }
    if (ctl.options) {
        ctl.selectedIndex = 0;
    } else if (ctl.type == "file") {
        try {
            ctl.value = null;
        }
        catch (nevermind) {}
        if (ctl.value) {
            ctl.parentNode.replaceChild(ctl.cloneNode(true), ctl);
        }
    } else {
        try {
            ir.set(ctl.id, "");
            if (ir.v(ctl.id) != "") {
                ir.set(ctl.id, 0);
            }
        }
        catch (e) {
            ir.log("clear('" + ctl.id + "'): " + e);
        }
    }
}, clearValues : function( /* varargs */) {
    if (arguments == null || arguments.length == 0) {
        alert("you must supply arguments to clearValues()");
    }
    var startAt = 0;
    var submit = false;
    if (typeof (arguments[0]) == "boolean") {
        submit = arguments[0];
        startAt = 1;
    }
    for (var i = startAt; i < arguments.length; i++) {
        var ctl = document.getElementById(arguments[i]);
        if (ctl == null) {
            ir.log("clearValues() cannot find element " + arguments[i]);
        } else {
            ir.clear(ctl);
        }
    }
    if (submit) {
        document.forms[0].submit();
    }
}, clientInfo : function() {
    var unknown = 'Unknown';
    var screenSize = '';
    if (screen.width) {
        var width = (screen.width) ? screen.width : '';
        var height = (screen.height) ? screen.height : '';
        screenSize += '' + width + " x " + height;
    }
    var nVer = navigator.appVersion;
    var nAgt = navigator.userAgent;
    var browser = navigator.appName;
    var version = '' + parseFloat(navigator.appVersion);
    var majorVersion = parseInt(navigator.appVersion, 10);
    var nameOffset, verOffset, ix;
    if ((verOffset = nAgt.indexOf('Opera')) != -1) {
        browser = 'Opera';
        version = nAgt.substring(verOffset + 6);
        if ((verOffset = nAgt.indexOf('Version')) != -1) {
            version = nAgt.substring(verOffset + 8);
        }
    } else if ((verOffset = nAgt.indexOf('MSIE')) != -1) {
        browser = 'Microsoft Internet Explorer';
        version = nAgt.substring(verOffset + 5);
    } else if ((browser == 'Netscape') && (nAgt.indexOf('Trident/') != -1)) {
        // IE 11 no longer identifies itself as MS IE, so trap it
        // http://stackoverflow.com/questions/17907445/how-to-detect-ie11
        browser = 'Microsoft Internet Explorer';
        version = nAgt.substring(verOffset + 5);
        if ((verOffset = nAgt.indexOf('rv:')) != -1) {
            version = nAgt.substring(verOffset + 3);
        }
    } else if ((verOffset = nAgt.indexOf('Chrome')) != -1) {
        browser = 'Chrome';
        version = nAgt.substring(verOffset + 7);
    } else if ((verOffset = nAgt.indexOf('Safari')) != -1) {
        browser = 'Safari';
        version = nAgt.substring(verOffset + 7);
        if ((verOffset = nAgt.indexOf('Version')) != -1) {
            version = nAgt.substring(verOffset + 8);
        }
        // Chrome on iPad identifies itself as Safari. Actual results do not
        // match what Google claims
        // at: https://developers.google.com/chrome/mobile/docs/user-agent?hl=ja
        // No mention of chrome in the user agent string. However it does
        // mention CriOS, which presumably
        // can be keyed on to detect it.
        if (nAgt.indexOf('CriOS') != -1) {
            // Chrome on iPad spoofing Safari...correct it.
            browser = 'Chrome';
            // Don't believe there is a way to grab the accurate version number,
            // so leaving that for now.
        }
    }
    // Firefox
    else if ((verOffset = nAgt.indexOf('Firefox')) != -1) {
        browser = 'Firefox';
        version = nAgt.substring(verOffset + 8);
    }
    // Other browsers
    else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
        browser = nAgt.substring(nameOffset, verOffset);
        version = nAgt.substring(verOffset + 1);
        if (browser.toLowerCase() == browser.toUpperCase()) {
            browser = navigator.appName;
        }
    }
    // trim the version string
    if ((ix = version.indexOf(';')) != -1)
        version = version.substring(0, ix);
    if ((ix = version.indexOf(' ')) != -1)
        version = version.substring(0, ix);
    if ((ix = version.indexOf(')')) != -1)
        version = version.substring(0, ix);
    majorVersion = parseInt('' + version, 10);
    if (isNaN(majorVersion)) {
        version = '' + parseFloat(navigator.appVersion);
        majorVersion = parseInt(navigator.appVersion, 10);
    }
    var mobile = /Mobile|mini|Fennec|Android|iP(ad|od|hone)/.test(nVer);
    var cookieEnabled = (navigator.cookieEnabled) ? true : false;
    if (typeof navigator.cookieEnabled == 'undefined' && !cookieEnabled) {
        document.cookie = 'testcookie';
        cookieEnabled = (document.cookie.indexOf('testcookie') != -1) ? true : false;
    }
    var os = unknown;
    var clientStrings = [ { s : 'Windows 3.11', r : /Win16/ }, { s : 'Windows 95', r : /(Windows 95|Win95|Windows_95)/ }, { s : 'Windows ME', r : /(Win 9x 4.90|Windows ME)/ }, { s : 'Windows 98', r : /(Windows 98|Win98)/ }, { s : 'Windows CE', r : /Windows CE/ }, { s : 'Windows 2000', r : /(Windows NT 5.0|Windows 2000)/ }, { s : 'Windows XP', r : /(Windows NT 5.1|Windows XP)/ }, { s : 'Windows Server 2003', r : /Windows NT 5.2/ }, { s : 'Windows Vista', r : /Windows NT 6.0/ }, { s : 'Windows 7', r : /(Windows 7|Windows NT 6.1)/ }, { s : 'Windows 8.1', r : /(Windows 8.1|Windows NT 6.3)/ }, { s : 'Windows 8', r : /(Windows 8|Windows NT 6.2)/ }, { s : 'Windows NT 4.0', r : /(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/ }, { s : 'Windows ME', r : /Windows ME/ }, { s : 'Android', r : /Android/ }, { s : 'Open BSD', r : /OpenBSD/ }, { s : 'Sun OS', r : /SunOS/ }, { s : 'Linux', r : /(Linux|X11)/ }, { s : 'iOS', r : /(iPhone|iPad|iPod)/ }, { s : 'Mac OS X', r : /Mac OS X/ }, { s : 'Mac OS', r : /(MacPPC|MacIntel|Mac_PowerPC|Macintosh)/ }, { s : 'QNX', r : /QNX/ }, { s : 'UNIX', r : /UNIX/ }, { s : 'BeOS', r : /BeOS/ }, { s : 'OS/2', r : /OS\/2/ }, { s : 'Search Bot', r : /(nuhk|Googlebot|Yammybot|Openbot|Slurp|MSNBot|Ask Jeeves\/Teoma|ia_archiver)/ } ];
    for ( var id in clientStrings) {
        var cs = clientStrings[id];
        if (cs.r.test(nAgt)) {
            os = cs.s;
            break;
        }
    }
    var osVersion = unknown;
    if (/Windows/.test(os)) {
        osVersion = /Windows (.*)/.exec(os)[1];
        os = 'Windows';
    }
    switch (os) {
        case 'Mac OS X':
            osVersion = /Mac OS X (10[\.\_\d]+)/.exec(nAgt)[1];
            break;
        case 'Android':
            osVersion = /Android ([\.\_\d]+)/.exec(nAgt)[1];
            break;
        case 'iOS':
            osVersion = /OS (\d+)_(\d+)_?(\d+)?/.exec(nVer);
            osVersion = osVersion[1] + '.' + osVersion[2] + '.' + (osVersion[3] | 0);
            break;
    }
    return browser + " " + version + " - " + os + " " + osVersion + (mobile ? ", mobile" : "") + (cookieEnabled ? "" : ", no cookies") + ", " + screenSize;
},
/** returns first non-blank,non-null value */
coalesce : function(v1, v2 /* ,v3... */) {
    for (var i = 0, z = arguments.length; i < z; i++) {
        var v = arguments[i];
        if (v != null && ir.trim(v) > "") {
            return v;
        }
    }
    return "";
},collapse : function(idOrElement) {    
    var element = ir.get(idOrElement);
    if (element) 
    {
        element.classList.add("collapse");  
    } 
    else 
    {
        ir.log("expand(" + idOrElement + " ): not found.");
    }
}, 
/** ir.copy will copy the object passed, optionally starting from
 * a passed result container, UNLESS the object passed is a simple
 * value or date or array, then the result container is ignored.
 */ 
copy : function(obj,optionalResultContainer) {
    if (null == obj || "object" != typeof obj) {
        return obj;
    }
    if (obj instanceof Date) {
        var result = new Date();
        result.setTime(obj.getTime());
        return result;
    }
    if (obj instanceof Array) {
        var result = [];
        for (var i = 0, len = obj.length; i < len; i++) {
            result[i] = ir.copy(obj[i]);
        }
        return result;
    }
    var result = optionalResultContainer || {};
    for ( var propertyName in obj) {
        if (obj.hasOwnProperty(propertyName)) {
        	var property = obj[propertyName];
            result[propertyName] = ir.copy(property);
        }
    }
    return result;
},
copyProperties : function(fromObj, toObj) {
	for ( var property in fromObj) {
	 	if (! fromObj.hasOwnProperty(property)) {
	 		continue;
	 	}
	 	var value = fromObj[property];
	 	if (value instanceof KeyedArray) {
	 		toObj[property] = value.copy();
	 	} else if (value.splice) {
	 		toObj[property] = value.slice();
	 	} else if (typeof value == 'object') {
	 		toObj[property] = JSON.parse(JSON.stringify(value));
		} else {
			toObj[property] = value;
	 	}
	}
	if (JSON.stringify(fromObj) != JSON.stringify(toObj))
	{
		return;//breakpoint parking
	}
}, 
dateDiff : function(d1, d2, unit) {
    if (d1 == d2)
        return 0;
    if (d1 == null || d2 == null)
        return 0;
    var millisDiff = d1.getTime() - d2.getTime();
    var divisor = 1000 * 60 * 60 * 24;
    if (unit > "") {
        unit = unit.toLowerCase();
        switch (unit) {
            case 'h':
                divisor = 1000 * 60 * 60;
                break;
            case 'm':
                divisor = 60000;
                break;
            case 's':
                divisor = 1000;
                break;
            case 'd':
            default:
                // already set
                break;
        }
    }
    return Math.round(millisDiff / divisor);
},
/** ir.digits coerces the argument to a string and then returns a result excluding non 0-9 characters */
digits:function(s) {
	return ir.filter(s+"","0123456789");	
},
/** ir.debug will write to console only if this is a dev box */
debug : function(m) {
    if (ir.isLocalHost == null) {
        ir.isLocalHost = window.location.href.indexOf("localhost") > -1;
    }
    if (ir.isLocalHost) {
        ir.log(m);
    }
},
decimal:function(v,dec)
{
	return ir.n(v).toFixed(dec);
},
/**
 * Removes the named or passed node from the DOM if it exists and
 * node.parent.removeChild works without throwing
 */
deleteNode : function(idOrNode) {
    var node = idOrNode;
    if (typeof (node) == "string") {
        node = document.getElementById(idOrNode);
    }
    try {
        if (node != null) {
            if (node.parentNode) {
                node.parentNode.removeChild(node);
            }
        }
    }
    catch (e) {
        ir.log("failed to remove Node " + (node == null ? "null" : node.id) + ":" + e);
    }
}, 
delimitedList: function(delim /* arguments */){
	var d = "";
	var value = "";
	for(var i=1, z=arguments.length; i<z;i++)
	{
		var a = arguments[i];
		if(a)
		{
			value += d +  a + "";
			d = delim || ", ";
		}
	}
	return value;
},
deduplicate : function(/* arguments */) {
    var wordHash = {};
    var space = "";
    var result = "";
    for (var i = 0, len = arguments.length; i < len; i++) {
        var words = (arguments[i] + "").split(" ");
        for (var j = 0; j < words.length; j++) {
            var word = words[j];
            if (!wordHash[word]) {
                result += space + word;
                wordHash[word] = 1;
                space = " ";
            }
        }
    }
    return result;
},
disable : function(idOrBox, boolOrNull) {
    var disabledOn = boolOrNull == null ? true : boolOrNull;
    var box = ir.get(idOrBox);
    if (disabledOn) {
        box.setAttribute("disabled", "disabled");
        box.blur();
    } else {
        box.removeAttribute("disabled");
    }
},
ellipsis : function(str,maxLength)
{
	if(str==null)
		{
			return "";
		}
   if(str.length < maxLength)
   {
      return str;
   }
   return str.substring(0,maxLength -3) + "...";
},
endsWith : function(str, suffix) {
    return str.substr(0-suffix.length) == suffix;;
},
escapeHtml:function (string)
{
	if(string==null)
	{
		return "";
	}
	return string.replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
},
evalNumber : function(expression) {
    if (expression instanceof Number) {
        return expression;
    }
    try {
        var result = new Function("return " + expression)();
        if (isNaN(result)){
            ir.log("NaN = ir.evalNumber(" + expression + ")");
        	return 0;
        } 
        return result;
    }
    catch (e) {
        ir.log("ir.evalNumber(" + expression + ") : " + e);
        return 0;
    }
}, evalObj : function(inExp) {// eval(
    if (typeof (inExp) == "object") {
        return inExp;
    }
    var expression = ir.trim(inExp);// coerce to string & trim
    if (expression == "") {
        return {};
    }
    try {
        var fn = new Function("return (" + expression + ")");
        var result = fn();
        return result;
    }
    catch (e) {
        ir.log("ir.evalObj(" + expression + "): " + e);
        return { ok : 0, exc : e };
    }
}, exists : function(id) {
    var ele = document.getElementById(id);
    if (ele == null) {
        var nl = document.getElementsByName(id);
        if (nl && nl.length > 0) {
            ele = nl[0];
        }
    }
    return ele != null;
},expand : function(idOrElement, optionalYesNo) {    
    var element = ir.get(idOrElement);
    if (element) 
    {
        if (optionalYesNo != null && !isTrue(optionalYesNo)) 
        {
        	ir.collapse(idOrElement);
        }
        else
        {
        	element.classList.remove("collapse");
    	}        
    } 
    else 
    {
        ir.log("expand(" + idOrElement + " ): not found.");
    }
}, filter : function(v, charsAllowed) {
    v = v + "";
    var ret = "";
    for (var i = 0; i < v.length; i++) {
        var c = v.charAt(i);
        if (-1 < charsAllowed.indexOf(c)) {
            ret += c;
        }
    }
    return ret;
}, focus : function(objectOrId) {
    var element = objectOrId;
    if (typeof (element) == "string") {
        element = document.getElementById(objectOrId);
    }
    if (element) {
        try {
            element.focus();
        }
        catch (exc) {
            window.status = "ir.focus failed to focus - " + exc;
        }
        if (element.value) {
            var pos = element.value.length;
            try {
                if (element.setSelectionRange) {
                    element.setSelectionRange(pos, pos);
                } else {
                    var range = element.createTextRange();
                    range.move("character", pos);
                    range.collapse(false);
                    range.select();
                }
            }
            catch (exc) {
                window.status = "ir.focus failed to set caret position - " + exc;
            }
        }
    }
},
/**
 * invokes passed function with parameters (element,index) for each element in
 * array
 * @param array
 * @param function
 */
forEach : function(arr, fn) {
    for (var i = 0, z = arr.length; i < z; i++) {
        fn(arr[i], i);
    }
}, format : function(k /* ,args */) {// mimics java.text.MessageFormat.format
    try {
        var v = k;
        if (arguments != null) {
            if (arguments.length > 1) {
                for (var i = 1; i < arguments.length; i++) {
                    var token = "{" + (i - 1) + "}";
                    v = ir.replace(v, token, arguments[i] + "");
                }
            }
        }
        return v;
    }
    catch (e) {
        return k;
    }
},
/** schedules a timeout to set the cell widths in headerTableId
 * to match the cell widths in the first table under scrollableDivId
 * as of Apr 6 2017 - does not work - more thought needed
 * -- what to do if header text is wider than data cell for instance
 */
freezeHeader:function(headerTableId,scrollableDivId) {
	var inner = function(headerTableId,scrollableDivId) {
		var dtaTable = document.getElementById(scrollableDivId).getElementsByTagName("TABLE")[0];
		if (dtaTable.rows.length==0) {
			return;
		}
		var hdrTable = document.getElementById(headerTableId);
		var hdrCells = hdrTable.rows[0].cells;
		var dtaCells = dtaTable.rows[0].cells;
		for (var i=0,z=hdrCells.length;i<z;i++) {
			var dta = dtaCells[i];
			var hdr = hdrCells[i];
			hdr.style.width = dta.offsetWidth + "px";
		}
	};
	window.setTimeout(function(){inner(headerTableId,scrollableDivId);},100);
},
get : function(idOrElement, suppressLogIfNull) {
    var element = idOrElement;
    if (typeof (element) == "string") {
        element = document.getElementById(idOrElement);
    }
    if (element==null) {
    	if (! suppressLogIfNull) {
    		ir.log("ir.get('" + idOrElement + "') yields null.");
    	}
    }
    return element;
},
/**
 * returns {top:?,left:?,bottom:?,width:?,height:?,right:?} for position of
 * element passed
 */
getBounds : function(elemOrId) {
    var elem = elemOrId;
    if (typeof (elem) == "string") {
        elem = document.getElementById(elemOrId);
        if (elem == null) {
            throw "ir.getBounds - element '" + elemOrId + "' not found on document.";
        }
    }
    // (1)
    var box = elem.getBoundingClientRect();
    var body = document.body;
    var docElem = document.documentElement;
    // (2)
    var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
    var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;
    // (3)
    var clientTop = docElem.clientTop || body.clientTop || 0;
    var clientLeft = docElem.clientLeft || body.clientLeft || 0;
    // (4)
    var top = Math.round(box.top + scrollTop - clientTop);
    var left = Math.round(box.left + scrollLeft - clientLeft);
    return { top : top, left : left, bottom : top + box.height,right : left + box.width, 
            width : box.width, height : box.height, center:box.left + (box.width/2),middle:box.top + (box.height/2) };
},
/** ir.getChildren recursively retrieves children of the passed element*/
getChildren : function(ele) {
	var result = [];
	function innerGet(node) {
		var children = node.childNodes;
		for (var i = 0, z = children.length; i < z; i++) {
			var c = children[i];
			result.push(c);
			if (c.hasChildNodes()) {
				innerGet(c);
			}
		}
	}
	innerGet(ele);
	return result;
},

getClientHeight:function(){
	return document.documentElement.clientHeight;
},
getClientWidth:function(){
	return document.documentElement.clientWidth;
},
getDateFromFormat:function(val, format)
{
	var i_val = 0;
	var i_format = 0;
	var c = "";
	var token = "";
	var x = 0, y = 0;
	var now = new Date();
	var year = now.getYear();
	var month = now.getMonth() + 1;
	var date = 1;
	var hh = now.getHours();
	var mm = now.getMinutes();
	var ss = now.getSeconds();
	var ampm = "";
	val = val + "";
	format = format + "";
	var decimal = val.lastIndexOf(".");
	if (decimal > 0 && decimal < val.length)
	{
		val = val.substring(0, decimal);
	}
	while (i_format < format.length)
	{
		// Get next token from format string
		c = format.charAt(i_format);
		token = "";
		while ((format.charAt(i_format) == c) && (i_format < format.length))
		{
			token += format.charAt(i_format++);
		}
		// Extract contents of value based on format token
		if (token == "yyyy" || token == "yy" || token == "y")
		{
			if (token == "yyyy")
			{
				x = 4;
				y = 4;
			}
			if (token == "yy")
			{
				x = 2;
				y = 2;
			}
			if (token == "y")
			{
				x = 2;
				y = 4;
			}
			year = ir.getInt(val, i_val, x, y);
			if (year == null)
			{
				return null;
			}
			i_val += year.length;
			if (year.length == 2)
			{
				if (year > 70)
				{
					year = 1900 + (year - 0);
				}
				else
				{
					year = 2000 + (year - 0);
				}
			}
		}
		else if (token == "MMM" || token == "NNN")
		{
			month = 0;
			for (var i = 0; i < MONTH_NAMES.length; i++)
			{
				var month_name = MONTH_NAMES[i];
				if (val.substring(i_val, i_val + month_name.length)
						.toLowerCase() == month_name.toLowerCase())
				{
					if (token == "MMM" || (token == "NNN" && i > 11))
					{
						month = i + 1;
						if (month > 12)
						{
							month -= 12;
						}
						i_val += month_name.length;
						break;
					}
				}
			}
			if ((month < 1) || (month > 12))
			{
				return null;
			}
		}
		else if (token == "EE" || token == "E")
		{
			for (var i = 0; i < DAY_NAMES.length; i++)
			{
				var day_name = DAY_NAMES[i];
				if (val.substring(i_val, i_val + day_name.length).toLowerCase() == day_name
						.toLowerCase())
				{
					i_val += day_name.length;
					break;
				}
			}
		}
		else if (token == "MM" || token == "M")
		{
			month = ir.getInt(val, i_val, token.length, 2);
			if (month == null || (month < 1) || (month > 12))
			{
				return null;
			}
			i_val += month.length;
		}
		else if (token == "dd" || token == "d")
		{
			date = ir.getInt(val, i_val, token.length, 2);
			if (date == null || (date < 1) || (date > 31))
			{
				return null;
			}
			i_val += date.length;
		}
		else if (token == "hh" || token == "h")
		{
			hh = ir.getInt(val, i_val, 1, 2);
			if (hh == null || (hh < 1) || (hh > 12))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "HH" || token == "H")
		{
			hh = ir.getInt(val, i_val, 1, 2);
			if (hh == null || (hh < 0) || (hh > 23))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "KK" || token == "K")
		{
			hh = ir.getInt(val, i_val, token.length, 2);
			if (hh == null || (hh < 0) || (hh > 11))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "kk" || token == "k")
		{
			hh = ir.getInt(val, i_val, token.length, 2);
			if (hh == null || (hh < 1) || (hh > 24))
			{
				return null;
			}
			i_val += hh.length;
			hh--;
		}
		else if (token == "mm" || token == "m")
		{
			mm = ir.getInt(val, i_val, token.length, 2);
			if (mm == null || (mm < 0) || (mm > 59))
			{
				return null;
			}
			i_val += mm.length;
		}
		else if (token == "ss" || token == "s")
		{
			ss = ir.getInt(val, i_val, token.length, 2);
			if (ss == null || (ss < 0) || (ss > 59))
			{
				return null;
			}
			i_val += ss.length;
		}
		else if (token == "a")
		{
			if (val.substring(i_val, i_val + 2).toLowerCase() == "am")
			{
				ampm = "AM";
			}
			else if (val.substring(i_val, i_val + 2).toLowerCase() == "pm")
			{
				ampm = "PM";
			}
			else
			{
				return null;
			}
			i_val += 2;
		}
		else
		{
			if (val.substring(i_val, i_val + token.length) != token)
			{
				return null;
			}
			else
			{
				i_val += token.length;
			}
		}
	}
	// If there are any trailing characters left in the value, it doesn't match
	if (i_val != val.length)
	{
		return null;
	}
	// Is date valid for month?
	if (month == 2)
	{
		// Check for leap year
		if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
		{ // leap year
			if (date > 29)
			{
				return null;
			}
		}
		else
		{
			if (date > 28)
			{
				return null;
			}
		}
	}
	if ((month == 4) || (month == 6) || (month == 9) || (month == 11))
	{
		if (date > 30)
		{
			return null;
		}
	}
	// Correct hours value
	if (hh < 12 && ampm == "PM")
	{
		hh = hh - 0 + 12;
	}
	else if (hh > 11 && ampm == "AM")
	{
		hh -= 12;
	}
	return new Date(year, month - 1, date, hh, mm, ss);
},
/**
 * ir.getExtension returns the url or file's extension NOT including the dot
 */
getExtension : function(url) {
    if (url == null || url == "") {
        return "";
    }
    var dotAt = url.lastIndexOf(".");
    if (dotAt == -1 || dotAt == url.length - 1) {
        return "";
    }
    return url.substring(dotAt + 1);
},
/**
 * returns characters after but not including the last slash, up to question mark query string marker
 */
getFileName:function(url) {
	if (url == null || url=="") {
		return "";
	}	
	var qAt = url.lastIndexOf("?");
	if (qAt > -1) {
		url = url.substring(0,qAt);
	}
	var foundAt = url.lastIndexOf("/");
	if (foundAt == -1 || foundAt == url.length - 1) {
		return "";
	}
	return url.substring(foundAt + 1);
},
getFromFrame:function(frameId,elementId) {
    if (typeof (elementId) == "string") {
		var frame = document.getElementById(frameId);
		if (frame != null) {
			return frame.contentWindow.document.getElementById(elementId);
		}
    }
    return elementId;
},
getIfExists : function(id) {
    return ir.get(id, true);
},
getImgHtm : function(src,width,height,align,title)
{
   var t = "<img src={0} title='{4}' style='vertical-align:{3};width:{1}px;height:{2}px;'>";
   return format(t,src,width,height,align || "middle",title || "");
}, 
getInt:function(str, i, minlength, maxlength)
{
	for (var x = maxlength; x >= minlength; x--)
	{
		var token = str.substring(i, i + x);
		if (token.length < minlength)
		{
			return null;
		}
		if (ir.isInteger(token))
		{
			return token;
		}
	}
	return null;
},
go : function(url) {
    window.location.href = url;
}, 
hide : function(id) {
    var ele = ir.get(id, true);
    if (ele == null) {
        ir.log("cannot hide " + id + " - not found.");
    } else {
        ele.style.display = "none";
    }
}, 
hideAll : function(classname) {
    var items = document.getElementsByClassName(classname);
    for(var i = 0; i < items.length; i ++){
    	ir.hide(items[i]);
    }
}, 
hideShow : function(idOrElement) {
    var element = ir.get(idOrElement);
    if (element) {
        var cur = element.style.display;
        if (cur == "none") {
            element.style.display = "";
        } else {
            element.style.display = "none";
        }
    } else {
        ir.log("ir.hideShow: " + idOrElement + " - not found.");
    }
}, htmEscape : function(text) {
    return text.replace("<=", "&le;").replace(">=", "&ge;").replace("<", "&lt;").replace(">", "&gt;");
}, 
hypot:function(v1,v2) {
	var sumOfSquares = v1*v1 + v2*v2;
	return sumOfSquares==0 ? 0 : Math.sqrt(sumOfSquares);
},
id : function(v) {
    return " id=" + ir.q(v) + " name=" + ir.q(v) + " ";
}, 
/** ir.indexOf
 * finds Array.indexOf or with coercion or passed function
 * @param a array
 * @param v value or function: boolean equals(arrayElement)
 * @returns
 */
indexOf : function(a, v) {
    if (a == null || a.length == 0 || v == null) {
        return -1;
    }
    var isFunc = typeof (v) == "function";
    if (!isFunc) {
        try {
            var found = a.indexOf(v);
            if (found > -1) {
                return found;
            }
        }
        catch (badBrowserOrNeedsCoercion) {}
    }
    for (var i = 0, len = a.length; i < len; i++) {
        if (isFunc) {
            if (v(a[i])) {
                return i;
            }
        } else {
            if (a[i] == v) {
                return i;
            }
        }
    }
    return -1;
}, indexOfInt : function(array, searchForInt) {
    if (array == null || array.length == 0 || typeof (searchForInt) != "number") {
        return -1;
    }
    for (var i = 0, z = array.length; i < z; i++) {
        if (array[i] == searchForInt) {
            return i;
        }
    }
    return -1;
},
/** ir.isInput indicates whether the passed element is an input control */
isInput : function(element) {
	return -1 < "INPUT|SELECT|TEXTAREA".indexOf(element.tagName);
},
isInteger:function(val)
{
	var digits = "1234567890";
	for (var i = 0; i < val.length; i++)
	{
		if (digits.indexOf(val.charAt(i)) == -1)
		{
			return false;
		}
	}
	return true;
}, isInView : function(idOrElement)
{
	var element = idOrElement;
	if (typeof (element) == "string")
	{
		element = ir.get(idOrElement);
	}
    return element.getBoundingClientRect().top >= element.parentNode.getBoundingClientRect().top
    	&& element.getBoundingClientRect().bottom <= element.parentNode.getBoundingClientRect().bottom;
}, isMobileDevice : function() {
    var mobileAgents = [ "mobile", "android", "iphone;", "blazer;", "palm;", "handspring;", "nokia;", "kyocera;", "samsung;", "motorola;", "smartphone;", "windows ce;", "blackberry;", "wap;", "sonyericsson;", "playstation portable;", "lg;", "mmp;", "opwv;", "symbian;", "epoc;" ];
    try {
        var alc = navigator.userAgent.toLowerCase();
        for (var i = 0, z = mobileAgents.length; i < z; i++) {
            if (alc.indexOf(mobileAgents[i]) > -1) {
                return true;
            }
        }
    }
    catch (ex) {
        ir.log("isMobileDevice: " + ex);
    }
    return false;
}, 
isTrue : function(v) {
    if (v == null) {
        return false;
    }
    if (typeof (v) == "boolean") {
        return v;
    }
    if (isNaN(v)) {
        v = v + "  ";// coerce to string
        return left(v, 1) == "t" || left(v, 1) == "y" || left(v, 2) == "on" || left(v, 7) == "checked";
    }
    return Number(v) != 0;
}, 
/** ir.join will join elements passed with the delimiter passed,
 * excluding null or blank elements.
 */
join : function(va, delim) {
    var res = "";
    var del = "";
    for (var i = 0, len = va.length; i < len; i++) {
        if (va[i] > "") {
            res += del + va[i];
            del = delim || ",";
        }
    }
    return res;
}, 
key : function(e) {
    if (e == null && window.event) {
        e = window.event;
    }
    if (e) {
        if (e.keyCode) {
            return e.keyCode;
        }
    }
    return -1;
}, 
left : function(s, len) {
    if (len <= 0) {
        return "";
    }
    s = s + "";
    if (s.length < len) {
        return s;
    }
    return s.substr(0, len);
},
/** ir.linkInchMillimeter: links two input boxes such that typing in one updates the other in the appropriate
 * converted units.
 */
linkInchMillimeter:function(inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals) {
	if (inchDecimals == undefined) { inchDecimals=5;}
	if (mmDecimals == undefined) {mmDecimals = 4;}
	ir.addEvent(inchBoxOrId,"input",function(){ir.linkInchMillimeterHandler(true,inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals);});
	ir.addEvent(mmBoxOrId,"input",function(){ir.linkInchMillimeterHandler(false,inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals);});
	//assume non-zero value is the source
	var inchVal = ir.vn(inchBoxOrId);
	var mmVal = ir.vn(mmBoxOrId);
	if (inchVal != mmVal) {
		if (inchVal==0) {
			ir.linkInchMillimeterHandler(false,inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals);
		} else {
			ir.linkInchMillimeterHandler(true,inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals);
		}
	}
},
/**  linkInchMillimeterHandler: event handler for ir.linkInchMillimeterHandler */
linkInchMillimeterHandler:function(thisIsInches,inchBoxOrId,mmBoxOrId,inchDecimals,mmDecimals) {
	var val = ir.vn(thisIsInches ? inchBoxOrId : mmBoxOrId);
	var linkedBoxOrId = thisIsInches ? mmBoxOrId : inchBoxOrId;
	if (thisIsInches) {
		ir.set(linkedBoxOrId,ir.nz(ir.round(val * 25.4,mmDecimals)));
	} else {
		ir.set(linkedBoxOrId,ir.nz(ir.round(val / 25.4,inchDecimals)));
	}
},
loadSelect : function(sb, array, valueAttrOrFunc, textAttrOrFunc) {
    var valueFunc = valueAttrOrFunc;
    var textFunc = textAttrOrFunc;
    if (typeof (valueFunc) == "string") {
        valueFunc = function(o) {
            return o[valueAttrOrFunc];
        };
    }
    if (typeof (textFunc) == "string") {
        textFunc = function(o) {
            return o[textAttrOrFunc];
        };
    }
    for (var i = 0, z = array.length; i < z; i++) {
        var obj = array[i];
        addOption(sb, valueFunc(obj), textFunc(obj));
    }
}, log : function(m) {
    if (window.console && window.console.log) {
        window.console.log(m);
    } else {
        window.status = m;
    }
    document.getElementById("djscons").innerHTML += "<br>" + m;
}, 
lpad : function(sVal, chFill, nLen) {
    sVal += "";// coerce to a string
    var padTimes = Math.max(0, nLen - sVal.length);
    var sNew = "";
    for (var i = 0; i < padTimes; i++) {
        sNew += chFill;
    }
    return sNew + sVal;
},
ltrim : function(s) {
    s = "" + s;
    var i = 0;
    while (s.charAt(i) == " " && i < s.length) {
        i++;
    }
    return i < s.length ? s.substr(i) : "";
}, 
/**
 * Converts/Coerces passed value to a number.  Will remove commas if some are included,
 * will interpret fractions if some are included.  Last resort, will return 0.
 */
 n : function(inV) {
    if (inV == null || inV=="") {
        return 0;
    }
    if (!isNaN(inV)) {
        return Number(inV);
    }
    var v = ir.replace(inV, ",", "");
    if (!isNaN(v)) {
        return Number(v);
    }
    if (v.indexOf(' ') > -1) {
        return ir.parseFraction(v);
    }
    var firstDotAt = v.indexOf('.');
    if (firstDotAt > -1 && v.indexOf('.',firstDotAt+1)) {
    	ir.log("Two commas in number expression " + inV);
    	return 0;
    }
    v = ir.filter(v, ".-0123456789");
    if (isNaN(v)) {
        return 0;
    }
    return Number(v);
},
/**
 * format number with commas, with optionalDecimalDigits if requested
 */
nc : function(x, decimals) {
    if (x==null || x=="") {
    	return "0";
	}
    var minus = (""+x)[0]=='-';
    if (minus) {
    	x = (""+x).substr(1);
    }
    var fillStr = "00000000000000000000000000000000000";
    var parts;
    if (decimals >= 0 && decimals < fillStr.length) {
        parts = ir.round(x, decimals).toString().split(".");
    } else {
        parts = x.toString().split(".");
    }
    
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    parts[1] = parts.length == 1 ? "" : parts[1];
    if (decimals > 0 && decimals < fillStr.length) {
        parts[1] = (parts[1] + fillStr).substr(0, decimals);
    }
    var result = parts.join(".");
    if (result.charAt(result.length - 1) == '.') {
        result = result.substring(0, result.length - 1);
    }
    return (minus ? "-" : "") + result;
},
/**
 * number value with specified max decimal digits, or if decimals not specified,
 * minimum non-zero digits
 */
nd : function(v, decs) {
    if (isNaN(v)) {
        return v;
    }
    if (decs) {
        v = ir.round(v, decs);
    }
    v = v + "";
    var decimalPointAt = v.indexOf('.');
    if (decimalPointAt == -1) {
        return v;
    }
    var lastNonZeroDigitIndex = -1;
    for (var i = v.length - 1; i > decimalPointAt; i--) {
        if (v.charAt(i) > '0') {
            lastNonZeroDigitIndex = i;
            break;
        }
    }
    if (lastNonZeroDigitIndex == -1) {
        return v.substring(0, decimalPointAt);
    }
    return v.substring(0, lastNonZeroDigitIndex + 1);
},
/**
 * Returns first non zero argument
 */
nonZero : function(v1, v2 /* ... */) {
    for (var i = 0; i < arguments.length; i++) {
        if (i == arguments.length - 1 || arguments[i] != 0) {
            return arguments[i];
        }
    }
},
/** no-operation */
noop : function() {},
/**
 * Returns num passed formatted with passed decimal positions, if any, EXCEPT if
 * value of num is zero, then it returns passed returnIfZero.
 */
nz : function(num, returnIfZero, decimals) {
    if (isNaN(num)) {
    	return num;
    }
    var nv = ir.n(num);    	
    if (nv == 0) {
        return returnIfZero || "";
    }
    if (decimals != undefined) {
        return ir.nd(nv,decimals);
    }
    return nv;
}, onFileSelect : function(box) {
    if (!box) {
        return true;
    }
    try {
        var maxSize = ir.vn("hhMaxSize" + box.id);
        var file = box.files[0];
        if (box.accept > "") {
            var typeCheck = box.accept;
            if (right(box.accept, 2) == "/*") {// we only check suffix
                typeCheck = box.accept.substring(0, box.accept.length - 2);
            }
            var issueAlert = false;
            var fileType = file.type;
            if (fileType == null || fileType == "") {
                fileType = ir.getExtension(file.name).toLowerCase();
                if (fileType > "") {
                    if (typeCheck == "image") {
                        issueAlert = fileType != "jpg" && fileType != "jpeg" && fileType != "bmp" && fileType != "gif" && fileType != "png";
                    } else if (typeCheck.indexOf("/") > 0) {
                        var extCheck = typeCheck.substring(typeCheck.indexOf("/") + 1).toLowerCase();
                        issueAlert = extCheck > "" && fileType != extCheck;
                    }
                }
            } else if (!ir.startsWith(fileType, typeCheck)) {
                issueAlert = true;
            }
            if (issueAlert) {
                box.style.border = "2px solid red";
                alert(format(ir.fileWrongType, file.name, fileType, typeCheck));
                return false;
            }
        }
        if (file.size > maxSize) {
            box.style.border = "2px solid red";
            alert(format(ir.fileTooBig, file.name, file.size, maxSize));
            return false;
        }
        return true;
    }
    catch (exc) {
        ir.log(exc);
        return true;
    }
}, pageName : function() {
    if (ir.pageNameSaved != null) {
        return ir.pageNameSaved;
    }
    var n = window.location.href;
    var lastSlash = n.lastIndexOf("/");
    if (lastSlash > 0) {
        n = n.substr(lastSlash + 1);
    }
    var qMark = n.indexOf("?");
    if (qMark > 0) {
        n = n.substr(0, qMark);
    }
    var hash = n.indexOf("#");
    if (hash > 0) {
        n = n.substr(0, hash);
    }
    ir.pageNameSaved = n;
    return n;
}, parseFraction : function(v) {
    var sum = 0;
    var expressions = [];
    var tokens = v.split(" ");
    var previousTokenNumeric = true;
    for (var i = 0; i < tokens.length; i++) {
        var token = tokens[i];
        if (previousTokenNumeric) {
            expressions.push(token);
        } else if (!isNaN(token)) {
            expressions.push(token);
            previousTokenNumeric = true;
        } else {
            if (previousTokenNumeric && token.length() > 1) {
                expressions.push(token);
            } else {
                if (i == 0) {
                    return 0;
                }
                expressions[i - 1] += token;
            }
            previousTokenNumeric = false;
        }
    }
    for (var i = 0; i < expressions.length; i++) {
        if (checkNumericExpression(expressions[i])) {
            sum += ir.evalNumber(expressions[i]);
        }
    }
    return sum;
},
/**
 * Always returns a date; Date(0) if input is not a valid date string in
 * yyyy-mm-dd format.
 */
parseYmd : function(str) {
    if (str.getTime) {
        return str;
    }
    if (!str || str.length == 0) {
        return new Date(0);
    }
    var dateTime = str.split(" ");
    var ymd = dateTime[0].split("-");
    if (ymd.length < 3) {
        return new Date(0);
    }
    var h = 0, m = 0, s = 0;
    if (dateTime.length > 1) {
        var time = dateTime[1].split(":");
        if (time.length > 0) {
            h = Number(time[0]);
        }
        if (time.length > 1) {
            m = Number(time[1]);
        }
        if (time.length > 2) {
            s = Number(time[2]);
        }
    }
    try {
        return new Date(Number(ymd[0]), Number(ymd[1]) - 1, Number(ymd[2]), h, m, s);
    }
    catch (exc) {
        ir.log("ir.parseYmd('" + str + "'): " + exc);
        return new Date(0);
    }
}, playSound : function(url) {
    try {
        var audio = new Audio(url);
        audio.play();
    }
    catch (e) {
        try {
            var id = ir.replace(url, "/", "_");
            var span = document.getElementById(id);
            if (span != null) {
                document.body.removeChild(span);
            }
            span = document.createElement("SPAN");
            span.id = id;
            span.innerHTML = "<EMBED SRC='" + url + "' LOOP='FALSE' AUTOSTART='TRUE' HIDDEN='TRUE'>";
            document.body.appendChild(span);
        }
        catch (ee) {
            ir.log(playSoundEmbed + ": " + url + ": " + e);
        }
    }
},
/** ir.plus returns a + sign for the number passed or blank */
plus:function(number) {
	return Number(number) >= 0 ? "+" : ""; 
},
/** ir.q wraps the passed arguments in quotes */
q : function(v) {
    v = v + "";
    if (v.indexOf("'") == -1) {
        return "'" + v + "'";
    }
    if (v.indexOf('"') == -1) {
        return '"' + v + '"';
    }
    return "'" + v.replace(/'/g, "&#39;") + "'";
}, 
/** ir.radioBold makes the label of the selected member of a radio button group bold and undecorated, 
 * while the other labels are normal weight and underlined.  The rationale is that it makes
 * it clear to users that the labels are clickable  */
radioBold : function(radioButtonGroupName) {
    var mob = ir.isMobileDevice();
    var list = document.getElementsByName(radioButtonGroupName);
    for (var i = 0; list && i < list.length; i++) {
        var rb = list[i];
        if (rb.nextSibling && rb.nextSibling.parentNode && rb.nextSibling.parentNode.tagName == "LABEL") {
            var style = rb.nextSibling.parentNode.style;
            if (rb.checked) {
                style.fontWeight = "bold";
                style.textDecoration = "none";
            } else {
                style.fontWeight = "normal";
                style.textDecoration = "underline";
            }
            if (mob) {
                style.fontSize = "16pt";
            }
        }
    }
},
remove : function(str, char) {
    var v = "";
    if (str == undefined || str == null) {
        return v;
    }
    for (var i = 0, z = str.length; i < z; i++) {
        if (str.charAt(i) != char) {
            v += str.charAt(i);
        }
    }
    return v;
}, replace : function() {
    if (arguments.length < 3 || arguments.length % 2 == 0) {
        alert("replace usage: replace(value,srch1,rep1[,srch2,rep2...])");
        return;
    }
    var result = "";
    var source = arguments[0] || "";
    if (source != "") {
        for (var i = 1; i < arguments.length; i += 2) {
            var srch = arguments[i] || "";
            if (srch == "") {
                continue;
            }
            var repl = "";
            if (arguments.length > i + 1) {
                repl = arguments[i + 1] + "";
            }
            var start = 0;
            result = "";
            var found = source.indexOf(srch);
            while (found > -1) {
                result += source.substring(start, found) + repl;
                start = found + srch.length;
                found = source.indexOf(srch, start);
            }
            if (start < source.length) {
                result += source.substring(start);
            }
            source = result;
        }
    }
    return result;
}, replaceChildren : function(parentElement, firstNewChild /* ,otherChildren */) {
    ir.clearChildren(parentElement);
    parentElement.appendChild(firstNewChild);
    for (var i = 2; i < arguments.length; i++) {
        parentElement.appendChild(arguments[i]);
    }
}, 
replaceNewLineRegex : new RegExp(/(\\r)|(\r)|(\n)|(\\n)/g), 
replaceNewLines : function(s, withWhat) {
    return s == null || s == "" ? "" : s.replace(ir.replaceNewLineRegex, withWhat || "<br>");
}, 
right : function(s, len) {
    if (len <= 0) {
        return "";
    }
    s = s + "";
    if (s.length < len) {
        return s;
    }
    return s.substr(s.length - len);
}, round : function(n, decimals) {
	if (decimals==null) {
		decimals = 2;
	}
    return Math.round(Number(n) * Math.pow(10, decimals)) / Math.pow(10, decimals);
}, roundFraction : function(dValue, nFraction) {
    return Math.round(dValue * nFraction + (dValue > 0 ? .0001 : 0 - .0001)) / nFraction;
},
/**
 * invokes passed function with parameters (element,index,resultContainer) for
 * each element in array.
 * @return resultContainer object
 */
reduce : function(arr, fn, resultContainer) {
    for (var i = 0, z = arr.length; i < z; i++) {
        fn(arr[i], i, resultContainer);
    }
    return resultContainer;
}, rtrim : function(v) {
    if (v == null) {
        return "";
    }
    var whitespace = " \r\n\t\b";
    v = v + "";// coerce to string
    var lastNonWhitespace = v.length - 1;
    while (whitespace.indexOf(v.charAt(lastNonWhitespace)) > -1 && lastNonWhitespace > 0) {
        lastNonWhitespace--;
    }
    return lastNonWhitespace == 0 ? "" : v.substr(0, lastNonWhitespace + 1);
}, scrollIntoView : function(element, container) {
    if (element.scrollIntoView) {
        element.scrollIntoView({top:-100,behavior:'smooth'});
    } else {
        container = container || element.parentNode;
        container.scrollTop = element.offsetTop;
    }
},

scrollToBottom:function(idOrElement)
{
	var element = idOrElement;
	if (typeof(element) == "string")
	{
		element = ir.get(idOrElement);
	}
	element.scrollTop = element.scrollHeight;
},
scrollToTop:function(id,offset) {		
	if(id)
	{
		ir.get(id).scroll = offset ? offset : 0;
	}
	else
	{	
		document.documentElement.scrollTop = offset ? offset : 0;
		if(isMobileDevice())
		{
			document.body.scrollTop = offset ? offset : 0;
		}
	}		
}, selectedText : function(idOrEle) {
    var selectBox = ir.get(idOrEle);
    if (selectBox.selectedIndex > -1 && selectBox.options && selectBox.selectedIndex < selectBox.options.length) {
        return selectBox.options[selectBox.selectedIndex].text;
    }
    return "";
}, sendClick : function(idOrElement) {
    var ele = ir.get(idOrElement);
    try {
        var evt = document.createEvent("MouseEvents");
        evt.initEvent("click", true, false);
        ele.dispatchEvent(evt);
    }
    catch (e) {
        ir.log("Failed to send click event to " + idOrElement + ": " + e);
        if (ele) {
            ele.focus();
        }
    }
}, set : function(id, val) {
    val = (val == null ? "" : val+"");
    var element = (typeof (id) == "string" ? document.getElementById(id) : id);
    var group = null;
    if (!element) {
    	//radio button group?
        group = document.getElementsByName(id);
        if (group == null || group.length == 0) {
            throw "ir.set cannot find '" + id + "'";
        }
        element = group[0];
    } 
    if (element.type == undefined || element.type == "" || element.type == "button") {
        element.innerHTML = val;
        if(element.value != null){
        	element.value = val;
        }
        return false;
    }
    if (element.type == "radio") {
        if (group == null) {
            group = document.getElementsByName(id);
        }
        for (var i = 0, len = group.length; i < len; i++) {
            var rb = group[i];
            if (val == null) {
                rb.checked = false;
            } else if (rb.value == val) {
                rb.checked = true;
                return false;
            }
        }
        if (val == null) {
            return false;
        }
    }
    if (element.type == "checkbox") {
        element.checked = isTrue(val);
        return false;
    }
    element.value = val;
    return false;
},
/** ir.setAttrs sets the attributes of a DOM element from an object that contains
 * property values.
 */
setAttrs:function(idOrElement,objectWithAttributes) {
	var element = ir.get(idOrElement);
	if (element) {
		for (var attrName in objectWithAttributes) {
			if (objectWithAttributes.hasOwnProperty(attrName)) {
				var attr = objectWithAttributes[attrName];
				if (attrName == "style") {				
					ir.style(element,attr);
				} else {
					element[attrName] = attr;
				}
			}
		}
	}
},
  setBackground : function(id, color) {
    document.getElementById(id).style.backgroundColor = color;
}, setCaret : function(idOrObj, pos) {
    var box = ir.get(idOrObj);
    var valueLength = box.value.length;
    if (pos == -1) {
        pos = valueLength;
    }
    try {
	    if (box.setSelectionRange) {
	        box.focus();
	        box.setSelectionRange(pos, pos);
	    } else {
	        var range = box.createTextRange();
	        range.move("character", pos);
	        range.collapse(false);
	        range.select();
	    }
    } catch (exc) {
        box.focus();    	
    }
}, 
setIfExists:function(idOrObj,value) {
	var obj = ir.get(idOrObj,true);
	if (obj != null) {
		ir.set(obj,value);
	}	
},
/** ir.setLocalIp will do a bunch of stuff I don't really understand and at the end
 *  the ir.localIp value will be set - note you cannot call this synchronously, you 
 *  have to set it and check back in a bit..
 */
setLocalIp:function(){
	if (ir.localIp!=null || croute.mobile) {
		return;
	}
	window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;  
	var pc= null;
	try{
		pc = new RTCPeerConnection({iceServers:[]});
	}
	catch (e) {
		log("Failed RTCPeerConnection: " + e);		
		return
	}
	var noop = function(){};      
	pc.createDataChannel("");    
	pc.createOffer(pc.setLocalDescription.bind(pc), noop);    
	pc.onicecandidate = function(ice) {
	    if(!ice || !ice.candidate || !ice.candidate.candidate) {
	    	return;
	    }
	    var myIP = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/.exec(ice.candidate.candidate)[1];
	    ir.localIp = myIP;   
	    pc.onicecandidate = noop;
		};
},
/** sets a value on an element ensuring the value is numeric if not blank, 
 * unless it's zero and blankIfZero is true, then it sets it to ""
 */
setn:function(idOrElement,numberValue,blankIfZero,decimals) {
	var sv="";
	if (numberValue!="" && ! isNaN(numberValue)) {
		sv=numberValue;
	} else if (numberValue==0 && blankIfZero) {
		sv = "";
	} else if (decimals != undefined) {
		if (! isNaN(decimals)) {
			sv = numberValue.toFixed(decimals);
		} else {
			sv = numberValue+"";
		}
	}
	ir.set(idOrElement,sv);
},
setReadOnly : function(idOrBox, boolOrNull) {
    var readOnlyOn = boolOrNull == null ? true : boolOrNull;
    var box = ir.get(idOrBox);
    if (readOnlyOn) {
        box.setAttribute("readonly", "readonly");
        box.style.color = "darkgray";
    } else {
        box.removeAttribute("readonly");
        box.style.color = "";
    }
}, show : function(idOrElement, optionalYesNo) {
    var mode = "";
    if (optionalYesNo != null && !isTrue(optionalYesNo)) {
        mode = "none";
    }
    var element = ir.get(idOrElement);
    if (element) {
        element.style.display = mode;
    } else {
        ir.log("show(" + idOrElement + " ): not found.");
    }
},
/** ir.split is like string.split except it excludes null and blank tokens */
split : function(src, delim) {
    var res = [];
    var va = src.split(delim || ",");
    for (var i = 0, len = va.length; i < len; i++) {
        var v = ir.trim(va[i]);
        if (v > "") {
            res.push(v);
        }
    }
    return res;
},
/** ir.startsWith mimics java.lang.String.startsWith */
startsWith : function(str, prefix, optionalLen) {
    return ir.startsWithLen(str, prefix, optionalLen || prefix.length);
},
/** ir.startsWithLen is a flavour of ir.startsWith optimized for
 * use in a loop comparing large numbers of strings because we don't
 * ask for length of the prefix.
 */
startsWithLen : function(str, prefix, prefixLen) {
    if (str.length < prefixLen) {
        return false;
    }
    for (var i = prefixLen - 1; (i >= 0) && (str[i] === prefix[i]); --i) {
        continue;
    }
    return i < 0;
},
/** sets the element's style from object having style name keys and values */
style:function(idOrElement,styleObj) {
	var element = ir.get(idOrElement);
	if (element) {
		for (var styleAttr in styleObj) {
			if (styleObj.hasOwnProperty(styleAttr)) {
				element.style[styleAttr] = styleObj[styleAttr];
			}
		}
	}
},
/**
 * submits the first form on the page, with the query string
 * @param sExtraParms querystring - leading ? or & optional
 */
submit : function(sExtraParms) {
    var frm = document.forms[0];
    if (null == frm) {
        window.status = "submitForm: no form.";
    } else {
        if (sExtraParms != undefined && sExtraParms > "") {
            if (frm.action.indexOf("?") == -1) {
                frm.action += "?";
            } else {
                frm.action += "&";
            }
            frm.action += sExtraParms;
        }
        frm.submit();
    }
},
/**
 * a naive,oversimplified mustache only suitable in browser:
 * <ul>
 * <li>no escaping of anything in the template or values</li>
 * <li>regular expressions not used</li>
 * <li>only {{ used not {{{ like mustache for html</li>
 * <li>no arrays of repeated values</li>
 * <li>no sub objects like {value:1,subobject:{subvalue:3}}</li>
 * </ul>
 * usage:<br>
 * ir.template("<div id='prefix{{row}}'>{{content}}</div>",{row:71,content:"hello,whirled"});
 * @param 1 can be a template string or the id of an element or script block
 *            with a template or the element itself
 * @param 2 must be a flat object like {row:71,content:"hello,whirled"}
 */
template : function(templateStrOrObjectOrId, valueObject) {
    var a = [];
    var ts = templateStrOrObjectOrId;
    if (ts.innerHTML || ts.value) {
        ts = ir.trim(ts.innerHTML || ts.value);
    } else {
        if (ts.indexOf("{{") == -1) {
            var element = document.getElementById(ts);
            if (element) {
                ts = ir.trim(element.innerHTML || ts.value);
            }
        }
    }
    var start = 0;
    var open = ts.indexOf("{{");
    var close = ts.indexOf("}}", open);
    while (start < ts.length && open > -1 && close > open) {
        if (start < open) {
            a.push(ts.substring(start, open));
        }
        var name = ts.substring(open + 2, close);
        a.push(valueObject[name]);
        start = close + 2;
        open = ts.indexOf("{{", start);
        close = ts.indexOf("}}", open);
    }
    a.push(ts.substring(start));
    return a.join("");
}, 
translate : function(idOrObj,x,y)
{
	var ele = idOrObj;
	if (typeof (ele) == "string")
	{
		ele = ir.get(obj);
	}
	if(x==0 && y==0)
	{
		ele.style.transform = "";
		ele.style.WebKitTransform = ""; // android, safari, chrome 
		ele.style.MozTransform = ""; 	// old firefox 
		ele.style.webKitTransform = "";
		return;
	}
	ele.style.transform = "translate("+x+"px,"+y+"px)";
	ele.style.WebKitTransform = "translate("+x+"px,"+y+"px)"; // android, safari, chrome 
	ele.style.MozTransform = "translate("+x+"px,"+y+"px)"; 	// old firefox 
	ele.style.webKitTransform = "translate("+x+"px,"+y+"px)";
	return;
},
trim : function(s) {
    if (s == null) {
        return "";
    }
    if (s.trim) {
        return s.trim();
    }
    var ls = ir.ltrim(s);
    var rs = ir.rtrim(ls);
    return rs;
},
/**
 * returns the value or checked property or innerHTML of the passed id or element
 */
v : function(idOrElement) {
    var o = idOrElement;
    if (typeof (o) == "string") {
        o = document.getElementById(idOrElement);
    }
    if (o == null || o.type == "radio") {
        var list = document.getElementsByName(idOrElement);
        if (list && list.length > 0) {
            for (var i = 0; i < list.length; i++) {
            	var box = list[i];
                if (box.checked) {
                    return box.value;
                }
            }
        return list[0].value;
        } else {
            log("ir.v('" + idOrElement + "'): element not found.");
            return "?" + idOrElement + "?";
        }
    }
    if (o == null) {
        log("ir.v('" + idOrElement + "'): element not found.");
        return "?" + idOrElement + "?";
    }
    if (o.type == "checkbox") {
        return o.checked;
    }
    return (o.value != null ? o.value : o.innerHTML) || "";
},
vdt : function(id) {
	var date = ir.v(id);
	if(date && date.length>0) {
		return irdate.parse(date);	
	}
	return date;
},
/** returns ir.n(ir.v(idOrElement)) */ 
vn : function(idOrElement) {
    return ir.n(ir.v(idOrElement));
},
/** indicates whether element passed is visible */ 
visible : function(idOrElement) {
    var ele = ir.get(idOrElement);
    if (ele.style.display == "none" || ele.style.visibility == "hidden" || (ele.width + ele.heigth == 0) || ele.classList.contains("collapse")) {
        return false;
    }
    return true;
}, winHeight : function() {
    return window.innerHeight || document.documentElement.clientHeight;
}, winTop : function() {
    return window.pageYOffset || document.body.scrollTop || 1;
}, winWidth : function() {
    return window.innerWidth || document.documentElement.clientWidth;
}, ymd : function(dt) {
    if (!dt) {
        return "0000-00-00";
    }
    return dt.getUTCFullYear() + "-" + right("0" + (dt.getMonth() + 1), 2) + "-" + right("0" + (dt.getDate()), 2);
}, ymdhm : function(dt) {
    if (!dt || !dt.getTime || dt.getTime() < 18000000) {
        return "0000-00-00 00:00";
    }
    return dt.getUTCFullYear() + "-" + right("0" + (dt.getMonth() + 1), 2) + "-" + right("0" + (dt.getDate()), 2) + " " + right("0" + (dt.getHours()), 2) + ":" + right("0" + (dt.getMinutes()), 2);
}, zzz_ir : 0 };
/** irpt: ir paging table */
var irpt = {/* ir - paging table */
bottom : function(tableId) {
    var div = ir.get("pt" + tableId + "Div");
    div.scrollTop = div.scrollHeight - div.clientHeight;
}, page : function(tableId, pages) {
    var div = ir.get("pt" + tableId + "Div");
    var at = div.scrollTop;
    var to = at + (pages * div.clientHeight);
    div.scrollTop = Math.max(0, Math.min(div.scrollHeight - div.clientHeight, to));
}, top : function(tableId) {
    ir.get("pt" + tableId + "Div").scrollTop = 0;
}, zz_irpt : 0 };
function addDays(date, daysOffset) {
    return new Date(date.getUTCFullYear(), date.getMonth(), date.getDate() + daysOffset);
}
function addEvent(obj, evType, fn) {
    ir.addEvent(obj, evType, fn);
}
function addMonths(date, monthsOffset) {
    return new Date(date.getUTCFullYear(), date.getMonth() + monthsOffset, date.getDate());
}
function addOption(selectObj, value, text) {
    var opt = document.createElement("OPTION");
    opt.value = value+"";
    opt.text = text;
    selectObj.options.add(opt);
    return opt;
}
function brightness(r, g, b) {
    if (arguments.length == 1) {// {r:?,g:?,b:?}
        return Math.round(Math.sqrt(.241 * r.r * r.r + .691 * r.g * r.g + .068 * r.b * r.b));
    }
    return Math.round(Math.sqrt(.241 * r * r + .691 * g * g + .068 * b * b));
}
function cancelEvent(e) {
    if (e) {
        if (isIE()) {// it is IE
            e.returnValue = false;
            e.cancelBubble = true;
        } else {// it could be firefox
            e.preventDefault();
            e.stopPropagation();
        }
    }
    return false;
}
/** Indicates whether expression evaluates to a Number */
function checkNumericExpression(expression) {
    try {
        return !isNaN(new Function("return " + expression)());
    }
    catch (e) {
        return false;
    }
}
function cell(tr, otherParmObj) {
    var td = tr.insertCell(-1);
    td.className = 'tdb';
    if (otherParmObj != null) {
        for ( var attr in otherParmObj) {
            td[attr] = otherParmObj[attr];
        }
    }
    return td;
}
function clearSelectBox(sbIdOrObj, leaveAtTop) {
    var sb = typeof (sbIdOrObj) == "string" ? get(sbIdOrObj) : sbIdOrObj;
    if (sb.options) {
        try {
            sb.options.length = leaveAtTop;
        }
        catch (nevermind) {}
        for (var i = sb.options.length - 1; i >= leaveAtTop; i--) {
            sb.options[i] = null;
        }
    }
}
function clearTable(tblOrId, nLeaveAtTop, nLeaveAtBottom) {
    var tbl = tblOrId;
    if (typeof (tblOrId) == "string") {
        tbl = ir.get(tblOrId);
    }
    nLeaveAtTop = nLeaveAtTop || 0;
    nLeaveAtBottom = nLeaveAtBottom || 0;
    var a = tbl.rows;
    while (a.length > nLeaveAtTop + nLeaveAtBottom) {
        tbl.deleteRow(nLeaveAtTop);
    }
}
function colorTable(tableId, startIndex) {
    var index = startIndex ? startIndex : 0;
    var rows = ir.get(tableId).rows;
    for (var i = 0, z = rows.length; i < z; i++) {
        var tr = rows[i];
        if (tr.style.display == "none") {
            continue;
        }
        tr.className = "tr" + (index % 2);
        index++;
    }
    return index;
}
function colorTables(/* tables... */) {
    for (var i = 0; i < arguments.length; i++) {
        colorTable(arguments[i]);
    }
}
function confirmEvent(e, msg) {
    if (!window.confirm(msg)) {
        return cancelEvent(e);
    }
    return true;
}
function copy(o) {
    var p = {};
    for ( var a in o) {
        p[a] = o[a];
    }
    return p;
}
function daysDifference(d1, d2) {
   /** use Date.UTC to avoid incorrect date difference around Day Light Savings (Mar 12th, Nov 5th)*/
   var t1 = new Date(Date.UTC(d1.getFullYear(),d1.getMonth(),d1.getDate(),0,0,0));
   var t2 = new Date(Date.UTC(d2.getFullYear(),d2.getMonth(),d2.getDate(),0,0,0));
   var millisDiff = t1.getTime() - t2.getTime();
   var divisor =  1000 * 60 * 60 * 24;
   return Math.floor(millisDiff / divisor);
}
function deg2rad(deg) {
    return deg * (Math.PI / 180);
}
function empty(hashOrArray) {
    if (hashOrArray.length != undefined) {
        return hashOrArray.length == 0;
    }
    for ( var prop in hashOrArray) {
        if (hashOrArray.hasOwnProperty(prop)) {
            return false;
        }
    }
    return true;
}
function enableDisable(id, enabled) {
    get(id).enabled = enabled;
}
function focusOnFirstControl() {
    if (null != document.forms[0]) {
        for (var i = 0; i < document.forms[0].elements.length; i++) {
            var o = document.forms[0].elements[i];
            try {
                if (-1 < "hiddenbuttonsubmit".indexOf(o.type) || o.style.visibility == "none" || o.style.visibility == "hidden" || o.disabled) {
                    continue;
                }
                if(-1 < "displayOnly".indexOf(o.classList.value))
            	{
                	continue;
            	}
                o.focus();
                window.status = "";
                return;
            }
            catch (e) {
                window.status = "focusOnFirstControl:" + e;
            }
        }
    }
}
/**
 * returns elements for which the passed function returns true
 * @param array
 * @param function
 */
function filter(arr, fn) {
    var result = [];
    var len = arr.length;
    for (var i = 0; i < len; i++) {
        var element = arr[i];
        if (fn(element)) {
            result.push(element);
        }
    }
    return result;
}
function format(k /* ,args */) {// mimics java.text.MessageFormat.format
    try {
        var v = k;
        if (arguments != null) {
            if (arguments.length > 1) {
                for (var i = 1; i < arguments.length; i++) {
                    var token = "{" + (i - 1) + "}";
                    v = ir.replace(v, token, arguments[i] + "");
                }
            }
        }
        return v;
    }
    catch (e) {
        return k;
    }
}
// ------------------------------------------------------------------
// formatDate (date_object, format)
// Returns a date in the output format specified.
// The format string uses the same abbreviations as in getDateFromFormat()
// y,yy,yyyy year
// M,MM,MMM month
// ------------------------------------------------------------------
function formatDate(date, format) {
    if (typeof (date) == "string") {
        date = ir.parseYmd(date);
    }
    format = format + "";
    var result = "";
    var i_format = 0;
    var c = "";
    var token = "";
    var y = date.getYear() + "";
    var M = date.getMonth() + 1;
    var d = date.getDate();
    var E = date.getDay();
    var H = date.getHours();
    var m = date.getMinutes();
    var s = date.getSeconds();
    // var yyyy,yy,MMM,MM,dd,hh,h,mm,ss,ampm,HH,KK,K,kk,k;
    // Convert real date parts into formatted versions
    var value = new Object();
    if (y.length < 4) {
        y = "" + (y - 0 + 1900);
    }
    value["y"] = "" + y;
    value["yyyy"] = y;
    value["yy"] = y.substring(2, 4);
    value["M"] = M;
    value["MM"] = LZ(M);
    value["MMM"] = MONTH_NAMES[M - 1];
    value["NNN"] = MONTH_NAMES[M + 11];
    value["d"] = d;
    value["dd"] = LZ(d);
    value["E"] = DAY_NAMES[E + 7];
    value["EE"] = DAY_NAMES[E];
    value["H"] = H;
    value["HH"] = LZ(H);
    if (H == 0) {
        value["h"] = 12;
    } else if (H > 12) {
        value["h"] = H - 12;
    } else {
        value["h"] = H;
    }
    value["hh"] = LZ(value["h"]);
    if (H > 11) {
        value["K"] = H - 12;
    } else {
        value["K"] = H;
    }
    value["k"] = H + 1;
    value["KK"] = LZ(value["K"]);
    value["kk"] = LZ(value["k"]);
    if (H > 11) {
        value["a"] = "PM";
    } else {
        value["a"] = "AM";
    }
    value["m"] = m;
    value["mm"] = LZ(m);
    value["s"] = s;
    value["ss"] = LZ(s);
    while (i_format < format.length) {
        c = format.charAt(i_format);
        token = "";
        while ((format.charAt(i_format) == c) && (i_format < format.length)) {
            token += format.charAt(i_format++);
        }
        if (value[token] != null) {
            result = result + value[token];
        } else {
            result = result + token;
        }
    }
    return result;
}
function get(id, suppressLogIfNull) {
    return ir.get(id, suppressLogIfNull);
}
function getAllByPrefix(sPfx) {
    return document.querySelectorAll("[id^=" + sPfx + "]");
}

function getDateFromFormat(val, format)
{
	var i_val = 0;
	var i_format = 0;
	var c = "";
	var token = "";
	var x = 0, y = 0;
	var now = new Date();
	var year = now.getYear();
	var month = now.getMonth() + 1;
	var date = 1;
	var hh = 0;
	var mm = 0;
	var ss = 0;
	var ampm = "";
	val = val + "";
	format = format + "";
	var decimal = val.lastIndexOf(".");
	if (decimal > 0 && decimal < val.length)
	{
		val = val.substring(0, decimal);
	}
	while (i_format < format.length)
	{
		// Get next token from format string
		c = format.charAt(i_format);
		token = "";
		while ((format.charAt(i_format) == c) && (i_format < format.length))
		{
			token += format.charAt(i_format++);
		}
		// Extract contents of value based on format token
		if (token == "yyyy" || token == "yy" || token == "y")
		{
			if (token == "yyyy")
			{
				x = 4;
				y = 4;
			}
			if (token == "yy")
			{
				x = 2;
				y = 2;
			}
			if (token == "y")
			{
				x = 2;
				y = 4;
			}
			year = getInt(val, i_val, x, y);
			if (year == null)
			{
				return null;
			}
			i_val += year.length;
			if (year.length == 2)
			{
				if (year > 70)
				{
					year = 1900 + (year - 0);
				}
				else
				{
					year = 2000 + (year - 0);
				}
			}
		}
		else if (token == "MMM" || token == "NNN")
		{
			month = 0;
			for (var i = 0; i < MONTH_NAMES.length; i++)
			{
				var month_name = MONTH_NAMES[i];
				if (val.substring(i_val, i_val + month_name.length)
						.toLowerCase() == month_name.toLowerCase())
				{
					if (token == "MMM" || (token == "NNN" && i > 11))
					{
						month = i + 1;
						if (month > 12)
						{
							month -= 12;
						}
						i_val += month_name.length;
						break;
					}
				}
			}
			if ((month < 1) || (month > 12))
			{
				return null;
			}
		}
		else if (token == "EE" || token == "E")
		{
			for (var i = 0; i < DAY_NAMES.length; i++)
			{
				var day_name = DAY_NAMES[i];
				if (val.substring(i_val, i_val + day_name.length).toLowerCase() == day_name
						.toLowerCase())
				{
					i_val += day_name.length;
					break;
				}
			}
		}
		else if (token == "MM" || token == "M")
		{
			month = getInt(val, i_val, token.length, 2);
			if (month == null || (month < 1) || (month > 12))
			{
				return null;
			}
			i_val += month.length;
		}
		else if (token == "dd" || token == "d")
		{
			date = getInt(val, i_val, token.length, 2);
			if (date == null || (date < 1) || (date > 31))
			{
				return null;
			}
			i_val += date.length;
		}
		else if (token == "hh" || token == "h")
		{
			hh = getInt(val, i_val, 1, 2);
			if (hh == null || (hh < 1) || (hh > 12))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "HH" || token == "H")
		{
			hh = getInt(val, i_val, 1, 2);
			if (hh == null || (hh < 0) || (hh > 23))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "KK" || token == "K")
		{
			hh = getInt(val, i_val, token.length, 2);
			if (hh == null || (hh < 0) || (hh > 11))
			{
				return null;
			}
			i_val += hh.length;
		}
		else if (token == "kk" || token == "k")
		{
			hh = getInt(val, i_val, token.length, 2);
			if (hh == null || (hh < 1) || (hh > 24))
			{
				return null;
			}
			i_val += hh.length;
			hh--;
		}
		else if (token == "mm" || token == "m")
		{
			mm = getInt(val, i_val, token.length, 2);
			if (mm == null || (mm < 0) || (mm > 59))
			{
				return null;
			}
			i_val += mm.length;
		}
		else if (token == "ss" || token == "s")
		{
			ss = getInt(val, i_val, token.length, 2);
			if (ss == null || (ss < 0) || (ss > 59))
			{
				return null;
			}
			i_val += ss.length;
		}
		else if (token == "a")
		{
			if (val.substring(i_val, i_val + 2).toLowerCase() == "am")
			{
				ampm = "AM";
			}
			else if (val.substring(i_val, i_val + 2).toLowerCase() == "pm")
			{
				ampm = "PM";
			}
			else
			{
				return null;
			}
			i_val += 2;
		}
		else
		{
			if (val.substring(i_val, i_val + token.length) != token)
			{
				return null;
			}
			else
			{
				i_val += token.length;
			}
		}
	}
	// If there are any trailing characters left in the value, it doesn't match
	if (i_val != val.length)
	{
		return null;
	}
	// Is date valid for month?
	if (month == 2)
	{
		// Check for leap year
		if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0))
		{ // leap year
			if (date > 29)
			{
				return null;
			}
		}
		else
		{
			if (date > 28)
			{
				return null;
			}
		}
	}
	if ((month == 4) || (month == 6) || (month == 9) || (month == 11))
	{
		if (date > 30)
		{
			return null;
		}
	}
	// Correct hours value
	if (hh < 12 && ampm == "PM")
	{
		hh = hh - 0 + 12;
	}
	else if (hh > 11 && ampm == "AM")
	{
		hh -= 12;
	}
	return new Date(year, month - 1, date, hh, mm, ss);
}
function getInt(str, i, minlength, maxlength)
{
	for (var x = maxlength; x >= minlength; x--)
	{
		var token = str.substring(i, i + x);
		if (token.length < minlength)
		{
			return null;
		}
		if (isInteger(token))
		{
			return token;
		}
	}
	return null;
}
function getKm(lat1, lon1, lat2, lon2) {
    var earthMeanRadiusKm = 6371;
    var dLat = deg2rad(lat2 - lat1);
    var dLon = deg2rad(lon2 - lon1);
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthMeanRadiusKm * c;
}
function getDocBase() {
    return document.URL.substring(0, document.URL.lastIndexOf("/") + 1);
}
function getPosition(obj) {
    var res = { x : 0, y : 0 };
    while (obj = obj.offsetParent) {
        res.x += obj.offsetLeft;
        res.y += obj.offsetTop;
    }
    return res;
}
function hex(n, leftPadLen) {
    var h = n.toString(16);
    if (leftPadLen > 0) {
        h = lpad(h, '0', leftPadLen);
    }
    return h;
}
/**
 * returns object {r:?n,g:?n,b:?n} from hex colour string -- must be strictly
 * compliant "#rrggbb"
 */
function hexColourToRGB(hexColour) {
    var r1 = parseInt(hexColour.substring(1, 2), 16) * 16;
    var r2 = parseInt(hexColour.substring(2, 3), 16);
    var r = r1 + r2;
    var g1 = parseInt(hexColour.substring(3, 4), 16) * 16;
    var g2 = parseInt(hexColour.substring(4, 5), 16);
    var g = g1 + g2;
    var b1 = parseInt(hexColour.substring(5, 6), 16) * 16;
    var b2 = parseInt(hexColour.substring(6, 7), 16);
    var b = b1 + b2;
    return { "r" : r, "g" : g, "b" : b };
}
/**
 * Returns " id='?' name='?' " where ? is concatenation of all arguments passed
 */
function id(n /* ... */) {
    var id = n;
    for (var i = 1; i < arguments.length; i++) {
        id += arguments[i];
    }
    return " id=" + ir.q(id) + " name=" + ir.q(id) + " ";
}
function intomm(idmm, idin) {
    ir.set(idmm, Math.round(ir.vn(idin) * 25.4 * 10000) / 10000);
}
function isIE() {
      return typeof document.documentMode == "number"
            || window.ActiveXObject != null || (/Edge\/\d./i.test(navigator.userAgent));
}
function isInteger(val)
{
	var digits = "1234567890";
	for (var i = 0; i < val.length; i++)
	{
		if (digits.indexOf(val.charAt(i)) == -1)
		{
			return false;
		}
	}
	return true;
}
function isiPad(){
	return (/\(iPad/i.test(navigator.userAgent));
}
function isTrue(v) {
    return ir.isTrue(v);
}
function left(s, len) {
    return ir.left(s, len);
}
function lpad(sVal, chFill, nLen) {
    sVal += "";// coerce to a string
    while (sVal.length <= nLen) {
        sVal = chFill + sVal;
    }
    return sVal.substring(sVal.length - nLen, sVal.length);
}
function log(m) {
    ir.log(m);
}
function LZ(x) {
    return (x < 0 || x > 9 ? "" : "0") + x;
}
function midnight(date) {
    return new Date(date.getUTCFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
}
function mmtoi(idin, idmm) {
    if (!isNaN(val(idmm)))
        get(idin).value = Math.round(num(idmm) / 25.4 * 10000) / 10000;
}
function num(id) {
    return ir.vn(id);
}
function numericInput(obj, allowDecimal, allowNegative, errorMsg) {
    if (!allowNegative && event.keyCode == 45) {
        event.keyCode = 77;// forces an error
    }
    if (!allowDecimal && event.keyCode == 46) {
        event.keyCode = 77;// forces an error
    }
    if (event.keyCode != 45 && event.keyCode != 46 && (event.keyCode < 48 || event.keyCode > 57)) {
        beepMsg(errorMsg);
        event.returnValue = false;
    } else {
        var v = obj.value || "";
        if (v.length > 0) {
            if (event.keyCode == 45) {
                v += "-";
            } else if (event.keyCode == 46) {
                v += ".";
            } else {
                v += (event.keyCode - 48);
            }
            if (isNaN(v)) {
                beepMsg(errorMsg);
                event.returnValue = false;
            }
        }
    }
}
function pick(idFromList, idToTextBox) {
    ir.set(idToTextBox, ir.v(idFromList));
}
function radians(degrees) {
    return degrees * Math.PI / 180.0;
}
function removeOption(sb, idx) {
    try {
        sb.options.remove(idx);
    }
    catch (e) {
        sb.remove(idx);
    }
}
function replaceHtml(el, html) {
    var oldEl = typeof el === "string" ? document.getElementById(el) : el;
    /*
     * @cc_on // Pure innerHTML is slightly faster in IE oldEl.innerHTML = html;
     * return oldEl; @
     */
    var newEl = oldEl.cloneNode(false);
    newEl.innerHTML = html;
    oldEl.parentNode.replaceChild(newEl, oldEl);
    /*
     * Since we just removed the old element from the DOM, return a reference to
     * the new element, which can be used to restore variable references.
     */
    return newEl;
}
function rgbHex(r, g, b) {
    return hex(r, 2) + hex(g, 2) + hex(b, 2);
}
function right(s, len) {
    return ir.right(s, len);
}
function round(n, decimals) {
    return ir.round(n, decimals);
}
function rpad(sVal, chFill, nLen) {
    sVal += "";// coerce to a string
    while (sVal.length <= nLen) {
        sVal += chFill;
    }
    return sVal;
}
function sameDay(date1, date2) {
    return sameMonth(date1, date2) && date1.getDate() == date2.getDate();
}
function sameMonth(date1, date2) {
    return date1.getUTCFullYear() == date2.getUTCFullYear() && date1.getMonth() == date2.getMonth();
}
function sameWeek(date1, date2) {
    return sameDay(sunday(date1), sunday(date2));
}
function ses() {
    var hash = typeof (sessionStorage) == "undefined" ? null : sessionStorage;
    if (hash == null) {
        var parent = window.ActiveXObject ? window : navigator;
        if (parent["ir"] == null) {
            parent["ir"] = {};
        }
        hash = parent["ir"];
    }
    return hash;
}
function sesGet(k, defaultIfNull) {
    var h = ses();
    return h[k] ? h[k] : defaultIfNull;
}
function sesSet(k, v) {
    var h = ses();
    h[k] = v;
}
function set(id, val) {
    ir.set(id, val);
    return false;
}
function setDisabled(id, boolOrNull) {
    var toBeDisabled = boolOrNull == null ? true : boolOrNull;
    get(id).disabled = toBeDisabled;
}
function setParameter(url, parmName, parmVal) {
    try {
        var parmAt = Math.max(url.indexOf("&" + parmName + "="), url.indexOf("?" + parmName + "="));
        if (parmAt == -1) {
            if (url.charAt(url.length - 1) != "?") {
                url += url.indexOf("?") == -1 ? "?" : "&";
            }
            return url + parmName + "=" + parmVal;
        }
        var andAt = url.indexOf("&", parmAt + 1);
        if (andAt == -1) {
            return url.substring(0, parmAt + 1) + parmName + "=" + parmVal;
        }
        return url.substring(0, parmAt + 1) + parmName + "=" + parmVal + url.substring(andAt);
    }
    catch (e) {
        alert("setParameter error: " + e);
    }
}
function setReadOnly(id, boolOrNull) {
    var ro = boolOrNull == null ? true : boolOrNull;
    if (ro) {
        get(id).setAttribute("readonly", "readonly");
    } else {
        get(id).removeAttribute("readonly");
    }
}
function show(id, optionalYesNo) {
    ir.show(id, optionalYesNo);
}
function showHide(id/* boolean cond */) {
    if (arguments != null) {
        if (arguments.length > 1) {// condition mode
            if (arguments[1]) {
                ir.show(id);
            } else {
                ir.hide(id);
            }
            return;
        }
    }
    // toggle mode
    return ir.hideShow(id);
}
function sunday(date) {
    return addDays(date, 0 - date.getDay());
}
function timestr(dateObj) {
    if (typeof (dateObj) == "string") {
        dateObj = getDateFromFormat(dateObj, "yyyy-MM-dd HH:mm:ss");
    }
    if (dateObj.getHours() + dateObj.getMinutes() == 0) {
        return "";
    }
    var h = dateObj.getHours();
    var ampm = h > 12 ? "&nbsp;pm" : "&nbsp;am";
    if (h > 6 && h <= 18) {
        ampm = "";
    }
    if (h > 12) {
        h -= 12;
    }
    if (dateObj.getMinutes() == 0) {
        return h + ampm;
    }
    return h + ":" + right("0" + dateObj.getMinutes(), 2) + ampm;
}
function title(s) {
    var u = true;
    var s2 = "";
    for (var i = 0; i < s.length; i++) {
        if (s.charAt(i) == '_') {
            s2 += " ";
            u = true;
            continue;
        }
        if (u) {
            s2 += ("" + s.charAt(i)).toUpperCase();
            u = false;
        } else {
            s2 += s.charAt(i);
        }
    }
    return s2;
}
function toggleBlock(anchorId, spanId) {
    var span = ir.get(spanId);
    var anchor = ir.get(anchorId);
    if (span.style.display == "none") {
        span.style.display = "";
        anchor.innerHTML = anchor.innerHTML.replace("+", "-");
    } else {
        span.style.display = "none";
        anchor.innerHTML = anchor.innerHTML.replace("-", "+");
    }
    return false;
}
function toggleBlockInline(anchorId, spanId) {
    if (get(spanId).style.display == "none") {
        get(spanId).style.display = "inline";
        get(anchorId).innerHTML = get(anchorId).innerHTML.replace("+", "-");
    } else {
        hide(spanId);
        get(anchorId).innerHTML = get(anchorId).innerHTML.replace("-", "+");
    }
    return false;
}
/** 
 * check/uncheck all checkboxes with a name prefix
 */
function toggleAll(sPfx, bVal) {
    var eles = getAllByPrefix(sPfx);
    for (var i = 0; i < eles.length; i++) {
    	var cb = eles[i];
    	if (cb.id != sPfx) {
    		cb.checked = bVal;
    	}
    }
    return false;
}
/** check/uncheck all checkboxes with same name
	prefix as obj passed
*/
function toggleSame(obj) {
    obj = ir.get(obj);
    toggleAll(obj.id, obj.checked);
    return true;
}
function toHash(arr, keyAttrOrFnOrNull, hashToAppendTo) {
    var hash = hashToAppendTo || {};
    if (keyAttrOrFnOrNull != null) {// key is a function or attribute
        var fn = keyAttrOrFnOrNull;
        if (typeof (fn).toLowerCase() == "string") {// key is an attribute -
                                                    // turn it into a function
            fn = function(o) {
                return o[keyAttrOrFnOrNull];
            };
        }
        for (var i = 0, len = arr.length; i < len; i++) {
            hash[fn(arr[i])] = arr[i];
        }
    } else {// treat elements as keys and values as flags
        for (var i = 0, len = arr.length; i < len; i++) {
            hash[arr[i]] = true;
        }
    }
    return hash;
}

function url( /* varargs */) {
    if (arguments == null || arguments.length == 0) {
        alert("you must supply arguments to url()");
        return "";
    }
    var s = arguments[0];
    if (arguments.length > 1) {
        var and = "?";
        if (s.indexOf("?") > 0) {
            and = "&";
        }
        for (var i = 1; i < arguments.length; i++) {
            if (i % 2 == 1) {
                s += and;
            } else {
                s += "=";
            }
            s += encodeURIComponent(arguments[i]);
            and = "&";
        }
    }
    return s;
}
function val(idOrRadioButtonGroupName) {
    return ir.v(idOrRadioButtonGroupName);
}
function wait(bOn) {
    if (document.body) {
        document.body.style.cursor = bOn ? "wait" : "default";
    }
}
function writeln(msg) {
    document.write(msg + "<br>");
}
function xlate(k /* ,args */) {
    try {
        var v = k;
        if (arguments != null) {
            if (arguments.length > 1) {
                for (var i = 1; i < arguments.length; i++) {
                    v = ir.replace(v, "{" + (i - 1) + "}", arguments[i] + "");
                }
            }
        }
        return v;
    }
    catch (e) {
        return k;
    }
}