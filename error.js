"use strict";
var view = {
		getClientHeight:function(){
			return document.documentElement.clientHeight;
		},
		getClientWidth:function(){
			return document.documentElement.clientWidth;
		},
		onload:function(){
			document.addEventListener('mousemove',view.slide);
			var iconPrefix = sr5.iconPath.length==0?"icons/":sr5.iconPath;
			var body = document.body;
			body.style.backgroundImage = "url('"+iconPrefix+"dead.svg')";
			hoverPop.init();
		},
		slide:function(e){
			var body = document.body;
			var container = document.getElementById("container");
			var social = document.getElementById("social");
			var cWidth = view.getClientWidth();
			var cHeight = view.getClientHeight();	
			var offsetY = cHeight/2;
			var offsetX = cWidth/2;
			var x = e.pageX-offsetX || 0;
			var y = e.pageY-offsetY || 0;
			var clientMagnitude = Math.sqrt(cWidth*cWidth + cHeight*cHeight);
			var divMagnitude = 60;
			var ratio = divMagnitude/clientMagnitude;
			body.style.backgroundPosition = (50 + (x*ratio)) + "% " + (40 + (y*ratio)) +"%";
			//container.style.transform = "translate(" + ((x*ratio/20)) + "%, " + ((y*ratio/20)) + "%)";
			//social.style.transform = "scale("+Math.max(1,(Math.sqrt(x*x + y*y)+60)/Math.sqrt(offsetX*offsetX + offsetY*offsetY)*ratio*6+1)+")";
			
		},
		submit : function()
		{
			return false;
		},
		zz_view:0
};